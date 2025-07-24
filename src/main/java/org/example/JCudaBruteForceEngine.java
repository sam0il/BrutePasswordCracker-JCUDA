package org.example;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static jcuda.driver.JCudaDriver.*;

public class JCudaBruteForceEngine {
    private final HashValidator validator;
    private final String charSet;
    private final int maxLength;
    private final MaskConfig maskConfig;

    private static final int BLOCK_SIZE = 256;
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final int BATCH_SIZE = 16_000_000;  // Number of combinations per kernel call

    public JCudaBruteForceEngine(HashValidator validator, String charSet, int maxLength, MaskConfig maskConfig) {
        this.validator = validator;
        this.charSet = charSet;
        this.maxLength = maxLength;
        this.maskConfig = maskConfig;

        JCudaDriver.setExceptionsEnabled(true);
        cuInit(0);
    }

    public String crackPassword() {
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        CUmodule module = new CUmodule();
        cuModuleLoad(module, "C:\\Users\\samoi\\IdeaProjects\\BrutePasswordCrackerJCuda\\src\\main\\java\\org\\example\\JCudaKernel.ptx");

        CUfunction function = new CUfunction();
        cuModuleGetFunction(function, module, "bruteForceKernel");

        byte[] charsetBytes = charSet.getBytes(StandardCharsets.UTF_8);
        int charsetLength = charsetBytes.length;

        CUdeviceptr dCharset = new CUdeviceptr();
        cuMemAlloc(dCharset, charsetLength);
        cuMemcpyHtoD(dCharset, Pointer.to(charsetBytes), charsetLength);

        CUdeviceptr dTargetHash = new CUdeviceptr();
        cuMemAlloc(dTargetHash, 16);
        cuMemcpyHtoD(dTargetHash, Pointer.to(hexStringToByteArray(validator.targetHash)), 16);

        CUdeviceptr dFoundPassword = new CUdeviceptr();
        cuMemAlloc(dFoundPassword, MAX_PASSWORD_LENGTH + 1);

        CUdeviceptr dFoundFlag = new CUdeviceptr();
        cuMemAlloc(dFoundFlag, Sizeof.INT);

        for (int length = 1; length <= maxLength; length++) {
            Set<Integer> currentMaskIndices = maskConfig.getValidMaskIndices(length);
            Map<Integer, Character> maskMap = maskConfig.getMaskMap(currentMaskIndices);
            int unknownCount = length - maskMap.size();

            if (unknownCount <= 0) continue;

            long totalCombinations = (long) Math.pow(charSet.length(), unknownCount);
            long attemptsSoFar = 0;
            int batchCounter = 0;


            System.out.println("\nStarting length " + length +
                    " | Total combinations: " + totalCombinations +
                    " | Mask: " + maskConfig.getMask());

            byte[] maskArray = new byte[length];
            Arrays.fill(maskArray, (byte) -1);
            for (Map.Entry<Integer, Character> entry : maskMap.entrySet()) {
                int index = entry.getKey();
                if (index < length) {
                    maskArray[index] = (byte) entry.getValue().charValue();
                }
            }

            // debug print
           // System.out.println("Mask array: " + Arrays.toString(maskArray));

            CUdeviceptr dMaskArray = new CUdeviceptr();
            cuMemAlloc(dMaskArray, length);  // Allocate EXACTLY the password length
            cuMemcpyHtoD(dMaskArray, Pointer.to(maskArray), length);  // Copy EXACTLY the password length

            while (attemptsSoFar < totalCombinations) {
                int batchSize = (int) Math.min(BATCH_SIZE, totalCombinations - attemptsSoFar);
                int gridSize = (int) Math.ceil((double) batchSize / BLOCK_SIZE);

                // Generate sample candidate for logging
                String sampleCandidate = generateSampleCandidate(
                        length,
                        attemptsSoFar,
                        maskMap,
                        charSet
                );
              //  System.out.println("Generated sample candidate: " + sampleCandidate);  // DEBUG

                // Log progress at the start of each batch
                System.out.printf(
                        "Length %d | Batch %d | Attempts: %d/%d (%.2f%%) | Current: %s%n",
                        length,
                        batchCounter,
                        attemptsSoFar,
                        totalCombinations,
                        (attemptsSoFar * 100.0) / totalCombinations,
                        sampleCandidate
                );

                // Reset found flag for this batch
                cuMemcpyHtoD(dFoundFlag, Pointer.to(new int[]{0}), Sizeof.INT);

                Pointer kernelParameters = Pointer.to(
                        Pointer.to(new int[]{length}),
                        Pointer.to(dCharset),
                        Pointer.to(new int[]{charsetLength}),
                        Pointer.to(new long[]{attemptsSoFar}),
                        Pointer.to(new long[]{totalCombinations}),
                        Pointer.to(dTargetHash),
                        Pointer.to(dFoundPassword),
                        Pointer.to(dFoundFlag),
                        Pointer.to(dMaskArray)
                );

                cuLaunchKernel(function,
                        gridSize, 1, 1,
                        BLOCK_SIZE, 1, 1,
                        0, null,
                        kernelParameters, null
                );

                int syncResult = cuCtxSynchronize();
                if (syncResult != CUresult.CUDA_SUCCESS) {
                    System.err.println("CUDA error: " + syncResult);
                    // Skip to next password length on error
                    attemptsSoFar = totalCombinations;
                    continue;
                }

                int[] foundFlagHost = {0};
                cuMemcpyDtoH(Pointer.to(foundFlagHost), dFoundFlag, Sizeof.INT);

                if (foundFlagHost[0] == 1) {
                    int actualLength = Math.min(length, MAX_PASSWORD_LENGTH);
                    byte[] foundPwdBytes = new byte[actualLength];
                    cuMemcpyDtoH(Pointer.to(foundPwdBytes), dFoundPassword, actualLength);
                    String foundPassword = new String(foundPwdBytes, StandardCharsets.UTF_8);

                    System.out.println("\nSUCCESS! Password found in length " + length);
                    freeAll(dCharset, dTargetHash, dFoundPassword, dFoundFlag, dMaskArray, context);
                    return foundPassword;
                }

                attemptsSoFar += batchSize;
                batchCounter++;
            }

            cuMemFree(dMaskArray);
            System.out.println("Completed length " + length);
        }

        freeAll(dCharset, dTargetHash, dFoundPassword, dFoundFlag, null, context);
        System.out.println("Password not found for lengths 1-" + maxLength);
        return null;
    }

    // Helper to generate sample candidate for logging
    private String generateSampleCandidate(
            int length,
            long index,
            Map<Integer, Character> maskMap,
            String charSet
    ) {
        char[] candidate = new char[length];
        int charsetLength = charSet.length();
        long temp = index;

        // Initialize with mask values
        for (int i = 0; i < length; i++) {
            if (maskMap.containsKey(i)) {
                candidate[i] = maskMap.get(i);
            } else {
                candidate[i] = '?'; // placeholder
            }
        }

        // Fill in variable positions
        for (int i = length - 1; i >= 0; i--) {
            if (!maskMap.containsKey(i)) {
                int charIndex = (int) (temp % charsetLength);
                candidate[i] = charSet.charAt(charIndex);
                temp /= charsetLength;
            }
        }

        return new String(candidate);
    }

    private void freeAll(CUdeviceptr charset, CUdeviceptr targetHash,
                         CUdeviceptr foundPassword, CUdeviceptr foundFlag,
                         CUdeviceptr mask, CUcontext context) {
        cuMemFree(charset);
        cuMemFree(targetHash);
        cuMemFree(foundPassword);
        cuMemFree(foundFlag);
        if (mask != null) cuMemFree(mask);
        cuCtxDestroy(context);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
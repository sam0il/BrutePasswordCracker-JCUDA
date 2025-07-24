package org.example;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static jcuda.driver.JCudaDriver.*;

public class JCudaDictionaryEngine {
    private final HashValidator validator;
    private static final int MAX_PASSWORD_LENGTH = 32;
    private static final String PTX_FILE_PATH = "C:\\Users\\samoi\\IdeaProjects\\BrutePasswordCrackerJCuda\\src\\main\\java\\org\\example\\JCudaKernel.ptx";

    public JCudaDictionaryEngine(HashValidator validator) {
        this.validator = validator;
        JCudaDriver.setExceptionsEnabled(true);
        cuInit(0);
    }

    public String crackPassword(String dictionaryPath) {
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Load dictionary
        List<String> words = loadDictionary(dictionaryPath);
        int wordCount = words.size();
        System.out.println("Loaded " + wordCount + " words from dictionary");

        if (wordCount == 0) {
            System.err.println("Error: No words loaded from dictionary at: " + dictionaryPath);
            cuCtxDestroy(context);
            return null;
        }

        // Find max word length (capped at MAX_PASSWORD_LENGTH)
        int maxWordLength = 0;
        for (String word : words) {
            if (word.length() > maxWordLength) {
                maxWordLength = word.length();
            }
        }
        maxWordLength = Math.min(maxWordLength, MAX_PASSWORD_LENGTH);
        System.out.println("Maximum word length: " + maxWordLength);

        // Prepare GPU memory for dictionary and word lengths
        byte[] dictBytes = new byte[wordCount * MAX_PASSWORD_LENGTH];
        int[] wordLengths = new int[wordCount];

        int index = 0;
        for (String word : words) {
            byte[] bytes = word.getBytes(StandardCharsets.UTF_8);
            int length = Math.min(bytes.length, MAX_PASSWORD_LENGTH);
            System.arraycopy(bytes, 0, dictBytes, index * MAX_PASSWORD_LENGTH, length);
            wordLengths[index] = length;
            index++;
        }

        // Load CUDA module and kernel function
        CUmodule module = new CUmodule();
        CUfunction function = new CUfunction();
        try {
            System.out.println("Loading PTX file from: " + PTX_FILE_PATH);
            cuModuleLoad(module, PTX_FILE_PATH);

            String kernelName = "dictionaryAttackKernel";
            System.out.println("Getting function: " + kernelName);
            cuModuleGetFunction(function, module, kernelName);
        } catch (jcuda.CudaException e) {
            System.err.println("Fatal Error: Failed to load CUDA module or kernel function.");
            System.err.println("Ensure the PTX file exists at: " + PTX_FILE_PATH);
            System.err.println("Error details: " + e.getMessage());
            cuCtxDestroy(context);
            return null;
        }

        // Allocate GPU memory
        CUdeviceptr dDictionary = new CUdeviceptr();
        cuMemAlloc(dDictionary, (long) wordCount * MAX_PASSWORD_LENGTH);
        cuMemcpyHtoD(dDictionary, Pointer.to(dictBytes), (long) wordCount * MAX_PASSWORD_LENGTH);

        CUdeviceptr dWordLengths = new CUdeviceptr();
        cuMemAlloc(dWordLengths, wordCount * Sizeof.INT);
        cuMemcpyHtoD(dWordLengths, Pointer.to(wordLengths), wordCount * Sizeof.INT);

        CUdeviceptr dTargetHash = new CUdeviceptr();
        cuMemAlloc(dTargetHash, 16);
        cuMemcpyHtoD(dTargetHash, Pointer.to(hexStringToByteArray(validator.targetHash)), 16);

        CUdeviceptr dFoundPassword = new CUdeviceptr();
        cuMemAlloc(dFoundPassword, MAX_PASSWORD_LENGTH + 1);

        CUdeviceptr dFoundFlag = new CUdeviceptr();
        cuMemAlloc(dFoundFlag, Sizeof.INT);
        cuMemcpyHtoD(dFoundFlag, Pointer.to(new int[]{0}), Sizeof.INT);

        // Launch kernel
        int blockSize = 256;
        int gridSize = (wordCount + blockSize - 1) / blockSize;

        Pointer kernelParameters = Pointer.to(
                Pointer.to(dDictionary),
                Pointer.to(dWordLengths),
                Pointer.to(new int[]{wordCount}),
                Pointer.to(new int[]{MAX_PASSWORD_LENGTH}),
                Pointer.to(dTargetHash),
                Pointer.to(dFoundPassword),
                Pointer.to(dFoundFlag)
        );

        cuLaunchKernel(function,
                gridSize, 1, 1,
                blockSize, 1, 1,
                0, null,
                kernelParameters, null
        );

        cuCtxSynchronize();

        // Check if password was found
        int[] foundFlagHost = {0};
        cuMemcpyDtoH(Pointer.to(foundFlagHost), dFoundFlag, Sizeof.INT);

        String foundPassword = null;
        if (foundFlagHost[0] == 1) {
            byte[] foundPwdBytes = new byte[MAX_PASSWORD_LENGTH];
            cuMemcpyDtoH(Pointer.to(foundPwdBytes), dFoundPassword, MAX_PASSWORD_LENGTH);
            foundPassword = new String(foundPwdBytes, StandardCharsets.UTF_8).trim();
            System.out.println("\nSUCCESS! Password found in dictionary: " + foundPassword);
        }

        // Clean up GPU memory
        cuMemFree(dDictionary);
        cuMemFree(dWordLengths);
        cuMemFree(dTargetHash);
        cuMemFree(dFoundPassword);
        cuMemFree(dFoundFlag);
        cuCtxDestroy(context);

        return foundPassword;
    }

    private List<String> loadDictionary(String filePath) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
        return words;
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
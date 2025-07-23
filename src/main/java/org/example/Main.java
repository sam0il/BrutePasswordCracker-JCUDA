package org.example;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final String dictionaryPath = "C:\\Users\\samoi\\IdeaProjects\\BrutePasswordCrackerParallel\\src\\main\\resources\\Passwords";
    public static String crackedPassword;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the target MD5 hash: ");
        String targetHash = scanner.nextLine().trim();

        if (targetHash.length() != 32 || !targetHash.matches("[a-fA-F0-9]+")) {
            System.out.println("Invalid MD5 hash. Must be 32 hex characters.");
            return;
        }

        System.out.print("Choose attack mode: 1 for Brute Force, 2 for Dictionary Attack: ");
        int mode = scanner.nextInt();
        scanner.nextLine();

        long startTime = System.currentTimeMillis();

        if (mode == 1) {
            // Brute force settings
            System.out.print("Enter maximum password length: ");
            int maxLength = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter character set or press Enter for default: ");
            String charset = scanner.nextLine().trim();
            if (charset.isEmpty()) {
                charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
                System.out.println("Using default charset: " + charset);
            }

            System.out.print("Enter mask indices (comma-separated): ");
            String maskIndicesInput = scanner.nextLine().trim();

            System.out.print("Enter mask (optional): ");
            String mask = scanner.nextLine().trim();

            // Parse mask indices
            Set<Integer> indices = new HashSet<>();
            if (!maskIndicesInput.isEmpty()) {
                for (String part : maskIndicesInput.split(",")) {
                    try {
                        int index = Integer.parseInt(part.trim());
                        if (index < 0) {
                            System.out.println("Error: Mask index must be non-negative");
                            return;
                        }
                        indices.add(index);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid mask index: " + part);
                        return;
                    }
                }
            }

            MaskConfig maskConfig = new MaskConfig(mask, indices);
            HashValidator validator = new HashValidator(targetHash);

            System.out.println("Starting brute-force attack on GPU...");

            JCudaBruteForceEngine engine = new JCudaBruteForceEngine(validator, charset, maxLength, maskConfig);
            crackedPassword = engine.crackPassword();

//        } else if (mode == 2) {
//            System.out.println("Starting dictionary attack...");
//            HashValidator validator = new HashValidator(targetHash);
//            ParallelDictionaryEngine dictEngine = new ParallelDictionaryEngine(validator, dictionaryPath);
//            crackedPassword = dictEngine.crackPassword();
        } else {
            System.out.println("Invalid mode selected.");
            return;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

        if (crackedPassword != null) {
            System.out.println("Password cracked: " + crackedPassword);
        } else {
            System.out.println("Failed to crack the password.");
        }
    }
}

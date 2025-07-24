package org.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashValidator {

    public final String targetHash;
    private final String algorithm;

    public HashValidator(String targetHash) {
        this.targetHash = targetHash.toLowerCase();
        this.algorithm = "MD5";
    }

    public boolean matches(String candidate) {
        return hash(candidate).equals(targetHash);
   }

    public String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Invalid algorithm: " + algorithm, e);
        }
    }
}

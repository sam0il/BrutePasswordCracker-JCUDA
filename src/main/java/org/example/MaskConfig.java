package org.example;

import java.util.*;

public class MaskConfig {
    private final String mask;
    private final Set<Integer> maskIndices;

    public MaskConfig(String mask, Set<Integer> maskIndices) {
        this.mask = mask;
        this.maskIndices = maskIndices;
    }

    public boolean isMasked() {
        return mask != null && !mask.isEmpty() && maskIndices != null && !maskIndices.isEmpty();
    }

    public Set<Integer> getValidMaskIndices(int targetLength) {
        Set<Integer> validIndices = new HashSet<>();
        if (!isMasked()) return validIndices;

        for (int index : maskIndices) {
            if (index < targetLength) {
                validIndices.add(index);
            }
        }
        return validIndices;
    }

    public Map<Integer, Character> getMaskMap(Set<Integer> currentMaskIndices) {
        Map<Integer, Character> maskMap = new HashMap<>();
        if (mask == null || mask.isEmpty()) return maskMap;

        // Create sorted list of indices
        List<Integer> sortedIndices = new ArrayList<>(currentMaskIndices);
        Collections.sort(sortedIndices);

        // Map mask characters in order of sorted indices
        int maskIndex = 0;
        for (int index : sortedIndices) {
            if (maskIndex < mask.length()) {
                maskMap.put(index, mask.charAt(maskIndex++));
            }
        }
        return maskMap;
    }

    public String getMask() {
        return mask;
    }

    public int getLength() {
        return mask != null ? mask.length() : 0;
    }
}
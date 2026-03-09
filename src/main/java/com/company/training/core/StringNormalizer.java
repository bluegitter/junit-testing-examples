package com.company.training.core;

public class StringNormalizer {

    /**
     * 將名稱轉換成正確的格式
     *
     * @param input 名稱
     * @return 正確格式的名稱
     */
    public String normalizeName(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "UNKNOWN";
        }
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
}

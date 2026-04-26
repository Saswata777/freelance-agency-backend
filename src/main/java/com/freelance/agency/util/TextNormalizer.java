package com.freelance.agency.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TextNormalizer {

    // Common words to ignore during analysis
    private static final List<String> STOP_WORDS = Arrays.asList(
            "i", "want", "a", "an", "the", "to", "for", "my", "me",
            "we", "our", "need", "build", "create", "make", "develop",
            "is", "are", "was", "have", "has", "can", "could", "would",
            "like", "looking", "help", "please", "hi", "hello", "hey"
    );

    /**
     * Normalize input text:
     * 1. Lowercase
     * 2. Remove punctuation
     * 3. Trim whitespace
     * 4. Remove stop words
     * 5. Return meaningful keywords
     */
    public List<String> normalize(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }

        String cleaned = input
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "") // remove punctuation
                .trim();

        String[] words = cleaned.split("\\s+");

        return Arrays.stream(words)
                .filter(word -> !word.isBlank())
                .filter(word -> !STOP_WORDS.contains(word))
                .filter(word -> word.length() > 1)
                .toList();
    }

    /**
     * Raw lowercase + trim only (no stop word removal)
     * Used for simple comparisons
     */
    public String normalizeRaw(String input) {
        if (input == null || input.isBlank()) return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .trim();
    }
}
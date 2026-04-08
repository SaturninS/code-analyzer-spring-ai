package com.devassist.codeanalyzer.domain.model;

public record CodeSnippet(
        String code,
        String language  // "java", "python", "typescript", "unknown"
) {
    public CodeSnippet {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Le code ne peut pas être vide");
        }
        if (language == null) {
            language = "unknown";
        }
    }
}
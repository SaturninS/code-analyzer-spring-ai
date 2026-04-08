package com.devassist.codeanalyzer.web.dto;

public record AnalyzeRequest(
        String code,
        String language
) {
    public AnalyzeRequest {
        if (language == null || language.isBlank()) {
            language = "unknown";
        }
    }
}
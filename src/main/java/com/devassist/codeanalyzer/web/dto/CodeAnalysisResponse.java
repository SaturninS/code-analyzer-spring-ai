package com.devassist.codeanalyzer.web.dto;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;

import java.util.List;

public record CodeAnalysisResponse(
        String language,
        List<String> bugs,
        List<String> securityIssues,
        List<String> suggestions,
        String complexityLevel,
        int overallScore,
        String summary
) {
    public static CodeAnalysisResponse from(CodeAnalysis analysis) {
        return new CodeAnalysisResponse(
                analysis.language(),
                analysis.bugs(),
                analysis.securityIssues(),
                analysis.suggestions(),
                analysis.complexityLevel(),
                analysis.overallScore(),
                analysis.summary()
        );
    }
}

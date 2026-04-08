package com.devassist.codeanalyzer.domain.model;

import java.util.List;

public record CodeAnalysis(
        String language,
        List<String> bugs,
        List<String> securityIssues,
        List<String> suggestions,
        String complexityLevel,
        int overallScore,
        String summary
) {}
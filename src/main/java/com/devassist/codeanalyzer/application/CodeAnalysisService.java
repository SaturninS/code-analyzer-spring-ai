package com.devassist.codeanalyzer.application;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.domain.port.CodeAnalysisPort;
import org.springframework.stereotype.Service;

@Service
public class CodeAnalysisService {

    private final CodeAnalysisPort codeAnalysisPort;

    public CodeAnalysisService(CodeAnalysisPort codeAnalysisPort) {
        this.codeAnalysisPort = codeAnalysisPort;
    }

    public CodeAnalysis analyze(CodeSnippet snippet) {
        return codeAnalysisPort.analyze(snippet);
    }

    public CodeAnalysis review(CodeSnippet snippet) {
        return codeAnalysisPort.review(snippet);
    }

    public String summarize(CodeSnippet snippet) {
        return codeAnalysisPort.summarize(snippet);
    }
}
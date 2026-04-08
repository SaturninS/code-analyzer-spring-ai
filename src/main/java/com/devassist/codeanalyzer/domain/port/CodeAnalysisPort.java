package com.devassist.codeanalyzer.domain.port;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;

public interface CodeAnalysisPort {
    CodeAnalysis analyze(CodeSnippet snippet);
    CodeAnalysis review(CodeSnippet snippet);
    String summarize(CodeSnippet snippet);
}
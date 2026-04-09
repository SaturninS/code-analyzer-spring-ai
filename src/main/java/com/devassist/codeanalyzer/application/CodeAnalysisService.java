package com.devassist.codeanalyzer.application;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.domain.port.CodeAnalysisPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CodeAnalysisService {

    private final CodeAnalysisPort codeAnalysisPort;
    private final Timer analyzeTimer;
    private final Timer reviewTimer;
    private final Timer summarizeTimer;

    public CodeAnalysisService(CodeAnalysisPort codeAnalysisPort, MeterRegistry meterRegistry) {
        this.codeAnalysisPort = codeAnalysisPort;
        this.analyzeTimer  = timer(meterRegistry, "analyze");
        this.reviewTimer   = timer(meterRegistry, "review");
        this.summarizeTimer = timer(meterRegistry, "summarize");
    }

    @Cacheable("analyses")
    public CodeAnalysis analyze(CodeSnippet snippet) {
        return analyzeTimer.record(() -> codeAnalysisPort.analyze(snippet));
    }

    @Cacheable("reviews")
    public CodeAnalysis review(CodeSnippet snippet) {
        return reviewTimer.record(() -> codeAnalysisPort.review(snippet));
    }

    @Cacheable("summaries")
    public String summarize(CodeSnippet snippet) {
        return summarizeTimer.record(() -> codeAnalysisPort.summarize(snippet));
    }

    public Flux<String> streamAnalyze(CodeSnippet snippet) {
        return codeAnalysisPort.streamAnalyze(snippet);
    }

    private static Timer timer(MeterRegistry registry, String operation) {
        return Timer.builder("code.analysis.duration")
                .tag("operation", operation)
                .description("Durée des appels LLM par opération")
                .register(registry);
    }
}

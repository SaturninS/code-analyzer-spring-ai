package com.devassist.codeanalyzer.infrastructure.llm;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.domain.port.CodeAnalysisPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
public class AnthropicCodeAnalysisAdapter implements CodeAnalysisPort {

    private final ChatClient chatClient;

    @Value("classpath:prompts/analyze.st")
    private Resource analyzePrompt;

    @Value("classpath:prompts/review.st")
    private Resource reviewPrompt;

    @Value("classpath:prompts/summarize.st")
    private Resource summarizePrompt;

    public AnthropicCodeAnalysisAdapter(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        Tu es un expert en code review avec 15 ans d'expérience.
                        Tu analyses du code de façon précise et actionnable.
                        Tu réponds TOUJOURS en JSON valide, sans markdown, sans explication.
                        """)
                .build();
    }

    @Override
    public CodeAnalysis analyze(CodeSnippet snippet) {
        String prompt = new PromptTemplate(analyzePrompt)
                .render(Map.of("language", snippet.language(), "code", snippet.code()));

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CodeAnalysis.class);
    }

    @Override
    public CodeAnalysis review(CodeSnippet snippet) {
        String prompt = new PromptTemplate(reviewPrompt)
                .render(Map.of("language", snippet.language(), "code", snippet.code()));

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CodeAnalysis.class);
    }

    @Override
    public String summarize(CodeSnippet snippet) {
        String prompt = new PromptTemplate(summarizePrompt)
                .render(Map.of("language", snippet.language(), "code", snippet.code()));

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public Flux<String> streamAnalyze(CodeSnippet snippet) {
        String prompt = new PromptTemplate(analyzePrompt)
                .render(Map.of("language", snippet.language(), "code", snippet.code()));

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}

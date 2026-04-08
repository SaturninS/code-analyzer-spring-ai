package com.devassist.codeanalyzer.infrastructure.llm;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.domain.port.CodeAnalysisPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class AnthropicCodeAnalysisAdapter implements CodeAnalysisPort {

    private final ChatClient chatClient;

    // Spring AI : on configure le client via le Builder injecté par Spring
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
        String prompt = """
            Analyse ce code %s et retourne un JSON avec cette structure exacte :
            {
              "language": "string",
              "bugs": ["string"],
              "securityIssues": ["string"],
              "suggestions": ["string"],
              "complexityLevel": "low|medium|high",
              "overallScore": 0-10,
              "summary": "string"
            }
            
            Code à analyser : %s
            """.formatted(snippet.language(), snippet.code());

        // Spring AI parse automatiquement le JSON vers CodeAnalysis
        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CodeAnalysis.class);
    }

    @Override
    public CodeAnalysis review(CodeSnippet snippet) {
        String prompt = """
        Effectue une code review approfondie de ce code %s.
        Concentre-toi sur : bugs potentiels, sécurité, performance, lisibilité.
        Retourne un JSON avec cette structure :
        {
          "language": "%s",
          "bugs": ["description précise du bug"],
          "securityIssues": ["problème de sécurité détaillé"],
          "suggestions": ["suggestion concrète et actionnable"],
          "complexityLevel": "low|medium|high",
          "overallScore": 0-10,
          "summary": "résumé en une phrase de la qualité du code"
        }
        
        Code : %s
        """.formatted(snippet.language(), snippet.language(), snippet.code());

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(CodeAnalysis.class);
    }

    @Override
    public String summarize(CodeSnippet snippet) {
        String prompt = """
        Résume en UNE SEULE phrase claire ce que fait ce code %s.
        Commence par un verbe d'action. Maximum 20 mots.
        Retourne uniquement la phrase, sans ponctuation finale, sans guillemets.
        
        Code : %s
        """.formatted(snippet.language(), snippet.code());

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
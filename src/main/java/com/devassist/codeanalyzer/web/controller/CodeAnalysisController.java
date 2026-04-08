package com.devassist.codeanalyzer.web.controller;

import com.devassist.codeanalyzer.application.CodeAnalysisService;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.web.dto.AnalyzeRequest;
import com.devassist.codeanalyzer.web.dto.CodeAnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CodeAnalysisController {

    private final CodeAnalysisService service;

    public CodeAnalysisController(CodeAnalysisService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    /**
     * Analyse générale : détecte la stack, la complexité, les problèmes.
     * Usage : soumettre n'importe quel snippet pour un premier regard.
     */
    @PostMapping("/analyze")
    public ResponseEntity<CodeAnalysisResponse> analyze(@RequestBody AnalyzeRequest request) {
        var snippet = new CodeSnippet(request.code(), request.language());
        return ResponseEntity.ok(CodeAnalysisResponse.from(service.analyze(snippet)));
    }

    /**
     * Code review ciblée : bugs, sécurité, suggestions concrètes.
     * Usage : soumettre du code que vous vous apprêtez à merger.
     */
    @PostMapping("/review")
    public ResponseEntity<CodeAnalysisResponse> review(@RequestBody AnalyzeRequest request) {
        var snippet = new CodeSnippet(request.code(), request.language());
        return ResponseEntity.ok(CodeAnalysisResponse.from(service.review(snippet)));
    }

    /**
     * Résumé en une phrase : idéal pour la génération de doc ou de commit message.
     * Usage : soumettre une méthode pour obtenir sa description en une ligne.
     */
    @PostMapping("/summarize")
    public ResponseEntity<String> summarize(@RequestBody AnalyzeRequest request) {
        var snippet = new CodeSnippet(request.code(), request.language());
        var summary = service.summarize(snippet);
        return ResponseEntity.ok(summary);
    }
}
package com.devassist.codeanalyzer.application;

import com.devassist.codeanalyzer.domain.model.CodeAnalysis;
import com.devassist.codeanalyzer.domain.model.CodeSnippet;
import com.devassist.codeanalyzer.domain.port.CodeAnalysisPort;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CodeAnalysisServiceTest {
    @Mock
    private CodeAnalysisPort codeAnalysisPort;

    private CodeAnalysisService codeAnalysisService;

    @BeforeEach
    void setUp() {
        codeAnalysisService = new CodeAnalysisService(codeAnalysisPort, new SimpleMeterRegistry());
    }

    @Test
    void analyseShouldDelegateToPort(){
        //Given
       var snippet = new CodeSnippet("def foo(): pass", "python");
       var expectedAnalysis = new CodeAnalysis(
               "python",
               List.of(),
               List.of(),
               List.of("Ajouter une docstring"),
               "low",
               7,
               "Fonction vide sans logique"
       );
       when(codeAnalysisPort.analyze(snippet)).thenReturn(expectedAnalysis);

       //when
        var result = codeAnalysisService.analyze(snippet);
        //Then
        assertThat(result).isEqualTo(expectedAnalysis);
        assertThat(result.overallScore()).isEqualTo(7);
        assertThat(result.language()).isEqualTo("python");
    }

    @Test
    void summariz_shouldReturnSummaryFromPort(){
        var snippet = new CodeSnippet("return a + b", "python");
        when(codeAnalysisPort.summarize(snippet)).thenReturn("Additionne deux valeurs");
        var result = codeAnalysisService.summarize(snippet);
        assertThat(result).isEqualTo("Additionne deux valeurs");
    }

}

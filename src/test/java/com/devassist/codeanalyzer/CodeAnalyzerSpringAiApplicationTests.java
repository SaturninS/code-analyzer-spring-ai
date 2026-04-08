package com.devassist.codeanalyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.ai.anthropic.api-key=test-key")
class CodeAnalyzerSpringAiApplicationTests {

	@Test
	void contextLoads() {
	}

}

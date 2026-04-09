package com.devassist.codeanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CodeAnalyzerSpringAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeAnalyzerSpringAiApplication.class, args);
	}

}

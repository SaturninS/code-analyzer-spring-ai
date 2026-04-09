# Code Analyzer — Spring AI

API REST d'analyse de code propulsée par l'IA, utilisant **Spring AI** et le modèle **Anthropic Claude**. Construite avec une architecture hexagonale sur Spring Boot 3 / Java 21.

## Fonctionnalités

- **Analyser** — détecte le langage, la complexité, les bugs et les failles de sécurité d'un snippet
- **Revue de code** — effectue une code review pré-merge avec des suggestions concrètes et actionnables
- **Résumer** — génère une description en une phrase de ce que fait un morceau de code

## Stack technique

| Couche | Technologie |
|---|---|
| Runtime | Java 21 (threads virtuels via Project Loom) |
| Framework | Spring Boot 3.4.4 |
| IA | Spring AI 1.0.0 + Anthropic Claude Haiku |
| Architecture | Hexagonale (ports & adaptateurs) |
| Conteneurisation | Docker + Docker Compose |
| CI | GitHub Actions |

## Prérequis

- Java 21+
- Maven 3.9+ (ou utiliser le wrapper `./mvnw` inclus)
- Une [clé API Anthropic](https://console.anthropic.com)

## Démarrage rapide

**1. Configurer la clé API**

```bash
cp .env.example .env
# Éditer .env et renseigner ANTHROPIC_API_KEY
```

**2. Lancer en local**

```bash
source .env && ./mvnw spring-boot:run
```

**3. Lancer avec Docker**

```bash
./mvnw package -DskipTests
source .env && docker compose up
```

## Référence API

URL de base : `http://localhost:8080/api/v1`

### Vérification de santé

```
GET /health
```

### Analyser du code

```
POST /analyze
Content-Type: application/json

{
  "code": "public int add(int a, int b) { return a + b; }",
  "language": "java"
}
```

### Réviser du code

```
POST /review
Content-Type: application/json

{
  "code": "...",
  "language": "python"
}
```

### Résumer du code

```
POST /summarize
Content-Type: application/json

{
  "code": "...",
  "language": "typescript"
}
```

Le champ `language` est optionnel — vaut `"unknown"` par défaut, Claude détecte automatiquement le langage.

### Schéma de réponse (`/analyze` et `/review`)

```json
{
  "language": "java",
  "bugs": ["Description précise du bug"],
  "securityIssues": ["Problème de sécurité détaillé"],
  "suggestions": ["Suggestion concrète et actionnable"],
  "complexityLevel": "low | medium | high",
  "overallScore": 8,
  "summary": "Description en une phrase du code"
}
```

## Architecture

```
src/main/java/com/devassist/codeanalyzer/
├── domain/
│   ├── model/          # CodeSnippet, CodeAnalysis (records domaine purs)
│   └── port/           # CodeAnalysisPort (interface)
├── application/
│   └── CodeAnalysisService.java   # Couche d'orchestration
├── infrastructure/
│   └── llm/
│       └── AnthropicCodeAnalysisAdapter.java  # Adaptateur Spring AI / Claude
└── web/
    ├── controller/     # Endpoints REST
    └── dto/            # AnalyzeRequest, CodeAnalysisResponse
```

Le domaine n'a aucune dépendance sur Spring ou le fournisseur LLM. `AnthropicCodeAnalysisAdapter` est le seul composant qui connaît Spring AI.

## Développement

```bash
# Lancer les tests
./mvnw test

# Lancer un test unitaire spécifique
./mvnw test -Dtest=CodeAnalyzerSpringAiApplicationTests

# Construire le JAR
./mvnw package -DskipTests
```

## Sécurité

Les vulnérabilités connues sont suivies et corrigées via des overrides de dépendances dans `pom.xml`. Jackson Core est épinglé en `2.18.6` pour corriger GHSA-72hv-8253-57qq et WS-2026-0003.

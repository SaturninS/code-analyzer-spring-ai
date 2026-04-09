# Fiche technique — Code Analyzer Spring AI

## Pourquoi ce projet ?

Exposer les capacités d'analyse de code d'un LLM via une API REST propre, maintenable et extensible. L'idée : envoyer un snippet, recevoir une analyse structurée (bugs, sécurité, complexité, score).

---

## Architecture hexagonale

Le projet suit le pattern **Ports & Adaptateurs** (Hexagonal Architecture d'Alistair Cockburn).

```
        [ HTTP / REST ]
              │
        [ Controller ]       ← Adaptateur primaire (pilote l'application)
              │
        [ Service ]          ← Couche application (orchestration)
              │
        [ Port (interface) ] ← Frontière du domaine
              │
        [ Adapter LLM ]      ← Adaptateur secondaire (piloté par l'application)
              │
        [ Anthropic API ]
```

**Le domaine (`domain/`) ne connaît ni Spring, ni Claude, ni HTTP.** Il ne contient que des records Java purs (`CodeSnippet`, `CodeAnalysis`) et une interface (`CodeAnalysisPort`).

Conséquence concrète : remplacer Claude par OpenAI ou un modèle local revient à écrire un nouvel `Adapter` qui implémente `CodeAnalysisPort`. Rien d'autre ne change.

---

## Choix techniques

### Spring AI 1.0.0 GA + Anthropic

Spring AI fournit une abstraction sur les LLMs. On utilise `ChatClient` (API fluente) plutôt que `ChatModel` directement, ce qui permet d'enchaîner system prompt, user message, et parsing de sortie en une seule chaîne.

Le parsing JSON vers `CodeAnalysis` est délégué à `.entity(CodeAnalysis.class)` — Spring AI utilise un `BeanOutputConverter` qui injecte automatiquement les contraintes de format dans le prompt.

### Prompt templates (fichiers `.st`)

Les prompts sont des fichiers `src/main/resources/prompts/*.st` chargés via `PromptTemplate` de Spring AI. Les variables (`{language}`, `{code}`) sont substituées au moment de l'appel.

**Avantage :** modifier un prompt ne nécessite pas de recompiler. Dans un pipeline CI/CD, on peut versionner et auditer les prompts comme du code.

### Streaming SSE

L'endpoint `POST /api/v1/stream/analyze` retourne un `Flux<String>` avec `Content-Type: text/event-stream`. Spring AI expose `.stream().content()` sur le `ChatClient`, qui s'appuie sur le streaming natif de l'API Anthropic (tokens envoyés au fur et à mesure).

Spring MVC supporte les types réactifs (`Flux`) sans passer à WebFlux, grâce à `reactor-core` déjà présent en transitive dependency.

### Cache Caffeine

`@Cacheable` sur les méthodes `analyze`, `review` et `summarize`. Le cache key est le `CodeSnippet` lui-même — étant un record Java, son `equals`/`hashCode` est basé sur (`code`, `language`).

Paramètres : 200 entrées max, TTL 60 minutes. Justification : la `temperature` est à `0.1` (réponses quasi-déterministes), donc mettre en cache un snippet identique est pertinent et économise des tokens Anthropic.

Le streaming n'est pas mis en cache — un flux ne peut pas être rejoué.

### Métriques Micrometer + Actuator

Chaque opération (`analyze`, `review`, `summarize`) est instrumentée avec un `Timer` Micrometer :

- **count** : nombre d'appels LLM réels (cache miss uniquement, puisque le timer est dans le service avant le proxy de cache — à noter)
- **totalTime / max** : latence des appels

Accessible via `/actuator/metrics/code.analysis.duration?tag=operation:analyze`.

---

## Sécurité des dépendances

`jackson-core` est épinglé à `2.18.6` via la propriété Spring Boot `jackson-bom.version` pour corriger :

- **GHSA-72hv-8253-57qq** : contournement de `maxNumberLength` dans le parser async JSON → risque de DoS
- **WS-2026-0003** : vulnérabilité connexe sur le même composant (Mend/WhiteSource)

---

## Ce qui n'a pas été fait (et pourquoi)

| Fonctionnalité | Raison de l'absence |
|---|---|
| Authentification | Hors scope du PoC — à ajouter via Spring Security si exposition publique |
| Persistance des analyses | Pas de besoin de traçabilité pour l'instant |
| WebFlux complet | Spring MVC suffit avec le support des types réactifs — migrer vers WebFlux apporterait peu à ce stade |
| Multi-modèles | L'architecture hexagonale le permet, mais un seul adaptateur est suffisant pour valider le concept |

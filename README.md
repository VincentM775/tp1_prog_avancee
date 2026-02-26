# MasterAnnonce - Backend API sécurisé

Application web et API REST de gestion d'annonces développée dans le cadre du TP Dev Avancé (BUT 3).

## Table des matières

1. [Architecture du projet](#architecture-du-projet)
2. [API REST](#api-rest)
3. [Authentification](#authentification)
4. [Technologies utilisées](#technologies-utilisées)
5. [Installation et configuration](#installation-et-configuration)
6. [Problèmes rencontrés et solutions](#problèmes-rencontrés-et-solutions)
7. [Tests](#tests)
8. [Fonctionnalités](#fonctionnalités)

---

## Architecture du projet

Le projet suit une **architecture en couches** (Layered Architecture) avec Spring Boot :

```
┌─────────────────────────────────────────────────────────────┐
│                      COUCHE WEB                             │
│  ┌──────────────────┐  ┌────────────────────────────────┐   │
│  │  @RestController  │  │  Filtres Spring Security       │   │
│  │  (AnnonceCtrl,   │  │  (JwtAuthFilter, CorrelationId)│   │
│  │   AuthCtrl, Meta)│  └────────────────────────────────┘   │
│  └──────────────────┘  ┌────────────────────────────────┐   │
│  ┌──────────────────┐  │  GlobalExceptionHandler         │   │
│  │  DTOs + MapStruct│  │  (@RestControllerAdvice)        │   │
│  └──────────────────┘  └────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE SERVICE                           │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐   │
│  │AnnonceService│  │ UserService  │  │CategoryService  │   │
│  │ (@Service)   │  │ (@Service)   │  │ (@Service)      │   │
│  └──────────────┘  └──────────────┘  └─────────────────┘   │
│  ┌──────────────┐  ┌──────────────────────────────────────┐ │
│  │  JwtService  │  │  @Transactional + @PreAuthorize      │ │
│  │ (security)   │  │  LoggingAspect (@Aspect / AOP)       │ │
│  └──────────────┘  └──────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   COUCHE REPOSITORY                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  Spring Data JPA (JpaRepository + Specifications)    │   │
│  ├──────────────┬──────────────┬─────────────────┬──────┤   │
│  │AnnonceRepo   │  UserRepo   │  CategoryRepo   │Specs │   │
│  └──────────────┴──────────────┴─────────────────┴──────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE ENTITY                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   Annonce   │  │    User     │  │     Category        │ │
│  │ (@Entity)   │  │  (@Entity)  │  │    (@Entity)        │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              INFRASTRUCTURE (Spring Boot Auto-Config)       │
│  ┌───────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│  │ application   │  │  Actuator    │  │  Docker +        │  │
│  │ .yml          │  │ (health,info)│  │  docker-compose  │  │
│  └───────────────┘  └──────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Structure des packages

```
src/main/java/org/univ_paris8/.../demo1/
├── MasterAnnonceApplication.java  # Classe principale Spring Boot
│
├── entity/                  # Entités JPA
│   ├── Annonce.java         # Entité principale avec relations + @Version
│   ├── AnnonceStatus.java   # Enum (DRAFT, PUBLISHED, ARCHIVED)
│   ├── User.java            # Utilisateur (avec rôle ROLE_USER/ROLE_ADMIN)
│   └── Category.java        # Catégorie d'annonces
│
├── repository/              # Spring Data JPA
│   ├── AnnonceRepository.java      # JpaRepository + JpaSpecificationExecutor
│   ├── AnnonceSpecifications.java  # Specifications composables (recherche dynamique)
│   ├── UserRepository.java
│   └── CategoryRepository.java
│
├── service/                 # Logique métier (@Service, @Transactional)
│   ├── AnnonceService.java       # Règles métier annonces + @PreAuthorize
│   ├── UserService.java          # Gestion utilisateurs + BCrypt
│   ├── CategoryService.java
│   ├── BusinessException.java    # Erreur métier (400)
│   ├── EntityNotFoundException.java  # Ressource non trouvée (404)
│   ├── ConflictException.java    # Conflit d'état (409)
│   ├── ForbiddenException.java   # Accès interdit (403)
│   └── ServiceException.java     # Exception de base
│
├── security/                # Sécurité JWT
│   ├── JwtService.java           # Génération/validation tokens JWT (JJWT)
│   └── JwtAuthenticationFilter.java  # OncePerRequestFilter
│
├── config/                  # Configuration Spring
│   ├── SecurityConfig.java       # SecurityFilterChain, BCrypt, stateless
│   └── OpenApiConfig.java        # Swagger/OpenAPI avec schéma Bearer
│
├── aspect/                  # AOP
│   └── LoggingAspect.java        # Logging centralisé des services
│
├── filter/                  # Filtres HTTP
│   └── CorrelationIdFilter.java  # MDC correlation ID
│
└── web/
    ├── controller/               # @RestController
    │   ├── AnnonceController.java    # CRUD + publish/archive
    │   ├── AuthController.java       # Login JWT
    │   └── MetaController.java       # Introspection entités
    ├── dto/                      # DTOs
    │   ├── AnnonceDTO.java, CreateAnnonceDTO.java, UpdateAnnonceDTO.java
    │   ├── LoginRequest.java, LoginResponse.java
    │   └── ApiError.java             # Format d'erreur normalisé
    ├── mapper/                   # MapStruct
    │   ├── AnnonceMapper.java, UserMapper.java, CategoryMapper.java
    └── exception/
        └── GlobalExceptionHandler.java  # @RestControllerAdvice
```

---

## API REST

Point d'entrée : `@ApplicationPath("/api")` via `RestApplication.java`

### Endpoints publics (sans authentification)

| Méthode | URL                       | Description                       |
|---------|---------------------------|-----------------------------------|
| GET     | `/api/helloWorld`         | Endpoint de test                  |
| GET     | `/api/params`             | Test QueryParams                  |
| GET     | `/api/params/{nom}/{age}` | Test PathParams                   |
| GET     | `/api/annonces`           | Lister les annonces publiées (paginé) |
| GET     | `/api/annonces/{id}`      | Détail d'une annonce              |
| POST    | `/api/auth/login`         | Authentification (retourne token) |

### Endpoints protégés (token Bearer requis)

| Méthode | URL                           | Description                          |
|---------|-------------------------------|--------------------------------------|
| POST    | `/api/annonces`               | Créer une annonce (statut DRAFT)     |
| PUT     | `/api/annonces/{id}`          | Modifier (DRAFT uniquement, auteur)  |
| DELETE  | `/api/annonces/{id}`          | Supprimer (ARCHIVED uniquement)      |
| POST    | `/api/annonces/{id}/publier`  | Publier une annonce                  |
| POST    | `/api/annonces/{id}/archiver` | Archiver une annonce                 |
| POST    | `/api/auth/logout`            | Révoquer le token                    |

### Gestion des erreurs (format normalisé ApiError)

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Description de l'erreur",
  "timestamp": "2025-01-01T12:00:00",
  "fieldErrors": [{"field": "title", "message": "ne doit pas être vide"}]
}
```

Codes HTTP : 200, 201, 204, 400, 401, 403, 404, 409, 500

---

## Authentification

Authentification stateless par token Bearer (UUID) :

1. **Login** : `POST /api/auth/login` avec `{"username": "...", "password": "..."}` retourne un token
2. **Utilisation** : Header `Authorization: Bearer <token>` sur les endpoints protégés
3. **Logout** : `POST /api/auth/logout` pour révoquer le token

### Règles métier sécurisées

- **Cycle de vie** : DRAFT -> PUBLISHED -> ARCHIVED
- Seul l'**auteur** peut modifier, publier, archiver ou supprimer ses annonces
- Une annonce **PUBLISHED** ne peut plus être modifiée
- Une annonce doit être **ARCHIVED** avant suppression
- Contrôle de concurrence optimiste via `@Version`

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 17+ | Langage |
| Spring Boot | 3.4.3 | Framework principal |
| Spring Web (MVC) | 6.x | API REST (@RestController) |
| Spring Data JPA | 3.x | Accès données (JpaRepository, Specifications) |
| Spring Security | 6.x | Authentification JWT, autorisation par rôles |
| Spring AOP | 6.x | Logging centralisé (@Aspect) |
| Spring Actuator | 3.x | Health checks, monitoring |
| MapStruct | 1.5.5 | Mapping Entity/DTO (annotation processor) |
| JJWT | 0.11.5 | Génération/validation tokens JWT |
| PostgreSQL | 16+ | Base de données production |
| H2 | 2.x | Base de données tests (fallback) |
| Testcontainers | 1.x | PostgreSQL en conteneur pour tests d'intégration |
| springdoc-openapi | 2.8.6 | Documentation Swagger UI |
| JUnit 5 + Mockito | 5.10+ | Tests unitaires |
| Spring MockMvc | 6.x | Tests d'intégration REST |
| JaCoCo | 0.8.12 | Couverture de code (89.8%) |
| Maven (Surefire + Failsafe) | 3.x | Build, tests unitaires / intégration |
| Docker | multi-stage | Conteneurisation (eclipse-temurin:17) |
| GitHub Actions | CI | Pipeline CI (matrice Java 17+21) |
| SonarQube | 10.x | Analyse qualité (0 bugs, 0 code smells) |

---

## Installation et configuration

### Prérequis

1. JDK 18+
2. Maven 3.9+ (ou utiliser le wrapper `./mvnw`)
3. PostgreSQL 15+
4. Tomcat 10+ (ou serveur compatible Jakarta EE 10)

### Configuration de la base de données

1. Créer la base de données :
```sql
CREATE DATABASE masterannonce;
CREATE USER masteruser WITH PASSWORD 'masterpass';
GRANT ALL PRIVILEGES ON DATABASE masterannonce TO masteruser;
```

2. Configurer `src/main/resources/META-INF/persistence.xml` si nécessaire.

### Build et déploiement

```bash
# Compilation
./mvnw clean compile

# Tests unitaires uniquement (*Test.java via Surefire)
./mvnw test

# Tests d'intégration (*IT.java via Failsafe)
./mvnw verify

# Package WAR
./mvnw clean package

# Le WAR est généré dans target/demo1-1.0-SNAPSHOT.war
```

---

## Problèmes rencontrés et solutions

### Problèmes des TPs précédents (TP1-TP3 : JAX-RS / JPA manuel)

<details>
<summary>Cliquer pour afficher les 9 problèmes des TPs 1-3</summary>

#### 1. LazyInitializationException sur les méthodes d'écriture
**Problème :** Les endpoints PUT, POST `/publier` et POST `/archiver` renvoyaient une erreur 500. Le `AnnonceDTO.fromEntity()` accédait à `annonce.getAuthor()` et `annonce.getCategory()` alors que l'EntityManager était fermé. Les méthodes d'écriture utilisaient `entityManager.find()` au lieu d'un `JOIN FETCH`.

**Solution :** Remplacer `find()` par une requête JPQL avec `JOIN FETCH` dans toutes les méthodes retournant une entité au contrôleur.

#### 2. Problème N+1 Select sur les listes paginées
**Problème :** Hibernate générait 1 + N + N requêtes SQL pour lister les annonces (1 pour les annonces, N pour les catégories, N pour les auteurs).

**Solution :** `LEFT JOIN FETCH a.category LEFT JOIN FETCH a.author` dans toutes les requêtes de listing.

#### 3. Conflits de clés primaires avec H2 en mode test
**Problème :** `test-data.sql` insérait des IDs explicites (1-7) mais `@GeneratedValue(IDENTITY)` avec H2 démarrait aussi à 1.

**Solution :** `ALTER TABLE ... ALTER COLUMN id RESTART WITH 100;` en fin de script.

#### 4. Pagination non déterministe
**Problème :** Doublons entre pages car toutes les annonces avaient la même date.

**Solution :** Tri secondaire par ID : `ORDER BY a.date DESC, a.id DESC`.

#### 5. Injection de mocks impossible (champ final inline)
**Problème :** Mockito ne pouvait pas remplacer `private final AnnonceService annonceService = new AnnonceService()`.

**Solution :** Constructeur package-private acceptant le service en paramètre.

#### 6. Detached Entity passée à persist
**Problème :** Entités chargées dans une transaction, utilisées dans une autre.

**Solution :** Tout faire dans la même transaction.

#### 7. Transaction non active en mode RESOURCE_LOCAL
**Problème :** `persist()` sans `tx.begin()`/`tx.commit()` = données jamais écrites (bug silencieux).

**Solution :** Centralisation dans `AbstractService.executeInTransaction()`.

#### 8. Fuite de connexions (EntityManager non fermé)
**Problème :** Certains chemins d'exception ne fermaient pas l'EM, épuisant le pool de connexions.

**Solution :** `try-finally` systématique avec `em.close()`.

#### 9. Incompatibilité de version Java
**Problème :** pom.xml ciblait Java 22, JDK installé en Java 18.

**Solution :** Aligner `maven.compiler.source/target` avec le JDK réel.

</details>

---

### Problèmes du TP4 (Migration Spring Boot)

#### 1. SecurityConfig : 403 au lieu de 401 pour les requêtes non authentifiées

**Problème :** Lors des tests d'intégration REST (exercice 8), les endpoints protégés renvoyaient `403 Forbidden` au lieu de `401 Unauthorized` quand aucun token n'était fourni. Par défaut, Spring Security utilise un `LoginUrlAuthenticationEntryPoint` qui tente de rediriger vers une page de login. En API REST stateless, ce comportement n'a pas de sens et provoquait un 403 au lieu du 401 attendu.

**Solution :** Configurer explicitement un `HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)` dans le `SecurityFilterChain` :
```java
.exceptionHandling(ex -> ex
    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
)
```
Cela force Spring Security à retourner un code 401 brut pour toute requête non authentifiée, sans redirection.

---

#### 2. AccessDeniedException renvoyait un 500 au lieu de 403

**Problème :** Les méthodes protégées par `@PreAuthorize("hasRole('ADMIN')")` lançaient une `AccessDeniedException` de Spring Security quand un utilisateur avec le rôle USER tentait d'y accéder. Mais cette exception n'était pas interceptée par le `GlobalExceptionHandler`, ce qui causait un `500 Internal Server Error` au lieu du `403 Forbidden` attendu.

**Solution :** Ajouter un handler dédié dans le `GlobalExceptionHandler` :
```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError(403, "Forbidden", ex.getMessage()));
}
```

---

#### 3. MapStruct générait du bytecode incompatible avec Java 17

**Problème :** Le projet utilise MapStruct pour la conversion Entity/DTO. L'annotation processor de MapStruct générait des classes compilées avec la version de bytecode par défaut du compilateur, qui pouvait ne pas correspondre à Java 17. Les tests échouaient avec `UnsupportedClassVersionError` sur les classes générées par MapStruct.

**Solution :** Remplacer `<maven.compiler.source>` / `<maven.compiler.target>` par la propriété `<release>17</release>` dans le pom.xml. La propriété `release` force le compilateur à produire du bytecode compatible avec la version spécifiée, y compris pour les classes générées par les annotation processors :
```xml
<properties>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

---

#### 4. Exécution de Maven sur Windows avec Git Bash

**Problème :** La commande `mvn` n'était pas trouvée dans le PATH, et `mvnw.cmd` lancé via `cmd.exe` ne produisait aucune sortie exploitable. Le wrapper Maven `mvnw` est un script shell qui ne s'exécute pas nativement sous Windows sans configuration spécifique du shell.

**Solution :** Exécuter le wrapper Maven via `bash mvnw` dans Git Bash, avec un export explicite de `JAVA_HOME` au format Unix :
```bash
export JAVA_HOME="/c/Program Files/Java/jdk-18.0.2.1"
bash mvnw clean verify
```
Le format `/c/Program Files/...` (au lieu de `C:\Program Files\...`) est nécessaire pour que bash interprète correctement le chemin.

---

#### 5. Testcontainers ne détectait pas Docker sur Windows

**Problème :** En local sous Windows, Testcontainers ne parvenait pas à détecter Docker Desktop. La JVM forkée par Maven Surefire/Failsafe n'héritait pas toujours des variables d'environnement Docker (`DOCKER_HOST`, pipes nommés Windows). Résultat : les tests d'intégration échouaient car le conteneur PostgreSQL ne pouvait pas démarrer.

**Solution :** Mise en place d'un mécanisme de fallback automatique dans `TestcontainersConfig` :
```java
private static final boolean DOCKER_AVAILABLE = isDockerAvailable();

private static boolean isDockerAvailable() {
    try {
        DockerClientFactory.instance().client();
        return true;
    } catch (Exception e) {
        return false;
    }
}
```
Si Docker n'est pas disponible, les tests utilisent automatiquement H2 en mémoire comme base de données de test. En CI (GitHub Actions), Docker est toujours disponible donc Testcontainers utilise un vrai PostgreSQL.

---

#### 6. JaCoCo ne capturait pas la couverture des tests d'intégration

**Problème :** Le rapport SonarQube affichait seulement **30.3% de couverture** alors que le projet contenait 67 tests (43 unitaires + 24 d'intégration). La cause : JaCoCo ne capturait que la couverture des tests unitaires (Surefire). Les tests d'intégration exécutés par Failsafe (`*IT.java`) n'étaient pas instrumentés, donc les 24 tests qui couvraient les controllers, la sécurité, les mappers et les exception handlers ne comptaient pas.

**Solution :** Configurer JaCoCo avec 3 goals supplémentaires :
1. `prepare-agent-integration` : instrumente la JVM de Failsafe (génère `jacoco-it.exec`)
2. `merge` : fusionne `jacoco.exec` (unitaires) et `jacoco-it.exec` (intégration) en `jacoco-merged.exec`
3. `report` : génère le rapport HTML à partir du fichier fusionné

```xml
<execution>
    <id>merge-results</id>
    <phase>verify</phase>
    <goals><goal>merge</goal></goals>
    <configuration>
        <fileSets>
            <fileSet>
                <directory>${project.build.directory}</directory>
                <includes>
                    <include>jacoco.exec</include>
                    <include>jacoco-it.exec</include>
                </includes>
            </fileSet>
        </fileSets>
        <destFile>${project.build.directory}/jacoco-merged.exec</destFile>
    </configuration>
</execution>
```
Impact : la couverture est passée immédiatement de 30.3% à 80.8%, puis à **89.8%** après l'ajout de tests unitaires supplémentaires.

---

#### 7. SonarQube : 18 code smells à corriger

**Problème :** L'analyse SonarQube initiale a détecté 18 code smells. Les plus notables :
- **S2139** (LoggingAspect) : log d'une exception puis re-throw (`catch` + `log.error(ex)` + `throw ex`). C'est un antipattern car l'exception sera logguée une deuxième fois plus haut dans la stack.
- **S1192** (AnnonceService) : la chaîne `"Annonce"` était dupliquée 5 fois.
- **S1710** (Controllers) : `@ApiResponses({@ApiResponse(...)})` au lieu de l'annotation répétable `@ApiResponse` directement.
- **S4144** (AnnonceController) : les méthodes `update()` et `patch()` avaient un corps identique.
- **S1168** (Mappers) : retourner `null` au lieu d'une collection vide.

**Solutions :**
- **S2139** : Remplacer le bloc `catch/log/rethrow` par un `finally` avec un flag `boolean success` :
```java
boolean success = false;
try {
    Object result = joinPoint.proceed();
    success = true;
    return result;
} finally {
    if (!success) {
        log.warn("[ERROR] {}.{} - exception thrown", className, methodName);
    }
}
```
- **S1192** : Extraction d'une constante `private static final String ENTITY_ANNONCE = "Annonce";`
- **S1710** : Suppression du wrapper `@ApiResponses({...})` et utilisation directe de `@ApiResponse` (annotation répétable depuis Swagger 2.x)
- **S4144** : Extraction d'une méthode privée `applyUpdate()` appelée par `update()` et `patch()`
- **S1168** : `return Collections.emptyMap()` au lieu de `return null`

Résultat final : **0 bugs, 0 vulnerabilities, 0 code smells**.

---

## Tests

Le projet inclut une suite complète de tests couvrant les 4 niveaux demandés :

### Niveau 1 - Tests Repository (Intégration)

**Fichier :** `RepositoryTest.java`

- Tests CRUD avec base de données réelle
- Tests de recherche par mot-clé (`findByKeyword`)
- Tests de filtrage par statut et catégorie
- Tests de pagination
- Vérification des requêtes JPQL personnalisées

```java
@Test
void testAnnoncePagination() {
    List<Annonce> page0 = annonceRepository.findPublished(0, 2);
    List<Annonce> page1 = annonceRepository.findPublished(1, 2);
    assertEquals(2, page0.size());
    assertNotEquals(page0.get(0).getId(), page1.get(0).getId());
}
```

### Niveau 2 - Tests Service (Unitaires avec Mockito)

**Fichier :** `UserServiceUnitTest.java`

- Utilisation de Mockito pour mocker les repositories
- Tests des règles métier isolément
- Vérification des appels et des états
- Tests des comportements d'erreur

```java
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Test
    void testFindByUsernameReturnsUser() {
        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(mockUser));
        // ...
        verify(userRepository, times(1)).findByUsername(anyString());
    }
}
```

### Niveau 3 - Tests d'Intégration Métier

**Fichier :** `IntegrationMetierTest.java`

- Enchaînement complet : création → publication → recherche → archivage
- Tests reproduisant le problème Lazy / N+1
- Vérification de la cohérence des données

```java
@Test
void testScenarioComplet() {
    // ÉTAPE 1 : Création (brouillon)
    Annonce created = annonceService.creer(annonce, userId, categoryId);
    assertEquals(AnnonceStatus.DRAFT, created.getStatus());

    // ÉTAPE 2 : Publication
    Annonce published = annonceService.publier(created.getId());
    assertEquals(AnnonceStatus.PUBLISHED, published.getStatus());

    // ÉTAPE 3 : Vérification dans les recherches
    PagedResult<Annonce> results = annonceService.rechercher(...);
    assertTrue(results.getContent().stream()
        .anyMatch(a -> a.getId().equals(created.getId())));
}
```

**Tests Lazy Loading :**
```java
@Test
void testLazyLoadingWithJoinFetch() {
    Optional<Annonce> found = annonceService.trouverParId(id);

    // Sans JOIN FETCH, ces accès causeraient LazyInitializationException
    assertDoesNotThrow(() -> {
        found.get().getCategory().getLabel();
        found.get().getAuthor().getUsername();
    });
}
```

### Niveau 4 - Tests Web

**Fichier :** `WebLayerTest.java`

- Tests du filtre d'authentification avec mocks HTTP
- Tests du système de validation (FormValidator, ValidationResult, FormData)
- Tests des comportements Servlet (forward, redirect, session)

```java
@Test
void testUnauthenticatedUserRedirected() throws Exception {
    when(request.getSession(false)).thenReturn(null);
    when(request.getContextPath()).thenReturn("/app");

    authFilter.doFilter(request, response, filterChain);

    verify(response).sendRedirect("/app/login");
    verify(filterChain, never()).doFilter(request, response);
}
```

### Niveau 5 - Tests REST Intégration (TP3)

**Fichier :** `AnnonceResourceIT.java`

- Tests REST complets avec Jersey Test Framework + H2 in-memory
- Vérification des codes HTTP ET des payloads JSON
- Tests de sécurité (401 sans token, 401 token invalide)
- Tests des règles métier via API (403 non-auteur, 409 conflit d'état)

```java
@Test
void testCreateWithoutToken() {
    Response response = target("/annonces").request()
            .post(Entity.entity(body, MediaType.APPLICATION_JSON));
    assertEquals(401, response.getStatus());
}
```

### Niveau 6 - Tests unitaires DTO et Resource (TP3)

| Fichier | Description |
|---------|-------------|
| `TokenServiceTest.java` | Génération, validation, révocation de tokens |
| `AnnonceDTOTest.java` | Mapping Entity ↔ DTO, Builder pattern |
| `AnnonceResourceTest.java` | Tests unitaires Resource avec Mockito |

### Niveau 7 - Test de charge (TP3)

**Fichier :** `LoadTestIT.java`

- 50 requêtes GET concurrentes (10 threads)
- Vérification que 90%+ des requêtes réussissent
- Mesure du débit (req/s) et de la durée totale

### Fichiers de test supplémentaires

| Fichier | Description |
|---------|-------------|
| `EntityManagerUtilTest.java` | Configuration JPA |
| `EntityMappingTest.java` | Mappings d'entités |
| `AnnonceServiceTest.java` | Règles métier annonces |
| `AnnonceRepositoryIT.java` | Tests repository H2 (CRUD, pagination) |

### Exécution des tests

```bash
# Tests unitaires uniquement (*Test.java via Surefire)
./mvnw test

# Tests d'intégration (*IT.java via Failsafe)
./mvnw verify

# Un test spécifique
./mvnw test -Dtest=TokenServiceTest

# Avec rapport détaillé
./mvnw test -Dsurefire.reportFormat=plain
```

### Logging

Logging structuré avec SLF4J + Logback :
- Console + fichier rotatif (`logs/masterannonce.log`)
- Format : `timestamp [thread] LEVEL logger - message`
- Rotation quotidienne, rétention 30 jours
- Configuration : `src/main/resources/logback.xml`

### Documentation API

Annotations OpenAPI/Swagger sur tous les endpoints REST (`@Operation`, `@ApiResponse`, `@Tag`).

---

## Fonctionnalités

### Gestion des annonces
- Création d'annonces (brouillon par défaut)
- Modification des annonces
- Publication / Archivage
- Suppression
- Recherche avec filtres (mot-clé, catégorie)
- Pagination des résultats

### Gestion des utilisateurs
- Inscription avec validation
- Connexion / Déconnexion
- Session utilisateur

### Interface utilisateur
- Design moderne avec Tailwind CSS
- Interface responsive (mobile/desktop)
- Messages flash (succès/erreur)
- Validation visuelle des formulaires

---

## Auteurs

Projet réalisé dans le cadre du TP JPA/Hibernate - IUT de Montreuil - Université Paris 8

---

## Licence

Projet académique - Usage éducatif uniquement

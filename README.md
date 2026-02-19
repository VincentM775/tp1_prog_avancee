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

Le projet suit une **architecture en couches** (Layered Architecture) :

```
┌─────────────────────────────────────────────────────────────┐
│                      COUCHE WEB                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Servlets  │  │    JSP      │  │   Filtres           │  │
│  │  (Contrôle) │  │   (Vues)    │  │  (Auth, Encoding)   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │              Validation (FormValidator, FormData)       ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE SERVICE                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │AnnonceService│ │ UserService │  │  CategoryService    │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │         AbstractService (gestion transactions)          ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   COUCHE REPOSITORY                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │AnnonceRepo  │  │  UserRepo   │  │   CategoryRepo      │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │    AbstractRepository (CRUD générique avec JPQL)        ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    COUCHE ENTITY                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Annonce   │  │    User     │  │     Category        │  │
│  │ (@Entity)   │  │  (@Entity)  │  │    (@Entity)        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  COUCHE PERSISTENCE                         │
│  ┌─────────────────────────────────────────────────────────┐│
│  │   EntityManagerUtil (Singleton EntityManagerFactory)    ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │              persistence.xml (PostgreSQL)               ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### Structure des packages

```
src/main/java/org/univ_paris8/.../demo1/
├── entity/                  # Entités JPA
│   ├── Annonce.java         # Entité principale avec relations + @Version
│   ├── AnnonceStatus.java   # Enum (DRAFT, PUBLISHED, ARCHIVED)
│   ├── User.java            # Utilisateur
│   └── Category.java        # Catégorie d'annonces
│
├── persistence/             # Gestion de la persistance
│   ├── EntityManagerUtil.java    # Singleton Factory (configurable pour tests)
│   └── PersistenceListener.java  # Lifecycle listener
│
├── repository/              # Couche accès données
│   ├── GenericRepository.java    # Interface CRUD
│   ├── AbstractRepository.java   # Implémentation générique
│   ├── AnnonceRepository.java    # Requêtes spécifiques annonces
│   ├── UserRepository.java
│   └── CategoryRepository.java
│
├── service/                 # Logique métier
│   ├── AbstractService.java      # Gestion transactions
│   ├── AnnonceService.java       # Règles métier annonces
│   ├── UserService.java
│   ├── CategoryService.java
│   ├── TokenService.java         # Gestion tokens stateless (singleton)
│   ├── PagedResult.java          # Wrapper pagination
│   ├── BusinessException.java    # Erreur métier (400)
│   ├── EntityNotFoundException.java  # Ressource non trouvée (404)
│   ├── ConflictException.java    # Conflit d'état (409)
│   └── ForbiddenException.java   # Accès interdit (403)
│
└── web/
    ├── api/                      # API REST (JAX-RS / Jersey)
    │   ├── RestApplication.java  # @ApplicationPath("/api")
    │   ├── dto/                  # DTOs (AnnonceDTO, CreateAnnonceDTO, LoginDTO, ApiError...)
    │   ├── filter/               # AuthenticationFilter (token Bearer)
    │   ├── mapper/               # ExceptionMappers (400, 401, 403, 404, 409, 500)
    │   └── resource/             # Ressources REST (AnnonceResource, AuthResource)
    ├── filter/
    │   └── AuthFilter.java       # Protection des routes (Servlets)
    ├── servlet/                  # Servlets (couche web classique)
    └── validation/               # Validation formulaires
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
| Java | 18 | Langage |
| Jakarta EE | 10 | API Web (Servlets, JSP, JAX-RS) |
| JAX-RS / Jersey | 3.1.5 | Framework REST |
| JPA / Hibernate | 6.4 | ORM / Persistance |
| PostgreSQL | 15+ | Base de données production |
| H2 | 2.2.224 | Base de données tests (in-memory) |
| SLF4J + Logback | 2.0 / 1.5 | Logging structuré |
| Bean Validation | 3.0 | Validation des entrées |
| Swagger / OpenAPI | 2.2.20 | Documentation API |
| JUnit 5 + Mockito | 5.10+ | Tests unitaires |
| Jersey Test Framework | 3.1.5 | Tests REST intégration |
| Maven (Surefire + Failsafe) | 3.2.5 | Build, tests unitaires / intégration |
| Tailwind CSS | 3.x (CDN) | UI moderne |

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

### 1. LazyInitializationException sur toutes les méthodes d'écriture (modifier, publier, archiver)

**Problème :** Les endpoints PUT, POST `/publier` et POST `/archiver` renvoyaient systématiquement une erreur 500 en production. Après investigation, le `AnnonceDTO.fromEntity()` tentait d'accéder à `annonce.getAuthor().getUsername()` et `annonce.getCategory().getLabel()` alors que l'EntityManager était déjà fermé. L'entité retournée par `entityManager.find()` dans la méthode `modifier()` ne chargeait pas les relations `@ManyToOne(fetch = LAZY)`. Seule la méthode `trouverParId()` utilisait un `JOIN FETCH`, mais les méthodes d'écriture (`modifier`, `publier`, `archiver`) utilisaient un simple `find()`, ce qui provoquait une `LazyInitializationException` au moment de la sérialisation JSON.

**Solution :** Remplacer `entityManager.find(Annonce.class, id)` par une requête JPQL avec `JOIN FETCH` dans **toutes** les méthodes qui retournent une entité au contrôleur REST :
```java
Annonce annonce = entityManager.createQuery(
    "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.id = :id",
    Annonce.class
).setParameter("id", id).getResultStream().findFirst().orElse(null);
```
La leçon : dès qu'une entité doit être convertie en DTO après la fermeture de l'EM, il faut un `JOIN FETCH`. Le `find()` ne suffit pas.

---

### 2. Problème N+1 Select sur les listes paginées

**Problème :** En listant les annonces publiées, Hibernate générait 1 requête pour les annonces + N requêtes pour charger chaque catégorie + N requêtes pour chaque auteur. Avec `hibernate.show_sql=true`, on observait parfois 15+ requêtes SQL pour un simple GET `/api/annonces?page=0&size=5`. Le temps de réponse augmentait linéairement avec le nombre d'annonces.

**Solution :** Ajout de `LEFT JOIN FETCH a.category LEFT JOIN FETCH a.author` dans toutes les requêtes de listing (listerPubliees, listerParAuteur, rechercher). Résultat : une seule requête SQL, quel que soit le nombre de résultats.

---

### 3. Conflits de clés primaires avec H2 en mode test

**Problème :** Les tests d'intégration (`AnnonceRepositoryIT`) échouaient avec `Unique index or primary key violation` lors de l'insertion de nouvelles annonces. Le fichier `test-data.sql` insérait des données avec des IDs explicites (1 à 7), mais la stratégie `@GeneratedValue(strategy = GenerationType.IDENTITY)` avec H2 démarrait son compteur auto-incrémenté à 1. Le premier `save()` dans un test générait donc l'ID 1, qui existait déjà.

**Solution :** Ajouter à la fin de `test-data.sql` des instructions pour repositionner les séquences d'auto-incrémentation après le dernier ID utilisé :
```sql
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 100;
ALTER TABLE annonces ALTER COLUMN id RESTART WITH 100;
```

---

### 4. Pagination non déterministe - Doublons entre les pages

**Problème :** Le test `testPaginationNoDuplicates` échouait de manière intermittente : un même ID apparaissait dans la page 0 et la page 1. La cause : toutes les annonces du jeu de test avaient le même `CURRENT_TIMESTAMP` en `date_creation`, et le tri `ORDER BY a.date DESC` ne garantissait aucun ordre entre les lignes de même date. La base de données renvoyait les résultats dans un ordre arbitraire qui pouvait changer entre deux requêtes.

**Solution :** Ajouter un critère de tri secondaire déterministe (l'ID, qui est unique et immuable) :
```java
ORDER BY a.date DESC, a.id DESC
```

---

### 5. Injection de mocks impossible dans AnnonceResource (tests unitaires)

**Problème :** `AnnonceResourceTest` utilisait `@InjectMocks` avec Mockito, mais le champ `annonceService` dans `AnnonceResource` était déclaré `private final AnnonceService annonceService = new AnnonceService()`. Mockito ne pouvait pas remplacer un champ `final` initialisé inline. Résultat : les tests appelaient le vrai service (qui tentait de se connecter à la base) au lieu du mock, provoquant des `ForbiddenException` et des échecs systématiques.

**Solution :** Ajouter un constructeur package-private qui accepte le service en paramètre, et rendre le champ non-final :
```java
private AnnonceService annonceService;

public AnnonceResource() {
    this.annonceService = new AnnonceService();
}

AnnonceResource(AnnonceService annonceService) {
    this.annonceService = annonceService;
}
```
Le test utilise le constructeur avec paramètre pour injecter le mock, tandis que JAX-RS utilise le constructeur par défaut en production. Le `@Context ContainerRequestContext` est injecté via réflexion dans le test.

---

### 6. Detached Entity - Entité détachée passée à persist

**Problème :** On récupérait l'auteur et la catégorie dans une première transaction, puis on essayait de les associer à l'annonce dans une seconde. Hibernate renvoyait `PersistenceException: detached entity passed to persist` parce que les entités n'étaient plus rattachées à un EntityManager actif.

**Solution :** Tout faire dans la même transaction pour que les entités restent managées :
```java
return executeInTransaction(em, entityManager -> {
    User author = entityManager.find(User.class, authorId);
    Category category = entityManager.find(Category.class, categoryId);
    annonce.setAuthor(author);
    annonce.setCategory(category);
    entityManager.persist(annonce);
    return annonce;
});
```

---

### 7. Transaction non active en mode RESOURCE_LOCAL

**Problème :** Les `em.persist()` effectués sans `tx.begin()` / `tx.commit()` ne provoquaient aucune erreur, mais les données n'étaient jamais écrites en base. En mode `RESOURCE_LOCAL` (hors serveur d'application), JPA ne démarre pas de transaction automatiquement. C'est un bug silencieux très difficile à diagnostiquer.

**Solution :** Centralisation du begin/commit/rollback dans `AbstractService.executeInTransaction()` pour ne jamais oublier. Pattern `try-catch-finally` avec rollback automatique en cas d'exception et fermeture garantie de l'EntityManager.

---

### 8. Fuite de connexions - EntityManager non fermé

**Problème :** Certains chemins d'exécution (notamment en cas d'exception) ne fermaient pas l'EntityManager. Le pool de connexions Hibernate (5 par défaut) s'épuisait après quelques requêtes, bloquant complètement l'application sans message d'erreur explicite.

**Solution :** Bloc `try-finally` systématique dans chaque méthode de service et de repository :
```java
EntityManager em = getEntityManager();
try {
    // ... opérations
} finally {
    em.close();
}
```

---

### 9. Incompatibilité de version Java entre compilation et exécution des tests

**Problème :** Le pom.xml ciblait Java 22 (`maven.compiler.source/target = 22`) mais le JDK installé localement était Java 18. La compilation Maven fonctionnait, mais le lanceur de tests Surefire échouait avec : `has been compiled by a more recent version of the Java Runtime (class file version 66.0), this version only recognizes class file versions up to 62.0`.

**Solution :** Aligner la version dans `pom.xml` avec le JDK réellement installé :
```xml
<maven.compiler.target>18</maven.compiler.target>
<maven.compiler.source>18</maven.compiler.source>
```

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

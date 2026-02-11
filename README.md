# MasterAnnonce - TP JPA/Hibernate

Application web de gestion d'annonces développée dans le cadre du TP Dev Avancé #2 (BUT 3).

## Table des matières

1. [Architecture du projet](#architecture-du-projet)
2. [Technologies utilisées](#technologies-utilisées)
3. [Installation et configuration](#installation-et-configuration)
4. [Problèmes rencontrés et solutions](#problèmes-rencontrés-et-solutions)
5. [Tests](#tests)
6. [Fonctionnalités](#fonctionnalités)

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
│   ├── Annonce.java         # Entité principale avec relations
│   ├── AnnonceStatus.java   # Enum (DRAFT, PUBLISHED, ARCHIVED)
│   ├── User.java            # Utilisateur
│   └── Category.java        # Catégorie d'annonces
│
├── persistence/             # Gestion de la persistance
│   ├── EntityManagerUtil.java    # Singleton Factory
│   └── PersistenceListener.java  # Lifecycle listener
│
├── repository/              # Couche d'accès aux données
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
│   ├── PagedResult.java          # Wrapper pagination
│   ├── ServiceException.java
│   ├── EntityNotFoundException.java
│   └── BusinessException.java
│
└── web/                     # Couche web
    ├── filter/
    │   └── AuthFilter.java       # Protection des routes
    ├── servlet/
    │   ├── LoginServlet.java
    │   ├── LogoutServlet.java
    │   ├── RegisterServlet.java
    │   ├── AnnonceListServlet.java
    │   ├── AnnonceDetailServlet.java
    │   ├── AnnonceCreateServlet.java
    │   ├── AnnonceEditServlet.java
    │   ├── AnnonceActionServlet.java
    │   └── MesAnnoncesServlet.java
    └── validation/
        ├── ValidationResult.java  # Stockage erreurs par champ
        ├── FormValidator.java     # Méthodes de validation
        └── FormData.java          # Conservation valeurs saisies
```

---

## Technologies utilisées

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 17+ | Langage |
| Jakarta EE | 10 | API Web (Servlets, JSP, JSTL) |
| JPA / Hibernate | 6.x | ORM / Persistance |
| PostgreSQL | 15+ | Base de données |
| Maven | 3.9+ | Build & Dépendances |
| Tailwind CSS | 3.x (CDN) | UI moderne |
| JUnit 5 | 5.10+ | Tests unitaires |

---

## Installation et configuration

### Prérequis

1. JDK 17+
2. Maven 3.9+
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
mvn clean package

# Lancer les tests
mvn test

# Le WAR est généré dans target/demo1-1.0-SNAPSHOT.war
```

---

## Problèmes rencontrés et solutions

### 1. LazyInitializationException - Accès aux relations hors transaction

**Problème :** En affichant une annonce dans la JSP, l'accès à `annonce.category.label` provoquait une `LazyInitializationException: could not initialize proxy - no Session`. Les relations `@ManyToOne(fetch = LAZY)` n'étaient pas chargées car l'`EntityManager` était déjà fermé à la sortie du service.

**Solution :** `JOIN FETCH` dans les requêtes JPQL pour charger les relations avant la fermeture de l'EntityManager :
```java
public Optional<Annonce> trouverParId(Long id) {
    return em.createQuery(
        "SELECT a FROM Annonce a " +
        "LEFT JOIN FETCH a.category " +
        "LEFT JOIN FETCH a.author " +
        "WHERE a.id = :id",
        Annonce.class
    ).setParameter("id", id)
     .getResultStream()
     .findFirst();
}
```

---

### 2. Problème N+1 Select - Requêtes SQL excessives sur les listes

**Problème :** En listant les annonces, Hibernate générait 1 requête pour les annonces puis N requêtes pour charger chaque catégorie et auteur un par un. Avec `hibernate.show_sql=true` on voyait des dizaines de SELECT défiler dans la console pour une simple liste.

**Solution :** Même approche que le problème 1, ajout de `JOIN FETCH` dans les requêtes de listing :
```java
SELECT a FROM Annonce a
LEFT JOIN FETCH a.category
LEFT JOIN FETCH a.author
WHERE a.status = :status
```

---

### 3. Detached Entity - Entité détachée passée à persist

**Problème :** On récupérait l'auteur et la catégorie dans une première transaction, puis on essayait de les associer à l'annonce dans une seconde. Hibernate renvoyait `PersistenceException: detached entity passed to persist` parce que les entités n'étaient plus rattachées à un EntityManager actif.

**Solution :** Tout faire dans la même transaction pour que les entités restent managées :
```java
public Annonce creer(Annonce annonce, Long authorId, Long categoryId) {
    EntityManager em = EntityManagerUtil.getEntityManager();
    return executeInTransaction(em, () -> {
        User author = em.find(User.class, authorId);
        Category category = em.find(Category.class, categoryId);
        annonce.setAuthor(author);
        annonce.setCategory(category);
        em.persist(annonce);
        return annonce;
    });
}
```

---

### 4. Transaction non active - Oubli du begin/commit

**Problème :** Au début, on faisait des `em.persist()` sans ouvrir de transaction. En mode `RESOURCE_LOCAL`, JPA ne démarre pas de transaction automatiquement. Résultat : les entités n'étaient jamais sauvegardées en base, sans aucune erreur visible, ce qui était difficile à débugger.

**Solution :** Centralisation du begin/commit/rollback dans `AbstractService` pour ne plus jamais oublier :
```java
protected <T> T executeInTransaction(EntityManager em, TransactionalOperation<T> operation) {
    EntityTransaction tx = em.getTransaction();
    try {
        tx.begin();
        T result = operation.execute();
        tx.commit();
        return result;
    } catch (Exception e) {
        if (tx.isActive()) tx.rollback();
        throw e;
    } finally {
        em.close();
    }
}
```

---

### 5. Mapping Enum - Statut stocké en ORDINAL par défaut

**Problème :** Sans annotation, Hibernate stockait le statut (`DRAFT`, `PUBLISHED`, `ARCHIVED`) comme un entier (0, 1, 2). En regardant la base on ne comprenait pas à quoi correspondaient les valeurs, et si on ajoutait un nouveau statut au milieu de l'enum ça décalait tout.

**Solution :** `@Enumerated(EnumType.STRING)` pour stocker le texte directement :
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private AnnonceStatus status;
```
En base on voit maintenant `'DRAFT'`, `'PUBLISHED'`, `'ARCHIVED'` directement.

---

### 6. Synchronisation bidirectionnelle - Incohérence des relations

**Problème :** On faisait `annonce.setAuthor(user)` sans ajouter l'annonce dans `user.getAnnonces()`. En base ça marchait, mais dans les tests quand on vérifiait `user.getAnnonces().size()` juste après, la liste était vide parce que le cache Hibernate n'était pas synchronisé.

**Solution :** Méthodes utilitaires pour gérer les deux côtés de la relation :
```java
public void addAnnonce(Annonce annonce) {
    annonces.add(annonce);
    annonce.setAuthor(this);
}

public void removeAnnonce(Annonce annonce) {
    annonces.remove(annonce);
    annonce.setAuthor(null);
}
```

---

### 7. Fuite de connexions - EntityManager non fermé

**Problème :** Certains chemins d'exécution (surtout en cas d'exception) ne fermaient pas l'`EntityManager`. Au bout de quelques requêtes le pool de connexions (5 max) était épuisé et l'application se bloquait.

**Solution :** `try-finally` systématique dans les repositories pour garantir la fermeture :
```java
public Optional<T> findById(ID id) {
    EntityManager em = EntityManagerUtil.getEntityManager();
    try {
        return Optional.ofNullable(em.find(entityClass, id));
    } finally {
        em.close();
    }
}
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

### Fichiers de test supplémentaires

| Fichier | Description |
|---------|-------------|
| `EntityManagerUtilTest.java` | Configuration JPA |
| `EntityMappingTest.java` | Mappings d'entités |
| `AnnonceServiceTest.java` | Règles métier annonces |

### Exécution des tests

```bash
# Tous les tests
mvn test

# Un test spécifique
mvn test -Dtest=WebLayerTest

# Avec rapport détaillé
mvn test -Dsurefire.reportFormat=plain
```

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

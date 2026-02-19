-- ============================================
-- Jeu de données de test pour H2 In-Memory
-- Chargé automatiquement par Hibernate au démarrage
-- ============================================

-- Utilisateurs de test
INSERT INTO users (id, username, email, password, created_at) VALUES (1, 'alice', 'alice@test.com', 'password123', CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, created_at) VALUES (2, 'bob', 'bob@test.com', 'password456', CURRENT_TIMESTAMP);
INSERT INTO users (id, username, email, password, created_at) VALUES (3, 'charlie', 'charlie@test.com', 'password789', CURRENT_TIMESTAMP);

-- Catégories de test
INSERT INTO categories (id, label) VALUES (1, 'Immobilier');
INSERT INTO categories (id, label) VALUES (2, 'Vehicules');
INSERT INTO categories (id, label) VALUES (3, 'Emploi');

-- Annonces de test (différents statuts pour couvrir tous les cas)
-- Annonces PUBLISHED (pour tester la pagination)
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (1, 'Appartement Paris 15', 'Bel appartement 3 pieces', 'Paris 75015', 'alice@test.com', 'PUBLISHED', CURRENT_TIMESTAMP, 1, 1, 0);
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (2, 'Maison Montreuil', 'Grande maison avec jardin', 'Montreuil 93100', 'alice@test.com', 'PUBLISHED', CURRENT_TIMESTAMP, 1, 1, 0);
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (3, 'Voiture Peugeot 308', 'Peugeot 308 2020 faible km', 'Lyon 69001', 'bob@test.com', 'PUBLISHED', CURRENT_TIMESTAMP, 2, 2, 0);
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (4, 'Renault Clio occasion', 'Clio 4 diesel 2018', 'Marseille 13001', 'bob@test.com', 'PUBLISHED', CURRENT_TIMESTAMP, 2, 2, 0);
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (5, 'Dev Java Senior', 'Poste CDI developpeur Java', 'Paris 75009', 'charlie@test.com', 'PUBLISHED', CURRENT_TIMESTAMP, 3, 3, 0);

-- Annonce DRAFT
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (6, 'Brouillon annonce', 'En cours de redaction', 'Paris', 'alice@test.com', 'DRAFT', CURRENT_TIMESTAMP, 1, 1, 0);

-- Annonce ARCHIVED
INSERT INTO annonces (id, title, description, adress, mail, status, date_creation, author_id, category_id, version) VALUES (7, 'Annonce archivee', 'Plus disponible', 'Lyon', 'bob@test.com', 'ARCHIVED', CURRENT_TIMESTAMP, 2, 2, 0);

-- Réinitialiser les séquences auto-incrémentées pour éviter les conflits de clé primaire
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 100;
ALTER TABLE annonces ALTER COLUMN id RESTART WITH 100;

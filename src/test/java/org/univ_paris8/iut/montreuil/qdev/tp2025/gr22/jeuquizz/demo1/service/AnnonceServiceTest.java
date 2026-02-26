package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.UpdateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper.AnnonceMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AnnonceMapper annonceMapper;

    @InjectMocks
    private AnnonceService annonceService;

    private User author;
    private Category category;
    private Annonce annonce;

    @BeforeEach
    void setUp() {
        author = new User("testuser", "test@email.com", "password");
        author.setId(1L);

        category = new Category("Immobilier");
        category.setId(10L);

        annonce = new Annonce("Titre test", "Description test", "Paris", "test@mail.com");
        annonce.setId(100L);
        annonce.setAuthor(author);
        annonce.setCategory(category);
        annonce.setStatus(AnnonceStatus.DRAFT);
    }

    // ==================== CREATION ====================

    @Nested
    @DisplayName("Création d'annonce")
    class CreerTests {

        @Test
        @DisplayName("Doit créer une annonce avec succès")
        void creer_succes() {
            Annonce nouvelle = new Annonce("Nouvelle", "Desc", "Lyon", "new@mail.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(author));
            when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
            when(annonceRepository.save(any(Annonce.class))).thenReturn(nouvelle);

            Annonce result = annonceService.creer(nouvelle, 1L, 10L);

            assertNotNull(result);
            assertEquals(AnnonceStatus.DRAFT, nouvelle.getStatus());
            assertEquals(author, nouvelle.getAuthor());
            assertEquals(category, nouvelle.getCategory());
            verify(annonceRepository).save(nouvelle);
        }

        @Test
        @DisplayName("Doit créer sans catégorie")
        void creer_sansCategorie() {
            Annonce nouvelle = new Annonce("Nouvelle", "Desc", "Lyon", "new@mail.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(author));
            when(annonceRepository.save(any(Annonce.class))).thenReturn(nouvelle);

            Annonce result = annonceService.creer(nouvelle, 1L, null);

            assertNotNull(result);
            assertNull(nouvelle.getCategory());
            verify(categoryRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Doit échouer si l'auteur n'existe pas")
        void creer_auteurInexistant() {
            Annonce nouvelle = new Annonce("Nouvelle", "Desc", "Lyon", "new@mail.com");
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> annonceService.creer(nouvelle, 999L, null));
        }

        @Test
        @DisplayName("Doit échouer si la catégorie n'existe pas")
        void creer_categorieInexistante() {
            Annonce nouvelle = new Annonce("Nouvelle", "Desc", "Lyon", "new@mail.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(author));
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> annonceService.creer(nouvelle, 1L, 999L));
        }
    }

    // ==================== MODIFICATION ====================

    @Nested
    @DisplayName("Modification d'annonce")
    class ModifierTests {

        @Test
        @DisplayName("Doit modifier une annonce DRAFT par son auteur")
        void modifier_succes() {
            UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
            dto.setTitle("Titre modifié");
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);

            Annonce result = annonceService.modifier(100L, dto, 1L);

            assertNotNull(result);
            verify(annonceMapper).updateEntityFromDto(dto, annonce);
            verify(annonceRepository).save(annonce);
        }

        @Test
        @DisplayName("Doit échouer si l'annonce est PUBLISHED")
        void modifier_annoncePubliee() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.modifier(100L, dto, 1L));
        }

        @Test
        @DisplayName("Doit échouer si l'annonce est ARCHIVED")
        void modifier_annonceArchivee() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.modifier(100L, dto, 1L));
        }

        @Test
        @DisplayName("Doit échouer si l'utilisateur n'est pas l'auteur")
        void modifier_pasAuteur() {
            UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ForbiddenException.class,
                    () -> annonceService.modifier(100L, dto, 999L));
        }

        @Test
        @DisplayName("Doit échouer si l'annonce n'existe pas")
        void modifier_annonceInexistante() {
            UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
            when(annonceRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> annonceService.modifier(999L, dto, 1L));
        }
    }

    // ==================== PUBLICATION ====================

    @Nested
    @DisplayName("Publication d'annonce")
    class PublierTests {

        @Test
        @DisplayName("Doit publier une annonce DRAFT")
        void publier_succes() {
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);

            Annonce result = annonceService.publier(100L, 1L);

            assertEquals(AnnonceStatus.PUBLISHED, result.getStatus());
            verify(annonceRepository).save(annonce);
        }

        @Test
        @DisplayName("Doit échouer si déjà publiée")
        void publier_dejaPubliee() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.publier(100L, 1L));
        }

        @Test
        @DisplayName("Doit échouer si archivée")
        void publier_archivee() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.publier(100L, 1L));
        }

        @Test
        @DisplayName("Doit échouer si l'utilisateur n'est pas l'auteur")
        void publier_pasAuteur() {
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ForbiddenException.class,
                    () -> annonceService.publier(100L, 999L));
        }
    }

    // ==================== ARCHIVAGE ====================

    @Nested
    @DisplayName("Archivage d'annonce")
    class ArchiverTests {

        @Test
        @DisplayName("Doit archiver une annonce PUBLISHED")
        void archiver_succes() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));
            when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);

            Annonce result = annonceService.archiver(100L, 1L);

            assertEquals(AnnonceStatus.ARCHIVED, result.getStatus());
            verify(annonceRepository).save(annonce);
        }

        @Test
        @DisplayName("Doit échouer si déjà archivée")
        void archiver_dejaArchivee() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.archiver(100L, 1L));
        }
    }

    // ==================== SUPPRESSION ====================

    @Nested
    @DisplayName("Suppression d'annonce")
    class SupprimerTests {

        @Test
        @DisplayName("Doit supprimer une annonce ARCHIVED")
        void supprimer_succes() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

            annonceService.supprimer(100L, 1L);

            verify(annonceRepository).delete(annonce);
        }

        @Test
        @DisplayName("Doit échouer si l'annonce n'est pas ARCHIVED")
        void supprimer_pasArchivee() {
            annonce.setStatus(AnnonceStatus.PUBLISHED);
            when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ConflictException.class,
                    () -> annonceService.supprimer(100L, 1L));
        }

        @Test
        @DisplayName("Doit échouer si l'utilisateur n'est pas l'auteur")
        void supprimer_pasAuteur() {
            annonce.setStatus(AnnonceStatus.ARCHIVED);
            when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

            assertThrows(ForbiddenException.class,
                    () -> annonceService.supprimer(100L, 999L));
        }
    }

    // ==================== RECHERCHE ====================

    @Nested
    @DisplayName("Recherche d'annonce")
    class RechercheTests {

        @Test
        @DisplayName("Doit trouver une annonce par ID")
        void trouverParId_succes() {
            when(annonceRepository.findByIdWithRelations(100L)).thenReturn(Optional.of(annonce));

            Annonce result = annonceService.trouverParId(100L);

            assertEquals(annonce, result);
        }

        @Test
        @DisplayName("Doit échouer si l'annonce n'existe pas")
        void trouverParId_inexistant() {
            when(annonceRepository.findByIdWithRelations(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> annonceService.trouverParId(999L));
        }
    }
}

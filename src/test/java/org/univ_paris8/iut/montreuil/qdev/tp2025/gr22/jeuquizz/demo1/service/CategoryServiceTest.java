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
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Immobilier");
        category.setId(1L);
    }

    // ==================== CREATION ====================

    @Nested
    @DisplayName("Création de catégorie")
    class CreerTests {

        @Test
        @DisplayName("Doit créer une catégorie avec succès")
        void creer_succes() {
            when(categoryRepository.existsByLabel("Véhicules")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

            Category result = categoryService.creer("Véhicules");

            assertNotNull(result);
            assertEquals("Véhicules", result.getLabel());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Doit échouer si le label existe déjà")
        void creer_labelExistant() {
            when(categoryRepository.existsByLabel("Immobilier")).thenReturn(true);

            assertThrows(BusinessException.class,
                    () -> categoryService.creer("Immobilier"));

            verify(categoryRepository, never()).save(any());
        }
    }

    // ==================== MODIFICATION ====================

    @Nested
    @DisplayName("Modification de catégorie")
    class ModifierTests {

        @Test
        @DisplayName("Doit modifier le label d'une catégorie")
        void modifier_succes() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByLabel("Nouveau label")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            Category result = categoryService.modifier(1L, "Nouveau label");

            assertEquals("Nouveau label", result.getLabel());
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("Ne doit pas vérifier l'unicité si le label ne change pas")
        void modifier_memLabel() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            Category result = categoryService.modifier(1L, "Immobilier");

            assertNotNull(result);
            verify(categoryRepository, never()).existsByLabel(anyString());
        }

        @Test
        @DisplayName("Doit échouer si la catégorie n'existe pas")
        void modifier_inexistante() {
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> categoryService.modifier(999L, "Label"));
        }

        @Test
        @DisplayName("Doit échouer si le nouveau label est déjà pris")
        void modifier_labelPris() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.existsByLabel("Véhicules")).thenReturn(true);

            assertThrows(BusinessException.class,
                    () -> categoryService.modifier(1L, "Véhicules"));
        }
    }

    // ==================== SUPPRESSION ====================

    @Nested
    @DisplayName("Suppression de catégorie")
    class SupprimerTests {

        @Test
        @DisplayName("Doit supprimer une catégorie sans annonces")
        void supprimer_succes() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            categoryService.supprimer(1L);

            verify(categoryRepository).delete(category);
        }

        @Test
        @DisplayName("Doit échouer si la catégorie a des annonces")
        void supprimer_avecAnnonces() {
            category.getAnnonces().add(new Annonce("Test", "Desc", "Paris", "t@m.com"));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            assertThrows(BusinessException.class,
                    () -> categoryService.supprimer(1L));

            verify(categoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Doit échouer si la catégorie n'existe pas")
        void supprimer_inexistante() {
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> categoryService.supprimer(999L));
        }
    }

    // ==================== RECHERCHE ====================

    @Nested
    @DisplayName("Recherche de catégories")
    class RechercheTests {

        @Test
        @DisplayName("Doit lister toutes les catégories")
        void listerToutes() {
            Category cat2 = new Category("Véhicules");
            cat2.setId(2L);
            when(categoryRepository.findAll()).thenReturn(List.of(category, cat2));

            List<Category> result = categoryService.listerToutes();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Doit trouver par ID")
        void trouverParId() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            Optional<Category> result = categoryService.trouverParId(1L);

            assertTrue(result.isPresent());
            assertEquals("Immobilier", result.get().getLabel());
        }
    }
}

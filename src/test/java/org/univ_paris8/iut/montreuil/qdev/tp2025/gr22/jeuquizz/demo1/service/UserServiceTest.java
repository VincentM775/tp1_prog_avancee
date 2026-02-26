package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@email.com", "hashedPassword");
        user.setId(1L);
    }

    // ==================== CREATION ====================

    @Nested
    @DisplayName("Création d'utilisateur")
    class CreerTests {

        @Test
        @DisplayName("Doit créer un utilisateur avec mot de passe hashé")
        void creer_succes() {
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = userService.creer("newuser", "new@email.com", "password");

            assertNotNull(result);
            assertEquals("newuser", result.getUsername());
            assertEquals("hashedPassword", result.getPassword());
            verify(passwordEncoder).encode("password");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Doit échouer si le username existe déjà")
        void creer_usernameExistant() {
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            assertThrows(BusinessException.class,
                    () -> userService.creer("testuser", "new@email.com", "password"));

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Doit échouer si l'email existe déjà")
        void creer_emailExistant() {
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

            assertThrows(BusinessException.class,
                    () -> userService.creer("newuser", "test@email.com", "password"));

            verify(userRepository, never()).save(any());
        }
    }

    // ==================== MODIFICATION ====================

    @Nested
    @DisplayName("Modification d'utilisateur")
    class ModifierTests {

        @Test
        @DisplayName("Doit modifier le username")
        void modifier_username() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("newname")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userService.modifier(1L, "newname", null);

            assertEquals("newname", result.getUsername());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Doit modifier l'email")
        void modifier_email() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("newemail@test.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userService.modifier(1L, null, "newemail@test.com");

            assertEquals("newemail@test.com", result.getEmail());
        }

        @Test
        @DisplayName("Doit échouer si l'utilisateur n'existe pas")
        void modifier_inexistant() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.modifier(999L, "name", null));
        }

        @Test
        @DisplayName("Doit échouer si le nouveau username est pris")
        void modifier_usernamePris() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername("taken")).thenReturn(true);

            assertThrows(BusinessException.class,
                    () -> userService.modifier(1L, "taken", null));
        }
    }

    // ==================== SUPPRESSION ====================

    @Nested
    @DisplayName("Suppression d'utilisateur")
    class SupprimerTests {

        @Test
        @DisplayName("Doit supprimer un utilisateur existant")
        void supprimer_succes() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.supprimer(1L);

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Doit échouer si l'utilisateur n'existe pas")
        void supprimer_inexistant() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.supprimer(999L));
        }
    }

    // ==================== RECHERCHE ====================

    @Nested
    @DisplayName("Recherche d'utilisateur")
    class RechercheTests {

        @Test
        @DisplayName("Doit trouver par ID")
        void trouverParId() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            Optional<User> result = userService.trouverParId(1L);

            assertTrue(result.isPresent());
            assertEquals(user, result.get());
        }

        @Test
        @DisplayName("Doit trouver par username")
        void trouverParUsername() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

            Optional<User> result = userService.trouverParUsername("testuser");

            assertTrue(result.isPresent());
            assertEquals("testuser", result.get().getUsername());
        }

        @Test
        @DisplayName("Doit retourner empty si non trouvé")
        void trouverParId_inexistant() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<User> result = userService.trouverParId(999L);

            assertTrue(result.isEmpty());
        }
    }
}

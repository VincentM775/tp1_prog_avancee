package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User creer(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Le nom d'utilisateur '" + username + "' est déjà utilisé");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("L'email '" + email + "' est déjà utilisé");
        }

        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Optional<User> trouverParId(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> trouverParUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> trouverParEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User modifier(Long id, String username, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur", id));

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new BusinessException("Le nom d'utilisateur '" + username + "' est déjà utilisé");
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BusinessException("L'email '" + email + "' est déjà utilisé");
            }
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void supprimer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur", id));
        userRepository.delete(user);
    }
}

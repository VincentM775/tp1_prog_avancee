package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.AnnonceSpecifications;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.UpdateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper.AnnonceMapper;

import java.sql.Timestamp;

@Service
@Transactional(readOnly = true)
public class AnnonceService {

    private static final String ENTITY_ANNONCE = "Annonce";

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnnonceMapper annonceMapper;

    public AnnonceService(AnnonceRepository annonceRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          AnnonceMapper annonceMapper) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.annonceMapper = annonceMapper;
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Annonce creer(Annonce annonce, Long authorId, Long categoryId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur", authorId));

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie", categoryId));
        }

        annonce.setAuthor(author);
        annonce.setCategory(category);
        annonce.setStatus(AnnonceStatus.DRAFT);
        annonce.setDate(new Timestamp(System.currentTimeMillis()));

        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Annonce modifier(Long id, UpdateAnnonceDTO dto, Long currentUserId) {
        Annonce annonce = annonceRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_ANNONCE, id));

        verifierAuteur(annonce, currentUserId);

        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new ConflictException("Impossible de modifier une annonce publiée");
        }
        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ConflictException("Impossible de modifier une annonce archivée");
        }

        annonceMapper.updateEntityFromDto(dto, annonce);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie", dto.getCategoryId()));
            annonce.setCategory(category);
        }

        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Annonce publier(Long id, Long currentUserId) {
        Annonce annonce = annonceRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_ANNONCE, id));

        verifierAuteur(annonce, currentUserId);

        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new ConflictException("L'annonce est déjà publiée");
        }
        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ConflictException("Impossible de publier une annonce archivée");
        }

        annonce.setStatus(AnnonceStatus.PUBLISHED);
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Annonce archiver(Long id, Long currentUserId) {
        Annonce annonce = annonceRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_ANNONCE, id));

        if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
            throw new ConflictException("L'annonce est déjà archivée");
        }

        annonce.setStatus(AnnonceStatus.ARCHIVED);
        return annonceRepository.save(annonce);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void supprimer(Long id, Long currentUserId) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_ANNONCE, id));

        verifierAuteur(annonce, currentUserId);

        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            throw new ConflictException(
                    "L'annonce doit être archivée avant suppression (statut actuel : " + annonce.getStatus() + ")");
        }

        annonceRepository.delete(annonce);
    }

    public Annonce trouverParId(Long id) {
        return annonceRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_ANNONCE, id));
    }

    public Page<Annonce> listerPubliees(Pageable pageable) {
        return annonceRepository.findByStatus(AnnonceStatus.PUBLISHED, pageable);
    }

    public Page<Annonce> listerParAuteur(Long authorId, Pageable pageable) {
        return annonceRepository.findByAuthorId(authorId, pageable);
    }

    /**
     * Recherche dynamique multi-critères via Specifications composables.
     */
    public Page<Annonce> rechercher(String keyword, AnnonceStatus status,
                                     Long categoryId, Long authorId,
                                     Timestamp fromDate, Timestamp toDate,
                                     Pageable pageable) {

        Specification<Annonce> spec = Specification.where(null);

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(AnnonceSpecifications.keywordInTitleOrDescription(keyword));
        }
        if (status != null) {
            spec = spec.and(AnnonceSpecifications.hasStatus(status));
        }
        if (categoryId != null) {
            spec = spec.and(AnnonceSpecifications.hasCategoryId(categoryId));
        }
        if (authorId != null) {
            spec = spec.and(AnnonceSpecifications.hasAuthorId(authorId));
        }
        if (fromDate != null) {
            spec = spec.and(AnnonceSpecifications.createdAfter(fromDate));
        }
        if (toDate != null) {
            spec = spec.and(AnnonceSpecifications.createdBefore(toDate));
        }

        return annonceRepository.findAll(spec, pageable);
    }

    private void verifierAuteur(Annonce annonce, Long currentUserId) {
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(currentUserId)) {
            throw new ForbiddenException("Vous n'êtes pas l'auteur de cette annonce");
        }
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category creer(String label) {
        if (categoryRepository.existsByLabel(label)) {
            throw new BusinessException("La catégorie '" + label + "' existe déjà");
        }
        Category category = new Category(label);
        return categoryRepository.save(category);
    }

    public Optional<Category> trouverParId(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> listerToutes() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category modifier(Long id, String nouveauLabel) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie", id));

        if (!nouveauLabel.equals(category.getLabel())) {
            if (categoryRepository.existsByLabel(nouveauLabel)) {
                throw new BusinessException("La catégorie '" + nouveauLabel + "' existe déjà");
            }
            category.setLabel(nouveauLabel);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void supprimer(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie", id));

        if (!category.getAnnonces().isEmpty()) {
            throw new BusinessException(
                    "Impossible de supprimer la catégorie : " + category.getAnnonces().size() + " annonce(s) y sont associée(s)");
        }

        categoryRepository.delete(category);
    }
}

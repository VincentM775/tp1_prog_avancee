package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé de la catégorie est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Column(unique = true, nullable = false, length = 100)
    private String label;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Annonce> annonces = new ArrayList<>();

    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }

    public void addAnnonce(Annonce annonce) {
        annonces.add(annonce);
        annonce.setCategory(this);
    }

    public void removeAnnonce(Annonce annonce) {
        annonces.remove(annonce);
        annonce.setCategory(null);
    }

    @Override
    public String toString() {
        return "Category{id=" + id + ", label='" + label + "'}";
    }
}

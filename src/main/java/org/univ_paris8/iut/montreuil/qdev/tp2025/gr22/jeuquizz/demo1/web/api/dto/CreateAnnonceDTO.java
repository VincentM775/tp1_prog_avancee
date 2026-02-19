package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;

/**
 * DTO d'entrée pour la création d'une Annonce.
 * L'auteur est automatiquement l'utilisateur authentifié (pas besoin de authorId).
 * Pattern Builder utilisé pour faciliter le mapping DTO → Entity.
 */
public class CreateAnnonceDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 64, message = "Le titre ne doit pas dépasser 64 caractères")
    private String title;

    @Size(max = 256, message = "La description ne doit pas dépasser 256 caractères")
    private String description;

    @Size(max = 64, message = "L'adresse ne doit pas dépasser 64 caractères")
    private String adress;

    @Email(message = "L'email doit être valide")
    @Size(max = 64, message = "L'email ne doit pas dépasser 64 caractères")
    private String mail;

    private Long categoryId;

    public CreateAnnonceDTO() {
    }

    private CreateAnnonceDTO(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.adress = builder.adress;
        this.mail = builder.mail;
        this.categoryId = builder.categoryId;
    }

    /**
     * Conversion DTO → Entity.
     */
    public Annonce toEntity() {
        return new Annonce(title, description, adress, mail);
    }

    // --- Getters & Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    // --- Builder ---

    public static class Builder {
        private String title;
        private String description;
        private String adress;
        private String mail;
        private Long categoryId;

        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder adress(String adress) { this.adress = adress; return this; }
        public Builder mail(String mail) { this.mail = mail; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }

        public CreateAnnonceDTO build() {
            return new CreateAnnonceDTO(this);
        }
    }
}

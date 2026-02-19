package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;

import java.sql.Timestamp;

/**
 * DTO de sortie pour une Annonce.
 * Pattern Builder utilisé pour faciliter le mapping Entity → DTO.
 */
public class AnnonceDTO {

    private Long id;
    private String title;
    private String description;
    private String adress;
    private String mail;
    private String status;
    private Timestamp dateCreation;
    private String authorUsername;
    private Long authorId;
    private String categoryLabel;
    private Long categoryId;

    public AnnonceDTO() {
    }

    private AnnonceDTO(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.adress = builder.adress;
        this.mail = builder.mail;
        this.status = builder.status;
        this.dateCreation = builder.dateCreation;
        this.authorUsername = builder.authorUsername;
        this.authorId = builder.authorId;
        this.categoryLabel = builder.categoryLabel;
        this.categoryId = builder.categoryId;
    }

    /**
     * Conversion Entity → DTO via le Builder.
     */
    public static AnnonceDTO fromEntity(Annonce annonce) {
        Builder builder = new Builder()
                .id(annonce.getId())
                .title(annonce.getTitle())
                .description(annonce.getDescription())
                .adress(annonce.getAdress())
                .mail(annonce.getMail())
                .status(annonce.getStatus().name())
                .dateCreation(annonce.getDate());

        if (annonce.getAuthor() != null) {
            builder.authorId(annonce.getAuthor().getId())
                   .authorUsername(annonce.getAuthor().getUsername());
        }
        if (annonce.getCategory() != null) {
            builder.categoryId(annonce.getCategory().getId())
                   .categoryLabel(annonce.getCategory().getLabel());
        }

        return builder.build();
    }

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    // --- Builder ---

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private String adress;
        private String mail;
        private String status;
        private Timestamp dateCreation;
        private String authorUsername;
        private Long authorId;
        private String categoryLabel;
        private Long categoryId;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder adress(String adress) { this.adress = adress; return this; }
        public Builder mail(String mail) { this.mail = mail; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder dateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; return this; }
        public Builder authorUsername(String authorUsername) { this.authorUsername = authorUsername; return this; }
        public Builder authorId(Long authorId) { this.authorId = authorId; return this; }
        public Builder categoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }

        public AnnonceDTO build() {
            return new AnnonceDTO(this);
        }
    }
}

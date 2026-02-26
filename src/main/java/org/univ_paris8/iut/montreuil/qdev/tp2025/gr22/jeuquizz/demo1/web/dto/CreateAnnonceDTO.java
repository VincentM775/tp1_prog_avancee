package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
}

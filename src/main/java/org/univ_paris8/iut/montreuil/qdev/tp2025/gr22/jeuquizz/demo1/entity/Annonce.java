package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

@Entity
@Table(name = "annonces")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 64, message = "Le titre ne doit pas dépasser 64 caractères")
    @Column(nullable = false, length = 64)
    private String title;

    @Size(max = 256, message = "La description ne doit pas dépasser 256 caractères")
    @Column(length = 256)
    private String description;

    @Size(max = 64, message = "L'adresse ne doit pas dépasser 64 caractères")
    @Column(length = 64)
    private String adress;

    @Email(message = "L'email doit être valide")
    @Size(max = 64, message = "L'email ne doit pas dépasser 64 caractères")
    @Column(length = 64)
    private String mail;

    @Column(name = "date_creation")
    private Timestamp date;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Annonce() {
    }

    public Annonce(String title, String description, String adress, String mail) {
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
        this.status = AnnonceStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = new Timestamp(System.currentTimeMillis());
        }
        if (status == null) {
            status = AnnonceStatus.DRAFT;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public AnnonceStatus getStatus() {
        return status;
    }

    public void setStatus(AnnonceStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Annonce{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}

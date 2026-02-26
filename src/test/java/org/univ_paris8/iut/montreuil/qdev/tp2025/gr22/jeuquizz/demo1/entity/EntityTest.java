package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    // ==================== Annonce ====================

    @Test
    void annonce_constructeur() {
        Annonce a = new Annonce("Titre", "Desc", "Adresse", "mail@test.com");
        assertEquals("Titre", a.getTitle());
        assertEquals("Desc", a.getDescription());
        assertEquals("Adresse", a.getAdress());
        assertEquals("mail@test.com", a.getMail());
        assertEquals(AnnonceStatus.DRAFT, a.getStatus());
    }

    @Test
    void annonce_gettersSetters() {
        Annonce a = new Annonce();
        a.setId(1L);
        a.setTitle("T");
        a.setDescription("D");
        a.setAdress("A");
        a.setMail("m@m.com");
        a.setVersion(2L);

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        a.setDate(ts);

        assertEquals(1L, a.getId());
        assertEquals("T", a.getTitle());
        assertEquals("D", a.getDescription());
        assertEquals("A", a.getAdress());
        assertEquals("m@m.com", a.getMail());
        assertEquals(2L, a.getVersion());
        assertEquals(ts, a.getDate());
    }

    @Test
    void annonce_toString() {
        Annonce a = new Annonce("Titre", "Desc", "Addr", "m@m.com");
        a.setId(1L);
        String s = a.toString();
        assertTrue(s.contains("Titre"));
        assertTrue(s.contains("DRAFT"));
    }

    @Test
    void annonce_onCreate() {
        Annonce a = new Annonce();
        a.onCreate();
        assertNotNull(a.getDate());
        assertEquals(AnnonceStatus.DRAFT, a.getStatus());
    }

    @Test
    void annonce_onCreateAvecDateExistante() {
        Annonce a = new Annonce();
        Timestamp ts = Timestamp.valueOf("2025-01-01 00:00:00");
        a.setDate(ts);
        a.setStatus(AnnonceStatus.PUBLISHED);
        a.onCreate();
        assertEquals(ts, a.getDate());
        assertEquals(AnnonceStatus.PUBLISHED, a.getStatus());
    }

    // ==================== User ====================

    @Test
    void user_constructeur() {
        User u = new User("user1", "user@test.com", "pass");
        assertEquals("user1", u.getUsername());
        assertEquals("user@test.com", u.getEmail());
        assertEquals("pass", u.getPassword());
        assertNotNull(u.getCreatedAt());
    }

    @Test
    void user_gettersSetters() {
        User u = new User();
        u.setId(1L);
        u.setUsername("u");
        u.setEmail("e@e.com");
        u.setPassword("p");
        u.setRole("ROLE_ADMIN");
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        u.setCreatedAt(ts);

        assertEquals(1L, u.getId());
        assertEquals("u", u.getUsername());
        assertEquals("e@e.com", u.getEmail());
        assertEquals("p", u.getPassword());
        assertEquals("ROLE_ADMIN", u.getRole());
        assertEquals(ts, u.getCreatedAt());
    }

    @Test
    void user_addRemoveAnnonce() {
        User u = new User("u", "e@e.com", "p");
        Annonce a = new Annonce("T", "D", "A", "m@m.com");

        u.addAnnonce(a);
        assertEquals(1, u.getAnnonces().size());
        assertEquals(u, a.getAuthor());

        u.removeAnnonce(a);
        assertEquals(0, u.getAnnonces().size());
        assertNull(a.getAuthor());
    }

    @Test
    void user_toString() {
        User u = new User("user1", "user@test.com", "pass");
        u.setId(1L);
        String s = u.toString();
        assertTrue(s.contains("user1"));
        assertTrue(s.contains("user@test.com"));
    }

    @Test
    void user_onCreate() {
        User u = new User();
        u.onCreate();
        assertNotNull(u.getCreatedAt());
    }

    @Test
    void user_setAnnonces() {
        User u = new User();
        u.setAnnonces(new java.util.ArrayList<>());
        assertNotNull(u.getAnnonces());
    }

    // ==================== Category ====================

    @Test
    void category_constructeur() {
        Category c = new Category("Immobilier");
        assertEquals("Immobilier", c.getLabel());
    }

    @Test
    void category_gettersSetters() {
        Category c = new Category();
        c.setId(1L);
        c.setLabel("Test");
        assertEquals(1L, c.getId());
        assertEquals("Test", c.getLabel());
    }

    @Test
    void category_addRemoveAnnonce() {
        Category c = new Category("Cat");
        Annonce a = new Annonce("T", "D", "A", "m@m.com");

        c.addAnnonce(a);
        assertEquals(1, c.getAnnonces().size());
        assertEquals(c, a.getCategory());

        c.removeAnnonce(a);
        assertEquals(0, c.getAnnonces().size());
        assertNull(a.getCategory());
    }

    @Test
    void category_setAnnonces() {
        Category c = new Category();
        c.setAnnonces(new java.util.ArrayList<>());
        assertNotNull(c.getAnnonces());
    }

    @Test
    void category_toString() {
        Category c = new Category("Label");
        c.setId(1L);
        String s = c.toString();
        assertTrue(s.contains("Label"));
    }
}

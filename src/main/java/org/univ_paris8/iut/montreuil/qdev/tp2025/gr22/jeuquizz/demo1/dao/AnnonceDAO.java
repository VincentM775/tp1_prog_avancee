package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.Annonce;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class AnnonceDAO extends DAO<Annonce> {

    @Override
    public Annonce create(Annonce obj) {
        try {
            String query = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";

            PreparedStatement prepare = this.connect.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            prepare.setString(1, obj.getTitle());
            prepare.setString(2, obj.getDescription());
            prepare.setString(3, obj.getAdress());
            prepare.setString(4, obj.getMail());

            prepare.executeUpdate();

            ResultSet result = prepare.getGeneratedKeys();
            if (result.next()) {
                obj.setId(result.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public Annonce find(long id) {
        Annonce annonce = new Annonce();
        try {
            ResultSet result = this.connect.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            ).executeQuery("SELECT * FROM annonce WHERE id = " + id);

            if(result.first()) {
                annonce.setId(result.getLong("id"));
                annonce.setTitle(result.getString("title"));
                annonce.setDescription(result.getString("description"));
                annonce.setAdress(result.getString("adress"));
                annonce.setMail(result.getString("mail"));
                annonce.setDate(result.getTimestamp("date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return annonce;
    }

    @Override
    public Annonce update(Annonce obj) {
        try {
            String query = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";
            PreparedStatement prepare = this.connect.prepareStatement(query);

            prepare.setString(1, obj.getTitle());
            prepare.setString(2, obj.getDescription());
            prepare.setString(3, obj.getAdress());
            prepare.setString(4, obj.getMail());
            prepare.setLong(5, obj.getId());

            prepare.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void delete(Annonce obj) {
        try {
            this.connect.createStatement().executeUpdate("DELETE FROM annonce WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Annonce> findAll() {
        List<Annonce> annonces = new ArrayList<>();
        try {
            ResultSet result = this.connect.createStatement().executeQuery("SELECT * FROM annonce ORDER BY date DESC");
            while (result.next()) {
                Annonce annonce = new Annonce();
                annonce.setId(result.getLong("id"));
                annonce.setTitle(result.getString("title"));
                annonce.setDescription(result.getString("description"));
                annonce.setAdress(result.getString("adress"));
                annonce.setMail(result.getString("mail"));
                annonce.setDate(result.getTimestamp("date"));
                annonces.add(annonce);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return annonces;
    }
}
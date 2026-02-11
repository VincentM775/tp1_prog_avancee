package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private String url = "jdbc:postgresql://localhost:5433/masterannonce";
    private String user = "postgres";
    private String passwd = "root";
    private static Connection connect;

    private ConnectionDB() throws ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getInstance() throws ClassNotFoundException {
        if (connect == null) {
            new ConnectionDB();
        }
        return connect;
    }
}

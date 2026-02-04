<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.Annonce" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Annonces</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h1>Liste des Annonces</h1>

    <div class="nav-menu">
        <a href="index.jsp" class="btn btn-secondary">Accueil</a>
        <a href="add-annonce" class="btn btn-success">Nouvelle Annonce</a>
    </div>

    <%
        List<Annonce> annonces = (List<Annonce>) request.getAttribute("annonces");
        if (annonces == null || annonces.isEmpty()) {
    %>
        <div class="empty-message">
            Aucune annonce disponible pour le moment.<br>
            <a href="add-annonce" class="link">Creer la premiere annonce</a>
        </div>
    <%
        } else {
    %>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Titre</th>
                <th>Description</th>
                <th>Adresse</th>
                <th>Email</th>
                <th>Date</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
        <%
            for (Annonce annonce : annonces) {
        %>
            <tr>
                <td><%= annonce.getId() %></td>
                <td><strong><%= annonce.getTitle() %></strong></td>
                <td><%= annonce.getDescription() %></td>
                <td><%= annonce.getAdress() %></td>
                <td><a href="mailto:<%= annonce.getMail() %>" class="link"><%= annonce.getMail() %></a></td>
                <td><%= annonce.getDate() %></td>
                <td class="actions">
                    <a href="update-annonce?id=<%= annonce.getId() %>" class="edit-link">Modifier</a>
                    <a href="delete-annonce?id=<%= annonce.getId() %>" class="delete-link" onclick="return confirm('Etes-vous sur de vouloir supprimer cette annonce ?');">Supprimer</a>
                </td>
            </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <div class="footer">
        <p>Total : <strong><%= annonces.size() %></strong> annonce(s)</p>
    </div>
    <%
        }
    %>
</div>

</body>
</html>

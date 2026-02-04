<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.Annonce" %>
<!DOCTYPE html>
<html>
<head>
    <title>Modifier une Annonce</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h1>Modifier l'Annonce</h1>

    <div class="nav-menu">
        <a href="index.jsp" class="btn btn-secondary">Accueil</a>
        <a href="list-annonce" class="btn">Voir les Annonces</a>
        <a href="add-annonce" class="btn btn-success">Nouvelle Annonce</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <%
        Annonce annonce = (Annonce) request.getAttribute("annonce");
        if (annonce != null) {
    %>

    <form action="update-annonce" method="post">
        <input type="hidden" name="id" value="<%= annonce.getId() %>">

        <label for="title">Titre :</label>
        <input type="text" id="title" name="title" value="<%= annonce.getTitle() %>" required>

        <label for="description">Description :</label>
        <textarea id="description" name="description" required><%= annonce.getDescription() %></textarea>

        <label for="adress">Adresse :</label>
        <input type="text" id="adress" name="adress" value="<%= annonce.getAdress() %>" required>

        <label for="mail">Email de contact :</label>
        <input type="email" id="mail" name="mail" value="<%= annonce.getMail() %>" required>

        <div style="text-align: center; margin-top: 20px; display: flex; gap: 15px; justify-content: center;">
            <button type="submit" class="btn btn-warning">Mettre a jour</button>
            <a href="list-annonce" class="btn btn-secondary">Annuler</a>
        </div>
    </form>

    <%
        } else {
    %>
        <div class="error-message">
            Annonce introuvable.
        </div>
        <div style="text-align: center; margin-top: 20px;">
            <a href="list-annonce" class="btn">Retour a la liste</a>
        </div>
    <%
        }
    %>
</div>

</body>
</html>

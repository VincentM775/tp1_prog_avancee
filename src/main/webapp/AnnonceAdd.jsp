<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Creer une Annonce</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h1>Nouvelle Annonce</h1>

    <div class="nav-menu">
        <a href="index.jsp" class="btn btn-secondary">Accueil</a>
        <a href="list-annonce" class="btn">Voir les Annonces</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <form action="add-annonce" method="post">
        <label for="title">Titre :</label>
        <input type="text" id="title" name="title" placeholder="Titre de l'annonce..." required>

        <label for="description">Description :</label>
        <textarea id="description" name="description" placeholder="Decrivez votre annonce en detail..." required></textarea>

        <label for="adress">Adresse :</label>
        <input type="text" id="adress" name="adress" placeholder="Adresse complete..." required>

        <label for="mail">Email de contact :</label>
        <input type="email" id="mail" name="mail" placeholder="votre@email.com" required>

        <div style="text-align: center; margin-top: 20px;">
            <button type="submit" class="btn btn-success">Publier l'annonce</button>
        </div>
    </form>
</div>

</body>
</html>

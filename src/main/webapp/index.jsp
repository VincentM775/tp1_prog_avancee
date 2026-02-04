<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>MasterAnnonce - Accueil</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<div class="container">
    <h1>MasterAnnonce</h1>

    <% if (request.getAttribute("monPrenom") != null) { %>
        <div class="welcome-message">
            Bienvenue, <strong><%= request.getAttribute("monPrenom") %></strong> !
        </div>

        <div class="nav-menu">
            <a href="add-annonce" class="btn btn-success">Nouvelle Annonce</a>
            <a href="list-annonce" class="btn">Voir les Annonces</a>
        </div>

        <div class="footer">
            <p>Connecte en tant que : <strong><%= request.getAttribute("monPrenom") %></strong></p>
        </div>

    <% } else { %>
        <form action="hello-servlet" method="post">
            <label for="prenom">Entrez votre prenom pour commencer :</label>
            <input type="text" id="prenom" name="prenom" placeholder="Votre prenom..." required>
            <button type="submit">Connexion</button>
        </form>
    <% } %>
</div>

</body>
</html>

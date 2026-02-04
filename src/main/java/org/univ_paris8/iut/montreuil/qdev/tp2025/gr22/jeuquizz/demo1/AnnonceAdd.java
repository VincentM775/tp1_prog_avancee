package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao.AnnonceDAO;

@WebServlet(name = "AnnonceAdd", value = "/add-annonce")
public class AnnonceAdd extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");

        if (title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            adress == null || adress.trim().isEmpty() ||
            mail == null || mail.trim().isEmpty()) {

            request.setAttribute("error", "Tout les champ sont obligatoire.");
            request.getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
            return;
        }

        Annonce annonce = new Annonce(title.trim(), description.trim(), adress.trim(), mail.trim());
        AnnonceDAO dao = new AnnonceDAO();
        dao.create(annonce);

        response.sendRedirect("list-annonce");
    }
}
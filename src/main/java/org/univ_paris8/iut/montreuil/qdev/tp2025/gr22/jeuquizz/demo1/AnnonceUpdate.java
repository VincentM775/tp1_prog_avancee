package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao.AnnonceDAO;

@WebServlet(name = "AnnonceUpdate", value = "/update-annonce")
public class AnnonceUpdate extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("list-annonce");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            AnnonceDAO dao = new AnnonceDAO();
            Annonce annonce = dao.find(id);

            if (annonce.getId() == 0) {
                response.sendRedirect("list-annonce");
                return;
            }

            request.setAttribute("annonce", annonce);
            request.getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect("list-annonce");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");

        // Validation : tous les champs sont obligatoires
        if (idParam == null || idParam.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            description == null || description.trim().isEmpty() ||
            adress == null || adress.trim().isEmpty() ||
            mail == null || mail.trim().isEmpty()) {

            request.setAttribute("error", "Tout les champs sont obligatoires.");
            doGet(request, response);
            return;
        }

        try {
            long id = Long.parseLong(idParam);

            Annonce annonce = new Annonce(title.trim(), description.trim(), adress.trim(), mail.trim());
            annonce.setId(id);

            AnnonceDAO dao = new AnnonceDAO();
            dao.update(annonce);

            response.sendRedirect("list-annonce");

        } catch (NumberFormatException e) {
            response.sendRedirect("list-annonce");
        }
    }
}

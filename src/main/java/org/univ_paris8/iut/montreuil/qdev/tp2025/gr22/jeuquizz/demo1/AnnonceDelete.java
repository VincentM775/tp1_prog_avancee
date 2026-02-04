package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao.AnnonceDAO;

@WebServlet(name = "AnnonceDelete", value = "/delete-annonce")
public class AnnonceDelete extends HttpServlet {

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

            if (annonce.getId() != 0) {
                dao.delete(annonce);
            }

            response.sendRedirect("list-annonce");

        } catch (NumberFormatException e) {
            response.sendRedirect("list-annonce");
        }
    }
}

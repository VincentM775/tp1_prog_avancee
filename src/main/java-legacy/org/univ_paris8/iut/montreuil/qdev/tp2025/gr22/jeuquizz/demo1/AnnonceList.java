package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao.AnnonceDAO;

@WebServlet(name = "AnnonceList", value = "/list-annonce")
public class AnnonceList extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        AnnonceDAO dao = new AnnonceDAO();
        List<Annonce> annonces = dao.findAll();

        request.setAttribute("annonces", annonces);
        request.getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
    }
}

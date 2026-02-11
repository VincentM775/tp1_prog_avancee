package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/annonces/detail")
public class AnnonceDetailServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() throws ServletException {
        annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Optional<Annonce> annonceOpt = annonceService.trouverParId(id);

            if (annonceOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce non trouvée");
                return;
            }

            Annonce annonce = annonceOpt.get();

            HttpSession session = request.getSession(false);
            boolean isOwner = false;
            if (session != null && session.getAttribute("userId") != null) {
                Long userId = (Long) session.getAttribute("userId");
                isOwner = annonce.getAuthor() != null && annonce.getAuthor().getId().equals(userId);
            }

            request.setAttribute("annonce", annonce);
            request.setAttribute("isOwner", isOwner);

            request.getRequestDispatcher("/WEB-INF/views/annonces/detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }
}

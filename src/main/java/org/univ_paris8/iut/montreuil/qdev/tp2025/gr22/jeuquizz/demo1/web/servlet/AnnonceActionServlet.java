package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.BusinessException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.EntityNotFoundException;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/annonces/action")
public class AnnonceActionServlet extends HttpServlet {

    private AnnonceService annonceService;

    @Override
    public void init() throws ServletException {
        annonceService = new AnnonceService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String action = request.getParameter("action");

        if (idStr == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
            return;
        }

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        Optional<Annonce> annonceOpt = annonceService.trouverParId(id);
        if (annonceOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce non trouvée");
            return;
        }

        Annonce annonce = annonceOpt.get();
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(userId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Action non autorisée");
            return;
        }

        try {
            switch (action.toLowerCase()) {
                case "publish":
                    annonceService.publier(id);
                    setFlashMessage(session, "success", "Annonce publiée avec succès");
                    break;

                case "archive":
                    annonceService.archiver(id);
                    setFlashMessage(session, "success", "Annonce archivée avec succès");
                    break;

                case "delete":
                    annonceService.supprimer(id);
                    setFlashMessage(session, "success", "Annonce supprimée avec succès");
                    response.sendRedirect(request.getContextPath() + "/mes-annonces");
                    return;

                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue : " + action);
                    return;
            }

            response.sendRedirect(request.getContextPath() + "/annonces/detail?id=" + id);

        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (BusinessException e) {
            setFlashMessage(session, "error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/annonces/detail?id=" + id);
        }
    }

    private void setFlashMessage(HttpSession session, String type, String message) {
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }
}

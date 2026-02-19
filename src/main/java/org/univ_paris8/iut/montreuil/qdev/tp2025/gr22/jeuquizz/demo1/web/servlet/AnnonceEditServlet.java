package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormData;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.ValidationResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/annonces/edit")
public class AnnonceEditServlet extends HttpServlet {

    private AnnonceService annonceService;
    private CategoryService categoryService;

    @Override
    public void init() throws ServletException {
        annonceService = new AnnonceService();
        categoryService = new CategoryService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
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

            HttpSession session = request.getSession();
            Long userId = (Long) session.getAttribute("userId");

            if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(userId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'êtes pas autorisé à modifier cette annonce");
                return;
            }

            if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
                request.setAttribute("error", "Impossible de modifier une annonce archivée");
                response.sendRedirect(request.getContextPath() + "/annonces/detail?id=" + id);
                return;
            }

            List<Category> categories = categoryService.listerToutes();
            request.setAttribute("annonce", annonce);
            request.setAttribute("categories", categories);
            request.setAttribute("isEdit", true);

            request.getRequestDispatcher("/WEB-INF/views/annonces/form.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID manquant");
            return;
        }

        Long id = Long.parseLong(idStr);

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        Optional<Annonce> annonceOpt = annonceService.trouverParId(id);
        if (annonceOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Annonce annonce = annonceOpt.get();
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(userId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        String categoryIdStr = request.getParameter("categoryId");

        FormData formData = FormData.fromRequest(request, "title", "description", "adress", "mail", "categoryId");
        request.setAttribute("formData", formData);

        ValidationResult validation = new ValidationResult();

        FormValidator.validateRequired(validation, "title", title, "Le titre est obligatoire");
        if (!validation.hasError("title")) {
            FormValidator.validateMaxLength(validation, "title", title, 64,
                "Le titre ne doit pas dépasser 64 caractères");
        }

        FormValidator.validateMaxLength(validation, "description", description, 256,
            "La description ne doit pas dépasser 256 caractères");

        FormValidator.validateMaxLength(validation, "adress", adress, 64,
            "L'adresse ne doit pas dépasser 64 caractères");

        if (mail != null && !mail.trim().isEmpty()) {
            FormValidator.validateEmail(validation, "mail", mail, "L'adresse email n'est pas valide");
            FormValidator.validateMaxLength(validation, "mail", mail, 64,
                "L'email ne doit pas dépasser 64 caractères");
        }

        if (validation.hasErrors()) {
            request.setAttribute("errors", validation.getErrors());
            forwardWithAnnonceAndCategories(request, response, annonce);
            return;
        }

        Long categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryIdStr);
            } catch (NumberFormatException e) {
            }
        }

        try {
            annonceService.modifier(id, title.trim(),
                description != null ? description.trim() : null,
                adress != null ? adress.trim() : null,
                mail != null ? mail.trim() : null,
                categoryId, userId);
            response.sendRedirect(request.getContextPath() + "/annonces/detail?id=" + id);

        } catch (EntityNotFoundException e) {
            validation.addError("categoryId", e.getMessage());
            request.setAttribute("errors", validation.getErrors());
            forwardWithAnnonceAndCategories(request, response, annonce);
        } catch (BusinessException e) {
            validation.addError("general", e.getMessage());
            request.setAttribute("errors", validation.getErrors());
            forwardWithAnnonceAndCategories(request, response, annonce);
        }
    }

    private void forwardWithAnnonceAndCategories(HttpServletRequest request, HttpServletResponse response, Annonce annonce)
            throws ServletException, IOException {
        List<Category> categories = categoryService.listerToutes();
        request.setAttribute("annonce", annonce);
        request.setAttribute("categories", categories);
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/WEB-INF/views/annonces/form.jsp").forward(request, response);
    }
}

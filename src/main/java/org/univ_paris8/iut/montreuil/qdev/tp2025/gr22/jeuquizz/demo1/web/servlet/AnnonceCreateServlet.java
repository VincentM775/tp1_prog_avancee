package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.CategoryService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormData;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.ValidationResult;

import java.io.IOException;
import java.util.List;

@WebServlet("/annonces/new")
public class AnnonceCreateServlet extends HttpServlet {

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

        List<Category> categories = categoryService.listerToutes();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/annonces/form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

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
            forwardWithCategories(request, response);
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
            Annonce annonce = new Annonce(
                title.trim(),
                description != null ? description.trim() : null,
                adress != null ? adress.trim() : null,
                mail != null ? mail.trim() : null
            );

            Annonce created = annonceService.creer(annonce, userId, categoryId);
            response.sendRedirect(request.getContextPath() + "/annonces/detail?id=" + created.getId());

        } catch (EntityNotFoundException e) {
            validation.addError("categoryId", e.getMessage());
            request.setAttribute("errors", validation.getErrors());
            forwardWithCategories(request, response);
        } catch (Exception e) {
            validation.addError("general", "Erreur lors de la création : " + e.getMessage());
            request.setAttribute("errors", validation.getErrors());
            forwardWithCategories(request, response);
        }
    }

    private void forwardWithCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Category> categories = categoryService.listerToutes();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/WEB-INF/views/annonces/form.jsp").forward(request, response);
    }
}

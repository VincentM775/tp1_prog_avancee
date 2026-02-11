package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.BusinessException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.UserService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormData;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.ValidationResult;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/annonces");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        FormData formData = FormData.fromRequest(request, "username", "email");
        request.setAttribute("formData", formData);

        ValidationResult validation = new ValidationResult();

        FormValidator.validateRequired(validation, "username", username,
            "Le nom d'utilisateur est obligatoire");
        if (!validation.hasError("username")) {
            FormValidator.validateUsername(validation, "username", username,
                "Le nom d'utilisateur doit contenir entre 3 et 50 caractères (lettres, chiffres, _)");
        }

        FormValidator.validateRequired(validation, "email", email,
            "L'email est obligatoire");
        if (!validation.hasError("email")) {
            FormValidator.validateEmail(validation, "email", email,
                "L'adresse email n'est pas valide");
        }

        FormValidator.validateRequired(validation, "password", password,
            "Le mot de passe est obligatoire");
        if (!validation.hasError("password")) {
            FormValidator.validateMinLength(validation, "password", password, 6,
                "Le mot de passe doit contenir au moins 6 caractères");
        }

        FormValidator.validateRequired(validation, "confirmPassword", confirmPassword,
            "Veuillez confirmer le mot de passe");
        if (!validation.hasError("confirmPassword") && !validation.hasError("password")) {
            FormValidator.validateEquals(validation, "confirmPassword", password, confirmPassword,
                "Les mots de passe ne correspondent pas");
        }

        if (validation.hasErrors()) {
            request.setAttribute("errors", validation.getErrors());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.creer(username.trim(), email.trim(), password);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            response.sendRedirect(request.getContextPath() + "/annonces");

        } catch (BusinessException e) {
            if (e.getMessage().contains("utilisateur")) {
                validation.addError("username", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                validation.addError("email", e.getMessage());
            } else {
                validation.addError("general", e.getMessage());
            }
            request.setAttribute("errors", validation.getErrors());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}

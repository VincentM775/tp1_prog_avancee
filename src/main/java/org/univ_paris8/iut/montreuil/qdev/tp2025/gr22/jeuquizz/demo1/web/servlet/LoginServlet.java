package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.UserService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOpt = userService.trouverParUsername(username.trim());

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());

            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            session.removeAttribute("redirectAfterLogin");

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/annonces");
            }
        } else {
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}

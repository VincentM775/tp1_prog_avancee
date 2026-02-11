package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.PagedResult;

import java.io.IOException;

@WebServlet("/mes-annonces")
public class MesAnnoncesServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private AnnonceService annonceService;

    @Override
    public void init() throws ServletException {
        annonceService = new AnnonceService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        int page = parseIntOrDefault(request.getParameter("page"), 0);
        int size = parseIntOrDefault(request.getParameter("size"), DEFAULT_PAGE_SIZE);

        PagedResult<Annonce> result = annonceService.listerParAuteur(userId, page, size);

        request.setAttribute("annonces", result.getContent());
        request.setAttribute("currentPage", result.getPage());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("totalElements", result.getTotalElements());
        request.setAttribute("hasNext", result.hasNext());
        request.setAttribute("hasPrevious", result.hasPrevious());

        request.getRequestDispatcher("/WEB-INF/views/annonces/mes-annonces.jsp").forward(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

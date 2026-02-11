package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.CategoryService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.PagedResult;

import java.io.IOException;
import java.util.List;

@WebServlet("/annonces")
public class AnnonceListServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;

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

        int page = parseIntOrDefault(request.getParameter("page"), 0);
        int size = parseIntOrDefault(request.getParameter("size"), DEFAULT_PAGE_SIZE);

        String keyword = request.getParameter("keyword");
        String categoryIdStr = request.getParameter("category");
        Long categoryId = categoryIdStr != null && !categoryIdStr.isEmpty()
            ? Long.parseLong(categoryIdStr) : null;

        PagedResult<Annonce> result = annonceService.rechercher(
            keyword,
            categoryId,
            AnnonceStatus.PUBLISHED,
            page,
            size
        );

        List<Category> categories = categoryService.listerToutes();

        request.setAttribute("annonces", result.getContent());
        request.setAttribute("currentPage", result.getPage());
        request.setAttribute("totalPages", result.getTotalPages());
        request.setAttribute("totalElements", result.getTotalElements());
        request.setAttribute("hasNext", result.hasNext());
        request.setAttribute("hasPrevious", result.hasPrevious());
        request.setAttribute("categories", categories);
        request.setAttribute("selectedCategory", categoryId);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher("/WEB-INF/views/annonces/list.jsp").forward(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

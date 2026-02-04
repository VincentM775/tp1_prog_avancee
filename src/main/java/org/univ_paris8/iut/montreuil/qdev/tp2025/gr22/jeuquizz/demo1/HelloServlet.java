package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import java.io.*;
import jakarta.servlet.ServletException; // Import nécessaire pour le forward
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String prenomRecu = request.getParameter("prenom");

        request.setAttribute("monPrenom", prenomRecu);

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    public void destroy() {
    }
}
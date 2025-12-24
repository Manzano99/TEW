package es.tew;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CarritoVista")
public class CarritoCompraVista extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException, ServletException {
        resp.sendRedirect(req.getContextPath() + "/CarritoEncadenado");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession sesion = req.getSession(true);

        @SuppressWarnings("unchecked")
        Map<String, Integer> carrito = (Map<String, Integer>) sesion.getAttribute("carrito");
        if (carrito == null) {
            carrito = new HashMap<>();
            sesion.setAttribute("carrito", carrito);
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Carrito de la compra</title></head><body>");
        out.println("<h1>Carrito de la compra</h1>");

        out.println("<form method='post' action='" + req.getContextPath() + "/CarritoEncadenado'>");
        out.println("<select name='productos' size='1'>");
        out.println("<option value='010'>La trampa</option>");
        out.println("<option value='345'>Los pájaros</option>");
        out.println("<option value='554'>Matrix reloaded</option>");
        out.println("<option value='111'>A</option>");
        out.println("<option value='222'>B</option>");
        out.println("<option value='333'>C</option>");
        out.println("<option value='444'>D</option>");
        out.println("<option value='555'>E</option>");
        out.println("<option value='666'>F</option>");
        out.println("<option value='777'>G</option>");
        out.println("</select>");
        out.println("<input type='submit' value='Añadir al carrito'>");
        out.println("</form>");

        out.println("<h2>Estado actual del carrito:</h2>");
        if (carrito.isEmpty()) {
            out.println("<p>Tu carrito está vacío.</p>");
        } else {
            out.println("<ul>");
            for (Map.Entry<String, Integer> entry : carrito.entrySet()) {
                out.println("<li>Producto " + entry.getKey() + " → " + entry.getValue() + " unidad(es)</li>");
            }
            out.println("</ul>");
        }

        out.println("</body></html>");
    }
}
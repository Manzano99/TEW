package es.tew;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

@WebServlet("/CarritoEncadenado")
public class CarritoCompraEncadenado extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession sesion = req.getSession(true);

        @SuppressWarnings("unchecked")
        Map<String, Integer> carrito = (Map<String, Integer>) sesion.getAttribute("carrito");
        if (carrito == null) {
            carrito = new HashMap<>();
            sesion.setAttribute("carrito", carrito);
        }

        String productoId = req.getParameter("productos");
        if (productoId != null) {
            carrito.put(productoId, carrito.getOrDefault(productoId, 0) + 1);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/CarritoVista");
        rd.forward(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        RequestDispatcher rd = req.getRequestDispatcher("/CarritoVista");
        rd.forward(req, resp);
    }
}

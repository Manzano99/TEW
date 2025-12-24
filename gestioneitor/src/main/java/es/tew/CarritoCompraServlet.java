package es.tew;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.RequestDispatcher;

@WebServlet("/CarritoCompra")
public class CarritoCompraServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession sesion = req.getSession(true);

        CarritoBean carrito = (CarritoBean) sesion.getAttribute("carrito");
        if (carrito == null) {
            carrito = new CarritoBean();
            sesion.setAttribute("carrito", carrito);
        }

        String producto = req.getParameter("productos");
        if (producto != null) {
            carrito.addProducto(producto);
        }

        RequestDispatcher dispatcher =
                getServletContext().getRequestDispatcher("/carrito.jsp");
        dispatcher.forward(req, resp);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doPost(req, resp);
    }
}
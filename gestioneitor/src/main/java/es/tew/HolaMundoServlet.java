package es.tew;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/HolaMundo")

public class HolaMundoServlet extends HttpServlet{
    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException,
    ServletException {
        String nombre = (String) req.getParameter("NombreUsuario");

        @SuppressWarnings("unchecked") // Quitamos el warning de cast
        Vector<String> listado = (Vector<String>)
        req.getSession().getAttribute("listado");

        if (listado == null){
            listado = new Vector<String>();
        }

        if (nombre != null) {
            listado.addElement(nombre);
        }

        req.getSession().setAttribute("listado",listado);

        Integer contador= (Integer) getServletContext().getAttribute("contador");
        if ( contador == null ){
            contador = 0;
        }
        contador++;
        getServletContext().setAttribute("contador", contador);

        RequestDispatcher dispatcher =
            getServletContext().getNamedDispatcher("HolaMundoVista");
        dispatcher.forward(req, resp);
    }
}

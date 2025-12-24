package es.tew;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "HolaMundoVista", urlPatterns = {"/HolaMundoVista"})
public class HolaMundoVistaServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException, ServletException {
    
        String nombre = req.getParameter("NombreUsuario");

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>Hola Mundo!</TITLE></HEAD>");
        out.println("<BODY>");

        @SuppressWarnings("unchecked")
        Vector<String> listado = (Vector<String>) req.getSession().getAttribute("listado");

        if ( nombre != null ){
            out.println("<br>Hola "+nombre+"<br>");
        }

        Integer contador = (Integer) getServletContext().getAttribute("contador");

        out.println("Bienvenido a mi primera página web!");
        out.println("<br>");
        out.println("Contigo, hoy me han visitado:<br>");
        for ( int i = 0 ; i < listado.size() ; i++ ){
        out.println("<br>"+(String)listado.elementAt(i));
        }
        out.println("<br><br>" + contador +" visitas");
        out.println("</BODY></HTML>");
    }
}

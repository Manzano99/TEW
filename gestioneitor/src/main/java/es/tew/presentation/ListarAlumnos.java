package es.tew.presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import es.tew.business.AlumnoService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Alumno;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ListarAlumnos")
public class ListarAlumnos extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AlumnoService service = ServiceFactory.getAlumnoService();
        List<Alumno> alumnos = service.getAlumnos(); 

        resp.setCharacterEncoding("UTF-8"); 
        resp.setContentType("text/html");  
        PrintWriter out = resp.getWriter();  
        out.println("<html>");  
        out.println("<head><title>Listado de alumnos</title></head>");  
        out.println("<body><table>");
        out.println("<thead><tr><th>id</th><th>idUser</th><th>Email</th><th>Nombre</th><th>Apellidos</th></tr></thead>");  
        
        for (Alumno alumno : alumnos) {
            out.println("<tr><td>" + alumno.getId() + "</td><td>" + alumno.getIdUser() + "</td><td>" + alumno.getEmail() + "</td><td>" + alumno.getNombre() + "</td><td>" + alumno.getApellidos() + "</td></tr>");
        }
        
        out.println("</table></body></html>"); 
    }
}

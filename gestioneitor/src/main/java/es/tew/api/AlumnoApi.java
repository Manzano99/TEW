package es.tew.api;

import es.tew.business.AlumnoService;
import es.tew.infrastructure.GestorSesion;
import es.tew.infrastructure.ServiceFactory;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/alumno")
public class AlumnoApi {
    
    private final AlumnoService service = ServiceFactory.getAlumnoService();

    @GET
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlumnos(@PathParam("token") String token) {
        if (GestorSesion.getInstance().comprobarToken(token) != null) {
            return Response.ok(service.getAlumnos()).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAlumno(AlumnoRequestData alumno) {
        if (GestorSesion.getInstance().comprobarToken(alumno.getToken()) != null) {
            try {
                service.saveAlumno(alumno); 
                return Response.ok().build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAlumno(AlumnoRequestData alumno) {
        if (GestorSesion.getInstance().comprobarToken(alumno.getToken()) != null) {
            try {
                service.updateAlumno(alumno);
                return Response.ok().build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @DELETE
    @Path("/{id}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAlumno(@PathParam("id") int id, @PathParam("token") String token) {
        if (GestorSesion.getInstance().comprobarToken(token) != null) {
            try {
                service.deleteAlumno(id);
                return Response.ok().build();
            } catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
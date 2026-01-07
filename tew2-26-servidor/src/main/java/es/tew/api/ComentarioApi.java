package es.tew.api;

import es.tew.business.ComentarioService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Comentario;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/comentarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComentarioApi {

    private ComentarioService comentarioService = new ServiceFactory().getComentarioService();

    @GET
    @Path("/incidencia/{id}")
    public Response getComentariosPorIncidencia(@PathParam("id") int idIncidencia) {
        List<Comentario> comentarios = comentarioService.getComentarios(idIncidencia);
        return Response.ok(comentarios).build();
    }

    @POST
    public Response addComentario(Comentario comentario) {
        try {
            comentarioService.addComentario(
                comentario.getIncidencia(),
                comentario.getAutor(),
                comentario.getMensaje()
            );
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error al guardar comentario").build();
        }
    }
}
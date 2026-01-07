package es.tew.api;

import es.tew.business.HistorialEstadoService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.HistorialEstado;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/historial")
@Produces(MediaType.APPLICATION_JSON)
public class HistorialEstadoApi {
    private HistorialEstadoService historialService = new ServiceFactory().getHistorialEstadoService();

    @GET
    @Path("/incidencia/{id}")
    public Response getHistorial(@PathParam("id") int idIncidencia) {
        try {
            List<HistorialEstado> historial = historialService.findByIncidencia(idIncidencia);
            return Response.ok(historial).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error al obtener el historial").build();
        }
    }
}
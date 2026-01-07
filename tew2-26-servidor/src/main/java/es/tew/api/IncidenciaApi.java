package es.tew.api;

import es.tew.business.IncidenciaService;
import es.tew.dto.EstadisticasDTO;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Incidencia;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/incidencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidenciaApi {

    private IncidenciaService incidenciaService = new ServiceFactory().getIncidenciaService();

    @GET
    public Response getIncidencias(@QueryParam("rol") String rol, 
                                   @QueryParam("dni") String dni) {
        try {
            List<Incidencia> lista;
            if ("usuario".equalsIgnoreCase(rol)) {
                lista = incidenciaService.getIncidenciasUsuario(dni);
            } else if ("tecnico".equalsIgnoreCase(rol)) {
                lista = incidenciaService.getIncidenciasTecnico(dni);
            } else {
                lista = incidenciaService.getTodasLasIncidencias(null, null);
            }
            return Response.ok(lista).build();
        } catch (Exception e) {
            return Response.serverError().entity("Error al obtener incidencias").build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getDetalle(@PathParam("id") int id) {
        Incidencia inc = incidenciaService.getDetalleIncidencia(id);
        if (inc != null) {
            return Response.ok(inc).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response crearIncidencia(Incidencia incidencia) {
        try {
            incidenciaService.registrarIncidencia(incidencia, incidencia.getSolicitante());
            return Response.status(Response.Status.CREATED).entity("Incidencia creada").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, String nuevoEstado) {
        try {
            String estadoLimpio = nuevoEstado.replace("\"", "");
            incidenciaService.cambiarEstado(id, estadoLimpio);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/estadisticas")
    public Response getEstadisticas() {
        EstadisticasDTO stats = incidenciaService.getEstadisticas();
        return Response.ok(stats).build();
    }

    @GET
    @Path("/sistema/backup")
    public Response exportarBackup() {
        try {
            Map<String, Object> data = incidenciaService.exportarDatosSistema();
            
            return Response.ok(data)
                    .header("Content-Disposition", "attachment; filename=backup_tew.json")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error al exportar la copia de seguridad.").build();
        }
    }

    @POST
    @Path("/sistema/backup")
    public Response importarBackup(Map<String, Object> datos) {
        try {
            incidenciaService.importarDatosSistema(datos);
            return Response.ok().entity("Importación completada correctamente.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error en la importación: " + e.getMessage()).build();
        }
    }
}
package es.tew.api;

import es.tew.business.CategoriaService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Categoria;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaApi {

    private CategoriaService service = new ServiceFactory().getCategoriaService();

    @GET
    public Response getCategorias() {
        List<Categoria> lista = service.getCategorias();
        return Response.ok(lista).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCategoria(Categoria categoria) {
        try {
            service.createCategoria(categoria);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
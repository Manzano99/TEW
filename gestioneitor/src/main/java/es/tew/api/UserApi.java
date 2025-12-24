package es.tew.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import es.tew.infrastructure.GestorSesion;
import es.tew.model.User;
import es.tew.infrastructure.ServiceFactory;

@Path("/user")
public class UserApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/login")
    public Response login(User user) {
        if (ServiceFactory.getUserService().verify(user).isPresent()) {
            return Response.ok(GestorSesion.getInstance().registrarLogin(user.getUsername())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
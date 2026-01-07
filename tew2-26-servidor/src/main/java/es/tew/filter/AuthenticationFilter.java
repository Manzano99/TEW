package es.tew.filter;

import es.tew.infrastructure.GestorSesion;
import es.tew.model.Usuario;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        if (path.endsWith("usuarios/login") || 
            path.endsWith("usuarios/health") || 
            method.equalsIgnoreCase("OPTIONS")) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortarPeticion(requestContext, "Token no proporcionado o formato incorrecto");
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        Usuario usuario = GestorSesion.getInstance().getUsuarioByToken(token);

        if (usuario == null) {
            abortarPeticion(requestContext, "Token inválido o sesión expirada");
        }
    }

    private void abortarPeticion(ContainerRequestContext context, String mensaje) {
        context.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + mensaje + "\"}")
                    .type("application/json")
                    .build()
        );
    }
}
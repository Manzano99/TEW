package es.tew.api;

import es.tew.business.UsuarioService;
import es.tew.infrastructure.GestorSesion;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Usuario;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioApi {

    private UsuarioService usuarioService = new ServiceFactory().getUsuarioService();

    @POST
    @Path("/login")
    public Response login(Usuario credentials) {
        try {
            Usuario usuario = usuarioService.login(credentials.getDni(), credentials.getPasswd());
    
            if (usuario == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Credenciales invalidas").build();
            }
    
            String token = GestorSesion.getInstance().registrarLogin(usuario);
            
            usuario.setPasswd(null);
    
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("usuario", usuario);
    
            return Response.ok(body).build();
    
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    public Response crearUsuario(Usuario nuevoUsuario) {
        try {
            if (nuevoUsuario == null || nuevoUsuario.getDni() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("Datos incompletos")
                               .build();
            }

            Usuario usuarioCreado = usuarioService.altaUsuario(nuevoUsuario);

            return Response.status(Response.Status.CREATED)
                           .entity(usuarioCreado)
                           .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("Error al crear usuario")
                           .build();
        }
    }

    @GET
    public Response getUsuarios() {
        List<Usuario> usuarios = usuarioService.getUsuarios();
        usuarios.forEach(u -> u.setPasswd(null));
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/tecnicos")
    public Response getTecnicos() {
        List<Usuario> tecnicos = usuarioService.getTecnicos();
        tecnicos.forEach(u -> u.setPasswd(null));
        return Response.ok(tecnicos).build();
    }

    @GET
    @Path("/health")
    public Response checkSystemHealth() {
        Map<String, Object> status = usuarioService.getSystemHealthCheck();
        if ("DOWN".equals(status.get("databaseStatus"))) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                           .entity(status)
                           .build();
        }
        return Response.ok(status).build();
    }
}
package es.tew.business;

import es.tew.model.Usuario;
import java.util.List;
import java.util.Map;

public interface UsuarioService {

    // Valida las credenciales de un usuario.
    Usuario login(String dni, String password);

    // Obtiene un listado de todos los usuarios.
    List<Usuario> getUsuarios();

    // Da de alta un nuevo usuario.
    Usuario altaUsuario(Usuario usuario) throws Exception;

    List<Usuario> getTecnicos();
    
    Usuario findByDni(String dni);

    Map<String, Object> getSystemHealthCheck();
}
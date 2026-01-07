package es.tew.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import es.tew.model.Usuario;

public class GestorSesion {

    private static GestorSesion instance;
    private Map<String, Usuario> logins = new HashMap<>();

    private GestorSesion() {
    }

    public static synchronized GestorSesion getInstance() {
        if (instance == null) {
            instance = new GestorSesion();
        }
        return instance;
    }

    public String registrarLogin(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        logins.put(token, usuario);
        return token;
    }

    public Usuario getUsuarioByToken(String token) {
        return logins.getOrDefault(token, null);
    }

    public void logout(String token) {
        logins.remove(token);
    }
}
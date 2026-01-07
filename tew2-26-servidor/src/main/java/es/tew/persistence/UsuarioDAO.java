package es.tew.persistence;

import es.tew.model.Usuario;
import java.sql.Connection;
import java.util.List;

public interface UsuarioDAO {
    Usuario findByDni(String dni);
    Usuario findByDni(Connection conn, String dni); 
    Usuario findByCredentials(String dni, String password);
    List<Usuario> findAll();
    List<Usuario> findByRol(String rol);

    void insert(Connection conn, Usuario usuario);
    void update(Connection conn, Usuario usuario);
    void delete(Connection conn, String dni);
}
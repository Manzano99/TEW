package es.tew.persistence;

import es.tew.model.Categoria;
import java.sql.Connection;
import java.util.List;

public interface CategoriaDAO {
    List<Categoria> findAll();
    Categoria findByNombre(String nombre);
    Categoria findByNombre(Connection conn, String nombre);
    void insert(Connection conn, Categoria categoria);
}

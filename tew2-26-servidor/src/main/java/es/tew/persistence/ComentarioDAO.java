package es.tew.persistence;

import es.tew.model.Comentario;
import java.sql.Connection;
import java.util.List;

public interface ComentarioDAO {
    List<Comentario> findByIncidencia(int idIncidencia);
    List<Comentario> findByIncidencia(Connection conn, int idIncidencia);
    void insert(Connection conn, Comentario comentario);
}
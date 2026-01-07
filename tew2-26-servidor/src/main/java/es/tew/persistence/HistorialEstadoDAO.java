package es.tew.persistence;

import es.tew.model.HistorialEstado;
import java.sql.Connection;
import java.util.List;

public interface HistorialEstadoDAO {
    List<HistorialEstado> findByIncidencia(int idIncidencia);
    List<HistorialEstado> findByIncidencia(Connection conn, int idIncidencia); 
    void insert(Connection conn, HistorialEstado historial);
}
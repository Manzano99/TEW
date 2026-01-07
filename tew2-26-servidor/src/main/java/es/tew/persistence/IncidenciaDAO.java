package es.tew.persistence;

import es.tew.model.Incidencia;
import java.sql.Connection;
import java.util.List;

public interface IncidenciaDAO {
    Incidencia findById(int id);
    Incidencia findById(Connection conn, int id);
    List<Incidencia> findAll();
    List<Incidencia> findBySolicitante(String dniSolicitante);
    List<Incidencia> findByTecnico(String dniTecnico);

    int getCountByTecnico(String dniTecnico);
    void updateTecnico(Connection conn, int idIncidencia, String nuevoTecnicoDni);

    int insert(Connection conn, Incidencia incidencia);
    void update(Connection conn, Incidencia incidencia);
    void delete(Connection conn, int id);
}
package es.tew.business;

import es.tew.model.HistorialEstado;
import java.util.List;

public interface HistorialEstadoService {
    List<HistorialEstado> findByIncidencia(int idIncidencia);
}
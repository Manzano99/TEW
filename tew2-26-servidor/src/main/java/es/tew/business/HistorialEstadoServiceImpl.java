package es.tew.business;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.HistorialEstado;
import java.util.List;

public class HistorialEstadoServiceImpl implements HistorialEstadoService {
    
    private PersistenceFactory factory = new PersistenceFactory();

    @Override
    public List<HistorialEstado> findByIncidencia(int idIncidencia) {
        return factory.getHistorialEstadoDAO().findByIncidencia(idIncidencia);
    }
}
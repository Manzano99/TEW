package es.tew.infrastructure;

import es.tew.persistence.ComentarioDAO;
import es.tew.persistence.ComentarioDAOImpl;
import es.tew.persistence.HistorialEstadoDAO;
import es.tew.persistence.HistorialEstadoDAOImpl;
import es.tew.persistence.IncidenciaDAO;
import es.tew.persistence.IncidenciaDAOImpl;
import es.tew.persistence.UsuarioDAO;
import es.tew.persistence.UsuarioDAOImpl;
import es.tew.persistence.CategoriaDAO;
import es.tew.persistence.CategoriaDAOImpl;

public class PersistenceFactory {

    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOImpl();
    }

    public IncidenciaDAO getIncidenciaDAO() {
        return new IncidenciaDAOImpl();
    }

    public ComentarioDAO getComentarioDAO() {
        return new ComentarioDAOImpl();
    }
    
    public HistorialEstadoDAO getHistorialEstadoDAO() {
        return new HistorialEstadoDAOImpl();
    }

    public CategoriaDAO getCategoriaDAO() {
        return new CategoriaDAOImpl();
    }
}
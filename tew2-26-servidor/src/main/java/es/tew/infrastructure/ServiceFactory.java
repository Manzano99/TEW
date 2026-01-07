package es.tew.infrastructure;

import es.tew.business.ComentarioService;
import es.tew.business.ComentarioServiceImpl;
import es.tew.business.HistorialEstadoService;
import es.tew.business.HistorialEstadoServiceImpl;
import es.tew.business.IncidenciaService;
import es.tew.business.IncidenciaServiceImpl;
import es.tew.business.UsuarioService;
import es.tew.business.UsuarioServiceImpl;
import es.tew.business.CategoriaService;
import es.tew.business.CategoriaServiceImpl;

public class ServiceFactory {

    public UsuarioService getUsuarioService() {
        return new UsuarioServiceImpl();
    }

    public IncidenciaService getIncidenciaService() {
        return new IncidenciaServiceImpl();
    }

    public ComentarioService getComentarioService() {
        return new ComentarioServiceImpl();
    }

    public HistorialEstadoService getHistorialEstadoService() {
        return new HistorialEstadoServiceImpl();
    }

    public CategoriaService getCategoriaService() {
        return new CategoriaServiceImpl();
    }
}
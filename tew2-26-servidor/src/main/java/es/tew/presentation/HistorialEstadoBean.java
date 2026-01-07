package es.tew.presentation;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import es.tew.model.HistorialEstado;
import es.tew.model.Incidencia;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("historialEstadoBean")
@RequestScoped
public class HistorialEstadoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private IncidenciaBean incidenciaBean;

    public HistorialEstadoBean() {
        // No necesita servicio propio, lee del bean de sesión
    }

    public List<HistorialEstado> getListado() {
        Incidencia seleccionada = incidenciaBean.getIncidenciaSeleccionada();
        
        if (seleccionada != null && seleccionada.getHistorial() != null) {
            return seleccionada.getHistorial();
        }
        
        return Collections.emptyList();
    }
}
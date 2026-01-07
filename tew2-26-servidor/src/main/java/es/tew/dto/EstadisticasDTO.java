package es.tew.dto;

import java.util.Map;
import es.tew.model.Incidencia;

public class EstadisticasDTO {
    private Map<String, Long> recuentoPorEstado;
    private Map<String, Long> recuentoPorCategoria;
    private double tiempoMedioResolucionHoras;
    private Incidencia incidenciaMasAntigua;

    // Getters y Setters
    public Map<String, Long> getRecuentoPorEstado() {
        return recuentoPorEstado;
    }
    public void setRecuentoPorEstado(Map<String, Long> recuentoPorEstado) {
        this.recuentoPorEstado = recuentoPorEstado;
    }
    public Map<String, Long> getRecuentoPorCategoria() {
        return recuentoPorCategoria;
    }
    public void setRecuentoPorCategoria(Map<String, Long> recuentoPorCategoria) {
        this.recuentoPorCategoria = recuentoPorCategoria;
    }
    public double getTiempoMedioResolucionHoras() {
        return tiempoMedioResolucionHoras;
    }
    public void setTiempoMedioResolucionHoras(double tiempoMedioResolucionHoras) {
        this.tiempoMedioResolucionHoras = tiempoMedioResolucionHoras;
    }
    public Incidencia getIncidenciaMasAntigua() {
        return incidenciaMasAntigua;
    }
    public void setIncidenciaMasAntigua(Incidencia incidenciaMasAntigua) {
        this.incidenciaMasAntigua = incidenciaMasAntigua;
    }
}
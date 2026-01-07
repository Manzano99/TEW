package es.tew.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// --- IMPORTACIONES AÑADIDAS ---
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class HistorialEstado implements Serializable {

    private static final long serialVersionUID = 3L; // Buena práctica

    private int id;
    private int incidencia;

    // --- CAMBIO: Marcar como 'transient' ---
    private transient LocalDateTime fecha;
    
    private String estadoAnterior;
    private String estadoNuevo;

    public HistorialEstado() {}

    public HistorialEstado(int id, int incidencia, LocalDateTime fecha,
                           String estadoAnterior, String estadoNuevo) {
        this.id = id;
        this.incidencia = incidencia;
        this.fecha = fecha;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }

    // --- Getters y Setters (sin cambios) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIncidencia() { return incidencia; }
    public void setIncidencia(int incidencia) { this.incidencia = incidencia; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public String getFechaFormateada() {
        if (fecha == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }
    
    // --- MÉTODOS DE SERIALIZACIÓN MANUAL AÑADIDOS ---

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(fecha != null ? fecha.toString() : null);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        String fechaStr = (String) ois.readObject();
        this.fecha = fechaStr != null ? LocalDateTime.parse(fechaStr) : null;
    }

    @Override
    public String toString() {
        return "HistorialEstado{" +
                "id=" + id +
                ", incidencia=" + incidencia +
                ", estadoAnterior='" + estadoAnterior + '\'' +
                ", estadoNuevo='" + estadoNuevo + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
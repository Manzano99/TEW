package es.tew.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// --- IMPORTACIONES AÑADIDAS ---
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class Comentario implements Serializable {

    private static final long serialVersionUID = 2L; // Buena práctica

    private int id;
    private int incidencia; 
    private String autor; 
    
    // --- CAMBIO: Marcar como 'transient' ---
    private transient LocalDateTime fecha;
    
    private String mensaje;

    public Comentario() {}

    public Comentario(int id, int incidencia, String autor, LocalDateTime fecha, String mensaje) {
        this.id = id;
        this.incidencia = incidencia;
        this.autor = autor;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    // --- Getters y Setters (sin cambios) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIncidencia() { return incidencia; }
    public void setIncidencia(int incidencia) { this.incidencia = incidencia; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

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
        return "Comentario{" +
                "id=" + id +
                ", incidencia=" + incidencia +
                ", autor='" + autor + '\'' +
                ", fecha=" + fecha +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
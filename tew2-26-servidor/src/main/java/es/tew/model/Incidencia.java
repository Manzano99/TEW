package es.tew.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// --- IMPORTACIONES AÑADIDAS ---
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class Incidencia implements Serializable {

    private static final long serialVersionUID = 1L; // Buena práctica

    private int id;
    private String titulo;
    private String descripcion;
    
    // --- CAMBIO: Marcar como 'transient' ---
    private transient LocalDateTime fechaCreacion;
    private transient LocalDateTime fechaUltimaModificacion;
    
    private String categoria;
    private String solicitante;
    private String tecnico;
    private String estadoActual;

    // Asegúrate de que Comentario e HistorialEstado también implementen esta lógica
    private List<Comentario> comentarios = new ArrayList<>();
    private List<HistorialEstado> historial = new ArrayList<>();

    public Incidencia() {}

    // --- Getters y Setters (sin cambios) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaUltimaModificacion() { return fechaUltimaModificacion; }
    public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }
    public String getTecnico() { return tecnico; }
    public void setTecnico(String tecnico) { this.tecnico = tecnico; }
    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }
    public List<Comentario> getComentarios() { return comentarios; }
    public void setComentarios(List<Comentario> comentarios) { this.comentarios = comentarios; }
    public List<HistorialEstado> getHistorial() { return historial; }
    public void setHistorial(List<HistorialEstado> historial) { this.historial = historial; }

    // --- Métodos de formato (sin cambios) ---
    public String getFechaCreacionFormateada() {
        if (fechaCreacion == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formatter);
    }
    public String getFechaUltimaModificacionFormateada() {
        if (fechaUltimaModificacion == null) return "-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaUltimaModificacion.format(formatter);
    }

    // --- MÉTODOS DE SERIALIZACIÓN MANUAL AÑADIDOS ---

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject(); // Guarda todos los campos no-transient
        // Escribimos manualmente los campos transient como String
        oos.writeObject(fechaCreacion != null ? fechaCreacion.toString() : null);
        oos.writeObject(fechaUltimaModificacion != null ? fechaUltimaModificacion.toString() : null);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject(); // Lee todos los campos no-transient
        // Leemos manualmente los campos transient desde String
        String fechaCreacionStr = (String) ois.readObject();
        this.fechaCreacion = fechaCreacionStr != null ? LocalDateTime.parse(fechaCreacionStr) : null;
        
        String fechaUltimaModStr = (String) ois.readObject();
        this.fechaUltimaModificacion = fechaUltimaModStr != null ? LocalDateTime.parse(fechaUltimaModStr) : null;
    }
    
    @Override
    public String toString() {
        return "Incidencia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", solicitante='" + solicitante + '\'' +
                ", tecnico='" + tecnico + '\'' +
                ", estadoActual='" + estadoActual + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaUltimaModificacion=" + fechaUltimaModificacion +
                '}';
    }
}
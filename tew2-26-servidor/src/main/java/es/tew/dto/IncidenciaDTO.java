package es.tew.dto;

import java.time.LocalDateTime;
import java.io.Serializable;

public class IncidenciaDTO implements Serializable {
    private int id;
    private String titulo;
    private String categoria;
    private LocalDateTime fechaCreacion;
    private String nombreSolicitante;
    private String nombreTecnico;

    public IncidenciaDTO() {}

    public IncidenciaDTO(int id, String titulo, String categoria, LocalDateTime fechaCreacion,
                         String nombreSolicitante, String nombreTecnico) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.fechaCreacion = fechaCreacion;
        this.nombreSolicitante = nombreSolicitante;
        this.nombreTecnico = nombreTecnico;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombreSolicitante() { return nombreSolicitante; }
    public void setNombreSolicitante(String nombreSolicitante) { this.nombreSolicitante = nombreSolicitante; }

    public String getNombreTecnico() { return nombreTecnico; }
    public void setNombreTecnico(String nombreTecnico) { this.nombreTecnico = nombreTecnico; }
}

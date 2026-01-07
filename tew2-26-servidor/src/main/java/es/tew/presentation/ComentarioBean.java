package es.tew.presentation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import es.tew.business.IncidenciaService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Comentario;
import es.tew.model.Incidencia;
import es.tew.model.Usuario;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("comentarioBean")
@RequestScoped
public class ComentarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient IncidenciaService incidenciaService;

    @Inject
    private UsuarioBean usuarioBean;

    @Inject
    private IncidenciaBean incidenciaBean;

    private String nuevoComentario;

    public ComentarioBean() {
        this.incidenciaService = new ServiceFactory().getIncidenciaService();
    }

    private IncidenciaService getIncidenciaService() {
        if (this.incidenciaService == null) {
            this.incidenciaService = new ServiceFactory().getIncidenciaService();
        }
        return this.incidenciaService;
    }

    // --- Getters y Setters ---
    public String getNuevoComentario() { return nuevoComentario; }
    public void setNuevoComentario(String nuevoComentario) { this.nuevoComentario = nuevoComentario; }

    public String irAAnadirComentario() {
        this.nuevoComentario = ""; 
        return "irAComentar"; 
    }

    public String guardarComentario() {
        try {
            Usuario autor = usuarioBean.getUsuarioActual();
            Incidencia incidencia = incidenciaBean.getIncidenciaSeleccionada();

            if (autor == null) {
                return "error";
            }
            if (incidencia == null) {
                return "error";
            }
            if (nuevoComentario == null || nuevoComentario.trim().isEmpty()) {
                return null;
            }

            // 1. Guardamos en la Base de Datos
            getIncidenciaService().addComentario(
                incidencia.getId(),
                autor.getDni(), 
                nuevoComentario
            );
            
            // 2. Actualizamos el objeto en memoria
            
            Comentario comentarioLocal = new Comentario();
            comentarioLocal.setAutor(autor.getDni()); 
            comentarioLocal.setMensaje(nuevoComentario);
            comentarioLocal.setFecha(LocalDateTime.now());
            comentarioLocal.setIncidencia(incidencia.getId());
            
            if (incidencia.getComentarios() == null) {
                incidencia.setComentarios(new ArrayList<>());
            }
            
            incidencia.getComentarios().add(0, comentarioLocal);

            // 3. Limpiamos
            this.nuevoComentario = "";
            
            // 4. Navegamos de vuelta
            return "detalleGuardado"; 

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar el comentario."));
            return null; 
        }
    }

    public boolean isComentarioAllowed() {
        Usuario usuario = usuarioBean.getUsuarioActual();
        Incidencia incidencia = incidenciaBean.getIncidenciaSeleccionada();

        if (incidencia == null || usuario == null) {
            return false;
        }
        
        String rol = usuario.getRol().toLowerCase();
        boolean esRolValido = rol.equals("usuario") || rol.equals("tecnico");
        boolean estaAbierta = !incidencia.getEstadoActual().equalsIgnoreCase("Cerrada");
        
        return esRolValido && estaAbierta;
    }
}

package es.tew.presentation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Serializable;
import java.util.List;

import es.tew.business.AlumnoService;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.Alumno;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.inject.Named;

@Named("beanAlumnos")
@SessionScoped
public class BeanAlumnos implements Serializable {

    private static final long serialVersionUID = 1L;

    private final AlumnoService alumnoService = ServiceFactory.getAlumnoService();
    private List<Alumno> alumnos;
    private Alumno alumno;

    public void iniciaAlumno() {
        alumno = new Alumno();
    }

    public BeanAlumnos() {
        this.alumno = new Alumno();
        this.alumnos = alumnoService.getAlumnos();
    }

    public List<Alumno> getAlumnos() { return alumnos; }
    public void setAlumnos(List<Alumno> alumnos) { this.alumnos = alumnos; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public String listado() {
        try {
            alumnos = alumnoService.getAlumnos();
            return "exito";
        } catch (Exception e) {
            putErrorInFlash("listado", e);
            return "error";
        }
    }

    public String salva() {
        try {
            if (alumno.getId() == -1) {
                alumnoService.saveAlumno(alumno);
            } else {
                alumnoService.updateAlumno(alumno);
            }
            alumnos = alumnoService.getAlumnos();
            return null;
        } catch (Exception e) {
            String detail = String.format("Vista: %s, Clase: %s, Método: %s, Excepción: %s, Mensaje: %s",
                    getViewId(),
                    this.getClass().getName(),
                    "salva",
                    e.getClass().getName(),
                    (e.getMessage() != null) ? e.getMessage() : "N/A");

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Error al Salvar", // Short title
                                                detail);           // Long detail
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    public String baja() {
        try {
            alumnoService.deleteAlumno(alumno.getId());
            alumnos = alumnoService.getAlumnos();
            return null;
        } catch (Exception e) {
             String detail = String.format("Vista: %s, Clase: %s, Método: %s, Excepción: %s, Mensaje: %s",
                    getViewId(),
                    this.getClass().getName(),
                    "baja",
                    e.getClass().getName(),
                    (e.getMessage() != null) ? e.getMessage() : "N/A");

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                "Error al dar de Baja",
                                                detail);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    private String getViewId() {
         FacesContext fc = FacesContext.getCurrentInstance();
         return (fc.getViewRoot() != null) ? fc.getViewRoot().getViewId() : "(desconocido)";
    }

    private void putErrorInFlash(String method, Exception e) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Flash flash = ec.getFlash();
        String viewId = (fc.getViewRoot() != null) ? fc.getViewRoot().getViewId() : "(desconocido)";
        String beanClass = this.getClass().getName();
        String exceptionType = e.getClass().getName();
        String message = e.getMessage();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stacktrace = sw.toString();
        flash.put("viewId", viewId);
        flash.put("beanClass", beanClass);
        flash.put("method", method);
        flash.put("exceptionType", exceptionType);
        flash.put("message", message);
        flash.put("stacktrace", stacktrace);
    }
}
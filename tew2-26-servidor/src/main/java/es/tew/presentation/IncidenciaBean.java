package es.tew.presentation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import es.tew.business.IncidenciaService;
import es.tew.business.UsuarioService;
import es.tew.dto.EstadisticasDTO;
import es.tew.infrastructure.ServiceFactory;
import es.tew.model.HistorialEstado;
import es.tew.model.Incidencia;
import es.tew.model.Usuario;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("incidenciaBean")
@SessionScoped
public class IncidenciaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // --- SERVICIOS (Transient) ---
    private transient IncidenciaService incidenciaService;
    private transient UsuarioService usuarioService;

    // --- DEPENDENCIAS ---
    @Inject
    private UsuarioBean usuarioBean;

    private List<Incidencia> incidencias;
    private String filtroEmpleado;
    private String filtroEstado;

    private String crearTitulo;
    private String crearDescripcion;
    private String crearCategoria;

    private Incidencia incidenciaSeleccionada;
    private String nuevoEstadoSeleccionado;
    private transient List<SelectItem> estadosDisponibles;

    private EstadisticasDTO estadisticasDto;

    private Usuario tecnicoActual;
    private List<Usuario> tecnicosDisponibles;
    private String tecnicoSeleccionadoDni;

    public IncidenciaBean() {
        ServiceFactory factory = new ServiceFactory();
        this.incidenciaService = factory.getIncidenciaService();
        this.usuarioService = factory.getUsuarioService();
    }

    private IncidenciaService getIncidenciaService() {
        if (this.incidenciaService == null) {
            this.incidenciaService = new ServiceFactory().getIncidenciaService();
        }
        return this.incidenciaService;
    }

    private UsuarioService getUsuarioService() {
        if (this.usuarioService == null) {
            this.usuarioService = new ServiceFactory().getUsuarioService();
        }
        return this.usuarioService;
    }

    @PostConstruct
    public void init() {
        cargarListado();
    }

    public void cargarListado() {
        Usuario usuarioActual = usuarioBean.getUsuarioActual();
        if (usuarioActual == null) return;

        switch (usuarioActual.getRol().toLowerCase()) {
            case "usuario":
                incidencias = getIncidenciaService().getIncidenciasUsuario(usuarioActual.getDni());
                break;
            case "tecnico":
                incidencias = getIncidenciaService().getIncidenciasTecnico(usuarioActual.getDni());
                break;
            case "administrador":
                incidencias = getIncidenciaService().getTodasLasIncidencias(null, null);
                break;
        }
    }

    public void filtrar() {
        Usuario usuarioActual = usuarioBean.getUsuarioActual();
        if (usuarioActual != null && "administrador".equalsIgnoreCase(usuarioActual.getRol())) {
            incidencias = getIncidenciaService().getTodasLasIncidencias(filtroEmpleado, filtroEstado);
        }
    }

    public List<Incidencia> getIncidencias() { return incidencias; }
    public String getFiltroEmpleado() { return filtroEmpleado; }
    public void setFiltroEmpleado(String filtroEmpleado) { this.filtroEmpleado = filtroEmpleado; }
    public String getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(String filtroEstado) { this.filtroEstado = filtroEstado; }

    public String getCrearTitulo() { return crearTitulo; }
    public void setCrearTitulo(String crearTitulo) { this.crearTitulo = crearTitulo; }
    public String getCrearDescripcion() { return crearDescripcion; }
    public void setCrearDescripcion(String crearDescripcion) { this.crearDescripcion = crearDescripcion; }
    public String getCrearCategoria() { return crearCategoria; }
    public void setCrearCategoria(String crearCategoria) { this.crearCategoria = crearCategoria; }

    public String doRegistrar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle msg = facesContext.getApplication().getResourceBundle(facesContext, "msg");
        
        Usuario usuarioLogueado = usuarioBean.getUsuarioActual();

        if (usuarioLogueado == null) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                msg.getString("global.error.summary"), msg.getString("registerIncident.error.noUser")));
            return null;
        }

        try {
            Incidencia nuevaIncidencia = new Incidencia();
            nuevaIncidencia.setTitulo(crearTitulo);
            nuevaIncidencia.setDescripcion(crearDescripcion);
            nuevaIncidencia.setCategoria(crearCategoria);

            getIncidenciaService().registrarIncidencia(nuevaIncidencia, usuarioLogueado.getDni());

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                msg.getString("global.success.summary"), 
                msg.getString("registerIncident.message.success")));
            
            // Limpiamos campos
            crearTitulo = null;
            crearDescripcion = null;
            crearCategoria = null;

            // Recargamos el listado
            cargarListado();

            return null;

        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                msg.getString("global.error.summary"), e.getMessage()));
            return null;
        }
    }

    public Incidencia getIncidenciaSeleccionada() {
        return incidenciaSeleccionada;
    }

    public void setIncidenciaSeleccionada(Incidencia incidenciaIncompleta) {
        if (incidenciaIncompleta == null) { return; }
        if (incidenciaSeleccionada != null && incidenciaSeleccionada.getId() == incidenciaIncompleta.getId()) { return; }

        this.incidenciaSeleccionada = getIncidenciaService().getDetalleIncidencia(incidenciaIncompleta.getId());
        this.nuevoEstadoSeleccionado = null; 
    }

    public String getNuevoEstadoSeleccionado() { return nuevoEstadoSeleccionado; }
    public void setNuevoEstadoSeleccionado(String nuevoEstadoSeleccionado) { this.nuevoEstadoSeleccionado = nuevoEstadoSeleccionado; }

    public List<SelectItem> getEstadosDisponibles() {
        if (this.estadosDisponibles == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ResourceBundle msg = ctx.getApplication().getResourceBundle(ctx, "msg");
            
            this.estadosDisponibles = new ArrayList<>();
            this.estadosDisponibles.add(new SelectItem("En progreso", msg.getString("status.inProgress")));
            this.estadosDisponibles.add(new SelectItem("Pendiente de usuario", msg.getString("status.pendingUser")));
            this.estadosDisponibles.add(new SelectItem("Cerrada", msg.getString("status.closed")));
        }
        return this.estadosDisponibles;
    }

    public String cambiarEstado() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ResourceBundle msg = ctx.getApplication().getResourceBundle(ctx, "msg");
        
        try {
            if (nuevoEstadoSeleccionado == null || nuevoEstadoSeleccionado.isEmpty()) {
                 ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, 
                     msg.getString("incidentDetail.changeState.invalid.summary"), 
                     msg.getString("incidentDetail.changeState.invalid.detail")));
                 return null;
            }

            getIncidenciaService().cambiarEstado(incidenciaSeleccionada.getId(), nuevoEstadoSeleccionado);

            HistorialEstado nuevoHistorial = new HistorialEstado();
            nuevoHistorial.setEstadoAnterior(incidenciaSeleccionada.getEstadoActual());
            nuevoHistorial.setEstadoNuevo(nuevoEstadoSeleccionado);
            nuevoHistorial.setFecha(LocalDateTime.now());
            nuevoHistorial.setIncidencia(incidenciaSeleccionada.getId());

            if (incidenciaSeleccionada.getHistorial() == null) {
                incidenciaSeleccionada.setHistorial(new ArrayList<>());
            }
            incidenciaSeleccionada.getHistorial().add(0, nuevoHistorial);

            incidenciaSeleccionada.setEstadoActual(nuevoEstadoSeleccionado);
            incidenciaSeleccionada.setFechaUltimaModificacion(LocalDateTime.now());
            
            this.nuevoEstadoSeleccionado = null;

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                msg.getString("global.success.summary"), 
                msg.getString("incidentDetail.changeState.success.detail")));
            
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                msg.getString("global.error.summary"), 
                msg.getString("incidentDetail.changeState.error.detail") + e.getMessage()));
        }
        return null;
    }

    public String volverListado() {
        return "/common/listar_incidencias.xhtml?faces-redirect=true";
    }

    public void cargarEstadisticas() {
        try {
            estadisticasDto = getIncidenciaService().getEstadisticas();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar estadísticas", e.getMessage()));
        }
    }

    public EstadisticasDTO getEstadisticasDto() {
        return estadisticasDto;
    }

    public Usuario getTecnicoActual() { return tecnicoActual; }
    public List<Usuario> getTecnicosDisponibles() { return tecnicosDisponibles; }
    public String getTecnicoSeleccionadoDni() { return tecnicoSeleccionadoDni; }
    public void setTecnicoSeleccionadoDni(String tecnicoSeleccionadoDni) { this.tecnicoSeleccionadoDni = tecnicoSeleccionadoDni; }

    public String irAReasignar(Incidencia incidencia) {
        try {
            // Reutilizamos la lógica de cargar detalle
            this.incidenciaSeleccionada = getIncidenciaService().getDetalleIncidencia(incidencia.getId());
            this.tecnicoActual = getUsuarioService().findByDni(incidencia.getTecnico());
            this.tecnicoSeleccionadoDni = null;

            List<Usuario> todosTecnicos = getUsuarioService().getTecnicos();

            // Lógica de negocio para ordenar por carga de trabajo
            for (Usuario t : todosTecnicos) {
                t.setIncidenciasAsignadas(getIncidenciaService().getCountByTecnico(t.getDni()));
            }

            this.tecnicosDisponibles = todosTecnicos.stream()
                .filter(t -> !t.getDni().equals(this.tecnicoActual.getDni()))
                .sorted(Comparator.comparingInt(Usuario::getIncidenciasAsignadas))
                .collect(Collectors.toList());

            return "irAReasignar";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo cargar la información para reasignar."));
            return null;
        }
    }

    public String doReasignar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle msg = facesContext.getApplication().getResourceBundle(facesContext, "msg");
        
        try {
            Usuario admin = usuarioBean.getUsuarioActual();
            Usuario nuevoTecnico = getUsuarioService().findByDni(tecnicoSeleccionadoDni);
            
            if (nuevoTecnico == null) {
                throw new Exception("El técnico seleccionado no es válido.");
            }

            getIncidenciaService().reasignarTecnico(
                incidenciaSeleccionada.getId(), 
                nuevoTecnico.getDni(), 
                admin.getDni(), 
                tecnicoActual.getNombreCompleto(), 
                nuevoTecnico.getNombreCompleto()
            );

            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
                msg.getString("global.success.summary"), 
                msg.getString("reassign.message.success")));
            
            facesContext.getExternalContext().getFlash().setKeepMessages(true);

            cargarListado();
            return "/common/listar_incidencias.xhtml?faces-redirect=true";

        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                msg.getString("global.error.summary"), e.getMessage()));
            return null;
        }
    }
}
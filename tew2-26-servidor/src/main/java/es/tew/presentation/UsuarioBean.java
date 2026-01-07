package es.tew.presentation;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import es.tew.infrastructure.ServiceFactory;
import es.tew.business.UsuarioService;
import es.tew.model.Usuario;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("usuarioBean")
@SessionScoped
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient UsuarioService usuarioService;

    private String dni;
    private String password;
    private Usuario usuarioActual;

    private String altaDni;
    private String altaNombre;
    private String altaApellidos;
    private String altaRol = "usuario"; // Valor por defecto
    private String altaPasswordGenerada = null;

    private List<Usuario> usuarios;

    public UsuarioBean() {
        this.usuarioService = new ServiceFactory().getUsuarioService();
    }

    private UsuarioService getUsuarioService() {
        if (this.usuarioService == null) {
            this.usuarioService = new ServiceFactory().getUsuarioService();
        }
        return this.usuarioService;
    }

    @PostConstruct
    public void init() {
        // Inicialización si fuera necesaria
    }


    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario usuarioActual) { this.usuarioActual = usuarioActual; }

    public String doLogin() {
        try {
            usuarioActual = getUsuarioService().login(dni, password);

            if (usuarioActual != null) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .getSessionMap().put("LOGGED_USER", usuarioActual);

                String rol = usuarioActual.getRol();

                switch (rol.toLowerCase()) {
                    case "usuario":
                        return "/usuario/home_usuario.xhtml?faces-redirect=true";
                    case "tecnico":
                        return "/tecnico/home_tecnico.xhtml?faces-redirect=true";
                    case "administrador":
                    case "admin": 
                        return "/admin/home_admin.xhtml?faces-redirect=true";
                    default:
                        FacesContext.getCurrentInstance().addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Rol desconocido: " + rol, null));
                        doLogout();
                        return null;
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Credenciales incorrectas", null));
                return null;
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al iniciar sesión: " + e.getMessage(), null));
            return null;
        }
    }

    public String doLogout() { 
        FacesContext context = FacesContext.getCurrentInstance();
        var externalContext = context.getExternalContext();
        
        this.usuarioActual = null;
        this.dni = null;
        this.password = null;

        externalContext.getSessionMap().remove("LOGGED_USER");
        
        return "/index.xhtml?faces-redirect=true"; 
    }

    public void verificarAccesoUsuario() {
        try {
            if (usuarioActual == null || !"usuario".equalsIgnoreCase(usuarioActual.getRol())) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestContextPath() + "/index.xhtml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verificarAccesoTecnico() {
        try {
            if (usuarioActual == null || !"tecnico".equalsIgnoreCase(usuarioActual.getRol())) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect(FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestContextPath() + "/index.xhtml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verificarAdmin() throws IOException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (usuarioActual == null || (!"administrador".equalsIgnoreCase(usuarioActual.getRol()) && !"admin".equalsIgnoreCase(usuarioActual.getRol()))) {
            ctx.getExternalContext().redirect(ctx.getExternalContext().getRequestContextPath() + "/index.xhtml");
        }
    }
    
    public String volverInicio() {
        if (usuarioActual == null) {
            return "/index.xhtml?faces-redirect=true";
        }
        switch (usuarioActual.getRol().toLowerCase()) {
            case "administrador":
            case "admin":
                return "/admin/home_admin.xhtml?faces-redirect=true";
            case "tecnico":
                return "/tecnico/home_tecnico.xhtml?faces-redirect=true";
            case "usuario":
                return "/usuario/home_usuario.xhtml?faces-redirect=true";
            default:
                return "/index.xhtml?faces-redirect=true";
        }
    }


    public String getAltaDni() { return altaDni; }
    public void setAltaDni(String altaDni) { this.altaDni = altaDni; }
    public String getAltaNombre() { return altaNombre; }
    public void setAltaNombre(String altaNombre) { this.altaNombre = altaNombre; }
    public String getAltaApellidos() { return altaApellidos; }
    public void setAltaApellidos(String altaApellidos) { this.altaApellidos = altaApellidos; }
    public String getAltaRol() { return altaRol; }
    public void setAltaRol(String altaRol) { this.altaRol = altaRol; }
    public String getAltaPasswordGenerada() { return altaPasswordGenerada; }

    public String doAltaUsuario() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ResourceBundle msg = facesContext.getApplication().getResourceBundle(facesContext, "msg");
        
        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setDni(altaDni);
            nuevoUsuario.setNombre(altaNombre);
            nuevoUsuario.setApellidos(altaApellidos);
            nuevoUsuario.setRol(altaRol);

            Usuario usuarioCreado = getUsuarioService().altaUsuario(nuevoUsuario);

            this.altaPasswordGenerada = usuarioCreado.getPasswd();

            String summary = msg.getString("createUser.message.successSummary");
            String detail = msg.getString("createUser.message.successUser") + ": " + usuarioCreado.getDni() 
                          + " | " + msg.getString("createUser.message.successPassword") + ": " + altaPasswordGenerada;
            
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
            facesContext.getExternalContext().getFlash().setKeepMessages(true);

            // Limpiamos campos
            altaDni = null;
            altaNombre = null;
            altaApellidos = null;
            altaRol = "usuario";

            return "/admin/lista_usuarios.xhtml?faces-redirect=true";

        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                msg.getString("global.error.summary"), e.getMessage()));
            this.altaPasswordGenerada = null;
            return null;
        }
    }

    // ==============================================================================
    // LÓGICA DE LISTAR USUARIOS (Ex-AdminBean)
    // ==============================================================================

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void cargarUsuarios() {
        try {
            usuarios = getUsuarioService().getUsuarios();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar los usuarios: " + e.getMessage(), null));
        }
    }
}

package es.tew.presentation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("beanRegistro")
@RequestScoped
public class BeanRegistro {

    private String nombreUsuario;
    private String password;
    private String confirmarPassword;
    private String mensaje;

    // Getters y setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String registrarManual() {
        if (password == null || confirmarPassword == null || !password.equals(confirmarPassword)) {
            mensaje = "Las contraseñas no coinciden";
            return null;
        }
        mensaje = "Registro manual correcto";
        return "exito";
    }

    public String registrarJSF() {
        mensaje = "Registro JSF correcto";
    return "exito";
}
}
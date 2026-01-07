package es.tew.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
    private String dni;
    private String rol;
    private String nombreCompleto;

    public UsuarioDTO() {}

    public UsuarioDTO(String dni, String rol, String nombreCompleto) {
        this.dni = dni;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + rol + ")";
    }
}

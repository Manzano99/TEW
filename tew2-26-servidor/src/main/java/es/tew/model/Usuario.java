package es.tew.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String dni;
    private String passwd;
    private String rol;
    private String nombre;
    private String apellidos;
    private transient int incidenciasAsignadas = 0;

    public Usuario() {}

    public Usuario(String dni, String passwd, String rol, String nombre, String apellidos) {
        this.dni = dni;
        this.passwd = passwd;
        this.rol = rol;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    // Getters y setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getPasswd() { return passwd; }
    public void setPasswd(String passwd) { this.passwd = passwd; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public int getIncidenciasAsignadas() {
        return incidenciasAsignadas;
    }
    public void setIncidenciasAsignadas(int incidenciasAsignadas) {
        this.incidenciasAsignadas = incidenciasAsignadas;
    }
    
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + rol + ")";
    }
}

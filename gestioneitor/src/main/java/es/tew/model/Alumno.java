package es.tew.model;

import java.io.Serializable;

public class Alumno implements Serializable {
    private Integer id;
    private String idUser;
    private String email;
    private String nombre;
    private String apellidos;
    public Alumno(int id, String idUser, String email, String nombre, String apellidos) {
    this.id = id;
    this.idUser = idUser;
    this.email = email;
    this.nombre = nombre;
    this.apellidos = apellidos;
    }

    public Alumno() {
        this.id = -1;
        this.idUser = "";
        this.email = "";
        this.nombre = "";
        this.apellidos = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
    

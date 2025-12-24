package es.tew.api;

import es.tew.model.Alumno;

public class AlumnoRequestData extends Alumno {

    public AlumnoRequestData() {
        super();
    }
    
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

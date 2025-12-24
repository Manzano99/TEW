package es.tew.business;

import es.tew.model.Alumno;

import java.util.List;

public interface AlumnoService {
    List<Alumno> getAlumnos();

    void saveAlumno(Alumno alumno);

    void updateAlumno(Alumno alumno);

    void deleteAlumno(int id);
}
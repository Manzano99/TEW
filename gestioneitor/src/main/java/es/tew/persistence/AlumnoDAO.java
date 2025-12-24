package es.tew.persistence;

import es.tew.model.Alumno;

import java.util.List;

public interface AlumnoDAO {
    void saveAlumno(Alumno alumno);
    void updateAlumno(Alumno alumno);
    void deleteAlumno(int id);
    List<Alumno> getAlumnos();
}

package es.tew.business;

import java.util.List;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.Alumno;
import es.tew.persistence.AlumnoDAO;

public class AlumnoServiceImpl implements AlumnoService {
    private final AlumnoDAO alumnoDAO = PersistenceFactory.getAlumnoDAO();
    
    @Override
    public List<Alumno> getAlumnos() {
        return alumnoDAO.getAlumnos();
    }

    @Override
    public void saveAlumno(Alumno alumno) {
        alumnoDAO.saveAlumno(alumno);
    }

    @Override
    public void updateAlumno(Alumno alumno) {
        alumnoDAO.updateAlumno(alumno);
    }

    @Override
    public void deleteAlumno(int id) {
        alumnoDAO.deleteAlumno(id);
    }
}

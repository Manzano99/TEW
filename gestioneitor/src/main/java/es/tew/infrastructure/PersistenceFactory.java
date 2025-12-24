package es.tew.infrastructure;

import es.tew.persistence.AlumnoDAO;
import es.tew.persistence.AlumnoDAOImpl;

public class PersistenceFactory {
    public static AlumnoDAO getAlumnoDAO() {
    return new AlumnoDAOImpl();
    }
   }
   
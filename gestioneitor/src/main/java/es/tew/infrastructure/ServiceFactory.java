package es.tew.infrastructure;

import es.tew.business.AlumnoService;
import es.tew.business.UserService;
import es.tew.business.AlumnoServiceImpl;
import es.tew.business.UserServiceImpl;

public class ServiceFactory {
    public static AlumnoService getAlumnoService() {
        return new AlumnoServiceImpl();
    }

    public static UserService getUserService() {
        return new UserServiceImpl();
    }
}

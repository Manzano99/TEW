package es.tew.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        sce.getServletContext().setInitParameter("primefaces.THEME", "luna-blue");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
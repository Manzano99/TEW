package es.tew.business;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.Comentario;
import es.tew.model.HistorialEstado;
import es.tew.persistence.ComentarioDAO;
import es.tew.persistence.HistorialEstadoDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class ComentarioServiceImpl implements ComentarioService {

    // Detalles de la Conexión JDBC
    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/localDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    private PersistenceFactory factory = new PersistenceFactory();

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el driver de HSQLDB.", e);
        }
    }

    @Override
    public void addComentario(int idIncidencia, String autorDni, String texto) throws Exception {
        
        Connection conn = null;
        try {
            HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();

            // Obtener estado actual de la incidencia
            List<HistorialEstado> historial = historialDAO.findByIncidencia(idIncidencia);
            String estadoActual = historial.stream()
                .max(Comparator.comparing(HistorialEstado::getFecha))
                .map(HistorialEstado::getEstadoNuevo)
                .orElse("Abierta");

            // Validar que la incidencia no esté cerrada 
            if ("Cerrada".equalsIgnoreCase(estadoActual)) {
                throw new Exception("No se pueden añadir comentarios a una incidencia cerrada.");
            }

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            ComentarioDAO comentarioDAO = factory.getComentarioDAO();
            
            Comentario comentario = new Comentario();
            comentario.setIncidencia(idIncidencia);
            comentario.setAutor(autorDni);
            comentario.setMensaje(texto);
            comentario.setFecha(LocalDateTime.now());
            comentarioDAO.insert(conn, comentario);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @Override
    public List<Comentario> getComentarios(int idIncidencia) {
        return factory.getComentarioDAO().findByIncidencia(idIncidencia);
    }
}
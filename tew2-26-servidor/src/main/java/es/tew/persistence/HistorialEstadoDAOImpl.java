package es.tew.persistence;

import es.tew.model.HistorialEstado;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Implementación de la interfaz HistorialEstadoDAO con JDBC

public class HistorialEstadoDAOImpl implements HistorialEstadoDAO {

    // Detalles de la Conexión JDBC
    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/localDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    // Bloque estático para cargar el driver de HSQLDB
    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el driver de HSQLDB.", e);
        }
    }

    @Override
    public List<HistorialEstado> findByIncidencia(int idIncidencia) {
        String SQL = "SELECT * FROM HISTORIALESTADOS WHERE INCIDENCIA = ?";
        List<HistorialEstado> historial = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, idIncidencia);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historial.add(mapHistorial(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar historial por incidencia", e);
        }
        return historial;
    }

    @Override
    public void insert(Connection conn, HistorialEstado historial) {
        String SQL = "INSERT INTO HISTORIALESTADOS (INCIDENCIA, FECHA, ESTADO_ANTERIOR, ESTADO_NUEVO) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, historial.getIncidencia());
            ps.setTimestamp(2, Timestamp.valueOf(historial.getFecha()));
            ps.setString(3, historial.getEstadoAnterior());
            ps.setString(4, historial.getEstadoNuevo());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar historial de estado", e);
        }
    }

    private HistorialEstado mapHistorial(ResultSet rs) throws SQLException {
        HistorialEstado hist = new HistorialEstado();
        
        hist.setId(rs.getInt("ID"));
        hist.setIncidencia(rs.getInt("INCIDENCIA"));
        hist.setEstadoAnterior(rs.getString("ESTADO_ANTERIOR"));
        hist.setEstadoNuevo(rs.getString("ESTADO_NUEVO"));
        
        // Mapea Timestamp (SQL) a LocalDateTime (Java)
        Timestamp ts = rs.getTimestamp("FECHA");
        if (ts != null) {
            hist.setFecha(ts.toLocalDateTime());
        }
        
        return hist;
    }

    @Override
    public List<HistorialEstado> findByIncidencia(Connection conn, int idIncidencia) {
        String SQL = "SELECT * FROM HISTORIALESTADOS WHERE INCIDENCIA = ?";
        List<HistorialEstado> historial = new ArrayList<>();
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, idIncidencia);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    historial.add(mapHistorial(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar historial (Tx) por incidencia", e);
        }
        return historial;
    }
}

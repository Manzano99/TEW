package es.tew.persistence;

import es.tew.model.Comentario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAOImpl implements ComentarioDAO {

    //Detalles de la Conexión JDBC
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
    public List<Comentario> findByIncidencia(int idIncidencia) {
        String SQL = "SELECT * FROM COMENTARIOS WHERE INCIDENCIA = ?";
        List<Comentario> comentarios = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, idIncidencia);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(mapComentario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar comentarios por incidencia", e);
        }
        return comentarios;
    }

    @Override
    public void insert(Connection conn, Comentario comentario) {
        // La BBDD  define las columnas: ID (autogen), incidencia, autor, fecha, mensaje
        String SQL = "INSERT INTO COMENTARIOS (INCIDENCIA, AUTOR, FECHA, MENSAJE) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, comentario.getIncidencia());
            ps.setString(2, comentario.getAutor());
            ps.setTimestamp(3, Timestamp.valueOf(comentario.getFecha()));
            ps.setString(4, comentario.getMensaje());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar comentario", e);
        }
    }

    private Comentario mapComentario(ResultSet rs) throws SQLException {
        Comentario com = new Comentario();
        
        com.setId(rs.getInt("ID"));
        com.setIncidencia(rs.getInt("INCIDENCIA"));
        com.setAutor(rs.getString("AUTOR"));
        com.setMensaje(rs.getString("MENSAJE"));
        
        // Mapea Timestamp (SQL) a LocalDateTime (Java)
        Timestamp ts = rs.getTimestamp("FECHA");
        if (ts != null) {
            com.setFecha(ts.toLocalDateTime());
        }
        
        return com;
    }

    @Override
    public List<Comentario> findByIncidencia(Connection conn, int idIncidencia) {
        String SQL = "SELECT * FROM COMENTARIOS WHERE INCIDENCIA = ?";
        List<Comentario> comentarios = new ArrayList<>();
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, idIncidencia);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comentarios.add(mapComentario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar comentarios (Tx) por incidencia", e);
        }
        return comentarios;
    }
}
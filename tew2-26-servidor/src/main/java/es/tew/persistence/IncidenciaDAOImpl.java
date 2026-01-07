package es.tew.persistence;

import es.tew.model.Incidencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;

// Implementación de la interfaz IncidenciaDAO con JDBC puro.
public class IncidenciaDAOImpl implements IncidenciaDAO {
    
    // Detalles de la Conexión JDB
    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/localDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    // Bloque estático para cargar el driver de HSQLDB una sola vez.
    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el driver de HSQLDB.", e);
        }
    }

    @Override
    public Incidencia findById(int id) {
        String SQL = "SELECT * FROM INCIDENCIAS WHERE ID = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapIncidencia(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar incidencia por ID", e);
        }
        return null;
    }

    @Override
    public Incidencia findById(Connection conn, int id) {
        String SQL = "SELECT * FROM INCIDENCIAS WHERE ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapIncidencia(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar incidencia (Tx) por ID", e);
        }
        return null;
    }

    @Override
    public List<Incidencia> findAll() {
        String SQL = "SELECT * FROM INCIDENCIAS";
        List<Incidencia> incidencias = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                incidencias.add(mapIncidencia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al listar todas las incidencias", e);
        }
        return incidencias;
    }

    @Override
    public List<Incidencia> findBySolicitante(String dniSolicitante) {
        String SQL = "SELECT * FROM INCIDENCIAS WHERE SOLICITANTE = ?";
        List<Incidencia> incidencias = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, dniSolicitante);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    incidencias.add(mapIncidencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar incidencias por solicitante", e);
        }
        return incidencias;
    }

    @Override
    public List<Incidencia> findByTecnico(String dniTecnico) {
        String SQL = "SELECT * FROM INCIDENCIAS WHERE TECNICO = ?";
        List<Incidencia> incidencias = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, dniTecnico);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    incidencias.add(mapIncidencia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar incidencias por tecnico", e);
        }
        return incidencias;
    }

    @Override
    public int getCountByTecnico(String dniTecnico) {
        String SQL = "SELECT COUNT(*) FROM INCIDENCIAS WHERE TECNICO = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, dniTecnico);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al contar incidencias por tecnico", e);
        }
        return 0;
    }

    @Override
    public void updateTecnico(Connection conn, int idIncidencia, String nuevoTecnicoDni) {
        String SQL = "UPDATE INCIDENCIAS SET TECNICO = ? WHERE ID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, nuevoTecnicoDni);
            ps.setInt(2, idIncidencia);
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el técnico de la incidencia", e);
        }
    }

    @Override
    public int insert(Connection conn, Incidencia incidencia) {
        String SQL = "INSERT INTO INCIDENCIAS (TITULO, DESCRIPCION, FECHA_CREACION, CATEGORIA, SOLICITANTE, TECNICO) VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet generatedKeys = null;

        try (PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, incidencia.getTitulo());
            ps.setString(2, incidencia.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(incidencia.getFechaCreacion())); 
            ps.setString(4, incidencia.getCategoria());
            ps.setString(5, incidencia.getSolicitante());
            ps.setString(6, incidencia.getTecnico());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                 throw new SQLException("La inserción de la incidencia falló, no se afectaron filas.");
            }

            generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("La inserción de la incidencia falló, no se obtuvo el ID.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar incidencia", e);
        } finally {
             if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    @Override
    public void update(Connection conn, Incidencia incidencia) {
        String SQL = "UPDATE INCIDENCIAS SET TITULO = ?, DESCRIPCION = ?, FECHA_CREACION = ?, CATEGORIA = ?, SOLICITANTE = ?, TECNICO = ? WHERE ID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, incidencia.getTitulo());
            ps.setString(2, incidencia.getDescripcion());
            ps.setTimestamp(3, Timestamp.valueOf(incidencia.getFechaCreacion()));
            ps.setString(4, incidencia.getCategoria());
            ps.setString(5, incidencia.getSolicitante());
            ps.setString(6, incidencia.getTecnico());
            ps.setInt(7, incidencia.getId());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar incidencia", e);
        }
    }

    @Override
    public void delete(Connection conn, int id) {
        String SQL = "DELETE FROM INCIDENCIAS WHERE ID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar incidencia", e);
        }
    }

    private Incidencia mapIncidencia(ResultSet rs) throws SQLException {
        Incidencia inc = new Incidencia();
    
        inc.setId(rs.getInt("ID"));
        inc.setTitulo(rs.getString("TITULO"));
        inc.setDescripcion(rs.getString("DESCRIPCION"));
    
        try {
            Object fechaObj = rs.getObject("FECHA_CREACION");
            if (fechaObj instanceof java.sql.Timestamp ts) {
                inc.setFechaCreacion(ts.toLocalDateTime());
            } else if (fechaObj != null) {
                // Si el driver devuelve String o Date, lo convertimos manualmente
                inc.setFechaCreacion(
                    rs.getTimestamp("FECHA_CREACION").toLocalDateTime()
                );
            } else {
                System.out.println("⚠ FECHA_CREACION NULL para ID=" + inc.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error leyendo FECHA_CREACION para ID=" + inc.getId() + ": " + e.getMessage());
        }
    
        inc.setCategoria(rs.getString("CATEGORIA"));
        inc.setSolicitante(rs.getString("SOLICITANTE"));
        inc.setTecnico(rs.getString("TECNICO"));
    
        return inc;
    }
    
    
    
}
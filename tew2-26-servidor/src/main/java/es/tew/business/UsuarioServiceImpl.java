package es.tew.business;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.Usuario;
import es.tew.persistence.UsuarioDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; 
import java.time.LocalDateTime;
import java.sql.Statement;

public class UsuarioServiceImpl implements UsuarioService {

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
    public Usuario login(String dni, String password) {
        UsuarioDAO dao = factory.getUsuarioDAO();
        return dao.findByCredentials(dni, password);
    }

@Override
public List<Usuario> getUsuarios() {
    List<Usuario> usuarios = new ArrayList<>();

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement ps = conn.prepareStatement("SELECT dni, nombre, apellidos, rol FROM usuarios")) {

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Usuario u = new Usuario();
            u.setDni(rs.getString("dni"));
            u.setNombre(rs.getString("nombre"));
            u.setApellidos(rs.getString("apellidos"));
            u.setRol(rs.getString("rol"));
            usuarios.add(u);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return usuarios;
}

    @Override
    public List<Usuario> getTecnicos() {
        return factory.getUsuarioDAO().findByRol("tecnico");
    }

    @Override
    public Usuario findByDni(String dni) {
        return factory.getUsuarioDAO().findByDni(dni);
    }


    @Override
    public Usuario altaUsuario(Usuario usuario) throws Exception {
        
        Connection conn = null;
        try {
            UsuarioDAO dao = factory.getUsuarioDAO();

            // Validar que el DNI no exista
            if (dao.findByDni(usuario.getDni()) != null) {
                throw new Exception("El DNI " + usuario.getDni() + " ya existe.");
            }
 
            String password = generarPasswordAleatoria();
            usuario.setPasswd(password);

            // Iniciar Transacción
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            dao.insert(conn, usuario);

            conn.commit();

            // Devolvemos el usuario con la contraseña generada
            return usuario;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Error al dar de alta al usuario", e);
        } catch (Exception e) {
            throw e; 
        } finally {
            // Cerrar la conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generarPasswordAleatoria() {
        // Genera un UUID y toma los primeros 8 caracteres.
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public Map<String, Object> getSystemHealthCheck() {
        Map<String, Object> status = new HashMap<>();
        
        // 1. Hora del servidor
        status.put("serverTime", LocalDateTime.now().toString());
        
        // 2. Estado de la Base de Datos
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS"); 
            
            status.put("databaseStatus", "OK");
            
            // 3. Estado del Servidor Web
            status.put("webServerStatus", "OK");
            
        } catch (Exception e) {
            status.put("databaseStatus", "DOWN");
            status.put("webServerStatus", "WARNING");
            status.put("error", e.getMessage());
        }
        
        return status;
    }
}
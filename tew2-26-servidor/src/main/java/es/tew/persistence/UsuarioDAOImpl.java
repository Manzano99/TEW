package es.tew.persistence;

import es.tew.model.Usuario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Implementación de la interfaz UsuarioDAO con JDBC puro.

public class UsuarioDAOImpl implements UsuarioDAO {

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
    public Usuario findByDni(String dni) {
        String SQL = "SELECT * FROM USUARIOS WHERE DNI = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace(); 
            throw new RuntimeException("Error al buscar usuario por DNI", e);
        }
        return null;
    }

    @Override
    public Usuario findByDni(Connection conn, String dni) {
        String SQL = "SELECT * FROM USUARIOS WHERE DNI = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUsuario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar usuario (Tx) por DNI", e);
        }
        return null;
    }

    @Override
    public Usuario findByCredentials(String dni, String password) {
        Usuario usuario = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT * FROM USUARIOS WHERE DNI = ? AND PASSWD = ?")) {
            ps.setString(1, dni);
            ps.setString(2, password);
    
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setDni(rs.getString("DNI"));
                    usuario.setPasswd(rs.getString("PASSWD"));
                    usuario.setRol(rs.getString("ROL"));
                    usuario.setNombre(rs.getString("NOMBRE"));
                    usuario.setApellidos(rs.getString("APELLIDOS"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }
    

    @Override
    public List<Usuario> findAll() {
        String SQL = "SELECT * FROM USUARIOS";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapUsuario(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al listar usuarios", e);
        }
        return usuarios;
    }

    @Override
    public List<Usuario> findByRol(String rol) {
        String SQL = "SELECT * FROM USUARIOS WHERE ROL = ?";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, rol);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapUsuario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar usuarios por rol", e);
        }
        return usuarios;
    }

    @Override
    public void insert(Connection conn, Usuario usuario) {
        String SQL = "INSERT INTO USUARIOS (DNI, PASSWD, ROL, NOMBRE, APELLIDOS) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getPasswd());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getNombre());
            ps.setString(5, usuario.getApellidos());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar usuario", e);
        }
    }

    @Override
    public void update(Connection conn, Usuario usuario) {
        String SQL = "UPDATE USUARIOS SET PASSWD = ?, ROL = ?, NOMBRE = ?, APELLIDOS = ? WHERE DNI = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            
            ps.setString(1, usuario.getPasswd());
            ps.setString(2, usuario.getRol());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellidos());
            ps.setString(5, usuario.getDni());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    @Override
    public void delete(Connection conn, String dni) {
        String SQL = "DELETE FROM USUARIOS WHERE DNI = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, dni);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();
        user.setDni(rs.getString("DNI"));
        user.setPasswd(rs.getString("PASSWD"));
        user.setRol(rs.getString("ROL"));
        user.setNombre(rs.getString("NOMBRE"));
        user.setApellidos(rs.getString("APELLIDOS"));
        return user;
    }
}
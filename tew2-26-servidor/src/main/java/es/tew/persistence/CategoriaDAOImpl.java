package es.tew.persistence;

import es.tew.model.Categoria;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {

    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/localDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try { Class.forName(JDBC_DRIVER); } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    @Override
    public List<Categoria> findAll() {
        List<Categoria> lista = new ArrayList<>();
        String SQL = "SELECT * FROM CATEGORIAS ORDER BY NOMBRE";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(new Categoria(rs.getString("NOMBRE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Categoria findByNombre(String nombre) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            return findByNombre(conn, nombre);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Categoria findByNombre(Connection conn, String nombre) {
        String SQL = "SELECT * FROM CATEGORIAS WHERE NOMBRE = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(rs.getString("NOMBRE"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(Connection conn, Categoria categoria) {
        String SQL = "INSERT INTO CATEGORIAS (NOMBRE) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, categoria.getNombre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar categoría", e);
        }
    }
}
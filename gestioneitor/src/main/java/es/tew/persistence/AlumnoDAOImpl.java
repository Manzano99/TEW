package es.tew.persistence;

import es.tew.model.Alumno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAOImpl implements AlumnoDAO {

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        return DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/localDB", "sa", "");
    }

    @Override
    public void saveAlumno(Alumno alumno) {
        String sql = "INSERT INTO ALUMNO (IDUSER, NOMBRE, APELLIDOS, EMAIL) VALUES (?, ?, ?, ?)";
        
        try (
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, alumno.getIdUser());
            ps.setString(2, alumno.getNombre());
            ps.setString(3, alumno.getApellidos());
            ps.setString(4, alumno.getEmail());
            
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateAlumno(Alumno alumno) {
        String sql = "UPDATE ALUMNO SET IDUSER=?, EMAIL=?, NOMBRE=?, APELLIDOS=? WHERE ID=?";
        
        try (
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, alumno.getIdUser());
            ps.setString(2, alumno.getEmail());
            ps.setString(3, alumno.getNombre());
            ps.setString(4, alumno.getApellidos());
            ps.setInt(5, alumno.getId());
            
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAlumno(int id) {
        String sql = "DELETE FROM ALUMNO WHERE ID=?";
        
        try (
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            
            ps.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Alumno> getAlumnos() {
        String sql = "SELECT * FROM ALUMNO";
        List<Alumno> alumnos = new ArrayList<>();

        try (
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                alumnos.add(new Alumno(
                    rs.getInt("id"), 
                    rs.getString("idUser"),
                    rs.getString("email"), 
                    rs.getString("nombre"), 
                    rs.getString("apellidos")
                ));
            }
            return alumnos;
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
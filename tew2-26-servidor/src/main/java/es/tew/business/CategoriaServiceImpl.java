package es.tew.business;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.Categoria;
import es.tew.persistence.CategoriaDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class CategoriaServiceImpl implements CategoriaService {
    private PersistenceFactory factory = new PersistenceFactory();

    @Override
    public List<Categoria> getCategorias() {
        return factory.getCategoriaDAO().findAll();
    }

    @Override
    public void createCategoria(Categoria categoria) throws Exception {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/localDB", "sa", "");
            conn.setAutoCommit(false);
            
            CategoriaDAO dao = new PersistenceFactory().getCategoriaDAO();
            if (dao.findByNombre(conn, categoria.getNombre()) != null) {
                throw new Exception("La categoría ya existe.");
            }
            dao.insert(conn, categoria);
            
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
}
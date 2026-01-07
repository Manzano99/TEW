package es.tew.business;

import es.tew.model.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> getCategorias();
    void createCategoria(Categoria categoria) throws Exception;
}
package es.tew.business;

import es.tew.model.Comentario;
import java.util.List;

public interface ComentarioService {

    // Añade un nuevo comentario a una incidencia.
    void addComentario(int idIncidencia, String autorDni, String texto) throws Exception;

    // Obtiene todos los comentarios de una incidencia.
    List<Comentario> getComentarios(int idIncidencia);
}
package es.tew.business;

import es.tew.model.Incidencia;
import es.tew.dto.EstadisticasDTO;

import java.util.List;
import java.util.Map;

public interface IncidenciaService {

    // Registra una nueva incidencia.
    void registrarIncidencia(Incidencia incidencia, String solicitanteDni) throws Exception;

    // Cambia el estado de una incidencia.
    void cambiarEstado(int idIncidencia, String nuevoEstado) throws Exception;

    // Obtiene el detalle de una incidencia.
    Incidencia getDetalleIncidencia(int idIncidencia);

    // Lista todas las incidencias creadas por un usuario.
    List<Incidencia> getIncidenciasUsuario(String dniSolicitante);

    // Lista todas las incidencias asignadas a un técnico.
    List<Incidencia> getIncidenciasTecnico(String dniTecnico);

    // Lista todas las incidencias del sistema con filtros opcionales.
    List<Incidencia> getTodasLasIncidencias(String filtroEmpleadoDni, String filtroEstado);

    // Obtiene estadísticas generales del sistema.
    EstadisticasDTO getEstadisticas();

    void addComentario(int idIncidencia, String autorDni, String mensaje) throws Exception;

    int getCountByTecnico(String dniTecnico);
    void reasignarTecnico(int idIncidencia, String nuevoTecnicoDni, String adminDni, 
                    String tecnicoActualNombre, String nuevoTecnicoNombre) throws Exception;
    
    Map<String, Object> exportarDatosSistema();

    // Recibe el mapa y procesa la importación controlando duplicados
    void importarDatosSistema(Map<String, Object> datos) throws Exception;
}
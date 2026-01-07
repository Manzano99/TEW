package es.tew.business;

import es.tew.infrastructure.PersistenceFactory;
import es.tew.model.Comentario;
import es.tew.model.HistorialEstado;
import es.tew.model.Incidencia;
import es.tew.model.Usuario;
import es.tew.persistence.CategoriaDAO; // <--- IMPORTACIÓN IMPORTANTE
import es.tew.persistence.ComentarioDAO;
import es.tew.persistence.HistorialEstadoDAO;
import es.tew.persistence.IncidenciaDAO;
import es.tew.persistence.UsuarioDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import es.tew.dto.EstadisticasDTO;

import java.util.function.Function;

public class IncidenciaServiceImpl implements IncidenciaService {

    private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String DB_URL = "jdbc:hsqldb:hsql://localhost/localDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    private final PersistenceFactory factory = new PersistenceFactory();

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se pudo cargar el driver de HSQLDB.", e);
        }
    }

    @Override
    public void registrarIncidencia(Incidencia incidencia, String solicitanteDni) throws Exception {
        Connection conn = null;
        try {
            // Asignar técnico automáticamente
            String tecnicoAsignadoDni = findTecnicoConMenosIncidencias();

            incidencia.setSolicitante(solicitanteDni);
            incidencia.setTecnico(tecnicoAsignadoDni);
            incidencia.setFechaCreacion(LocalDateTime.now());
            incidencia.setFechaUltimaModificacion(incidencia.getFechaCreacion()); 

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();
            HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
            CategoriaDAO catDao = factory.getCategoriaDAO(); // <--- DAO DE CATEGORÍA

            // --- VALIDACIÓN DE CATEGORÍA ---
            // Verifica que la categoría no sea nula y exista en la BBDD
            if (incidencia.getCategoria() == null || 
                catDao.findByNombre(conn, incidencia.getCategoria()) == null) {
                throw new Exception("La categoría indicada ('" + incidencia.getCategoria() + "') no es válida o no existe en el sistema.");
            }
            // -------------------------------

            // Insertar la incidencia
            int nuevoIdIncidencia = incidenciaDAO.insert(conn, incidencia);

            // Crear el primer estado del historial
            HistorialEstado estadoInicial = new HistorialEstado();
            estadoInicial.setIncidencia(nuevoIdIncidencia);
            estadoInicial.setFecha(incidencia.getFechaCreacion());
            estadoInicial.setEstadoAnterior(null);
            estadoInicial.setEstadoNuevo("Abierta");
            
            historialDAO.insert(conn, estadoInicial);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace(); // Imprime el error en la consola del servidor para depurar
            throw new RuntimeException("Error al registrar la incidencia: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private String findTecnicoConMenosIncidencias() {
        UsuarioDAO usuarioDAO = factory.getUsuarioDAO();
        IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();

        List<Usuario> tecnicos = usuarioDAO.findAll().stream()
                .filter(u -> "tecnico".equalsIgnoreCase(u.getRol()))
                .collect(Collectors.toList());

        if (tecnicos.isEmpty()) {
            throw new RuntimeException("No hay técnicos disponibles en el sistema.");
        }

        return tecnicos.stream()
                .min(Comparator.comparingInt(t -> incidenciaDAO.findByTecnico(t.getDni()).size()))
                .map(Usuario::getDni)
                .orElseThrow(() -> new RuntimeException("No se pudo determinar el técnico menos ocupado."));
    }

    @Override
    public int getCountByTecnico(String dniTecnico) {
        return factory.getIncidenciaDAO().getCountByTecnico(dniTecnico);
    }

    @Override
    public void reasignarTecnico(int idIncidencia, String nuevoTecnicoDni, String adminDni,
                                 String tecnicoActualNombre, String nuevoTecnicoNombre) throws Exception {
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();
            ComentarioDAO comentarioDAO = factory.getComentarioDAO();

            incidenciaDAO.updateTecnico(conn, idIncidencia, nuevoTecnicoDni);

            String mensajeLog = "Administrador ha reasignado el técnico " + 
                                tecnicoActualNombre + " por el " + nuevoTecnicoNombre;
            
            Comentario logComentario = new Comentario();
            logComentario.setIncidencia(idIncidencia);
            logComentario.setAutor(adminDni);
            logComentario.setMensaje(mensajeLog);
            logComentario.setFecha(LocalDateTime.now());

            comentarioDAO.insert(conn, logComentario);
            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al reasignar el técnico", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public void cambiarEstado(int idIncidencia, String nuevoEstado) throws Exception {
        Connection conn = null;
        try {
            HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();

            String estadoActual = historialDAO.findByIncidencia(idIncidencia).stream()
                    .max(Comparator.comparing(HistorialEstado::getFecha))
                    .map(HistorialEstado::getEstadoNuevo)
                    .orElse("Abierta");

            if ("Cerrada".equalsIgnoreCase(estadoActual)) {
                throw new Exception("No se puede modificar una incidencia cerrada.");
            }
            if (estadoActual.equalsIgnoreCase(nuevoEstado)) {
                return;
            }

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            HistorialEstado nuevoRegistro = new HistorialEstado();
            nuevoRegistro.setIncidencia(idIncidencia);
            nuevoRegistro.setFecha(LocalDateTime.now());
            nuevoRegistro.setEstadoAnterior(estadoActual);
            nuevoRegistro.setEstadoNuevo(nuevoEstado);

            historialDAO.insert(conn, nuevoRegistro);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public Incidencia getDetalleIncidencia(int idIncidencia) {
        
        IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();
        Incidencia incidencia = incidenciaDAO.findById(idIncidencia);

        if (incidencia == null) {
            return null;
        }

        HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
        List<HistorialEstado> historial = historialDAO.findByIncidencia(idIncidencia);
        
        historial.sort(Comparator.comparing(HistorialEstado::getFecha).reversed());
        incidencia.setHistorial(historial); 

        ComentarioDAO comentarioDAO = factory.getComentarioDAO(); 
        List<Comentario> comentarios = comentarioDAO.findByIncidencia(idIncidencia);

        comentarios.sort(Comparator.comparing(Comentario::getFecha));
        incidencia.setComentarios(comentarios); 

        calcularCamposDinamicos(incidencia, historial, comentarios);

        return incidencia;
    }

    private void calcularCamposDinamicos(Incidencia inc, List<HistorialEstado> historial, List<Comentario> comentarios) {
        
        HistorialEstado ultimoHistorial = (historial == null) ? null : historial.stream()
                .filter(h -> h != null && h.getFecha() != null) 
                .max(Comparator.comparing(HistorialEstado::getFecha))
                .orElse(null);

        String estado = (ultimoHistorial != null) ? ultimoHistorial.getEstadoNuevo() : "Abierta";
        inc.setEstadoActual(estado); 

        LocalDateTime fechaMax = inc.getFechaCreacion();

        if (ultimoHistorial != null && ultimoHistorial.getFecha().isAfter(fechaMax)) {
            fechaMax = ultimoHistorial.getFecha();
        }

        if (comentarios != null && !comentarios.isEmpty()) {
            LocalDateTime fechaUltimoComentario = comentarios.stream()
                .filter(c -> c.getFecha() != null)
                .max(Comparator.comparing(Comentario::getFecha))
                .map(Comentario::getFecha)
                .orElse(fechaMax);
            
            if (fechaUltimoComentario.isAfter(fechaMax)) {
                fechaMax = fechaUltimoComentario;
            }
        }

        inc.setFechaUltimaModificacion(fechaMax);
    }

    @Override
    public List<Incidencia> getIncidenciasUsuario(String dniSolicitante) {
        IncidenciaDAO dao = factory.getIncidenciaDAO();
        HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
        ComentarioDAO comentarioDAO = factory.getComentarioDAO(); 

        List<Incidencia> incidencias = dao.findBySolicitante(dniSolicitante);
        
        incidencias.forEach(inc -> {
            List<HistorialEstado> historial = historialDAO.findByIncidencia(inc.getId());
            List<Comentario> comentarios = comentarioDAO.findByIncidencia(inc.getId()); 
            calcularCamposDinamicos(inc, historial, comentarios); 
        });

        incidencias.sort(Comparator.comparing(
                Incidencia::getFechaCreacion,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return incidencias;
    }

    @Override
    public List<Incidencia> getIncidenciasTecnico(String dniTecnico) {
        IncidenciaDAO dao = factory.getIncidenciaDAO();
        HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
        ComentarioDAO comentarioDAO = factory.getComentarioDAO(); 

        List<Incidencia> incidencias = dao.findByTecnico(dniTecnico);

        incidencias.forEach(inc -> {
            List<HistorialEstado> historial = historialDAO.findByIncidencia(inc.getId());
            List<Comentario> comentarios = comentarioDAO.findByIncidencia(inc.getId()); 
            calcularCamposDinamicos(inc, historial, comentarios); 
        });

        incidencias.sort(Comparator.comparing(
                Incidencia::getFechaCreacion,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return incidencias;
    }

    @Override
    public List<Incidencia> getTodasLasIncidencias(String filtroEmpleadoDni, String filtroEstado) {
        IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();
        HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
        ComentarioDAO comentarioDAO = factory.getComentarioDAO(); 

        List<Incidencia> todas = incidenciaDAO.findAll();

        todas.forEach(inc -> {
            List<HistorialEstado> historial = historialDAO.findByIncidencia(inc.getId());
            List<Comentario> comentarios = comentarioDAO.findByIncidencia(inc.getId()); 
            calcularCamposDinamicos(inc, historial, comentarios); 
        });

        Stream<Incidencia> stream = todas.stream();

        if (filtroEmpleadoDni != null && !filtroEmpleadoDni.trim().isEmpty()) {
            stream = stream.filter(inc -> inc.getSolicitante() != null && inc.getSolicitante().equalsIgnoreCase(filtroEmpleadoDni));
        }

        if (filtroEstado != null && !filtroEstado.trim().isEmpty()) {
            stream = stream.filter(inc -> inc.getEstadoActual() != null && inc.getEstadoActual().equalsIgnoreCase(filtroEstado));
        }

        return stream.sorted(Comparator.comparing(
                Incidencia::getFechaCreacion,
                Comparator.nullsLast(Comparator.reverseOrder())
        )).collect(Collectors.toList());
    }

    @Override
    public EstadisticasDTO getEstadisticas() {
        IncidenciaDAO incidenciaDAO = factory.getIncidenciaDAO();
        HistorialEstadoDAO historialDAO = factory.getHistorialEstadoDAO();
        
        List<Incidencia> todasLasIncidencias = incidenciaDAO.findAll();
        
        EstadisticasDTO dto = new EstadisticasDTO();

        Map<String, Long> recuentoCategoria = todasLasIncidencias.stream()
                .filter(inc -> inc.getCategoria() != null)
                .collect(Collectors.groupingBy(
                        Incidencia::getCategoria, 
                        Collectors.counting()
                ));
        dto.setRecuentoPorCategoria(recuentoCategoria);

        Map<Incidencia, String> mapaEstadoActual = new HashMap<>();
        for (Incidencia inc : todasLasIncidencias) {
            String estado = historialDAO.findByIncidencia(inc.getId())
                    .stream()
                    .filter(h -> h.getFecha() != null)
                    .max(Comparator.comparing(HistorialEstado::getFecha))
                    .map(HistorialEstado::getEstadoNuevo)
                    .orElse("Abierta");
            mapaEstadoActual.put(inc, estado);
        }

        Map<String, Long> recuentoEstado = mapaEstadoActual.values().stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        dto.setRecuentoPorEstado(recuentoEstado);

        Optional<Incidencia> masAntigua = mapaEstadoActual.entrySet().stream()
                .filter(entry -> !"Cerrada".equalsIgnoreCase(entry.getValue()))
                .map(Map.Entry::getKey)
                .filter(inc -> inc.getFechaCreacion() != null)
                .min(Comparator.comparing(Incidencia::getFechaCreacion));
        
        dto.setIncidenciaMasAntigua(masAntigua.orElse(null));

        double totalHoras = 0;
        int resueltas = 0;

        List<Incidencia> cerradas = mapaEstadoActual.entrySet().stream()
                .filter(entry -> "Cerrada".equalsIgnoreCase(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Incidencia incCerrada : cerradas) {
            Optional<HistorialEstado> registroCierre = historialDAO.findByIncidencia(incCerrada.getId())
                    .stream()
                    .filter(h -> "Cerrada".equalsIgnoreCase(h.getEstadoNuevo()) && h.getFecha() != null) 
                    .min(Comparator.comparing(HistorialEstado::getFecha));

            if (registroCierre.isPresent() && incCerrada.getFechaCreacion() != null) {
                LocalDateTime fechaCreacion = incCerrada.getFechaCreacion();
                LocalDateTime fechaCierre = registroCierre.get().getFecha(); 
                
                if (fechaCierre != null) { 
                    totalHoras += Duration.between(fechaCreacion, fechaCierre).toHours();
                    resueltas++;
                }
            }
        }
        
        if (resueltas > 0) {
            dto.setTiempoMedioResolucionHoras(totalHoras / resueltas);
        } else {
            dto.setTiempoMedioResolucionHoras(0);
        }

        return dto;
    }

    @Override
    public void addComentario(int idIncidencia, String autorDni, String mensaje) throws Exception {
        
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setIncidencia(idIncidencia);
        nuevoComentario.setAutor(autorDni);
        nuevoComentario.setMensaje(mensaje);
        nuevoComentario.setFecha(LocalDateTime.now());

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false); 

            ComentarioDAO comentarioDAO = factory.getComentarioDAO();
            comentarioDAO.insert(conn, nuevoComentario);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al añadir el comentario", e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    @Override
    public Map<String, Object> exportarDatosSistema() {
        Connection conn = null;
        Map<String, Object> datos = new HashMap<>();
    
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            UsuarioDAO uDao = factory.getUsuarioDAO();
            IncidenciaDAO iDao = factory.getIncidenciaDAO();
            ComentarioDAO cDao = factory.getComentarioDAO(); 
            HistorialEstadoDAO hDao = factory.getHistorialEstadoDAO();
    
            // 1. Exportar USUARIOS
            List<Usuario> usuarios = uDao.findAll(); 
            List<Map<String, Object>> listaUsuarios = new ArrayList<>();
            for (Usuario u : usuarios) {
                Map<String, Object> uMap = new HashMap<>();
                uMap.put("dni", u.getDni());
                uMap.put("nombre", u.getNombre());
                uMap.put("apellidos", u.getApellidos());
                uMap.put("rol", u.getRol());
                uMap.put("passwd", u.getPasswd());
                listaUsuarios.add(uMap);
            }
            datos.put("usuarios", listaUsuarios);
    
            // 2. Exportar INCIDENCIAS con detalles
            List<Incidencia> incidencias = iDao.findAll();
            List<Map<String, Object>> listaIncidencias = new ArrayList<>();
    
            for (Incidencia inc : incidencias) {
                Map<String, Object> iMap = new HashMap<>();
                iMap.put("id", inc.getId());
                iMap.put("titulo", inc.getTitulo());
                iMap.put("descripcion", inc.getDescripcion());
                iMap.put("categoria", inc.getCategoria());
                iMap.put("solicitante", inc.getSolicitante());
                iMap.put("tecnico", inc.getTecnico());
    
                if (inc.getFechaCreacion() != null) {
                    iMap.put("fechaCreacion", inc.getFechaCreacion().toString());
                }
                if (inc.getFechaUltimaModificacion() != null) {
                    iMap.put("fechaUltimaModificacion", inc.getFechaUltimaModificacion().toString());
                }
    
                List<HistorialEstado> historial = hDao.findByIncidencia(inc.getId());
                List<Map<String, Object>> listaHistorial = new ArrayList<>();
                for (HistorialEstado h : historial) {
                    Map<String, Object> hMap = new HashMap<>();
                    hMap.put("estadoAnterior", h.getEstadoAnterior());
                    hMap.put("estadoNuevo", h.getEstadoNuevo());
                    hMap.put("fecha", h.getFecha() != null ? h.getFecha().toString() : null);
                    listaHistorial.add(hMap);
                }
                iMap.put("historial", listaHistorial);
    
                List<Comentario> comentarios = cDao.findByIncidencia(inc.getId());
                List<Map<String, Object>> listaComentarios = new ArrayList<>();
                for (Comentario c : comentarios) {
                    Map<String, Object> cMap = new HashMap<>();
                    cMap.put("autor", c.getAutor());
                    cMap.put("mensaje", c.getMensaje());
                    cMap.put("fecha", c.getFecha() != null ? c.getFecha().toString() : null);
                    listaComentarios.add(cMap);
                }
                iMap.put("comentarios", listaComentarios);
    
                listaIncidencias.add(iMap);
            }
            datos.put("incidencias", listaIncidencias);
    
            return datos;
        } catch (Exception e) {
            throw new RuntimeException("Error exportando backup completo: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ex) {}
        }
    }
    
    @Override
    public void importarDatosSistema(Map<String, Object> datos) throws Exception {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN
    
            UsuarioDAO uDao = factory.getUsuarioDAO();
            IncidenciaDAO iDao = factory.getIncidenciaDAO();
            ComentarioDAO cDao = factory.getComentarioDAO();
            HistorialEstadoDAO hDao = factory.getHistorialEstadoDAO();
            CategoriaDAO catDao = factory.getCategoriaDAO(); // <--- IMPORTANTE: DAO PARA VALIDAR
    
            // 1. Importar Usuarios
            if (datos.containsKey("usuarios")) {
                List<Map<String, Object>> listaU = (List<Map<String, Object>>) datos.get("usuarios");
                for (Map<String, Object> uMap : listaU) {
                    String dni = (String) uMap.get("dni");
                    
                    if (uDao.findByDni(conn, dni) == null) {
                        Usuario u = new Usuario();
                        u.setDni(dni);
                        u.setNombre((String) uMap.get("nombre"));
                        u.setApellidos((String) uMap.get("apellidos"));
                        u.setRol((String) uMap.get("rol"));
                        u.setPasswd((String) uMap.get("passwd"));
                        uDao.insert(conn, u);
                    }
                }
            }
    
            // 2. Importar Incidencias
            if (datos.containsKey("incidencias")) {
                List<Map<String, Object>> listaI = (List<Map<String, Object>>) datos.get("incidencias");
                for (Map<String, Object> iMap : listaI) {
                    Number idOriginal = (Number) iMap.get("id");
                    
                    // --- VALIDACIÓN DE CATEGORÍA ---
                    String nombreCategoria = (String) iMap.get("categoria");
                    if (nombreCategoria != null && catDao.findByNombre(conn, nombreCategoria) == null) {
                        throw new Exception("Error de importación: La categoría '" + nombreCategoria + 
                                            "' (Incidencia ID " + idOriginal + ") no existe en el sistema.");
                    }
                    // -------------------------------

                    int idIncidenciaFinal;
                    
                    // Verificamos existencia
                    boolean existe = (idOriginal != null && iDao.findById(conn, idOriginal.intValue()) != null);

                    if (!existe) {
                        Incidencia inc = new Incidencia();
                        inc.setTitulo((String) iMap.get("titulo"));
                        inc.setDescripcion((String) iMap.get("descripcion"));
                        inc.setCategoria(nombreCategoria);
                        inc.setSolicitante((String) iMap.get("solicitante"));
                        inc.setTecnico((String) iMap.get("tecnico"));
                        
                        inc.setFechaCreacion(parseFechaFlexible((String) iMap.get("fechaCreacion")));
                        inc.setFechaUltimaModificacion(parseFechaFlexible((String) iMap.get("fechaUltimaModificacion")));
    
                        idIncidenciaFinal = iDao.insert(conn, inc);
                    } else {
                        idIncidenciaFinal = idOriginal.intValue();
                    }

                    // Importar Historial (Merge)
                    if (iMap.containsKey("historial")) {
                        List<Map<String, Object>> listaH = (List<Map<String, Object>>) iMap.get("historial");
                        List<HistorialEstado> historialEnBD = hDao.findByIncidencia(conn, idIncidenciaFinal); 

                        for (Map<String, Object> hMap : listaH) {
                            String estAnt = (String) hMap.get("estadoAnterior");
                            String estNue = (String) hMap.get("estadoNuevo");
                            LocalDateTime fecha = parseFechaFlexible((String) hMap.get("fecha"));

                            boolean yaEsta = historialEnBD.stream().anyMatch(h -> 
                                (h.getEstadoNuevo() != null && h.getEstadoNuevo().equals(estNue)) && 
                                (h.getFecha() != null && h.getFecha().isEqual(fecha))
                            );

                            if (!yaEsta) {
                                HistorialEstado h = new HistorialEstado();
                                h.setIncidencia(idIncidenciaFinal);
                                h.setEstadoAnterior(estAnt);
                                h.setEstadoNuevo(estNue);
                                h.setFecha(fecha);
                                hDao.insert(conn, h);
                            }
                        }
                    }

                    // Importar Comentarios (Merge)
                    if (iMap.containsKey("comentarios")) {
                        List<Map<String, Object>> listaC = (List<Map<String, Object>>) iMap.get("comentarios");
                        List<Comentario> comentariosEnBD = cDao.findByIncidencia(conn, idIncidenciaFinal);

                        for (Map<String, Object> cMap : listaC) {
                            String autor = (String) cMap.get("autor");
                            String mensaje = (String) cMap.get("mensaje");
                            LocalDateTime fecha = parseFechaFlexible((String) cMap.get("fecha"));

                            boolean yaEsta = comentariosEnBD.stream().anyMatch(c -> 
                                (c.getAutor() != null && c.getAutor().equals(autor)) && 
                                (c.getMensaje() != null && c.getMensaje().equals(mensaje)) &&
                                (c.getFecha() != null && c.getFecha().isEqual(fecha))
                            );

                            if (!yaEsta) {
                                Comentario c = new Comentario();
                                c.setIncidencia(idIncidenciaFinal);
                                c.setAutor(autor);
                                c.setMensaje(mensaje);
                                c.setFecha(fecha);
                                cDao.insert(conn, c);
                            }
                        }
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    private LocalDateTime parseFechaFlexible(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            return LocalDateTime.parse(fechaStr);
        } catch (DateTimeParseException e1) {
            try {
                if (fechaStr.length() == 16) {
                    return LocalDateTime.parse(fechaStr + ":00");
                }
            } catch (DateTimeParseException e2) {
                System.err.println("No se pudo parsear la fecha: " + fechaStr);
            }
        }
        
        return LocalDateTime.now();
    }
}
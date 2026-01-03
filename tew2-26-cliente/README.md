# Práctica 2: Tecnologías de cliente

## Equipo TEW2 - 26

- [Pelayo Iglesias Manzano](https://github.com/PelayoIglesiass) - UO266600@uniovi.es
- [David Vallina Antuña](https://github.com/DavidVA03) - UO290072@uniovi.es 


## Contenido

- [Mapa de Navegación](#mapa-de-navegación1)
- [Descripción de las acciones asociadas a las transiciones de pantalla](#descripción-de-las-acciones-asociadas-a-las-transiciones-de-pantalla-2)
- [Decisiones de implementación](#decisiones-de-implementación3)


## Mapa de Navegación[^1]

A continuación se presenta el mapa de navegación de la aplicación:

````mermaid
stateDiagram-v2
    direction TB

    state "index.html (Login)" as Login
    [*] --> Login
    Login --> Login: Error Credenciales

    state "index.html" as SPA {
        
        state "home.html (Dashboard)" as Home
        Login --> Home:Login OK

        Home --> Home: HealthCheck (Alert [Todos los roles])
        Home --> Login: Logout (Cerrar Sesión)

        state "Módulo de Incidencias (Técnico y Usuario)" as ModuloIncidencias {
            state "listado.html" as Listado
            state "detalle.html" as Detalle
            state "formulario.html" as Formulario

            Home --> Listado: Menú "Listar Incidencias"
            Home --> Formulario: Menú "Registrar Incidencia" (Solo Usuario)

            Listado --> Detalle: Btn "Ver"
            
            Formulario --> Listado: Btn "Guardar" / "Cancelar"
            
            Detalle --> Listado: Btn "Volver al listado"
            Detalle --> Detalle: Acciones (Comentar[Técnico y Usuario] / Cambiar Estado [Usuario])
        }

        state "Módulo de Administración (Admin)" as ModuloAdmin {
            state "listado-usuarios.html" as ListUsers
            state "alta-usuario.html" as AltaUser
            state "estadisticas.html" as Stats
            state "backup.html" as Backup

            %% Navegación desde Home
            Home --> ListUsers: Menú "Listar Usuarios"
            Home --> AltaUser: Menú "Alta Usuarios"
            Home --> Stats: Menú "Estadísticas"
            Home --> Backup: Menú "Exportar/Importar"

            %% Flujos Internos
            ListUsers --> AltaUser: Btn "Alta" (vía Menú o Btn)
            AltaUser --> ListUsers: Btn "Dar de Alta" / "Cancelar"
            
            Backup --> Backup: Btn "Importar" / "Descargar"
            
            %% Conexión entre módulos
            Stats --> Detalle: Btn "Ver Detalle" (Incidencia Antigua)
        }
    }
````

## Descripción de las acciones asociadas a las transiciones de pantalla [^2]

Entramos en `index.html` y realizamos el login, esto cargará la interfaz donde veremos el menú dinámico de la aplicación. Dependiendo del rol (administrador, técnico o usuario) asignado al usuario con el que hemos hecho login tendremos diferentes funcionalidades y transiciones.

### Administrador
Nos encontramos visualizando `index.html` y `home.html`:
* Pulsando en **TewIncidencias** volvemos al `home.html`.
* Si tocamos en **Func. Incidencias, Func. Usuarios o Func. Sistema** se expanden los submenús con las opciones del administrador:
  * **Listar Incidencias** (en Func. Incidencias) nos lleva a `listado.html`, donde se muestran las incidencias del sistema, además de botones **Ver**. Si tocamos uno de estos botones visualizaremos `detalle.html` con todos los detalles de la incidencia seleccionada y también un botón **Volver al listado** que nos devuelve a `listado.html`.
  * **HealthCheck Sistema** (en Func. Sistema) nos mostrará un alert con los datos del servidor y la base de datos. Al pulsar **Aceptar** se cerrará y permaneceremos en la pantalla en la que estábamos.
  * **Listar Usuarios** (en Func. Usuarios) nos redirige a `listado-usuarios.html` y visualizaremos una lista con todos los usuarios del sistema junto a sus datos personales.
  * **Alta Usuarios** (en Func. Usuarios) muestra el formulario de `alta-usuario.html` y podremos rellenarlo con datos personales para crear un usuario. Si apretamos **Cancelar** se nos mostrará `listado-usuarios.html`, pero si damos en **Dar de Alta** aparecerá un cartel con la contraseña del usuario creado y un botón **Ir al listado**, que será lo único que podremos pulsar en la pantalla y nos llevará a `listado-usuarios.html`.
  * **Estadísticas** (en Func. Incidencias) nos lleva a `estadisticas.html`, donde se muestran las estadísticas sobre incidencias del sistema. En el panel de incidencia más antigua tendremos un botón **Ver detalle** que nos redirigirá a `detalle.html` de dicha incidencia.
  * **Exportar/Importar Incidencias** (en Func. Incidencias) muestra la vista `backup.html`, donde tendremos 2 opciones, **Descargar Backup** para descargar un json con todos los datos de incidencias del sistema y mostrando una alert (Backup descargado correctamente) y botón **Aceptar** para cerrarla, o **Importar Backup** (previa selección de archivo json válido), mostrando una alert de aviso y los botones **Aceptar o Cancelar**. Si pulsamos en **Aceptar** se lleva a cabo la importación y se muestra un alert avisando de su éxito y otro **Aceptar** para cerrarla, pero si pulsamos en **Cancelar** se quitará la alert y seguiremos en `backup.html`.
  * En todas las vistas hay un botón **Volver a inicio** que nos envía de vuelta al `home.html`.
* A la derecha del todo tendremos un submenú con el nombre del usuario y su rol entre paréntesis, el cual si pulsamos muestra la opción para **Cerrar Sesión** y nos lleva de nuevo al login `index.html`.

### Usuario
Nos encontramos visualizando `index.html` y `home.html`:
* Pulsando en **TewIncidencias** volvemos al `home.html`.
* Si tocamos en **Func. Incidencias o Func. Sistema** se expanden los submenús con las opciones del usuario:
  * **Listar Incidencias** (en Func. Incidencias) nos lleva a `listado.html`, donde se muestran mis incidencias registradas. Aquí encontraremos botones **Ver** en cada fila, si tocamos uno de estos botones visualizaremos `detalle.html` con la información de la incidencia, el historial de cambios y un chat para enviar comentarios. También tendremos un botón Volver al listado que nos devuelve a `listado.html`.
  * **HealthCheck Sistema** (en Func. Sistema) nos mostrará un alert con los datos del servidor y la base de datos. Al pulsar **Aceptar** se cerrará y permaneceremos en la pantalla en la que estábamos.
  * **Registrar Incidencia** (en Func. Incidencias) muestra el formulario de `formulario.html`, donde podremos rellenar con datos de una nueva incidencia. Si apretamos **Cancelar** se nos mostrará `listado.html`, pero si damos en **Guardar** se registrará la incidencia en el sistema, aparecerá una alerta de éxito y seremos redirigidos automáticamente a `listado.html` con la nueva incidencia añadida.
  * En todas las vistas hay un botón **Volver a inicio** que nos envía de vuelta al `home.html`.
* A la derecha del todo tendremos un submenú con el nombre del usuario y su rol entre paréntesis, el cual si pulsamos muestra la opción para **Cerrar Sesión** y nos lleva de nuevo al login `index.html`.

### Técnico
Nos encontramos visualizando `index.html` y `home.html`:
* Pulsando en **TewIncidencias** volvemos al `home.html`.
* Si tocamos en **Func. Incidencias o Func. Sistema** se expanden los submenús con las opciones del técnico:
  * **Listar Incidencias** (en Func. Incidencias) nos lleva a `listado.html`, donde se muestran las incidencias asignadas o generales del sistema, además de botones **Ver**. Si tocamos uno de estos botones visualizaremos `detalle.html` con todos los detalles. En esta vista, a diferencia de otros roles, veremos un panel para cambiar el estado de la incidencia (En progreso, Pendiente, Cerrar). Al pulsar cualquiera de estos botones, se actualiza el estado y el historial en la misma pantalla `detalle.html`. También disponemos del botón **Volver al listado** que nos devuelve a `listado.html`.
  * **HealthCheck Sistema** (en Func. Sistema) nos mostrará un alert con los datos del servidor y la base de datos. Al pulsar Aceptar se cerrará y permaneceremos en la pantalla en la que estábamos.
  * En todas las vistas hay un botón **Volver a inicio** que nos envía de vuelta al `home.html`.
* A la derecha del todo tendremos un submenú con el nombre del usuario y su rol entre paréntesis, el cual si pulsamos muestra la opción para **Cerrar Sesión** y nos lleva de nuevo al login `index.html`.

## Decisiones de implementación[^3]
## Servidor (Backend)
La aplicación se ha estructurado siguiendo el patrón de capas, separando la API REST de la lógica de negocio y la persistencia.
### /api
`Configuration.java` - Utilizamos @ApplicationPath("/api") para tener un prefijo común para todos los endpoints. Con esto separamos las rutas de la API para evitar conflictos de rutas. Es el punto de entrada para JAX-RS.
`UsuarioApi.java` - Usamos esta clase para gestionar todas las operaciones de los usuarios, registro, autenticación, etc.
`IncidenciaApi.java` - Con esta clase manejamos todo el ciclo de vida de una incidencia, permitiendo el filtrado por rol y ordenación.
`ComentarioApi.java` - Aquí gestionamos el chat de comentarios dentro de una incidencia.
`HistorialEstadoApi.java` - Para manejar los cambios de estado de una incidencia.

### /infrastructure
`CORSFilter.java` - Necesitábamos implementar un filtro que interceptase todas las respuestas del servidor y gestionar la seguridad del navegador mediante cabeceras CORS.
`GestorSesion.java` - Con esta clase gestionamos el estado de autenticación de los usuarios en el servidor. Actúa como almacenamiento en memoria. Asocia los Tokens de seguridad (UUID) generados en el login con los objetos Usuario.

### /filter 
`AuthenticationFilter.java`: Filtro que intercepta todas las peticiones HTTP entrantes antes de llegar a la API. Verifica la cabecera Authorization: Bearer <token>, comprueba su validez en `GestorSesion` y aborta con 401 Unauthorized si el token no es válido.

## Cliente (Frontend)
Las vistas de la aplicación con la razón de su implementación:
* `index.html`: Es el contenedor principal de la aplicación (SPA). Define la estructura base (barra de navegación, pie de página, contenedor de contenido dinámico) y carga las librerías necesarias.
* `home.html`: Pantalla de bienvenida que se visualiza tras iniciar sesión. Muestra información básica del usuario logueado.
* `listado.html`: Muestra la tabla de incidencias. Incluye filtros de búsqueda (para administradores), botones para visualizar el detalle de cada incidencia y opción de reordenar la tabla en sentido ascendente/descendente en cada atributo (menos la fecha de última modificación).
* `formulario.html`: Contiene el formulario para registrar una nueva incidencia, solicitando título, descripción y categoría.
* `detalle.html`: Vista completa de una incidencia específica. Muestra su información, el historial de cambios de estado y el chat de comentarios.
* `listado-usuarios.html`: Vista de administración que muestra una tabla con todos los usuarios registrados en el sistema junto con su información personal menos la contraseña, permitiendo su ordenación por atributos.
* `alta-usuario.html`: Formulario exclusivo de administradores para dar de alta nuevos empleados (técnicos o usuarios) completando con sus datos personales y generar sus contraseñas.
* `estadisticas.html`: Panel de control para administradores. Muestra métricas como el tiempo medio de resolución, la incidencia más antigua y recuentos de incidencias por estado/categoría.
* `backup.html`: Interfaz de administración para exportar (descargar JSON) e importar (subir JSON) los datos del sistema.

### crudIncidencias.js
Es el archivo que hemos implementado para encargarse de la lógica de datos, la comunicación con las API, mostrar los datos en pantalla y recibir las acciones del cliente, para ello. Las decisiones para implementar las clases que forman el patrón MVC son:

1. Clase Model: La creamos para que los datos y la conexión con el servidor estén en un solo sitio. Aquí guardamos la lista de incidencias y el usuario para tenerlos a mano sin tener que pedirlos al servidor todo el rato.

2. Clase View: Su único trabajo es tocar el HTML. Si queremos cambiar el diseño, solo tocamos esta clase y la lógica sigue funcionando igual. Se encarga de mostrar y ocultar pantallas o rellenar las tablas.

3. Clase Controller: Es el que conecta los clics del usuario con las otras dos clases, de esta forma el Model y la View no deben comunicarse directamente. El Controller recibe la orden del usuario, le pasa los datos al Model para que los envíe, y luego le dice a la View que actualice la pantalla.

Gracias a todo esto cumplimos con el patrón que se nos pide en el proyecto.


## Gestión de Seguridad y Tokens (Autenticación Stateless)
Como nuestra API es REST y no guarda el estado (stateless), para la seguridad hemos implementado un sistema de Tokens Bearer que funciona así:

* Login: Cuando el usuario se identifica, el servidor comprueba los datos y, si están bien, genera un Token único (un UUID) en el GestorSesion.

* Almacenamiento: El cliente (JavaScript) recibe ese token y lo guardamos en el sessionStorage del navegador para no perderlo al recargar.

* Uso: En cada petición privada que hacemos (como listar o borrar), inyectamos el token en la cabecera HTTP: Authorization: Bearer <TOKEN>. Funciona como una "llave" temporal.

* Logout: Al salir, simplemente borramos el token del navegador y avisamos al servidor para que lo invalide.

Hemos decidido enviar el token en la cabecera Authorization y no en otros sitios por dos razones:
1. Por Seguridad: No debemos enviar tokens en la URL porque se quedan guardadas en el historial del navegador y en los logs del sistema.

2. Por el Estándar HTTP: No podemos enviarlo dentro del JSON (el cuerpo del mensaje) porque muchas peticiones, como las de tipo GET (usadas para listar incidencias o usuarios), no llevan cuerpo. Usar la cabecera nos permite tener un mecanismo estándar que funciona igual para todos los tipos de petición (GET, POST, PUT, DELETE).

## Funcionalidades
* Login/Logout: Para la seguridad, hemos optado por un sistema Stateless basado en Tokens Bearer. El token JWT se almacena en sessionStorage para garantizar que la sesión se destruya al cerrar la pestaña.
* Listado de Incidencias: Hemos implementado la obtención de datos mediante una llamada GET para que el servidor filtre los datos según el rol.
* Detalle: La vista de detalle reutiliza la misma pantalla para todos los roles, pero renderiza condicionalmente los botones de acción. Hemos decidido ocultar las transiciones de estado inválidas (como volver a "Abierta" o seleccionar el estado actual) modificando el DOM en tiempo de ejecución dentro de la clase View para prevenir errores de lógica antes de llamar a la API. En cuanto a añadir comentarios , disponible para usuarios y técnicos, hemos optado por un formato de chat y así se van viendo los comentarios anteriores, junto al DNI y la fecha.
* Gestión de Usuarios: Hemos restringido la carga del menú y la vista exclusivamente al rol 'administrador' desde el Controller. Para el alta, hemos decidido generar la contraseña en el servidor y devolverla en la respuesta, mostrándola en un panel temporal en el cliente, evitando así enviarla por la red o almacenarla en texto plano, y al cerrarlo redireccionamos al listado de usuarios.
* Estadísticas: Hemos delegado el cálculo al Backend, exponiendo un endpoint específico (/estadisticas) que devuelve un DTO ya procesado.
* Backup (Importar/Exportar): Para la exportación, se utiliza la API del navegador para generar un archivo .json descargable. Para la importación, se usa FileReader para procesar el JSON localmente antes de enviarlo al servidor, permitiendo una validación previa del formato.
* HealthCheck: En cuanto al healthcheck del sistema hemos tenido en cuenta que en función de lo que falle, base de datos, apache o wildfly, se muestre un healthcheck y un mensaje de error diferente. También hemos querido tener en cuenta cuando fallan 2 cosas simultáneamente.

**Para asegurarnos de la limpieza en general tanto del código como de la aplicación, hemos decidido tratar de eliminar cualquier issue o hidden que pudiese aparecer en consola.**

---
# Examen
1. Modificación de la Base de Datos: Hemos creado una nueva tabla CATEGORIAS y se ha modificado la tabla INCIDENCIAS.
Decidimos utilizar el NOMBRE de la categoría como clave primaria (PK) para simplificar la relación y mantener la compatibilidad con los datos existentes en INCIDENCIAS. Eliminamos el CHECK constraint que limitaba los valores de categoría.

2. Servidor: En model, persistence, business damos soporte a la nueva tabla.
* Modelo: Creamos de la clase Categoria.java.
* DAO: Creamos de CategoriaDAO y CategoriaDAOImpl.
* Implementamos métodos findAll() para el desplegable e insert() para el alta.
* Factories: Actualizamos PersistenceFactory y ServiceFactory para inyectar las nuevas dependencias.
* Validación: Modificamos IncidenciaServiceImpl.java. Antes de insertar o importar una incidencia, el sistema ahora verifica contra BBDD si la categoría existe usando catDao.findByNombre().

3. Capa de Servicios API:
* Nuevo Endpoint: CategoriaApi.java mapeado en /categorias: GET /: Devuelve JSON con todas las categorías. POST /: Recibe un JSON y crea una nueva categoría.

4. Capa de Cliente:
* Vista (formulario.html): Eliminamos las etiquetas <option> estáticas del <select id="categoria">.
* Controlador: En el método cargarVistaCrear, realizamos una llamada a model.getCategorias() antes de mostrar el formulario.
* Vista: Añadimos el método renderCategoriasSelect() que recibe el JSON y puebla el DOM dinámicamente.
* Nueva Vista de Administración: alta-categoria.html con un formulario simple.
* Navegación: Modificamos renderDynamicMenu en la clase View para mostrar la opción "Añadir Categoría" solo si el rol es administrador.
* Implementamos los métodos crearCategoria en el Modelo y guardarCategoria en el Controlador para gestionar la petición POST.
---

[^1]: Mapa de navegación de la aplicación.

[^2]: Descripción textual de cada una de las acciones asociadas a transiciones en el mapa de pantallas

[^3]: Descripción de las decisiones de implementación tomadas en cada una de las funcionalidades.

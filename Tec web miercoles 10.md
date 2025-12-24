Jueves 18 entregar el trabajo en grupo

Viernes 19 examen teoría y practica

SIMULACRO DE EXAMEN

* ¿Qué ocurre si un servlet lanza una excepción que no es capturada por ningún filtro ni manejador?

La petición vuelve al servlet anterior

**El contenedor la convierte en un error 500**



* El método destroy de un servlet se ejecuta cuando

termina cada petición

el usuario cierra el nav

**Se para el contenedor o se redespliega la app. destroy() marca el fin del ciclo de vida del servlet**

La sesión expira





* Si un atributo existe en sesión y aplicación con el mismo nombre ¿Qué se muestra al evaluar $atributo? JSP

El primero creado

**El de sesión. Se prioriza sesión por encima de application**

El de application

Error de ambigüedad



* Que afirmación sobre cookies es correcta

No se envían en peticiones HTTPS

**El cliente puede modificarlas. Las cookies están en el cliente, puede alterarlas**

Pueden almacenar objetos java

El servidor puede leerlas sin que el cliente las envíe



* Un bean en RequestScope es adecuado cuando

Se necesita conservar datos entre vistas

El bean contiene datos globales

Se necesita interacción AJX compleja

**Los datos se usan una sola vez por petición. RequestScope es para datos por petición**



* Que hace la anotación @FacesValidator en JSF

Que se aplique automáticamente

Que se ejecute antes de los convertidores

**Que el validador se registre en el contenedor del JSF. Registra el validador en el motor JSF**

Que sea único e inyectable





* Un servlet mapeado con /\*:

solo recibe JSP

No puede coexistir con otros servlets

solo recibe peticiones estáticas

**Recibe todas las peticiones sin archivo físico. Captura rutas que no coinciden con recursos reales**



* un listener de sesión no puede

detectar destrucción de sesión

detectar creación de sesión

**Evitar que una sesión expire. La expiración depende del contenedor, no del listener**

Saber cuando cambia un atributo



* Que protocolo usa la web para la comunicación entre cliente y servidor

FTP

**HTTP. HTTP es el protocolo estándar de la comunicación web**

SMTP

SSH



* Que permite el patrón Front Crontoller en una aplicación web

Crear sesiones por vista

Ejecutar múltiples servlets simultáneamente

Centralizar el manejo de peticiones. Un inico punto de entreada

Evitar el uso de JSP



* En JSF, un validation error provoca

el bean se destruya

que se reinicie la vista

se ejecutan métodos action

**Se omiten las fases posteriores. El ciclo JSF se detiene tras error de validación**



* Que fase de JSF procesa AJAX

Solo en Render Response

Solo en Update Model

**En cada fase del ciclo estándar. AJAX sigue el mismo ciclo JSF**

En una fase indenpendiente



* Cual de las siguientes tecnologías se usa para definir la estructura de una pagina web

CSS

**HTML. HTML define la estructura y contenido básico de una página**

Java

SQL



* Que ocurre si un filtro omite chain.doFilter()

El servlet se ejecuta igualmente

**La petición nunca llega al destino. Sin chain.doFilter() no continua la cadena**

Se produce un ciclo infinito

El contenedor ll ignora



* Que archivo suele utilizarse para describir la configuración de una aplicación web Java clásica

pom.xml

index.jsp

**web.xml. Es el descriptor estándar de despliegue en aplicaciones Java EE tradicionales**

faces-config.xml



* Ventaja de applicationScope

Es el mas seguro

Evita multithrading

Lo hace serializable

\*\*Permite compartir info entre todos los usuarios.\*\*applicationScope **Es global**



* Que es un convertidor estándar de JSF

h:convertText

c:convertNumber

f:convertDouble

**f:convertInteger. JSF proporciona convertidores básicos como Integer**



* Un error típico usando Primefaces es

Usar componentes sin Bean

Que no pueda usarse con JSF

**No declarar el manespace p: Sin xmlns:p no se interpretan etiquetas Primefaces**



* Que hace init-param en web.xml

Define variables globales

Configura codificación general

inicializa sesión

**Configura los parametros para un servlets.Define parámetros específicos del servlet**



* ¿Cuando se pierde sessionScope en JSF?

En navegación implícita

Al refrescar la vista

**Cuando expira la sesión. La expiración borra todos los datos de sesión**

En peticiones AJAX









 


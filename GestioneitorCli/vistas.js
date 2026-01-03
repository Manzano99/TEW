function showLogin() {
	fetch("login.html?v=").then(res => res.text()).then(html =>
		document.getElementsByTagName("main")[0].innerHTML = html);
}

function showFormulario() {
	fetch("formulario.html?v=").then(res => res.text()).then(html =>
		document.getElementsByTagName("main")[0].innerHTML = html);
}

function showListado() {
	fetch("listado.html?v=").then(res => res.text()).then(html =>
		document.getElementsByTagName("main")[0].innerHTML = html);
}

function crearAlumno() {
    const alumno = {
        idUser: document.getElementById('idUser').value,
        nombre: document.getElementById('nombre').value,
        apellidos: document.getElementById('apellidos').value,
        email: document.getElementById('email').value
    };

    fetch('http://localhost:8090/gestioneitor/api/alumno', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(alumno)
    })
    .then(response => {
        if (response.ok) {
            mostrarMensaje('Alumno creado con exito!', 'green');
            document.getElementById('idUser').value = '';
            document.getElementById('nombre').value = '';
            document.getElementById('apellidos').value = '';
            document.getElementById('email').value = '';
        } else {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
    })
    .catch(error => {
        console.error('Error al crear alumno:', error);
        mostrarMensaje(`Error de conexion: ${error.message}`, 'red');
    });
}

function solicitarDatosAlumnos() {
    fetch('http://localhost:8090/gestioneitor/api/alumno')
        .then(response => response.json()).catch(err => alert(err))
        .then(data => {
            let cuerpoTabla = document.getElementsByTagName('tbody')[0];
            cuerpoTabla.innerHTML = '';
            data.forEach(alumno => {
                cuerpoTabla.innerHTML += `<tr>
                <td>${alumno.idUser}</td>
                <td>${alumno.nombre}</td>
                <td>${alumno.apellidos}</td>
                <td>${alumno.email}</td></tr>`
            });
        });
}

let orden = 1; 

function ordenarTabla(indiceColumna) {
    const tbody = document.getElementsByTagName("tbody")[0];
    const filas = Array.from(tbody.rows);

    filas.sort((filaA, filaB) => {
        const textoA = filaA.cells[indiceColumna].innerText.toLowerCase();
        const textoB = filaB.cells[indiceColumna].innerText.toLowerCase();

        return textoA.localeCompare(textoB) * orden;
    });

    orden = orden * -1;
    filas.forEach(fila => tbody.appendChild(fila));
}

function mostrarMensaje(mensaje, color) {
    const messageContainer = document.getElementById('demo');
    if (messageContainer) {
        messageContainer.innerHTML = mensaje;
        messageContainer.style.color = color;
    }
}
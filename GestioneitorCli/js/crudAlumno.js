class Model {
    constructor() {
        this.alumnos = null;
    }

    setToken(token) {
        sessionStorage.setItem("token", token);
    }

    async login(user) {
        try {
            const respuesta = await fetch('http://localhost:8090/gestioneitor/api/user/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user)
            });

            if (respuesta.ok) {
                return await respuesta.text();
            } else {
                return "";
            }
        } catch (error) {
            console.error(error);
            return "";
        }
    }

    async load() {
        try {
            const token = sessionStorage.getItem("token");
            const respuesta = await fetch(`http://localhost:8090/gestioneitor/api/alumno/${token}`);
            this.alumnos = await respuesta.json();
        } catch (error) {
            console.error(error);
        }
    }

    async add(alumno) {
        try {
            alumno.token = sessionStorage.getItem("token");

            await fetch('http://localhost:8090/gestioneitor/api/alumno', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(alumno)
            });
        } catch (error) {
            console.error(error);
        }
    }

    async edit(alumno) {
        try {
            alumno.token = sessionStorage.getItem("token");

            await fetch('http://localhost:8090/gestioneitor/api/alumno', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(alumno)
            });
        } catch (error) {
            console.error(error);
        }
    }

    async remove(idAlumno) {
        try {
            const token = sessionStorage.getItem("token");
            await fetch(`http://localhost:8090/gestioneitor/api/alumno/${idAlumno}/${token}`, {
                method: 'DELETE'
            });
        } catch (error) {
            console.error(error);
        }
    }

    find(idAlumno) {
        return this.alumnos.find(alumno => alumno.idUser == idAlumno);
    }
}

class View {
    showListado() {
        return fetch("listado.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => document.getElementsByTagName("main")[0].innerHTML = html);
    }

    showFormulario() {
        return fetch("formulario.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => document.getElementsByTagName("main")[0].innerHTML = html);
    }

    showLogin() {
        return fetch("login.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => document.getElementsByTagName("main")[0].innerHTML = html);
    }

    showAlumnosInTable(alumnos) {
        const tabla = document.getElementById('tabla-alumnos');
        if (tabla && alumnos) {
            tabla.innerHTML = '';
            alumnos.forEach(alumno => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${alumno.idUser}</td>
                    <td>${alumno.nombre}</td>
                    <td>${alumno.apellidos}</td>
                    <td>${alumno.email}</td>
                    <td>
                        <button class="btn btn-primary" onclick="controller.editarAlumno(${alumno.id})">Editar</button>
                    </td>
                    <td>
                        <button class="btn btn-danger" onclick="controller.eliminarAlumno(${alumno.id})">Eliminar</button>
                    </td>
                `;
                tabla.appendChild(tr);
            });
        }
    }

    loadAlumnoFromForm() {
        let idVal = document.getElementById('id').value;
        if (idVal === "") {
            idVal = 0; 
        }

        return {
            id: idVal,
            idUser: document.getElementById('idUser').value,
            nombre: document.getElementById('nombre').value,
            apellidos: document.getElementById('apellidos').value,
            email: document.getElementById('email').value
        };
    }

    loadAlumnoInForm(alumno) {
        document.getElementById('id').value = alumno ? alumno.id : '';
        document.getElementById('idUser').value = alumno ? alumno.idUser : '';
        document.getElementById('nombre').value = alumno ? alumno.nombre : '';
        document.getElementById('apellidos').value = alumno ? alumno.apellidos : '';
        document.getElementById('email').value = alumno ? alumno.email : '';

        const titulo = document.querySelector("main h1");
        const boton = document.getElementById("boton");

        if (alumno) {
            titulo.innerHTML = "Editar usuario";
            boton.value = "Actualizar";
        } else {
            titulo.innerHTML = "Alta de usuario";
            boton.value = "Crear Alumno";
        }
    }

    loadUserFromForm() {
        return {
            username: document.getElementById("username").value,
            password: document.getElementById("passwd").value
        };
    }

    loadUserInForm(user) {
        document.getElementById("username").value = user.username;
        document.getElementById("passwd").value = user.password;
    }
}

class Controller {
    constructor(model, view) {
        this.model = model;
        this.view = view;
    }

    init() {
        document.getElementById('btnAltaAlumno').addEventListener('click', () => {
            this.view.showFormulario().then(() => {
                this.view.loadAlumnoInForm(null);
            });
        });

        document.getElementById('btnListadoAlumnos').addEventListener('click', () => {
            this.view.showListado();
            this.model.load().then(() => {
                this.view.showAlumnosInTable(this.model.alumnos);
            });
        });
    }

    login() {
        const user = this.view.loadUserFromForm();
        this.model.login(user).then(token => {
            if (token === "") {
                console.log("Login incorrecto");
            } else {
                console.log("Token recibido:", token);
                this.model.setToken(token);
                this.model.load().then(async () => {
                    await this.view.showListado();
                    this.view.showAlumnosInTable(this.model.alumnos);
                });
            }
        });
    }

    enviarFormulario() {
        const alumno = this.view.loadAlumnoFromForm();

        let accion;
        if (alumno.id) {
            accion = this.model.edit(alumno);
        } else {
            accion = this.model.add(alumno);
        }

        accion.then(() => {
            this.view.showListado().then(() => {
                this.model.load().then(() => {
                    this.view.showAlumnosInTable(this.model.alumnos);
                });
            });
        });
    }

    eliminarAlumno(idAlumno) {
        this.model.remove(idAlumno).then(() => {
            this.model.load().then(() => {
                this.view.showAlumnosInTable(this.model.alumnos);
            });
        });
    }

    editarAlumno(idAlumno) {
        const alumno = this.model.alumnos.find(a => a.id == idAlumno);
        this.view.showFormulario().then(() => {
            this.view.loadAlumnoInForm(alumno);
        });
    }
}

var model = new Model();
var view = new View();
var controller = new Controller(model, view);

window.addEventListener('load', () => {
    // INICIALIZACIONES
    controller.init();
    // DEF. FUNCIONES DE USUARIO
    // DEF. MANEJADORES DE EVENTOS
});
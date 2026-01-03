class Model {
    constructor() {
        this.BASE_URL = "http://localhost:8090/tew1_26/api";
        this.incidencias = [];
        this.usuarios = [];
        this.currentUser = null;
        
        this.ordenActual = {
            columna: null,
            tipo: 'asc' 
        };
    }

    saveSession(user) {
        sessionStorage.setItem("user", JSON.stringify(user));
        this.currentUser = user;
    }

    loadSession() {
        const u = sessionStorage.getItem("user");
        if (u) {
            this.currentUser = JSON.parse(u);
            return this.currentUser;
        }
        return null;
    }

    logout() {
        sessionStorage.removeItem("user");
        sessionStorage.removeItem("token");
        sessionStorage.removeItem("lastView");
        this.currentUser = null;
    }

    async login(dni, password) {
        try {
            const response = await fetch(`${this.BASE_URL}/usuarios/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ dni: dni, passwd: password })
            });

            if (response.ok) {
                const data = await response.json();
                if (data.token && data.usuario) {
                    sessionStorage.setItem("token", data.token);
                    this.saveSession(data.usuario);
                    return data.usuario;
                }
            }
            return null;
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    async loadIncidencias() {
        if (!this.currentUser) return;
        try {
            const { rol, dni } = this.currentUser;
            const response = await fetch(`${this.BASE_URL}/incidencias?rol=${rol}&dni=${dni}`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });

            if (response.ok) {
                this.incidencias = await response.json();
                this.incidencias.sort((a, b) => {
                    const fechaA = a.fechaCreacion || "";
                    const fechaB = b.fechaCreacion || "";
                    return String(fechaB).localeCompare(String(fechaA));
                });
            }
        } catch (error) {
            console.error(error);
        }
    }

    async getDetalle(id) {
        try {
            const response = await fetch(`${this.BASE_URL}/incidencias/${id}`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });
            if (response.ok) return await response.json();
        } catch (error) {
            console.error(error);
        }
        return null;
    }

    async add(incidencia) {
        try {
            const response = await fetch(`${this.BASE_URL}/incidencias`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: JSON.stringify(incidencia)
            });

            if (response.ok) return true;

            const msg = await response.text();
            alert("Error del servidor: " + msg);
            return false;

        } catch (error) {
            console.error(error);
            alert("Error de conexión: " + error);
            return false;
        }
    }

    async changeStatus(id, nuevoEstado) {
        try {
            await fetch(`${this.BASE_URL}/incidencias/${id}/estado`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: nuevoEstado
            });
        } catch (error) {
            console.error(error);
        }
    }

    async addComentario(comentario) {
        try {
            await fetch(`${this.BASE_URL}/comentarios`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: JSON.stringify(comentario)
            });
        } catch (error) {
            console.error(error);
        }
    }

    async loadUsuarios() {
        try {
            const response = await fetch(`${this.BASE_URL}/usuarios`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });

            if (response.ok) {
                this.usuarios = await response.json();
                return this.usuarios;
            } else {
                return [];
            }
        } catch (error) {
            console.error(error);
            return [];
        }
    }

    async crearUsuario(usuario) {
        try {
            const response = await fetch(`${this.BASE_URL}/usuarios`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: JSON.stringify(usuario)
            });

            if (response.ok) {
                return await response.json();
            }
            return null;
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    async getEstadisticas() {
        try {
            const response = await fetch(`${this.BASE_URL}/incidencias/estadisticas`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });
            if (response.ok) return await response.json();
        } catch (error) {
            console.error(error);
        }
        return null;
    }

    async downloadBackup() {
        try {
            const response = await fetch(`${this.BASE_URL}/incidencias/sistema/backup`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = "backup_tew.json";
                document.body.appendChild(a);
                a.click();
                a.remove();
                return true;
            }
        } catch (error) {
            console.error(error);
        }
        return false;
    }

    async uploadBackup(datosJson) {
        try {
            let usuariosRaw = [];
            let incidenciasRaw = [];

            if (Array.isArray(datosJson)) {
                incidenciasRaw = datosJson;
            } else {
                usuariosRaw = datosJson.usuarios || [];
                incidenciasRaw = datosJson.incidencias || [];
            }

            const incidenciasNuevas = incidenciasRaw.map(inc => {
                let fechaISO = inc.fechaCreacion;
                if (fechaISO && fechaISO.length === 16) {
                    fechaISO = fechaISO + ":00";
                }
                
                const fechaObj = new Date(fechaISO);
                const fechaFormateada = fechaObj.toLocaleString('es-ES', {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                }).replace(',', '');

                const estadoActual = inc.estadoActual || (inc.tecnico ? "En progreso" : "Abierta");

                return {
                    id: inc.id,
                    titulo: inc.titulo,
                    descripcion: inc.descripcion,
                    categoria: inc.categoria,
                    solicitante: inc.solicitante,
                    tecnico: inc.tecnico || null,
                    
                    fechaCreacion: fechaISO,
                    fechaUltimaModificacion: inc.fechaUltimaModificacion || fechaISO,
                    estadoActual: estadoActual,
                    
                    fechaCreacionFormateada: fechaFormateada,
                    fechaUltimaModificacionFormateada: inc.fechaUltimaModificacionFormateada || fechaFormateada,
                    
                    comentarios: inc.comentarios || [],
                    historial: inc.historial || []
                };
            });

            // 3. CAMBIO: Usamos usuariosRaw en la comprobación
            if (usuariosRaw.length === 0 && incidenciasNuevas.length === 0) {
                alert("El backup no contiene datos procesables.");
                return true; 
            }

            const payload = {
                usuarios: usuariosRaw, // 4. CAMBIO: Enviamos todos los usuarios del JSON
                incidencias: incidenciasNuevas,
                backupDate: new Date().toISOString()
            };

            const response = await fetch(`${this.BASE_URL}/incidencias/sistema/backup`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: JSON.stringify(payload)
            });

            if (response.status === 401 || response.status === 403) {
                throw new Error("Sesión caducada. Haz Logout.");
            }

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error servidor (${response.status}): ${errorText}`);
            }

            return true;

        } catch (error) {
            alert("Error: " + error.message);
            return false;
        }
    }

    async getSystemHealth() {
        try {
            const response = await fetch(`${this.BASE_URL}/usuarios/health`, {
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                }
            });
            const data = await response.json();
            return { success: response.ok, data: data };
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    ordenarDatos(lista, campo) {
        if (this.ordenActual.columna === campo) {
            this.ordenActual.tipo = (this.ordenActual.tipo === 'asc') ? 'desc' : 'asc';
        } else {
            this.ordenActual.columna = campo;
            this.ordenActual.tipo = 'asc';
        }

        const orden = this.ordenActual.tipo === 'asc' ? 1 : -1;

        lista.sort((a, b) => {
            let valorA = a[campo];
            let valorB = b[campo];

            if (campo === 'nombreCompleto' && valorA === undefined) {
                valorA = (a.nombre + " " + a.apellidos).toLowerCase();
                valorB = (b.nombre + " " + b.apellidos).toLowerCase();
            }

            if (campo.includes('fecha')) {
                const parseFecha = (valor) => {
                    if (!valor) return 0;
                    if (valor.includes('/')) {
                        let [fechaPart, horaPart] = valor.split(' ');
                        let [dia, mes, anio] = fechaPart.split('/');
                        return new Date(`${mes}/${dia}/${anio} ${horaPart || '00:00'}`).getTime();
                    }
                    return new Date(valor).getTime();
                };
                const fechaA = parseFecha(valorA);
                const fechaB = parseFecha(valorB);
                return ((isNaN(fechaA) ? 0 : fechaA) - (isNaN(fechaB) ? 0 : fechaB)) * orden;
            }

            if (valorA == null) valorA = "";
            if (valorB == null) valorB = "";

            if (typeof valorA === 'number' && typeof valorB === 'number') {
                return (valorA - valorB) * orden;
            }

            return String(valorA).toLowerCase().localeCompare(String(valorB).toLowerCase()) * orden;
        });
    }

    async getCategorias() {
        try {
            const response = await fetch(`${this.BASE_URL}/categorias`, {
                headers: { 'Authorization': 'Bearer ' + sessionStorage.getItem("token") }
            });
            if (response.ok) return await response.json();
        } catch (error) {
            console.error(error);
        }
        return [];
    }

    async crearCategoria(categoriaObj) {
        try {
            const response = await fetch(`${this.BASE_URL}/categorias`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + sessionStorage.getItem("token")
                },
                body: JSON.stringify(categoriaObj)
            });
            
            if (response.ok) return true;
            
            const errorText = await response.text();
            alert("Error: " + errorText);
            return false;
        } catch (error) {
            console.error(error);
            return false;
        }
    }
}

class View {
    constructor() {
        this.mainContainer = document.getElementById("main-content");
    }

    // --- MÉTODOS DE LOGIN Y SPA ---
    
    showLogin() {
        // Ocultar elementos de navegación y aside
        const nav = document.getElementById('main-navbar');
        const aside = document.getElementById('sidebar-info');
        
        if (nav) nav.classList.add('d-none');
        if (aside) aside.classList.add('d-none');
        
        const main = document.getElementById('main-content');
        main.className = 'col-12'; 
        
        main.innerHTML = `
            <div class="d-flex justify-content-center align-items-center" style="min-height: 50vh;">
                <div class="card shadow p-4" style="max-width: 400px; width: 100%;">
                    <div class="text-center mb-3">
                        <i class="bi bi-person-circle display-1 text-primary"></i>
                        <h3 class="mt-3">Iniciar Sesión</h3>
                    </div>
                    <form onsubmit="event.preventDefault(); controller.doLogin()">  
                        <div class="mb-3">
                            <label for="dni" class="form-label fw-bold">DNI</label>
                            <input type="text" id="dni" class="form-control" placeholder="Ej: 71000000A" autocomplete="username" required>
                        </div>
                        <div class="mb-4">
                            <label for="password" class="form-label fw-bold">Contraseña</label>
                            <input type="password" id="password" class="form-control" placeholder="********" autocomplete="current-password" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 py-2">Entrar</button>
                    </form>
                    
                    <div id="login-error" class="alert alert-danger mt-3 d-none text-center" role="alert"></div>
                </div>
            </div>
        `;
    }

    // Restaura el diseño de la aplicación (barra lateral, menú, etc.)
    _activarLayoutApp() {
        const nav = document.getElementById('main-navbar');
        const aside = document.getElementById('sidebar-info');

        if (nav) nav.classList.remove('d-none');
        if (aside) aside.classList.remove('d-none');

        const main = document.getElementById('main-content');
        main.className = 'col-lg-9 col-md-8'; // Restaura las columnas
    }

    // --- RESTO DE MÉTODOS ---

    renderDynamicMenu(rol) {
        const menuInc = document.getElementById('menu-incidencias');
        if (menuInc) {
            let htmlInc = `<li><a class="dropdown-item" href="#" onclick="controller.cargarVistaListado()">Listar incidencias</a></li>`;
            
            if (rol === 'usuario') {
                htmlInc += `<li><hr class="dropdown-divider"></li>`;
                htmlInc += `<li><a class="dropdown-item" href="#" onclick="controller.cargarVistaCrear()">Registrar Incidencia</a></li>`;
            }
            
            if (rol === 'administrador') {
                htmlInc += `<li><hr class="dropdown-divider"></li>`;
                htmlInc += `<li><a class="dropdown-item" href="#" onclick="controller.cargarEstadisticas()">Estadísticas</a></li>`;
                htmlInc += `<li><hr class="dropdown-divider"></li>`;
                htmlInc += `<li><a class="dropdown-item" href="#" onclick="controller.cargarVistaBackup()">Exportar/Importar Incidencias</a></li>`;
                htmlInc += `<li><hr class="dropdown-divider"></li>`;
                htmlInc += `<li><a class="dropdown-item" href="#" onclick="controller.cargarVistaAltaCategoria()">Añadir Categoría</a></li>`;
            }
            menuInc.innerHTML = htmlInc;
        }

        const navItemUsuarios = document.getElementById('nav-item-usuarios');
        const menuUser = document.getElementById('menu-usuarios');
        
        if (rol === 'administrador') {
            if (navItemUsuarios) navItemUsuarios.classList.remove('d-none');
            if (menuUser) {
                let htmlUser = `<li><a class="dropdown-item" href="#" onclick="controller.cargarListadoUsuarios()">Listar usuarios</a></li>`;
                htmlUser += `<li><hr class="dropdown-divider"></li>`;
                htmlUser += `<li><a class="dropdown-item" href="#" onclick="controller.cargarVistaAltaUsuario()">Alta Usuarios</a></li>`;
                menuUser.innerHTML = htmlUser;
            }
        } else {
            if (navItemUsuarios) navItemUsuarios.classList.add('d-none');
        }

        const menuSys = document.getElementById('menu-sistema');
        if (menuSys) {
            menuSys.innerHTML = `<li><a class="dropdown-item" href="#" onclick="controller.ejecutarHealthCheck()">HealthCheck Sistema</a></li>`;
        }
    }

    renderHeaderUser(user) {
        const el = document.getElementById('header-user-info');
        if (el && user) el.textContent = `${user.nombre} (${user.rol})`;
    }

    showHome() {
        this._activarLayoutApp();
        return fetch("vistas/home.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    showListado() {
        this._activarLayoutApp();
        return fetch("vistas/listado.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    showFormulario() {
        this._activarLayoutApp();
        return fetch("vistas/formulario.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    showDetalle() {
        this._activarLayoutApp();
        return fetch("vistas/detalle.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    renderHomeUser(user) {
        const el = document.getElementById('home-nombre-usuario');
        if (el && user) el.textContent = user.nombre;
    }

    renderIncidenciasTable(incidencias, rol) {
        const tbody = document.getElementById('tabla-incidencias-body');

        if (tbody && incidencias) {
            tbody.innerHTML = '';
            incidencias.forEach(inc => {
                const tr = document.createElement('tr');
                let celdaFechaMod = '';
                if (rol === 'administrador') {
                    let fechaTexto = inc.fechaUltimaModificacionFormateada; 
                    
                    if (!fechaTexto && inc.fechaUltimaModificacion) {
                        const raw = inc.fechaUltimaModificacion;
                        if (Array.isArray(raw)) {
                             const d = new Date(raw[0], raw[1]-1, raw[2], raw[3]||0, raw[4]||0);
                             fechaTexto = d.toLocaleString();
                        } else {
                             fechaTexto = new Date(raw).toLocaleString();
                        }
                    }
                    
                    if (!fechaTexto || fechaTexto === '-') {
                        fechaTexto = inc.fechaCreacionFormateada || '-';
                    }
                    celdaFechaMod = `<td>${fechaTexto}</td>`;
                }

                tr.innerHTML = `
                    <td>${inc.id}</td>
                    <td>${inc.titulo}</td>
                    <td>${inc.fechaCreacionFormateada || '-'}</td>
                    ${celdaFechaMod} 
                    <td>
                        <span class="badge bg-${this._getColor(inc.estadoActual)}">
                            ${inc.estadoActual}
                        </span>
                    </td>
                    <td>${inc.categoria}</td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm" onclick="controller.verDetalle(${inc.id})">
                            <i class="bi bi-eye"></i> Ver
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    }

    renderDetalle(inc, userRol) {
        if (!inc) return;

        document.getElementById('det-titulo').textContent = inc.titulo;
        document.getElementById('det-desc').textContent = inc.descripcion;
        document.getElementById('det-cat').textContent = inc.categoria;
        document.getElementById('det-solicitante').textContent = inc.solicitante;
        document.getElementById('det-tecnico').textContent = inc.tecnico || 'Sin asignar';

        const badge = document.getElementById('det-estado');
        badge.textContent = inc.estadoActual;
        badge.className = `badge bg-${this._getColor(inc.estadoActual)}`;

        const panelTecnico = document.getElementById('panel-acciones-tecnico');
        
        if (userRol === 'tecnico' && inc.estadoActual !== 'Cerrada') {
            panelTecnico.classList.remove('d-none');
            
            document.querySelectorAll('.btn-estado').forEach(btn => {
                const estadoDestino = btn.dataset.estado;
                
                if (estadoDestino === inc.estadoActual || estadoDestino === 'Abierta') {
                    btn.style.display = 'none';
                    return; 
                }

                btn.style.display = 'inline-block';

                const newBtn = btn.cloneNode(true);
                btn.parentNode.replaceChild(newBtn, btn);
                newBtn.onclick = () => controller.cambiarEstado(inc.id, estadoDestino);
            });
        } else {
            panelTecnico.classList.add('d-none');
        }

        const historialBody = document.getElementById('historial-body');
        const historialVacio = document.getElementById('historial-vacio');

        if (historialBody) {
            historialBody.innerHTML = '';
            if (inc.historial && inc.historial.length > 0) {
                historialVacio.classList.add('d-none');
                inc.historial.forEach(h => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>${h.fechaFormateada || h.fecha || '-'}</td>
                        <td><span class="badge bg-${this._getColor(h.estadoAnterior)}">${h.estadoAnterior}</span></td>
                        <td><span class="badge bg-${this._getColor(h.estadoNuevo)}">${h.estadoNuevo}</span></td>
                    `;
                    historialBody.appendChild(tr);
                });
            } else {
                historialVacio.classList.remove('d-none');
            }
        }

        const chatBox = document.getElementById('chat-box');
        chatBox.innerHTML = '';
        if (inc.comentarios && inc.comentarios.length > 0) {
            inc.comentarios.forEach(c => {
                const div = document.createElement('div');
                div.className = "p-2 border-bottom";
                div.innerHTML = `
                    <strong>${c.autor}</strong>
                    <small class="text-muted">(${c.fechaFormateada || '-'})</small>:
                    <br> ${c.mensaje}
                `;
                chatBox.appendChild(div);
            });
        } else {
            chatBox.innerHTML = '<p class="text-muted text-center">No hay comentarios aún.</p>';
        }

        document.getElementById('id-incidencia-hidden').value = inc.id;
        const formComentarios = document.getElementById('form-comentario-container');
        const esRolPermitido = (userRol === 'usuario' || userRol === 'tecnico');

        if (esRolPermitido && inc.estadoActual !== 'Cerrada') {
            formComentarios.classList.remove('d-none');
        } else {
            formComentarios.classList.add('d-none');
        }
    }

    getLoginData() {
        return {
            dni: document.getElementById('dni').value,
            pass: document.getElementById('password').value
        };
    }

    getIncidenciaData() {
        return {
            titulo: document.getElementById('titulo').value,
            descripcion: document.getElementById('descripcion').value,
            categoria: document.getElementById('categoria').value
        };
    }

    getComentarioData() {
        return {
            idIncidencia: document.getElementById('id-incidencia-hidden').value,
            texto: document.getElementById('txt-comentario').value
        };
    }

    _getColor(est) {
        if (!est) return 'primary';
        switch (est) {
            case 'Abierta': return 'success';
            case 'En progreso': return 'warning';
            case 'Cerrada': return 'danger';
            case 'Pendiente de usuario': return 'info';
            default: return 'primary';
        }
    }

    showListadoUsuarios() {
        this._activarLayoutApp();
        return fetch("vistas/listado-usuarios.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    renderUsuariosTable(usuarios) {
        const tbody = document.getElementById('tabla-usuarios-body');
        const msg = document.getElementById('msg-no-usuarios');

        if (!tbody) return;
        tbody.innerHTML = '';

        if (usuarios && usuarios.length > 0) {
            if (msg) msg.classList.add('d-none');

            usuarios.forEach(u => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${u.dni || u.login}</td>
                    <td>${u.nombre}</td>
                    <td>${u.apellidos}</td>
                    <td>${u.nombreCompleto || u.nombre + ' ' + u.apellidos}</td>
                    <td>
                        <span class="badge bg-${u.rol === 'administrador' ? 'success' : (u.rol === 'tecnico' ? 'warning' : 'info')}">
                            ${u.rol}
                        </span>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            if (msg) msg.classList.remove('d-none');
        }
    }

    showAltaUsuario() {
        this._activarLayoutApp();
        return fetch("vistas/alta-usuario.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => {
                this.mainContainer.innerHTML = html;
                document.getElementById('form-alta-usuario').onsubmit = (e) => {
                    e.preventDefault();
                    controller.registrarUsuario();
                };
            });
    }

    getDatosAltaUsuario() {
        return {
            dni: document.getElementById('new-dni').value,
            nombre: document.getElementById('new-nombre').value,
            apellidos: document.getElementById('new-apellidos').value,
            rol: document.getElementById('new-rol').value
        };
    }

    showPasswordModal(password) {
        const el = document.getElementById('generated-pass-display');
        if (el) el.textContent = password;
        const modalEl = document.getElementById('modalPassword');
        const modal = new bootstrap.Modal(modalEl);
        modal.show();
    }

    showEstadisticas() {
        this._activarLayoutApp();
        return fetch("vistas/estadisticas.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    renderEstadisticas(stats) {
        if (!stats) return;

        const horas = stats.tiempoMedioResolucionHoras || 0;
        document.getElementById('stat-tiempo').textContent = Number(horas).toFixed(2);

        const antigua = stats.incidenciaMasAntigua;
        const bodyAntigua = document.getElementById('stat-antigua-body');

        if (antigua) {
            document.getElementById('antigua-titulo').textContent = antigua.titulo;
            document.getElementById('antigua-id').textContent = antigua.id;
            document.getElementById('antigua-fecha').textContent = antigua.fechaCreacionFormateada || '-';

            const container = document.getElementById('antigua-btn-container');
            container.innerHTML = `
                <button class="btn btn-outline-danger mt-2" onclick="controller.verDetalle(${antigua.id})">
                    Ver Detalle <i class="bi bi-arrow-right"></i>
                </button>
            `;
        } else {
            bodyAntigua.innerHTML = '<p class="text-muted text-center">¡Enhorabuena! No hay incidencias pendientes.</p>';
        }

        this._renderStatList('lista-estados', stats.recuentoPorEstado);
        this._renderStatList('lista-categorias', stats.recuentoPorCategoria);
    }

    _renderStatList(elementId, mapData) {
        const list = document.getElementById(elementId);
        if (!list) return;
        list.innerHTML = '';

        if (mapData) {
            Object.keys(mapData).forEach(key => {
                const count = mapData[key];
                let badgeClass = 'bg-primary';
                if (elementId === 'lista-estados') badgeClass = `bg-${this._getColor(key)}`;

                const li = document.createElement('li');
                li.className = "list-group-item d-flex justify-content-between align-items-center";
                li.innerHTML = `
                    ${key}
                    <span class="badge ${badgeClass} rounded-pill">${count}</span>
                `;
                list.appendChild(li);
            });
        } else {
            list.innerHTML = '<li class="list-group-item">Sin datos</li>';
        }
    }

    showBackup() {
        this._activarLayoutApp();
        return fetch("vistas/backup.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    getFileContent() {
        const fileInput = document.getElementById('file-backup');
        const file = fileInput.files[0];

        return new Promise((resolve, reject) => {
            if (!file) {
                reject("No se ha seleccionado ningún archivo.");
                return;
            }
            const reader = new FileReader();
            reader.onload = (e) => {
                try {
                    const json = JSON.parse(e.target.result);
                    resolve(json);
                } catch (err) {
                    reject("El archivo no es un JSON válido.");
                }
            };
            reader.readAsText(file);
        });
    }

    showAltaCategoria() {
        this._activarLayoutApp();
        return fetch("vistas/alta-categoria.html?v=" + Date.now())
            .then(res => res.text())
            .then(html => this.mainContainer.innerHTML = html);
    }

    getDatosCategoria() {
        return {
            nombre: document.getElementById('cat-nombre').value
        };
    }

    renderCategoriasSelect(categorias) {
        const select = document.getElementById('categoria');
        if (!select) return;

        select.innerHTML = '<option value="" disabled selected>Seleccione una categoría...</option>';
        
        categorias.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.nombre;
            option.textContent = cat.nombre;
            select.appendChild(option);
        });
    }
}

class Controller {
    constructor(model, view) {
        this.model = model;
        this.view = view;
    }

    init() {
        const user = this.model.loadSession();

        if (user) {
            this.view._activarLayoutApp();
            this.view.renderHeaderUser(user);
            this.view.renderDynamicMenu(user.rol);

            const btnLogout = document.getElementById('btn-logout');
            if (btnLogout) {
                btnLogout.onclick = () => this.logout();
            }

            const lastView = sessionStorage.getItem("lastView");

            switch (lastView) {
                case 'listado':
                    this.cargarVistaListado();
                    break;
                case 'crear':
                    if (user.rol === 'usuario') this.cargarVistaCrear();
                    else this.cargarVistaHome();
                    break;
                case 'usuarios':
                    if (user.rol === 'administrador') this.cargarListadoUsuarios();
                    else this.cargarVistaHome();
                    break;
                case 'altaUsuario':
                    if (user.rol === 'administrador') this.cargarVistaAltaUsuario();
                    else this.cargarVistaHome();
                    break;
                case 'estadisticas':
                    if (user.rol === 'administrador') this.cargarEstadisticas();
                    else this.cargarVistaHome();
                    break;
                case 'backup':
                    if (user.rol === 'administrador') this.cargarVistaBackup();
                    else this.cargarVistaHome();
                    break;
                case 'altaCategoria':
                    if (user.rol === 'administrador') this.cargarVistaAltaCategoria();
                    else this.cargarVistaHome();
                    break;
                default:
                    this.cargarVistaHome();
                    break;
            }
        } else {
            this.cargarVistaLogin();
        }
    }

    cargarVistaLogin() {
        sessionStorage.clear();
        this.view.showLogin();
    }

    async doLogin() {
        const { dni, pass } = this.view.getLoginData();
        const errorDiv = document.getElementById('login-error');
        
        if (errorDiv) {
            errorDiv.classList.add('d-none'); // Ocultar
            errorDiv.textContent = '';        // Limpiar texto anterior
        }

        const user = await this.model.login(dni, pass);
        
        if (user) {
            this.init();
        } else {
            if (errorDiv) {
                // Inyectamos el texto solo cuando ocurre el error
                errorDiv.textContent = "Usuario o contraseña incorrectos";
                errorDiv.classList.remove('d-none');
            }
        }
    }

    logout() {
        this.model.logout();
        this.cargarVistaLogin();
    }

    async cargarVistaHome() {
        sessionStorage.setItem("lastView", "home");
        await this.view.showHome();
        this.view.renderHomeUser(this.model.currentUser);
    }

    async cargarVistaListado() {
        if (!this.model.currentUser) {
            this.cargarVistaLogin();
            return;
        }

        sessionStorage.setItem("lastView", "listado");
        await this.view.showListado();
        
        await this.model.loadIncidencias();
        if (!this.model.currentUser) return;

        const panelFiltros = document.getElementById('panel-filtros-admin');
        const headerFechaMod = document.getElementById('th-fecha-mod');

        if (this.model.currentUser.rol === 'administrador') {
            if (panelFiltros) panelFiltros.classList.remove('d-none');
            if (headerFechaMod) headerFechaMod.classList.remove('d-none');
        } else {
            if (panelFiltros) panelFiltros.classList.add('d-none');
            if (headerFechaMod) headerFechaMod.classList.add('d-none');
        }

        this.view.renderIncidenciasTable(this.model.incidencias, this.model.currentUser.rol);
    }

    aplicarFiltros() {
        const estado = document.getElementById('filtro-estado').value;
        const textoEmpleado = document.getElementById('filtro-empleado').value.toLowerCase().trim();
        const listaFiltrada = this.model.incidencias.filter(inc => {
            const coincideEstado = estado === "" || inc.estadoActual === estado;
            const tecnico = (inc.tecnico || "").toLowerCase();
            const solicitante = (inc.solicitante || "").toLowerCase();
            const coincideEmpleado = textoEmpleado === "" || 
                                     tecnico.includes(textoEmpleado) || 
                                     solicitante.includes(textoEmpleado);

            return coincideEstado && coincideEmpleado;
        });
        this.view.renderIncidenciasTable(listaFiltrada, this.model.currentUser.rol);
    }

    limpiarFiltros() {
        document.getElementById('filtro-estado').value = "";
        document.getElementById('filtro-empleado').value = "";
        this.view.renderIncidenciasTable(this.model.incidencias, this.model.currentUser.rol);
    }

    async cargarVistaCrear() {
        sessionStorage.setItem("lastView", "crear");
        await this.view.showFormulario();
        const categorias = await this.model.getCategorias();
        this.view.renderCategoriasSelect(categorias);
    }

    async verDetalle(id) {
        const inc = await this.model.getDetalle(id);

        if (inc) {
            await this.view.showDetalle();
            this.view.renderDetalle(inc, this.model.currentUser.rol);
        }
    }

    async guardarIncidencia() {
        const data = this.view.getIncidenciaData();
        
        const regexTitulo = /^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s.-]+$/;
        if (!data.titulo || !regexTitulo.test(data.titulo)) {
            alert("El título no es válido: Solo se permiten letras, números, espacios, puntos y guiones.");
            return;
        }

        if (!data.categoria || data.categoria === "") {
            alert("Por favor, selecciona una categoría.");
            return;
        }

        data.solicitante = this.model.currentUser.dni;
        
        const exito = await this.model.add(data);
        
        if (exito) {
            alert("Incidencia creada con éxito");
            await this.cargarVistaListado();
        }
    }

    async cambiarEstado(id, nuevoEstado) {
        if (!confirm(`¿Cambiar estado a ${nuevoEstado}?`)) return;
        await this.model.changeStatus(id, nuevoEstado);
        await this.verDetalle(id);
    }

    async enviarComentario() {
        const { idIncidencia, texto } = this.view.getComentarioData();
        if (!texto) return;
        const comentario = {
            incidencia: idIncidencia,
            autor: this.model.currentUser.dni,
            mensaje: texto
        };
        await this.model.addComentario(comentario);
        await this.verDetalle(idIncidencia);
    }

    async cargarListadoUsuarios() {
        sessionStorage.setItem("lastView", "usuarios");
        await this.view.showListadoUsuarios();
        const usuarios = await this.model.loadUsuarios();
        this.view.renderUsuariosTable(usuarios);
    }

    async cargarVistaAltaUsuario() {
        sessionStorage.setItem("lastView", "altaUsuario");
        await this.view.showAltaUsuario();
    }

    async registrarUsuario() {
        const user = this.view.getDatosAltaUsuario();
        
        const dniRegex = /^\d{8}[A-Z]$/;
        if (!dniRegex.test(user.dni)) {
            alert("Error en el DNI: Debe tener 8 números y terminar en una letra mayúscula (Ej: 12345678Z).");
            return;
        }

        if (!user.nombre || user.nombre.trim() === "" || /\d/.test(user.nombre)) {
            alert("Error en el Nombre: Es obligatorio y no puede contener números.");
            return;
        }

        if (!user.apellidos || user.apellidos.trim() === "" || /\d/.test(user.apellidos)) {
            alert("Error en los Apellidos: Son obligatorios y no pueden contener números.");
            return;
        }

        user.login = user.dni;
        const usuarioCreado = await this.model.crearUsuario(user);
        if (usuarioCreado && usuarioCreado.passwd) {
            this.view.showPasswordModal(usuarioCreado.passwd);
        } else {
            alert("Error: No se pudo crear el usuario. Verifique si el DNI ya existe.");
        }
    }

    cerrarModalPassword() {
        const modalEl = document.getElementById('modalPassword');
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();

        document.body.classList.remove('modal-open');
        const backdrop = document.querySelector('.modal-backdrop');
        if (backdrop) backdrop.remove();

        this.cargarListadoUsuarios();
    }

    async cargarEstadisticas() {
        sessionStorage.setItem("lastView", "estadisticas");
        await this.view.showEstadisticas();
        const stats = await this.model.getEstadisticas();
        this.view.renderEstadisticas(stats);
    }

    async cargarVistaBackup() {
        sessionStorage.setItem("lastView", "backup");
        await this.view.showBackup();
    }

    async exportarBackup() {
        const exito = await this.model.downloadBackup();
        if (exito) {
            alert("Backup descargado correctamente.");
        } else {
            alert("Error al descargar el backup.");
        }
    }

    async importarBackup() {
        try {
            const datos = await this.view.getFileContent();
            if (confirm("Se van a importar los datos. Los registros existentes se mantendrán. ¿Continuar?")) {
                const exito = await this.model.uploadBackup(datos);
                if (exito) {
                    alert("Importación completada con éxito.");
                } else {
                    alert("Error en la importación. Revisa el formato del archivo.");
                }
            }
        } catch (error) {
            alert(error);
        }
    }

    async ejecutarHealthCheck() {
        let apacheVivo = true;
        try {
            const urlAntiCache = window.location.pathname + "?t=" + Date.now();
            const resp = await fetch(urlAntiCache, { method: 'HEAD', cache: 'no-store' });
            if (!resp.ok) apacheVivo = false;
        } catch (e) {
            apacheVivo = false;
        }

        const resultado = await this.model.getSystemHealth();
        let data = {};
        let estadoGlobal = "";

        if (resultado && resultado.data) {
            data = resultado.data;
        } else {
            data = { databaseStatus: 'Unknown', serverTime: null };
        }

        if (!apacheVivo) {
            // CASO A: Apache Caído
            data.webServerStatus = 'DOWN';
            
            if (resultado) {
                // Apache caído, pero Wildfly activo
                estadoGlobal = "Parcialmente Operativo";
                data.error = "El servidor web (Apache) no responde, pero Wildfly sigue activo.";
            } else {
                // Todo caído (Apache y Wildfly)
                estadoGlobal = "Inaccesible";
                data.error = "Fallo total de conexión (Apache y Wildfly caídos).";
                data.databaseStatus = "Inaccesible";
            }

        } else if (resultado) {
            // CASO B: Apache vivo y Wildfly activo
            if (data.databaseStatus === 'DOWN') {
                data.webServerStatus = 'OK'; 
            }
            estadoGlobal = resultado.success ? "Operativo" : "Problemas detectados";

        } else {
            // CASO C: Apache Vivo pero Wildfly caído
            estadoGlobal = "Fallo de API";
            data.webServerStatus = 'OK';
            data.databaseStatus = 'Inaccesible';
            data.error = "No se pudo contactar con el servidor de aplicaciones.";
        }

        const horaServidor = data.serverTime ? new Date(data.serverTime).toLocaleString() : 'N/A';
        const mensaje = `
            Reporte de Estado del Sistema
            -----------------------------
            Estado Global: ${estadoGlobal}
            Hora Servidor: ${horaServidor || 'N/A'}
            Servidor Web: ${data.webServerStatus || 'Unknown'}
            Base de Datos: ${data.databaseStatus || 'Unknown'}
            ${data.error ? `Detalle Error: ${data.error}` : ''}
        `;

        alert(mensaje);
    }

    ordenarIncidencias(campo) {
        this.model.ordenarDatos(this.model.incidencias, campo);
        this.aplicarFiltros();
    }

    ordenarUsuarios(campo) {
        this.model.ordenarDatos(this.model.usuarios, campo);
        this.view.renderUsuariosTable(this.model.usuarios);
    }

    async cargarVistaAltaCategoria() {
        sessionStorage.setItem("lastView", "altaCategoria");
        await this.view.showAltaCategoria();
    }

    async guardarCategoria() {
        const data = this.view.getDatosCategoria();
        
        if (!data.nombre || data.nombre.trim() === "") {
            alert("El nombre de la categoría no puede estar vacío.");
            return;
        }

        const exito = await this.model.crearCategoria(data);
        if (exito) {
            alert("Categoría creada con éxito.");
            this.cargarVistaHome();
        }
    }
}

var model = new Model();
var view = new View();
var controller = new Controller(model, view);

window.onload = function() {
    controller.init();
};
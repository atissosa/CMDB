/* global propiedades */

$(function () {
    //Se asocian eventos de navegación en los menús
    $(".nav-tabs a").on("show.bs.tab", opcionMenuPrincipalMostrar);
    $(".nav-pills a").on("show.bs.tab", opcionMenuSecundarioMostrar);
    $("#tab-subagencias").on("show.bs.tab", cargarSubagencias);
    $("#pill-departamentos-listado").on("show.bs.tab", cargarDepartamentosCentral);
    $("#pill-services-listado").on("show.bs.tab", cargarServicios);
    $("#pill-equipos-listado").on("show.bs.tab", mostrarListadoEquipos);
    $("#pill-mantenimiento-prestamos").on("show.bs.tab", cargarListadoMantenimientoPrestamos);
    $("#pill-equipos-alta, #pill-equipos-modificar").on("show.bs.tab", precargaEquipo);
    $("#pill-equipos-modificar").on("shown.bs.tab", function (event) {
        opcionModificarMostrar(event, cargarEquipo, "#inputSerieEquipoModificar");
    });
    $("#pill-departamentos-modificar").on("show.bs.tab", function (event) {
        opcionModificarMostrar(event, cargarDepartamento, "#inputIdDepartamentoModificar");
    });
    $("#pill-services-modificar").on("show.bs.tab", function (event) {
        opcionModificarMostrar(event, cargarService, "#inputIdServiceModificar");
    });
    $("#pill-departamentos-modificar, #pill-services-modificar, #pill-equipos-modificar").on("hide.bs.tab", opcionModificarOculta);

    //Se asocian eventos de botones
    $("#formulario-login").submit(function (event) {
        login();
        event.preventDefault();
    });
    $("#btnSalir").on("click", cerrarSesion);
    $("#formulario-altaDepartamento").submit(function (event) {
        altaDepartamento();
        event.preventDefault();
    });
    $("#formulario-altaService").submit(function (event) {
        altaService();
        event.preventDefault();
    });
    $("#formulario-modificarDepartamento").submit(function (event) {
        modificarDepartamento();
        event.preventDefault();
    });
    $("#formulario-modificarService").submit(function (event) {
        modificarService();
        event.preventDefault();
    });
    $("#formulario-altaEquipo").submit(function (event) {
        altaEquipo();
        event.preventDefault();
    });
    $("#formulario-modificarEquipo").submit(function (event) {
        modificarEquipo();
        event.preventDefault();
    });
    $("#formulario-modificar-parametros").submit(function (event) {
        modificarParametrosMantenimientoPrestamo();
        event.preventDefault();
    });
    $("#btnPrestarEquipoSubagencia").on("click", prestarEquipoSubagencia);
    $("#btnLimpiarListadoEquipos").on("click", limpiarFiltroEquipos);
    $("#btnActualizarListadoEquipos").on("click", cargarEquipos);

    //Se asocia evento al cambio de tipo de equipo
    $("#selectTipoEquipo, #selectTipoEquipoModificar").on("change", cambioTipoEquipo);

    // Se asocia evento al cambio de marca de equipo
    $(".marca-equipo").on("change", cambioMarcaEquipo);

    // Se asocia evento al cambio de modelo de equipo
    $(".modelo-equipo").on("change", cambioModeloEquipo);

    // Se asocian filtros de busqueda
    $("#filtroCategoria-mantenimiento-prestamos").on("keyup", filtrarPorCategoriaPrestamo);
    $("#filtroNombre-departamentos-listado").on("keyup", filtrarPorNombreDepartamentos);
    $("#filtroNombre-services-listado").on("keyup", filtrarPorNombreServices);
    $("#filtroNombre-subagencias-listado").on("keyup", filtrarPorNumeroSubagencias);

    //Se asocian eventos de modales
    $("#modalPrestarEquipoSubagencia").on("show.bs.modal", mostrarModalPrestarEquipoSubagencia);
    $("#modalPrestarEquipoSubagencia").on("hidden.bs.modal", ocultarModalPrestarEquipoSubagencia);
    $("#modalPrestarEquipoSubagencia select.subagencia").on("change", cambioSubagenciaPrestamo);
    $("#modalHistoricoEquipo").on("show.bs.modal", mostrarModalHistoricoEquipo);
    $("#modalHistoricoEquipo").on("hidden.bs.modal", ocultarModalHistoricoEquipo);
    $("#btnModalParametrosMantenimiento").click(mostrarModalParametros);
    $("#inputInformacionMantenimientoPrestamos").on("click", function () {
        $("#modalInformacionMantenimientoPrestamo").modal("show");
    });
    //Se bloquea la modificacion de lugar cuando es de prestamo en el alta
    $(".esPrestamo").on("change", function () {
        var checkbox = $(this);
        if (checkbox.prop("checked")) {
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.lugar").val(1);
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.lugar").attr("disabled", true);
        } else {
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.lugar").attr("disabled", false);
        }
    });

//Se bloquea la modificacion de IP cuando es por dhcp
    $(".DHCP").on("change", function () {
        var checkbox = $(this);
        if (checkbox.prop("checked")) {
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane input.DireccionIp").val("0.0.0.0");
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane input.DireccionIp").attr("disabled", true);
        } else {
            $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane input.DireccionIp").attr("disabled", false);
        }
    });
    cargarPagina();
});


/*  NAVEGACION   */

function cargarPagina() {
    //Si no hay sesión activa se muestra login, de lo contrario panel principal
    if (sesionActiva()) {
        $("#tagNombre").text(usuarioNombre());
        $("#pagina-login").hide();
        $("#pagina-principal, #logout").show();
        var tabActiva = $("#menu-principal" + " a.nav-link.active");
        $(tabActiva).removeClass("active");
        $(tabActiva).tab("show");
    } else {
        document.title = "Inicio de sesión";
        $("#pagina-login").show();
        $("#pagina-principal, #logout").hide();
    }
}

//Previo a mostrar una sección del menú principal
function opcionMenuPrincipalMostrar(event) {
    document.title = $(event.target).text();
    var tabActiva = $("#" + $(event.target).attr("aria-controls") + " a.nav-link.active");
    $(tabActiva).removeClass("active");
    $(tabActiva).tab("show");
}

//Previo a mostrar una opción de los menús secundarios
function opcionMenuSecundarioMostrar(event) {
    var titulo = document.title.split(" - ");
    document.title = titulo[0] + " - " + $(event.target).text();
}

//Previo a mostrar un panel de modificacion carga los datos del id pasado o deja en blanco para ingresar a mano
function opcionModificarMostrar(event, carga, txtId) {
    var panel = $("#" + $(event.target).attr("aria-controls"));
    var id = panel.data("id");
    if (id == 0) {
        $(txtId).val("");
        $(txtId).attr("readonly", false);
    } else {
        $(txtId).val(id);
        $(txtId).attr("readonly", true);
        carga();
    }
}

//Luego de ocultar un panel de modificación se limpian los campos
function opcionModificarOculta(event) {
    var panel = $("#" + $(event.target).attr("aria-controls"));
    panel.data("id", "0");
    $("#" + panel.attr("id") + " input.form-control").val("");
    $("#" + panel.attr("id") + " textarea.form-control").val("");
}


/*  COMUNICACION   */

function hacerRequest(metodo, recurso, ok, parametros, credenciales, cabeceras) {
    var retorno;
    $("#myModal").modal("show");
    if (metodo.toUpperCase() != "GET" && parametros != null)
        parametros = JSON.stringify(parametros);
    try {
        var error = function (xhr, status, txtstatus) {
            $("#myModal").modal("hide");
            //En caso de error se muestra mensaje en pantalla
            try {
                var respuesta = JSON.parse(xhr.responseText);
                mostrarMensaje("Error " + respuesta.codigo + ": " + respuesta.mensaje);
                if (respuesta.codigo == 14)
                    cerrarSesion();
            } catch (ex) {
                mostrarMensaje("ERROR DE RESPUESTA: tipo de formato no compatible");
            }
        };
        retorno = $.ajax({
            type: metodo.toUpperCase(),
            url: propiedades.recursosBackend + recurso,
            contentType: 'application/json',
            dataType: 'json',
            data: parametros,
            headers: cabeceras,
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Authorization", credenciales);
            },
            async: true,
            success: function (respuesta) {
                $("#myModal").modal("hide");
                ok(respuesta);
            },
            error: error});
    } catch (ex) {
        //Se muestran en pantalla posibles excepciones
        mostrarMensaje(ex);
    }
    return retorno;
}

/*  SESION   */

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function deleteCookie(cname, cvalue, exdays) {
    document.cookie = cname + "=" + cvalue + ";" + "expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
}

function checkCookie() {
    var user = getCookie("username");
    if (user != "") {
        alert("Welcome again " + user);
    } else {
        user = prompt("Please enter your name:", "");
        if (user !== "" && user !== null) {
            setCookie("username", user, 365);
        }
    }
}

//Devuelve si hay un usuario con la sesion iniciada
function sesionActiva() {
    return usuario() !== "";
}

//Devuelve el usuario activa
function usuario() {
    return getCookie("nombreUsuario");
}

//Devuelve el nombre para mostrar del usuario activo
function usuarioNombre() {
    return getCookie("nombreUsuario");
}

//Setea el usuario activo
function guardarUsuario(usr) {
    //Si viene dato se guarda
    if (usr != null) {
        setCookie("idUsuario", usr.id, 1);
        setCookie("nombreUsuario", usr.usuario, 1);
        setCookie("usuarioRol", usr.rol, 1);
        setCookie("usuarioDepartamento", usr.departamento, 1);
    } else {
        //Si viene null se borra
        deleteCookie("nombreUsuario", "");
        deleteCookie("usuarioRol", "");
        deleteCookie("usuarioDepartamento", "");
    }
}

//Hace el cierre de sesion borrando datos del usuario
function cerrarSesion() {
    var ok = function () {};
    hacerRequest("delete", "login", ok, null, "Basic " + btoa(usuarioNombre()));
    guardarUsuario(null);
    cargarPagina();
}

function login() {
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else if (respuesta.retorno == null) {
            mostrarMensaje(respuesta.mensaje);
        } else {
            //Si son correctos se inicia sesion
            var usuario = respuesta.retorno;
            guardarUsuario(usuario);
            $("#tagNombre").text(usuario.usuario);
            //mostrarMensaje("Bienvenido " + usuario.usuario);
            $("#pagina-login").hide();
            $("#pagina-principal").show();
            cargarPagina();
        }
    };
    hacerRequest("post", "login", ok, null, "Basic " + btoa($("#inputUsuario").val() + ":" + $("#inputPass").val()));
    resetearFormulario("formulario-login");
}


/*  DEPARTAMENTOS   */

function filtrarPorNombreDepartamentos() {
    var value = $(this).val().toLowerCase();
    $("#tabla-departamentos tbody tr").filter(function () {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
    });
}

function cargarDepartamentosCentral() {
    //Se carga la lista de departamentos
    hacerRequest("get", "departamento", function (respuesta) {
        var tabla = "";
        //Si el pedido devuelve elementos se muestra info
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Nombre</th>';
            tabla += '<th>Activo</th>';
            tabla += '<th>Descripción</th>';
            tabla += '<th></th>';

            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.activo + '" data-id="' + v.id + '" >';
                tabla += '<td>' + v.nombre + '</td>';
                tabla += '<td>' + v.activo + '</td>';
                tabla += '<td>' + v.descripcion + '</td>';
                tabla += '<td>';
                tabla += '<div class="dropdown"><button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">';
                tabla += 'Opciones</button>';
                tabla += '<div class="dropdown-menu">';
                tabla += '<a class="modificar-departamento dropdown-item" href="#">Modificar</a>';
                tabla += '<a class="borrar-departamento dropdown-item" href="#">Baja</a>';
                tabla += '</div></div>';
                tabla += '</td>';
                tabla += '</tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
        } else {
            //Si no se muestra mensaje
            tabla = "<h2>No hay departamentos registrados</h2>";
        }
        $("#tabla-departamentos").html(tabla);
        //Se asocian los eventos de los botones
        $(".modificar-departamento").on("click", function () {
            //Se setea el id en la pagina de detalle que se usara luego de redireccionar
            $("#departamentos-modificar").data("id", $(this).parent().parent().parent().parent().data("id"));
            $("#pill-departamentos-modificar").tab("show");
        });
        $(".borrar-departamento").on("click", function () {
            borrarDepartamento($(this).parent().parent().parent().parent().data("id"));
        });
    });
}

function cargarDepartamento() {
    var ok = function (respuesta) {
        $("#inputNombreDepartamentoModificar").val(respuesta.retorno[0].nombre);
        $("#inputDescripcionDepartamentoModificar").val(respuesta.retorno[0].descripcion);
        $("#inputActivoDepartamentoModificar").val(respuesta.retorno[0].activo.toString());
    };
    var departamento = {
        id: $("#inputIdDepartamentoModificar").val()
    };
    hacerRequest("get", "departamento", ok, departamento);
}

function borrarDepartamento(id) {
    if (confirm("¿Desea eliminar el elemento?")) {
        var parametros = {id: id};
        var ok = function (respuesta) {
            if (respuesta.tipo == "OK") {
                mostrarMensaje("Elemento eliminado correctamente");
                cargarDepartamentosCentral();
            }
        };
        hacerRequest("DELETE", "departamento", ok, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function altaDepartamento() {
    var parametros = {
        nombre: $("#inputNombreDepartamento").val(),
        descripcion: $("#inputDescripcionDepartamento").val()
    };
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else {
            //Si son correctos se inicia sesion
            mostrarMensaje(respuesta.mensaje);
        }
    };
    //Se hace un post al servidor con los datos ingresados
    hacerRequest("post", "departamento", ok, parametros, "Basic " + btoa(usuarioNombre()));
}

function modificarDepartamento() {
    var ok = function (respuesta) {
        mostrarMensaje(respuesta.mensaje);
    };
    var departamento = {
        id: $("#inputIdDepartamentoModificar").val(),
        nombre: $("#inputNombreDepartamentoModificar").val(),
        descripcion: $("#inputDescripcionDepartamentoModificar").val(),
        activo: $("#inputActivoDepartamentoModificar").val()
    };
    hacerRequest("put", "departamento", ok, departamento, "Basic " + btoa(usuarioNombre()));
}


/*  SERVICES   */

function filtrarPorNombreServices() {
    var value = $(this).val().toLowerCase();
    $("#tabla-services tbody tr").filter(function () {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
    });
}

function cargarServicios() {
    //Se carga la lista de Services
    hacerRequest("get", "service", function (respuesta) {
        var tabla = "";
        //Si el pedido devuelve elementos se muestra info
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Nombre</th>';
            tabla += '<th>Activo</th>';
            tabla += '<th>Descripción</th>';
            tabla += '<th>Dirección</th>';
            tabla += '<th></th>';

            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.activo + '"data-id="' + v.id + '" >';
                tabla += '<td>' + v.nombre + '</td>';
                tabla += '<td>' + v.activo + '</td>';
                tabla += '<td>' + v.descripcion + '</td>';
                tabla += '<td>' + v.direccion + '</td>';
                tabla += '<td>';
                tabla += '<div class="dropdown"><button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">';
                tabla += 'Opciones</button>';
                tabla += '<div class="dropdown-menu">';
                tabla += '<a class="modificar-service dropdown-item" href="#">Modificar</a>';
                tabla += '<a class="borrar-service dropdown-item" href="#">Baja</a>';
                tabla += '</div></div>';
                tabla += '</td>';
                tabla += '</tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
        } else {
            //Si no se muestra mensaje
            tabla = "<h2>No hay services registrados</h2>";
        }
        $("#tabla-services").html(tabla);
        //Se asocian los eventos de los botones
        $(".modificar-service").on("click touchstart", function () {
            //Se setea el id en la pagina de detalle que se usara luego de redireccionar
            $("#services-modificar").data("id", $(this).parent().parent().parent().parent().data("id"));
            $("#pill-services-modificar").tab("show");
        });
        $(".borrar-service").on("click touchstart", function () {
            borrarService($(this).parent().parent().parent().parent().data("id"));
        });
    });
}

function cargarService() {
    var ok = function (respuesta) {
        $("#inputNombreServiceModificar").val(respuesta.retorno[0].nombre);
        $("#inputDescripcionServiceModificar").val(respuesta.retorno[0].descripcion);
        $("#inputDireccionServiceModificar").val(respuesta.retorno[0].direccion);
        $("#inputActivoServiceModificar").val(respuesta.retorno[0].activo.toString());
    };
    var equipo = {
        id: $("#inputIdServiceModificar").val()
    };
    hacerRequest("get", "service", ok, equipo);
}

function borrarService(id) {
    if (confirm("¿Desea eliminar el elemento?")) {
        var parametros = {id: id};
        var ok = function (respuesta) {
            if (respuesta.tipo == "OK") {
                mostrarMensaje("Elemento eliminado correctamente");
                cargarServicios();
            }
        };
        hacerRequest("DELETE", "service", ok, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function altaService() {
    var parametros = {
        nombre: $("#inputNombreService").val(),
        descripcion: $("#inputDescripcionService").val(),
        direccion: $("#inputDireccionService").val()
    };
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else {
            //Si son correctos se inicia sesion
            mostrarMensaje(respuesta.mensaje);
        }
    };
    //Se hace un post al servidor con los datos ingresados
    hacerRequest("post", "service", ok, parametros, "Basic " + btoa(getCookie("nombreUsuario")));
}

function modificarService() {
    var ok = function (respuesta) {
        mostrarMensaje(respuesta.mensaje);
    };
    var service = {
        id: $("#inputIdServiceModificar").val(),
        nombre: $("#inputNombreServiceModificar").val(),
        descripcion: $("#inputDescripcionServiceModificar").val(),
        direccion: $("#inputDireccionServiceModificar").val(),
        activo: $("#inputActivoServiceModificar").val()
    };
    hacerRequest("put", "service", ok, service, "Basic " + btoa(usuarioNombre()));
}


/*  SUBAGENCIAS   */

function filtrarPorNumeroSubagencias() {
    var value = $(this).val().toLowerCase();
    $("#tabla-subagencias tbody tr").filter(function () {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
    });
}

function cargarSubagencias() {
    //Se carga la lista de Subagencias
    hacerRequest("get", "subagencia", function (respuesta) {
        var tabla = "";
        //Si el pedido devuelve elementos se muestra info
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Número</th>';
            tabla += '<th>Nombre</th>';
            tabla += '<th>Activo</th>';
            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.activo + '">';
                tabla += '<td>' + v.numero + '</td>';
                tabla += '<td>' + v.nombre + '</td>';
                tabla += '<td>' + v.activo + '</td>';
                tabla += '</tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
        } else {
            //Si no se muestra mensaje
            tabla = "<h2>No hay subagencias registradas</h2>";
        }
        $("#tabla-subagencias").html(tabla);
    });
}


/*  EQUIPOS   */

function filtrarPorNumeroSerie() {
    var value = $(this).val().toLowerCase();
    $("#tabla-equipos tbody tr").filter(function () {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
    });
}

function filtrarPorCategoriaPrestamo() {
    var value = $(this).val().toLowerCase();
    $("#tabla-mantenimiento-prestamos tbody tr").filter(function () {
        $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
    });
}

function cargarListadoMantenimientoPrestamos() {
    var functionOk = function (respuesta) {
        var tabla = "";
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Categoria</th>';
            tabla += '<th>S/N</th>';
            tabla += '<th>Modelo</th>';
            tabla += '<th>Tipo</th>';
            tabla += '<th>Estado</th>';
            tabla += '<th>Meses en prestamo</th>';
            tabla += '<th>Veces prestado</th>';
            tabla += '<th>Depreciacion mensual</th>';
            tabla += '<th>Costo venta($)</th>';

            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.estado + '" data-id="' + v.numeroSerie + '" data-tipo="' + v.tipo.toLowerCase() + '" >';
                tabla += '<td>';
                switch (v.categoriaMantenimiento) {
                    case "ROJO":
                        tabla += '<svg width="20" height="20"><circle cx="10" cy="10" r="5" stroke="red" stroke-width="4" fill="red" /></svg>  ';
                        break;
                    case "AMARILLO":
                        tabla += '<svg width="20" height="20"><circle cx="10" cy="10" r="5" stroke="yellow" stroke-width="4" fill="yellow" /></svg>  ';
                        break;
                    case "VERDE":
                        tabla += '<svg width="20" height="20"><circle cx="10" cy="10" r="5" stroke="green" stroke-width="4" fill="green" /></svg>  ';
                        break;
                }
                tabla += v.categoriaMantenimiento + '</td>';
                tabla += '<td>' + v.numeroSerie + '</td>';
                tabla += '<td>' + v.modelo + '</td>';
                tabla += '<td>' + formatearValorEnum(v.tipo) + '</td>';
                tabla += '<td>' + v.estado + '</td>';
                tabla += '<td>' + v.cantidadMesesEnPrestamo + '</td>';
                tabla += '<td>' + v.cantidadVecesPrestado + '</td>';
                tabla += '<td>' + v.depreciacionMensual + '</td>';
                tabla += '<td>' + v.costoVentaUsado + '</td>';
                tabla += '</tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
        } else {
            tabla = "<h2>No hay equipos registrados</h2>";
        }
        $("#tabla-mantenimiento-prestamos").html(tabla);
    };
    hacerRequest("get", "mantenimientoPrestamo", functionOk, null, "Basic " + btoa(usuarioNombre()));
}

function modificarParametrosMantenimientoPrestamo() {
    var parametros = {
        RCantMesesEnPrestamo: $("#inputRojoCantMinMesesEnPrestamo").val(),
        RCantVecesPrestado: $("#inputRojoCantMinVecesPrestado").val(),
        ACantMesesEnPrestamo: $("#inputAmarilloCantMinMesesEnPrestamo").val(),
        ACantVecesPrestado: $("#inputAmarilloCantMinVecesPrestado").val(),
        VCantMesesEnPrestamo: $("#inputVerdeCantMaxMesesEnPrestamo").val(),
        VCantVecesPrestado: $("#inputVerdeCantMaxVecesPrestado").val()
    };
    var ok = function (respuesta) {
        if (respuesta.tipo == "OK") {
            mostrarMensaje(respuesta.mensaje);
            $("#modalModificarParametrosMantenimiento").modal("hide");
            cargarListadoMantenimientoPrestamos();
        }
    };
    hacerRequest("post", "mantenimientoPrestamo", ok, parametros);
}

function mostrarListadoEquipos() {
    cargarTiposEquipo();
    cargarEstadosEquipo();
}

function cargarEquipos() {
    var codigo;
    var txtCodigo = $("#filtroCodigo-equipos-listado").val();
    if (txtCodigo)
        codigo = [txtCodigo];
    var numeroSerie;
    var txtNumeroSerie = $("#filtroNumeroSerie-equipos-listado").val();
    if (txtNumeroSerie)
        numeroSerie = [txtNumeroSerie];
    var esPrestamo;
    var valPrestamo = $("#filtroPrestamo-equipos-listado").val();
    if (valPrestamo != "")
        esPrestamo = valPrestamo;
    var parametros = {
        codigo: codigo,
        numeroSerie: numeroSerie,
        esPrestamo: esPrestamo,
        tipo: $("#filtroTipo-equipos-listado").val(),
        estado: $("#filtroEstado-equipos-listado").val()
    };
    var ok = function (respuesta) {
        var tabla = "";
        //Si el pedido devuelve elementos se muestra info
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Codigo</th>';
            tabla += '<th>S/N</th>';
            tabla += '<th>Modelo</th>';
            tabla += '<th>Tipo</th>';
            tabla += '<th>Lugar actual</th>';
            tabla += '<th>Estado</th>';
            tabla += '<th></th>';
            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.estado + '" data-id="' + v.numeroSerie + '" data-tipo="' + v.tipo.toLowerCase() + '" >';
                tabla += '<td>' + v.codigo + '</td>';
                tabla += '<td>' + v.numeroSerie + '</td>';
                tabla += '<td>' + v.modelo + '</td>';
                tabla += '<td>' + formatearValorEnum(v.tipo) + '</td>';
                tabla += '<td>' + v.lugarActual + '</td>';
                tabla += '<td>' + formatearValorEnum(v.estado) + '</td>';
                tabla += '<td>';
                tabla += '<div class="dropdown"><button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">';
                tabla += 'Opciones</button>';
                tabla += '<div class="dropdown-menu">';
                tabla += '<a class="ver-historico-equipo dropdown-item" href="#">Ver histórico</a>';
                if (!(v.esPrestamo || v.estado == "BAJA") && v.idLugar == 1)
                    tabla += '<a class="asignar-para-prestamo-equipo dropdown-item" href="#">Asignar para prestamo</a>';
                if (v.estado == "PRESTAMO_DISPONIBLE")
                    tabla += '<a class="prestar-a-subagencia dropdown-item" href="#">Prestar a subagencia</a>';
                if (v.estado == "PRESTADO" || v.estado == "EN_RECUPERACION")
                    tabla += '<a class="devolver-a-central dropdown-item" href="#">Devolver</a>';
                if (v.estado == "AVERIADO")
                    tabla += '<a class="reparar-equipo dropdown-item" href="#">Iniciar Reparacion</a>';
                if (v.estado == "INACTIVO")
                    tabla += '<a class="despachar-equipo dropdown-item" href="#">Despachar</a>';
                if (v.estado == 'ACTIVO')
                    tabla += '<a class="marcar-equipoRoto dropdown-item" href="#">Equipo averiado</a>';
                if (v.estado == 'EN_REPARACION')
                    tabla += '<a class="finalizar-reparacionEquipo dropdown-item" href="#">Finalizar reparacion</a>';
                tabla += '<a class="modificar-equipo dropdown-item" href="#">Modificar</a>';
                if (v.estado != "BAJA")
                    tabla += '<a class="borrar-equipo dropdown-item" href="#">Baja</a>';
                tabla += '</div></div>';
                tabla += '</td>';
                tabla += '</tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
        } else {
            //Si no se muestra mensaje
            tabla = "<h2>No hay equipos que coincidan con los filtros</h2>";
        }
        $("#tabla-equipos").html(tabla);
        //Se asocian los eventos de los botones
        $(".ver-historico-equipo").on("click touchstart", function () {
            //Se setea el S/N en el modal de detalle que se usara luego de redireccionar
            $("#modalHistoricoEquipo").data("id", $(this).parent().parent().parent().parent().data("id"));
            $("#modalHistoricoEquipo").modal("show");
        });
        $(".modificar-equipo").on("click touchstart", function () {
            //Se setea el S/N en la pagina de modificar que se usara luego de redireccionar
            $("#equipos-modificar").data("id", $(this).parent().parent().parent().parent().data("id"));
            $("#equipos-modificar").data("tipo", $(this).parent().parent().parent().parent().data("tipo"));
            $("#pill-equipos-modificar").tab("show");
        });
        $(".borrar-equipo").on("click touchstart", function () {
            borrarEquipo($(this).parent().parent().parent().parent().data("id"));
        });
        $(".asignar-para-prestamo-equipo").on("click touchstart", function () {
            asignarEquipoParaPrestamo($(this).parent().parent().parent().parent().data("id"));
        });
        $(".prestar-a-subagencia").on("click touchstart", function () {
            $("#modalPrestarEquipoSubagencia").data("id", $(this).parent().parent().parent().parent().data("id"));
            $("#modalPrestarEquipoSubagencia").data("tipo", $(this).parent().parent().parent().parent().data("tipo"));
            $("#modalPrestarEquipoSubagencia").modal("show");
        });
        $(".devolver-a-central").on("click touchstart", function () {
            devolverEquipoCentral($(this).parent().parent().parent().parent().data("id"));
        });
        $(".reparar-equipo").on("click touchstart", function () {
            iniciarReparacionEquipo($(this).parent().parent().parent().parent().data("id"));
        });
        $(".despachar-equipo").on("click touchstart", function () {
            despacharEquipo($(this).parent().parent().parent().parent().data("id"));
        });
        $(".marcar-equipoRoto").on("click touchstart", function () {
            marcarEquipoComoRoto($(this).parent().parent().parent().parent().data("id"));
        });
        $(".finalizar-reparacionEquipo").on("click touchstart", function () {
            finalizarReparacionEquipo($(this).parent().parent().parent().parent().data("id"));
        });
    };
    hacerRequest("post", "equipos", ok, parametros);
}

function finalizarReparacionEquipo(numeroSerie) {
    if (confirm("¿Desea continuar con la operacion?")) {
        var parametros = {
            numeroSerie: numeroSerie,
            usuario: getCookie("nombreUsuario")
        };
        var funcionOk = function (respuesta) {
            mostrarMensaje(respuesta.mensaje);
            cargarEquipos();
        };
        hacerRequest("delete", "reparacion", funcionOk, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function iniciarReparacionEquipo(numeroSerie) {
    if (confirm("¿Desea continuar con la operacion?")) {
        var parametros = {
            numeroSerie: numeroSerie,
            usuario: getCookie("nombreUsuario")
        };
        var funcionOk = function (respuesta) {
            mostrarMensaje(respuesta.mensaje);
            cargarEquipos();
        };
        hacerRequest("put", "reparacion", funcionOk, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function marcarEquipoComoRoto(numeroSerie) {
    if (confirm("¿Desea continuar con la operacion?")) {
        var parametros = {
            numeroSerie: numeroSerie,
            usuario: getCookie("nombreUsuario")
        };
        var funcionOk = function (respuesta) {
            mostrarMensaje(respuesta.mensaje);
            cargarEquipos();
        };
        hacerRequest("post", "reparacion", funcionOk, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function despacharEquipo(numeroSerie) {
    if (confirm("¿Desea continuar con la operacion?")) {
        var parametros = {
            numeroSerie: numeroSerie,
            usuario: getCookie("nombreUsuario")
        };
        var funcionOk = function (respuesta) {
            mostrarMensaje(respuesta.mensaje);
            cargarEquipos();
        };
        hacerRequest("post", "equipo", funcionOk, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function asignarEquipoParaPrestamo(numeroSerie) {
    if (confirm("¿Desea continuar con la operacion?")) {
        var parametros = {
            numeroSerie: [numeroSerie],
            usuario: [usuarioNombre()]
        };
        var funcionOk = function (respuesta) {
            mostrarMensaje(respuesta.mensaje);
            cargarEquipos();
        };
        hacerRequest("put", "equipo", funcionOk, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function cargarEquipo() {
    var recurso = $("#equipos-modificar").data("tipo");
    if (recurso != "pc" && recurso != "impresora" && recurso != "validadora")
        recurso = "otroEquipo";
    var equipo = {
        numeroSerie: $("#equipos-modificar").data("id")
    };
    var ok = function (respuesta) {
        //Se cargan campos generales
        $("#inputIdEquipoModificar").val(respuesta.retorno.id);
        $("#inputCodigoEquipoModificar").val(respuesta.retorno.codigo);
        $("#inputSerieEquipoModificar").val(respuesta.retorno.numeroSerie);
        $("#inputMarcaEquipoModificar").val(respuesta.retorno.marca);
        $("#inputModeloEquipoModificar").val(respuesta.retorno.modelo);
        $("#inputPrecioEquipoModificar").val(respuesta.retorno.precioDeCompra);
        $("#inputDescripcionEquipoModificar").val(respuesta.retorno.descripcion);
        $("#inputGarantiaEquipoModificar").val(respuesta.retorno.mesesGarantia);
        $("#inputPrestamoEquipoModificar").prop("checked", respuesta.retorno.esPrestamo);
        $("#inputPrecintoEquipoModificar").val(respuesta.retorno.numeroPrecinto);
        $("#selectLugarEquipoModificar").val(respuesta.retorno.idLugar);
        $("#selectLugarActualEquipoModificar").val(respuesta.retorno.lugarActual);
        $("#inputFechaEquipoModificar").val(respuesta.retorno.fechaAlta);
        $("#inputUsuarioEquipoModificar").val(respuesta.retorno.usuario);
        $("#selectTipoEquipoModificar").val(respuesta.retorno.tipo);
        $("#selectEstadoEquipoModificar").val(respuesta.retorno.estado);
        $("#selectLugarEquipoModificar").change();
        $("#inputPrestamoEquipoModificar").change();
        $("#selectTipoEquipoModificar").change();
        $("#inputSerieEquipoModificar").attr("readonly", false);
        switch (respuesta.retorno.tipo) {
            case "PC":
                $("#inputIpEquipoModificar").val(respuesta.retorno.ip);
                $("#inputHostEquipoModificar").val(respuesta.retorno.nombreHost);
                $("#inputProcesadorEquipoModificar").val(respuesta.retorno.procesador);
                $("#inputRamEquipoModificar").val(respuesta.retorno.memoriaRam);
                $("#inputDiscoEquipoModificar").val(respuesta.retorno.tamanoDisco);
                break;
            case "IMPRESORA":
                $("#inputIpEquipoModificar").val(respuesta.retorno.ip);
                $("#inputHostEquipoModificar").val(respuesta.retorno.nombreHost);
                $("#selectOpcionEquipoModificar").val(respuesta.retorno.opcion);
                $("#inputMultifuncionEquipoModificar").prop("checked", respuesta.retorno.multifuncion);
                $("#selectConexionEquipoModificar").val(respuesta.retorno.conexion);
                break;
        }
    };
    hacerRequest("get", recurso, ok, equipo);
}

function borrarEquipo(id) {
    if (confirm("¿Desea eliminar el elemento?")) {
        var parametros = {
            numeroSerie: [id],
            usuario: [usuarioNombre()]
        };
        var ok = function (respuesta) {
            if (respuesta.tipo == "OK") {
                mostrarMensaje(respuesta.mensaje);
                cargarEquipos();
            }
        };
        hacerRequest("DELETE", "equipo", ok, parametros, "Basic " + btoa(usuarioNombre()));
    }
}

function altaEquipo() {
    var recurso = $("#selectTipoEquipo").val().toString().toLowerCase();
    var parametros;
    switch (recurso) {
        case "pc":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                numeroSerie: $("#inputSerieEquipo").val(),
                marca: $("#inputMarcaEquipo").val(),
                modelo: $("#inputModeloEquipo").val(),
                precioDeCompra: $("#inputPrecioEquipo").val(),
                descripcion: $("#inputDescripcionEquipo").val(),
                mesesGarantia: $("#inputGarantiaEquipo").val(),
                esPrestamo: $("#inputPrestamoEquipo").is(":checked"),
                numeroPrecinto: $("#inputPrecintoEquipo").val(),
                idLugar: $("#selectLugarEquipo").val(),
                ip: $("#inputIpEquipo").val(),
                fechaAlta: $("#inputFechaEquipo").val(),
                nombreHost: $("#inputHostEquipo").val(),
                procesador: $("#inputProcesadorEquipo").val(),
                memoriaRam: $("#inputRamEquipo").val(),
                tamanoDisco: $("#inputDiscoEquipo").val(),
                tipo: recurso
            };
            break;
        case "impresora":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                numeroSerie: $("#inputSerieEquipo").val(),
                marca: $("#inputMarcaEquipo").val(),
                modelo: $("#inputModeloEquipo").val(),
                precioDeCompra: $("#inputPrecioEquipo").val(),
                descripcion: $("#inputDescripcionEquipo").val(),
                mesesGarantia: $("#inputGarantiaEquipo").val(),
                esPrestamo: $("#inputPrestamoEquipo").is(":checked"),
                numeroPrecinto: 1,
                idLugar: $("#selectLugarEquipo").val(),
                ip: $("#inputIpEquipo").val(),
                fechaAlta: $("#inputFechaEquipo").val(),
                nombreHost: $("#inputHostEquipo").val(),
                opcion: $("#selectOpcionEquipo").val(),
                multifuncion: $("#inputMultifuncionEquipo").is(":checked"),
                conexion: $("#selectConexionEquipo").val(),
                tipo: recurso
            };
            break;
        case "validadora":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                numeroSerie: $("#inputSerieEquipo").val(),
                marca: $("#inputMarcaEquipo").val(),
                modelo: $("#inputModeloEquipo").val(),
                precioDeCompra: $("#inputPrecioEquipo").val(),
                descripcion: $("#inputDescripcionEquipo").val(),
                fechaAlta: $("#inputFechaEquipo").val(),
                mesesGarantia: $("#inputGarantiaEquipo").val(),
                esPrestamo: $("#inputPrestamoEquipo").is(":checked"),
                numeroPrecinto: 1,
                idLugar: $("#selectLugarEquipo").val(),
                tipo: recurso
            };
            break;
        default:
            recurso = "otroEquipo";
            parametros = {
                usuario: getCookie("nombreUsuario"),
                numeroSerie: $("#inputSerieEquipo").val(),
                marca: $("#inputMarcaEquipo").val(),
                modelo: $("#inputModeloEquipo").val(),
                precioDeCompra: $("#inputPrecioEquipo").val(),
                descripcion: $("#inputDescripcionEquipo").val(),
                fechaAlta: $("#inputFechaEquipo").val(),
                mesesGarantia: $("#inputGarantiaEquipo").val(),
                esPrestamo: $("#inputPrestamoEquipo").is(":checked"),
                numeroPrecinto: 1,
                idLugar: $("#selectLugarEquipo").val(),
                tipo: $("#selectTipoEquipo").val()
            };
            break;
    }
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else {
            mostrarMensaje(respuesta.mensaje);
            if (respuesta.tipo == "OK")
                resetearFormulario("formulario-altaEquipo");
        }
    };
    //Se hace un post al servidor con los datos ingresados
    hacerRequest("post", recurso, ok, parametros, "Basic " + btoa(usuarioNombre()));
}

function precargaEquipo(event) {
    var tab = $(event.target).parents(".tab-pane");
    $("#" + $(tab).attr("id") + " .fechaAlta").val(new Date().toISOString().slice(0, 10));
    if ($.when(cargarEstadosEquipo()).done()) {
        if ($.when(cargarOpcionesImpresora()).done()) {
            if ($.when(cargarConexionesImpresora()).done()) {
                if ($.when(cargarTiposEquipo()).done()) {
                    cargarLugaresEquipo();
                }
            }
        }
    }
}

function cargarMarcasEquipo() {
    var tipoEquipo = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.tipo-equipo").val();
    var parametros;
    if (tipoEquipo != "PC" && tipoEquipo != "IMPRESORA" && tipoEquipo != "VALIDADORA") {
        parametros = {tipo: tipoEquipo};
        tipoEquipo = "OTROEQUIPO";
    }
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.marca-equipo");
        var marcas = "";
        $.each(respuesta.retorno, function (k, v) {
            marcas += "<option value='" + v + "'>" + formatearValorEnum(v) + "</option>";
        });
        marcas += "<option value='" + "OTRO" + "'>" + "Otro" + "</option>";
        $(select).html(marcas);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.marca-equipo") != undefined)
        retorno = hacerRequest("get", "plantilla" + tipoEquipo, ok, parametros);
    return retorno;
}

function cargarTiposEquipo() {
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.tipo-equipo");
        var tipos = "";
        $.each(respuesta.retorno, function (k, v) {
            tipos += "<option value='" + v + "'>" + formatearValorEnum(v) + "</option>";
        });
        $(select).html(tipos);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.tipo-equipo") != undefined)
        retorno = hacerRequest("get", "tiposEquipos", ok);
    return retorno;
}

function cargarEstadosEquipo() {
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.estado-equipo");
        var estados = "";
        $.each(respuesta.retorno, function (k, v) {
            estados += "<option value='" + v + "'>" + formatearValorEnum(v) + "</option>";
        });
        $(select).html(estados);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.estado-equipo") != undefined)
        retorno = hacerRequest("get", "estadosEquipos", ok);
    return retorno;
}

function cargarOpcionesImpresora() {
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.opcion-impresora");
        var opciones = "";
        $.each(respuesta.retorno, function (k, v) {
            opciones += "<option value='" + v + "'>" + formatearValorEnum(v) + "</option>";
        });
        $(select).html(opciones);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.opcion-impresora") != undefined)
        retorno = hacerRequest("get", "opcionesImpresoras", ok);
    return retorno;
}

function cargarConexionesImpresora() {
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.conexion-impresora");
        var conexiones = "";
        $.each(respuesta.retorno, function (k, v) {
            conexiones += "<option value='" + v + "'>" + formatearValorEnum(v) + "</option>";
        });
        $(select).html(conexiones);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.conexion-impresora") != undefined)
        retorno = hacerRequest("get", "conexionesImpresoras", ok);
    return retorno;
}

function cambioMarcaEquipo() {
    var tipoEquipo = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.tipo-equipo").val();
    var marcaEquipo = $("#inputMarcaEquipo").val();
    var parametros;
    if (tipoEquipo != "PC" && tipoEquipo != "IMPRESORA" && tipoEquipo != "VALIDADORA") {
        parametros = {marca: marcaEquipo, tipo: tipoEquipo};
        tipoEquipo = "OTROEQUIPO";
    } else {
        parametros = {marca: marcaEquipo};
    }
    var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.modelo-equipo");
    var ok = function (respuesta) {
        var modelos = "";
        $.each(respuesta.retorno, function (k, v) {
            modelos += "<option value='" + v.id + "'>" + formatearValorEnum(v.modelo) + "</option>";
        });
        modelos += "<option value='" + 0 + "'>" + "Otro" + "</option>";
        $(select).html(modelos);
        $(select).change();
    };
    hacerRequest("get", "plantilla" + tipoEquipo, ok, parametros);
}

function cambioModeloEquipo() {
    var tipoEquipo = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.tipo-equipo").val();
    if (tipoEquipo != "PC" && tipoEquipo != "IMPRESORA" && tipoEquipo != "VALIDADORA")
        tipoEquipo = "OTROEQUIPO";
    var modeloEquipoId = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.modelo-equipo").val();
    if (modeloEquipoId != null) {
        var parametros = {
            id: modeloEquipoId};
        var ok = function (respuesta) {
            if (respuesta.codigo == 4) {
                $("#inputPrecioEquipo").val(respuesta.retorno.precioCompra);
                $("#inputGarantiaEquipo").val(respuesta.retorno.mesesGarantia);
                switch (tipoEquipo) {
                    case "PC":
                        $("#inputProcesadorEquipo").val(respuesta.retorno.procesador);
                        $("#inputRamEquipo").val(respuesta.retorno.memoriaRam);
                        $("#inputDiscoEquipo").val(respuesta.retorno.tamanoñoDisco);
                        break;
                    case "IMPRESORA":
                        $("#inputMultifuncionEquipo").prop("checked", respuesta.retorno.multifuncion);
                        $("#selectOpcionEquipo").val(respuesta.retorno.opcion);
                        $("#selectConexionEquipo").val(respuesta.retorno.conexion);
                    default:
                        break;
                }
            } else {
                $("#inputPrecioEquipo").val("");
                $("#inputGarantiaEquipo").val("");
                $("#inputProcesadorEquipo").val("");
                $("#inputRamEquipo").val("");
                $("#inputDiscoEquipo").val("");
                $("#inputMultifuncionEquipo").prop("checked", false);
                $("#selectOpcionEquipo").val("");
                $("#selectConexionEquipo").val("");
            }
        };
        hacerRequest("post", "plantilla" + tipoEquipo, ok, parametros);
    }
}

function cambioTipoEquipo(event) {
    var tipo = $(event.target).val();
    var tab = $(event.target).parents(".tab-pane");
    $(tab).find("div .campoOpcional").hide();
    switch (tipo) {
        case "PC":
            $(tab).find("div .campoOpcional.campoImpresora .form-control").attr("disabled", true);
            $(tab).find("div .campoOpcional.campoPC .form-control").attr("disabled", false);
            $(tab).find("div .campoOpcional.ambos .form-control").attr("disabled", false);
            $(tab).find("div .campoOpcional.campoPC").show();
            break;
        case "IMPRESORA":
            $(tab).find("div .campoOpcional.campoImpresora .form-control").attr("disabled", false);
            $(tab).find("div .campoOpcional.campoPC .form-control").attr("disabled", true);
            $(tab).find("div .campoOpcional.ambos .form-control").attr("disabled", false);
            $(tab).find("div .campoOpcional.campoImpresora").show();
            break;
        default :
            $(tab).find("div .campoOpcional .form-control").attr("disabled", true);
            break;
    }
    if ($(tab).attr("id") != "equipos-modificar")
        cargarMarcasEquipo();
}

function modificarEquipo() {
    var recurso = $("#selectTipoEquipoModificar").val().toString().toLowerCase();
    var parametros;
    switch (recurso) {
        case "pc":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                id: $("#inputIdEquipoModificar").val(),
                codigo: $("#inputCodigoEquipoModificar").val(),
                numeroSerie: $("#inputSerieEquipoModificar").val(),
                marca: $("#inputMarcaEquipoModificar").val(),
                modelo: $("#inputModeloEquipoModificar").val(),
                precioDeCompra: $("#inputPrecioEquipoModificar").val(),
                descripcion: $("#inputDescripcionEquipoModificar").val(),
                mesesGarantia: $("#inputGarantiaEquipoModificar").val(),
                esPrestamo: $("#inputPrestamoEquipoModificar").is(":checked"),
                numeroPrecinto: $("#inputPrecintoEquipoModificar").val(),
                fechaAlta: $("#inputFechaEquipoModificar").val(),
                idLugar: $("#selectLugarEquipoModificar").val(),
                estado: $("#selectEstadoEquipoModificar").val(),
                ip: $("#inputIpEquipoModificar").val(),
                nombreHost: $("#inputHostEquipoModificar").val(),
                procesador: $("#inputProcesadorEquipoModificar").val(),
                memoriaRam: $("#inputRamEquipoModificar").val(),
                tamanoDisco: $("#inputDiscoEquipoModificar").val(),
                tipo: $("#selectTipoEquipoModificar").val()
            };
            break;
        case "impresora":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                id: $("#inputIdEquipoModificar").val(),
                codigo: $("#inputCodigoEquipoModificar").val(),
                numeroSerie: $("#inputSerieEquipoModificar").val(),
                marca: $("#inputMarcaEquipoModificar").val(),
                modelo: $("#inputModeloEquipoModificar").val(),
                precioDeCompra: $("#inputPrecioEquipoModificar").val(),
                descripcion: $("#inputDescripcionEquipoModificar").val(),
                mesesGarantia: $("#inputGarantiaEquipoModificar").val(),
                esPrestamo: $("#inputPrestamoEquipoModificar").is(":checked"),
                numeroPrecinto: 1,
                fechaAlta: $("#inputFechaEquipoModificar").val(),
                idLugar: $("#selectLugarEquipoModificar").val(),
                estado: $("#selectEstadoEquipoModificar").val(),
                ip: $("#inputIpEquipoModificar").val(),
                nombreHost: $("#inputHostEquipoModificar").val(),
                opcion: $("#selectOpcionEquipoModificar").val(),
                multifuncion: $("#inputMultifuncionEquipoModificar").is(":checked"),
                conexion: $("#selectConexionEquipoModificar").val(),
                tipo: $("#selectTipoEquipoModificar").val()
            };
            break;
        case "validadora":
            parametros = {
                usuario: getCookie("nombreUsuario"),
                id: $("#inputIdEquipoModificar").val(),
                codigo: $("#inputCodigoEquipoModificar").val(),
                numeroSerie: $("#inputSerieEquipoModificar").val(),
                marca: $("#inputMarcaEquipoModificar").val(),
                modelo: $("#inputModeloEquipoModificar").val(),
                precioDeCompra: $("#inputPrecioEquipoModificar").val(),
                descripcion: $("#inputDescripcionEquipoModificar").val(),
                mesesGarantia: $("#inputGarantiaEquipoModificar").val(),
                esPrestamo: $("#inputPrestamoEquipoModificar").is(":checked"),
                numeroPrecinto: 1,
                fechaAlta: $("#inputFechaEquipoModificar").val(),
                idLugar: $("#selectLugarEquipoModificar").val(),
                estado: $("#selectEstadoEquipoModificar").val(),
                tipo: $("#selectTipoEquipoModificar").val()
            };
            break;
        default:
            recurso = "otroEquipo";
            parametros = {
                usuario: getCookie("nombreUsuario"),
                id: $("#inputIdEquipoModificar").val(),
                codigo: $("#inputCodigoEquipoModificar").val(),
                numeroSerie: $("#inputSerieEquipoModificar").val(),
                marca: $("#inputMarcaEquipoModificar").val(),
                modelo: $("#inputModeloEquipoModificar").val(),
                precioDeCompra: $("#inputPrecioEquipoModificar").val(),
                descripcion: $("#inputDescripcionEquipoModificar").val(),
                mesesGarantia: $("#inputGarantiaEquipoModificar").val(),
                esPrestamo: $("#inputPrestamoEquipoModificar").is(":checked"),
                numeroPrecinto: 1,
                fechaAlta: $("#inputFechaEquipoModificar").val(),
                idLugar: $("#selectLugarEquipoModificar").val(),
                estado: $("#selectEstadoEquipoModificar").val(),
                tipo: $("#selectTipoEquipoModificar").val()
            };
            break;
    }
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else {
            //Si son correctos se inicia sesion
            mostrarMensaje(respuesta.mensaje);
        }
    };
    //Se hace un post al servidor con los datos ingresados
    hacerRequest("put", recurso, ok, parametros, "Basic " + btoa(usuarioNombre()));
}

function cargarLugaresEquipo() {
    var ok = function (respuesta) {
        var select = $($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.lugar, .tab-pane select.lugarActual");
        var lugares = "";
        $.each(respuesta.retorno, function (k, v) {
            lugares += "<option value='" + v.id + "'>" + v.textoAmostrar + "</option>";
        });
        $(select).html(lugares);
        $(select).change();
    };
    var retorno;
    if ($($("#menu-equipos a.nav-link.active").attr("href") + ".tab-pane select.lugar, .tab-pane select.lugarActual") != undefined)
        retorno = hacerRequest("get", "comboLugares", ok);
    return retorno;
}

function mostrarModalPrestarEquipoSubagencia() {
    var ok = function (respuesta) {
        var select = $("#modalPrestarEquipoSubagencia select.subagencia");
        var subagencias = "";
        $.each(respuesta.retorno, function (k, v) {
            subagencias += "<option value='" + v.id + "'>" + v.numero + " - " + v.nombre + "</option>";
        });
        $(select).html(subagencias);
        $(select).change();
    };
    hacerRequest("get", "subagencia", ok);
}

function cambioSubagenciaPrestamo() {
    var parametros = {
        tipo: [$("#modalPrestarEquipoSubagencia").data("tipo")],
        estado: ["AVERIADO"],
        idLugar: [$("#modalPrestarEquipoSubagencia select.subagencia").val()]
    };
    var ok = function (respuesta) {
        var select = $("#modalPrestarEquipoSubagencia select.averiado");
        var averiados = "";
        averiados += "<option value=''>Ninguno</option>";
        $.each(respuesta.retorno, function (k, v) {
            averiados += "<option value='" + v.numeroSerie + "'>" + v.tipo + ": " + v.codigo + " (S/N " + v.numeroSerie + ")</option>";
        });
        $(select).html(averiados);
    };
    hacerRequest("post", "equipos", ok, parametros);
}

function ocultarModalPrestarEquipoSubagencia() {
    $("#modalPrestarEquipoSubagencia").data("id", "0");
    $("#modalPrestarEquipoSubagencia").data("tipo", "");
    $("#modalPrestarEquipoSubagencia").modal("hide");
}

function prestarEquipoSubagencia() {
    var equipo = {
        numeroSerie: $("#modalPrestarEquipoSubagencia").data("id"),
        idLugarActual: $("#modalPrestarEquipoSubagencia select.subagencia").val(),
        numeroSerieEquipoPrestamo: $("#modalPrestarEquipoSubagencia select.averiado").val(),
        usuario: getCookie("nombreUsuario")
    };
    var ok = function (respuesta) {
        if (respuesta == null) {
            mostrarMensaje("Ocurrio un error inesperado");
        } else {
            mostrarMensaje(respuesta.mensaje);
            ocultarModalPrestarEquipoSubagencia();
            cargarEquipos();
        }
    };
    hacerRequest("post", "prestamo", ok, equipo, "Basic " + btoa(usuarioNombre()));
}

function devolverEquipoCentral(sn) {
    if (confirm("¿Desea marcar el equipo como recuperado?")) {
        var equipo = {
            numeroSerie: sn,
            usuario: getCookie("nombreUsuario")
        };
        var ok = function (respuesta) {
            if (respuesta == null) {
                mostrarMensaje("Ocurrio un error inesperado");
            } else {
                mostrarMensaje(respuesta.mensaje);
                cargarEquipos();
            }
        };
        hacerRequest("delete", "prestamo", ok, equipo, "Basic " + btoa(usuarioNombre()));
    }
}

function mostrarModalHistoricoEquipo() {
    var parametro = {
        numeroSerie: $("#modalHistoricoEquipo").data("id")
    };
    var ok = function (respuesta) {
        var tabla = "";
        //Si el pedido devuelve elementos se muestra info
        if (respuesta.retorno.length > 0) {
            tabla += '<table class="table table-condensed table-bordered table-striped">';
            tabla += '<thead class="thead-dark">';
            tabla += '<tr>';
            tabla += '<th>Fecha</th>';
            tabla += '<th>Lugar</th>';
            tabla += '<th>Usuario</th>';
            tabla += '<th>Estado</th>';
            tabla += '<th>Equipo asociado</th>';
            tabla += '</tr>';
            tabla += '</thead>';
            tabla += '<tbody>';
            $.each(respuesta.retorno, function (k, v) {
                tabla += '<tr class="' + v.estado + '" >';
                tabla += '<td>' + v.fecha + '</td>';
                tabla += '<td>';
                if (v.tipoLugar == "SUBAGENCIA") // PARA MOSTRAR EL NUMERO DE SUBAGENCIA
                    tabla += v.idLugar + " - ";
                tabla += v.nombreLugar + '</td>';
                tabla += '<td>' + v.usuario + '</td>';
                tabla += '<td>' + formatearValorEnum(v.estado) + '</td><td>';
                if (v.codigoEquipoPrestamo != undefined)
                    tabla += v.codigoEquipoPrestamo;
                if (v.numeroSerieEquipoPrestamo != undefined)
                    tabla += ' (S/N: ' + v.numeroSerieEquipoPrestamo + ')';
                tabla += '</td></tr>';
            });
            tabla += '</tbody>';
            tabla += '</table>';
            $("#snHistorico").html(" S/N: " + $("#modalHistoricoEquipo").data("id"));
        } else {
            //Si no se muestra mensaje
            tabla = "<h2>No hay histórico para el equipo</h2>";
        }
        $("#tabla-historico").html(tabla);
    };
    hacerRequest("get", "historicoEquipo", ok, parametro);
}

function ocultarModalHistoricoEquipo() {
    $("#modalHistoricoEquipo").data("id", "0");
    $("#tabla-historico").html("");
    $("#modalHistoricoEquipo").modal("hide");
}

function limpiarFiltroEquipos() {
    $("#filtroCodigo-equipos-listado").val(null);
    $("#filtroNumeroSerie-equipos-listado").val(null);
    $("#filtroTipo-equipos-listado").val([]);
    $("#filtroEstado-equipos-listado").val([]);
    $("#filtroPrestamo-equipos-listado").val("");
}


/*  VARIOS   */

//resetea los campos del formularioId
function resetearFormulario(idFormulario) {
    document.getElementById(idFormulario).reset();
    $("#" + idFormulario + " *").attr("disabled", false);
}

//Muestra mensaje en un pop-up
function mostrarMensaje(texto) {
    alert(texto);
}

//Recibe un valor de enum y lo devuelve como texto capitalizado sin _
function formatearValorEnum(valor) {
    valor = valor.charAt(0).toUpperCase() + valor.slice(1).toLowerCase();
    return valor.replace(/_/g, " ");
}

function mostrarModalParametros() {
    var funcionOK = function (respuesta) {
        if (respuesta.tipo == "OK") {
            $("#inputRojoCantMinMesesEnPrestamo").val(respuesta.retorno[0]);
            $("#inputRojoCantMinVecesPrestado").val(respuesta.retorno[1]);
            $("#inputAmarilloCantMinMesesEnPrestamo").val(respuesta.retorno[2]);
            $("#inputAmarilloCantMinVecesPrestado").val(respuesta.retorno[3]);
            $("#inputVerdeCantMaxMesesEnPrestamo").val(respuesta.retorno[4]);
            $("#inputVerdeCantMaxVecesPrestado").val(respuesta.retorno[5]);
        }
    };
    hacerRequest("get", "prestamo", funcionOK);
    $("#modalModificarParametrosMantenimiento").modal("show");
}

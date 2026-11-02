package com.atissosa.cmdb_backend.dto;

import java.util.HashMap;
import java.util.Map;

public class Respuesta {
    
    public enum Tipos {
        OK,
        ERROR,
        NO_AUTORIZADO,
        NO_AUTENTICADO
    }
    
    private static Map<Integer, Mensaje> mensajes;
    
    private Integer codigo;
    private Tipos tipo;
    private String mensaje;
    private Object retorno;
    
    
    private Respuesta(Integer codigo, Mensaje mensaje, Object retorno) {
        this.codigo = codigo;
        this.tipo = mensaje.getTipo();
        this.mensaje = mensaje.getTexto();
        this.retorno = retorno;
    }
    
    
    public Integer getCodigo() { return this.codigo; }
    
    public Tipos getTipo() { return this.tipo; }
    
    public String getMensaje() { return this.mensaje; }
    
    public Object getRetorno() { return this.retorno; }
    public void setRetorno(Object retorno) { this.retorno = retorno; }
    
    
    public static Respuesta nuevaRespuesta(Integer codigo, Object retorno) {
        Respuesta respuesta = null;
        Mensaje mensaje = Respuesta.mensajes.get(codigo);
        if(mensaje != null)
            respuesta = new Respuesta(codigo, mensaje, retorno);
        return respuesta;
    }
    
    
    public static void cargarMensajes() {
        Respuesta.mensajes = new HashMap();
        Respuesta.mensajes.put(0, new Mensaje(Tipos.ERROR, "Error desconocido."));
        Respuesta.mensajes.put(1, new Mensaje(Tipos.OK, "Inicio de sesión exitoso."));
        Respuesta.mensajes.put(2, new Mensaje(Tipos.NO_AUTENTICADO, "Usuario y/o contraseña incorrectos."));
        Respuesta.mensajes.put(3, new Mensaje(Tipos.ERROR, "Error de conexión con la base de datos."));
        Respuesta.mensajes.put(4, new Mensaje(Tipos.OK, "Resultado de búsqueda."));
        Respuesta.mensajes.put(5, new Mensaje(Tipos.OK, "Alta exitosa."));
        Respuesta.mensajes.put(6, new Mensaje(Tipos.OK, "Actualización exitosa."));
        Respuesta.mensajes.put(7, new Mensaje(Tipos.ERROR, "Ya exite un registro con los datos ingresados."));
        Respuesta.mensajes.put(8, new Mensaje(Tipos.ERROR, "Actualización incorrecta."));
        Respuesta.mensajes.put(9, new Mensaje(Tipos.OK, "Baja exitosa."));
        Respuesta.mensajes.put(10, new Mensaje(Tipos.ERROR, "Baja incorrecta."));
        Respuesta.mensajes.put(11, new Mensaje(Tipos.ERROR, "Parámetros incorrectos."));
        Respuesta.mensajes.put(12, new Mensaje(Tipos.OK, "Resultado de busqueda vacío"));
        Respuesta.mensajes.put(13, new Mensaje(Tipos.NO_AUTORIZADO, "No tiene permisos para realizar la operación solicitada."));
        Respuesta.mensajes.put(14, new Mensaje(Tipos.NO_AUTENTICADO, "Debe iniciar sesión para acceder a las funcionalidades."));
        Respuesta.mensajes.put(15, new Mensaje(Tipos.OK, "Elemento no encontrado"));
        Respuesta.mensajes.put(16, new Mensaje(Tipos.OK, "Los elementos seleccionados no se encontraron en el sistema"));
        Respuesta.mensajes.put(17, new Mensaje(Tipos.OK, "Asignación exitosa."));
        Respuesta.mensajes.put(18, new Mensaje(Tipos.ERROR, "Equipo no disponible para préstamo."));
        Respuesta.mensajes.put(19, new Mensaje(Tipos.ERROR, "Equipo ya se encuentra asignado a Casa Central."));
        Respuesta.mensajes.put(20, new Mensaje(Tipos.ERROR, "Equipo no se encuentra operativo en Subagencia."));
        Respuesta.mensajes.put(21, new Mensaje(Tipos.ERROR, "Equipo no se encuentra registrado como averiado."));
        Respuesta.mensajes.put(22, new Mensaje(Tipos.ERROR, "Equipo no está pronto para devolver de la reparación."));
        Respuesta.mensajes.put(23, new Mensaje(Tipos.ERROR, "Equipo no se ingresó a Central."));
        Respuesta.mensajes.put(24, new Mensaje(Tipos.ERROR, "Equipo no se encuentra en proveedor."));
        Respuesta.mensajes.put(25, new Mensaje(Tipos.ERROR, "Equipo ya fue despachado."));
        Respuesta.mensajes.put(26, new Mensaje(Tipos.OK, "Ya hay una sesion iniciada del usuario."));
        Respuesta.mensajes.put(27, new Mensaje(Tipos.OK, "El equipo asociado a un préstamo debe ser otro equipo averiado del mismo tipo."));
        Respuesta.mensajes.put(28, new Mensaje(Tipos.OK, "El equipo asociado a uno averiado debe otro del mismo tipo disponible para préstamo."));
        Respuesta.mensajes.put(29, new Mensaje(Tipos.ERROR, "Solo se pueden recuperar equipos prestados que se encuentren en Subagencia."));
    }
    
}

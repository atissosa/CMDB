package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.Acceso;
import com.atissosa.cmdb_backend.domain.ProcesoX;
import com.atissosa.cmdb_backend.domain.Rol;
import com.atissosa.cmdb_backend.domain.Session;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.atissosa.cmdb_backend.global.Propiedades;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.atissosa.cmdb_backend.persistency.MapeadorAcceso;
import com.atissosa.cmdb_backend.persistency.MapeadorUsuario;
import com.atissosa.cmdb_backend.persistency.Persistencia;

public class SubSistemaUsuarios {

    private ITipoLogin tipo;
    private LinkedList<Session> sessiones;

    protected SubSistemaUsuarios() {
        switch (Propiedades.LOGIN_TIPO) {
            case CLASICO:
                this.tipo = new LoginClasico();
                break;
            case ACTIVE_DIRECTORY:
                this.tipo = new LoginActiveDirectory();
                break;
        }
        this.sessiones = new LinkedList();
    }

    protected Usuario login(String usr, String pass) throws ExcepcionConexion {
        Usuario u = this.tipo.login(usr, pass);
        if (u != null) {
            // este proceso es el que mantiene la cuenta de cuanto tiiempo lleva
            // con la session iniciada
            ProcesoX procesoContador = new ProcesoX(usr + " - " + new Date(), usr, this);
            Session s = new Session(u, procesoContador);
            procesoContador.continuar(); // inicio el contador
            this.sessiones.add(s);
        }
        return u;
    }

    protected boolean usuarioLogueado(String usuario){
        int i = 0;
        boolean estaLogueado = false;
        while(i < this.sessiones.size() && !estaLogueado){
            if(this.sessiones.get(i).getUsuario().getUsuario().equalsIgnoreCase(usuario)){
                estaLogueado = true;
            }
            i++;
        }
        return estaLogueado;
    }
    
    protected Usuario buscarUsuario(String nombreUsuario) throws ExcepcionConexion {
        Usuario u = new Usuario();
        u.setUsuario(nombreUsuario);
        MapeadorUsuario mapeador = MapeadorUsuario.nuevoMapeador(u);
        Persistencia.getInstancia().buscar(mapeador);
        if (mapeador.getObjeto() == null) {
            u = null;
        }
        return u;
    }
    
    protected Usuario buscarUsuario(Integer id) throws ExcepcionConexion {
        Usuario u = new Usuario();
        u.setId(id);
        MapeadorUsuario mapeador = MapeadorUsuario.nuevoMapeador(u);
        Persistencia.getInstancia().buscar(mapeador);
        if (mapeador.getObjeto() == null) {
            u = null;
        }
        return u;
    }

    public void logout(String user) throws ExcepcionConexion {
        int indiceSession = this.buscarSession(user);
        if(indiceSession >= 0){
            Session session = this.sessiones.get(indiceSession);
            session.getContador().terminar();
            session.setContador(null);
            this.sessiones.removeIf(s -> s.getUsuario().getUsuario().equalsIgnoreCase(user));
            session.setUsuario(null);
        }
    }
    
    private Integer buscarSession(String usuario){
        int i = 0;
        int indice = -1;
        while(i < this.sessiones.size() && indice == -1){
            if(this.sessiones.get(i).getUsuario().getUsuario().equalsIgnoreCase(usuario)){
                indice = i;
            }
            i++;
        }
        return indice;
    }
    
    protected boolean accesoPermitido(String nombreMetodo, Rol rol) {
        boolean retorno = false;
        try {
            Acceso acceso = new Acceso();
            acceso.setOperacion(nombreMetodo);
            acceso.setRol(rol);
            MapeadorAcceso map = MapeadorAcceso.nuevoMapeador(acceso);
            Persistencia.getInstancia().buscar(map);
            retorno = map.getObjeto() != null;
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);
            retorno = false;

        } catch (Exception ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);
            retorno = false;
        }
        return retorno;
    }
}

package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.atissosa.cmdb_backend.persistency.MapeadorUsuario;
import com.atissosa.cmdb_backend.persistency.Persistencia;

public class LoginClasico implements ITipoLogin {

    @Override
    public Usuario login(String usr, String pass) throws ExcepcionConexion {
        Usuario usuario = new Usuario();
        usuario.setUsuario(usr);
        MapeadorUsuario mapeador = MapeadorUsuario.nuevoMapeador(usuario);
        Persistencia.getInstancia().buscar(mapeador);
        if(mapeador.getObjeto() == null || !usuario.getActivo() || !usuario.getContrasena().equals(pass)) {
            usuario = null;
        }
        return usuario;
    }

}

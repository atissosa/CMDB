package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;

public interface ITipoLogin {
    
    Usuario login(String nombreUsuario, String pass) throws ExcepcionConexion;
    
}

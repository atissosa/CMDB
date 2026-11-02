package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;

public class LoginActiveDirectory implements ITipoLogin {

    @Override
    public Usuario login(String usr, String pass) throws ExcepcionConexion {
        return null;
    }

}

package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.util.ArrayList;

public class Persistencia {
    
    private static Persistencia instancia;
    
    private Persistencia(){
        
    }
    
    public static Persistencia getInstancia(){
        if(Persistencia.instancia == null)
            Persistencia.instancia = new Persistencia();
        return Persistencia.instancia;
    }
    
    
    public ArrayList buscar(Mapeador mapeador) throws ExcepcionConexion {
        ArrayList encontrados = null;
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            encontrados = mapeador.leer(bd.ejecutarConsulta(mapeador.sqlBuscar(), mapeador.parametrosBuscar()));
        }
        catch (ExcepcionConexion e) {
            throw e;
        }
        finally {
            bd.desconectar();
        }
        return encontrados;
    }

    public int insertarSP(Mapeador mapeador) throws ExcepcionConexion {
        int resultado = 0;
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            resultado = bd.ejecutarProcedimiento(mapeador.sqlInsertar(), mapeador.parametrosInsertar());
        }
        catch (ExcepcionConexion e) {
            throw e;
        }
        finally {
            bd.desconectar();
        }
        return resultado;
    }

    public int actualizar(Mapeador mapeador) throws ExcepcionConexion {
        int resultado = 0;
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            resultado = bd.ejecutarSentencia(mapeador.sqlActualizar(), mapeador.parametrosActualizar());
        }
        catch (ExcepcionConexion e) {
            throw e;
        }
        finally {
            bd.desconectar();
        }
        return resultado;
    }

    public int eliminar(Mapeador mapeador) throws ExcepcionConexion {
        int resultado = 0;
        BD bd = BD.nuevaConexion();
        try {
            bd.conectar();
            resultado = bd.ejecutarSentencia(mapeador.sqlEliminar(), mapeador.parametrosEliminar());
        }
        catch (ExcepcionConexion e) {
            throw e;
        }
        finally {
            bd.desconectar();
        }
        return resultado;
    }
    
}

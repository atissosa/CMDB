package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaValidadora;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorPlantillaValidadoraSqlServer extends MapeadorPlantillaValidadora{

    MapeadorPlantillaValidadoraSqlServer(PlantillaValidadora plantillaValidadora) {
        super(plantillaValidadora);
    }

    @Override
    protected String sqlInsertar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosInsertar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlActualizar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosActualizar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlEliminar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected ArrayList parametrosEliminar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String sqlBuscar() {
        String sql = "SELECT * FROM VPlantillasValidadoras";
        String condicion = "";
        if (this.getPlantillaValidadora().getId() != null) {
            condicion += " Id = ?";
        }
        if (this.getPlantillaValidadora().getMarca() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " marca = ?";
        }
        if (this.getPlantillaValidadora().getModelo() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " modelo = ?";
        }
        if (!condicion.equals("")) {
            condicion = " WHERE" + condicion;
        }
        return sql + condicion;
    }

    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if (this.getPlantillaValidadora().getId() != null) {
            parametros.add(this.getPlantillaValidadora().getId());
        }
        if (this.getPlantillaValidadora().getMarca() != null) {
            parametros.add(this.getPlantillaValidadora().getMarca());
        }
        if (this.getPlantillaValidadora().getModelo() != null) {
            parametros.add(this.getPlantillaValidadora().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<PlantillaValidadora> plantillas = new ArrayList();
        PlantillaValidadora plantillaValidadora;
        try {
            while (rs.next()) {
                plantillaValidadora = new PlantillaValidadora();
                plantillaValidadora.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    plantillaValidadora.setId(null);
                plantillaValidadora.setMarca(rs.getString("marca"));
                plantillaValidadora.setModelo((rs.getString("modelo")));
                plantillaValidadora.setMesesGarantia(rs.getInt("mesesGarantia"));
                if(rs.wasNull())
                    plantillaValidadora.setMesesGarantia(null);
                plantillaValidadora.setPrecioCompra(rs.getDouble("precioDeCompra"));
                plantillas.add(plantillaValidadora);
            }
            if (plantillas.size() == 1) {
                PlantillaValidadora p = plantillas.get(0);
                this.getPlantillaValidadora().setId(p.getId());
                this.getPlantillaValidadora().setMarca(p.getMarca());
                this.getPlantillaValidadora().setModelo(p.getModelo());
                this.getPlantillaValidadora().setMesesGarantia(p.getMesesGarantia());
                this.getPlantillaValidadora().setPrecioCompra(p.getPrecioCompra());
            } else {
                this.setPlantillaValidadora(null);
            }

        } catch (SQLException e) {
            plantillas.clear();
            this.setPlantillaValidadora(null);
            throw new ExcepcionConexion("No se pudo cargar el o los registros de la BD.", e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new ExcepcionConexion("No se pudo cerrar el ResultSet traído por la consulta.", e);
            }
        }
        return plantillas;
    }    
}

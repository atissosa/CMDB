package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaOtroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorPlantillaOtroEquipoSqlServer extends MapeadorPlantillaOtroEquipo {

    MapeadorPlantillaOtroEquipoSqlServer(PlantillaOtroEquipo plantillaOtroEquipo) {
        super(plantillaOtroEquipo);
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
        String sql = "SELECT * FROM VPlantillasOtroEquipo";
        String condicion = "";
        if (this.getPlantillaOtroEquipo().getId() != null) {
            condicion += " Id = ?";
        }
        if (this.getPlantillaOtroEquipo().getMarca() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " marca = ?";
        }
        if (this.getPlantillaOtroEquipo().getModelo() != null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " modelo = ?";
        }
        if (this.getPlantillaOtroEquipo().getTipo()!= null) {
            if(condicion.endsWith("?"))
                condicion += " AND";
            condicion += " tipo = ?";
        }
        if (!condicion.equals("")) {
            condicion = " WHERE" + condicion;
        }
        return sql + condicion;
    }

    @Override
    protected ArrayList parametrosBuscar() {
        ArrayList parametros = new ArrayList();
        if (this.getPlantillaOtroEquipo().getId() != null) {
            parametros.add(this.getPlantillaOtroEquipo().getId());
        }
        if (this.getPlantillaOtroEquipo().getMarca() != null) {
            parametros.add(this.getPlantillaOtroEquipo().getMarca());
        }
        if (this.getPlantillaOtroEquipo().getModelo() != null) {
            parametros.add(this.getPlantillaOtroEquipo().getModelo());
        }
        if (this.getPlantillaOtroEquipo().getTipo()!= null) {
            parametros.add(this.getPlantillaOtroEquipo().getTipo().toString());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<PlantillaOtroEquipo> plantillas = new ArrayList();
        PlantillaOtroEquipo plantillaOtroEquipo;
        try {
            while (rs.next()) {
                plantillaOtroEquipo = new PlantillaOtroEquipo();
                plantillaOtroEquipo.setId(rs.getInt("Id"));
                if(rs.wasNull())
                    plantillaOtroEquipo.setId(null);
                plantillaOtroEquipo.setMarca(rs.getString("marca"));
                plantillaOtroEquipo.setModelo((rs.getString("modelo")));
                plantillaOtroEquipo.setMesesGarantia(rs.getInt("mesesGarantia"));
                if(rs.wasNull())
                    plantillaOtroEquipo.setMesesGarantia(null);
                plantillaOtroEquipo.setPrecioCompra(rs.getDouble("precioDeCompra"));
                plantillaOtroEquipo.setTipo(OtroEquipo.TiposEquipo.valueOf(rs.getString("tipo")));
                plantillas.add(plantillaOtroEquipo);
            }
            if (plantillas.size() == 1) {
                PlantillaOtroEquipo p = plantillas.get(0);
                this.getPlantillaOtroEquipo().setId(p.getId());
                this.getPlantillaOtroEquipo().setMarca(p.getMarca());
                this.getPlantillaOtroEquipo().setModelo(p.getModelo());
                this.getPlantillaOtroEquipo().setMesesGarantia(p.getMesesGarantia());
                this.getPlantillaOtroEquipo().setPrecioCompra(p.getPrecioCompra());
            } else {
                this.setPlantillaOtroEquipo(null);
            }

        } catch (SQLException e) {
            plantillas.clear();
            this.setPlantillaOtroEquipo(null);
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

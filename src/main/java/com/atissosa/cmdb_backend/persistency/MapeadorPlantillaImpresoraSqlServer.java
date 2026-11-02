package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.PlantillaImpresora;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorPlantillaImpresoraSqlServer extends MapeadorPlantillaImpresora {

    public MapeadorPlantillaImpresoraSqlServer(PlantillaImpresora plantillaImpresora) {
        super(plantillaImpresora);
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
        String sql = "SELECT * FROM VPlantillasImpresoras";
        String condicion = "";
        if (this.getPlantillaImpresora().getId() != null) {
            condicion += " Id = ?";
        }
        if (this.getPlantillaImpresora().getMarca() != null) {
            if (condicion.endsWith("?")) {
                condicion += " AND";
            }
            condicion += " marca = ?";
        }
        if (this.getPlantillaImpresora().getModelo() != null) {
            if (condicion.endsWith("?")) {
                condicion += " AND";
            }
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
        if (this.getPlantillaImpresora().getId() != null) {
            parametros.add(this.getPlantillaImpresora().getId());
        }
        if (this.getPlantillaImpresora().getMarca() != null) {
            parametros.add(this.getPlantillaImpresora().getMarca());
        }
        if (this.getPlantillaImpresora().getModelo() != null) {
            parametros.add(this.getPlantillaImpresora().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<PlantillaImpresora> plantillas = new ArrayList();
        PlantillaImpresora plantillaImpresora;
        try {
            while (rs.next()) {
                plantillaImpresora = new PlantillaImpresora();
                plantillaImpresora.setId(rs.getInt("Id"));
                if (rs.wasNull()) {
                    plantillaImpresora.setId(null);
                }
                plantillaImpresora.setMarca(rs.getString("marca"));
                plantillaImpresora.setModelo((rs.getString("modelo")));
                plantillaImpresora.setMesesGarantia(rs.getInt("mesesGarantia"));
                if (rs.wasNull()) {
                    plantillaImpresora.setMesesGarantia(null);
                }
                plantillaImpresora.setPrecioCompra(rs.getDouble("precioDeCompra"));
                plantillaImpresora.setMultifuncion(rs.getBoolean("multifuncion"));
                plantillaImpresora.setOpcion(Impresora.OpcionesImpresora.valueOf(rs.getString("opcion")));
                plantillaImpresora.setConexion(Impresora.ConexionesImpresora.valueOf(rs.getString("conexion")));
                plantillas.add(plantillaImpresora);
            }
            if (plantillas.size() == 1) {
                PlantillaImpresora p = plantillas.get(0);
                this.getPlantillaImpresora().setId(p.getId());
                this.getPlantillaImpresora().setMarca(p.getMarca());
                this.getPlantillaImpresora().setModelo(p.getModelo());
                this.getPlantillaImpresora().setMesesGarantia(p.getMesesGarantia());
                this.getPlantillaImpresora().setPrecioCompra(p.getPrecioCompra());
                this.getPlantillaImpresora().setMultifuncion(p.isMultifuncion());
                this.getPlantillaImpresora().setOpcion(p.getOpcion());
                this.getPlantillaImpresora().setConexion(p.getConexion());
            } else {
                this.setPlantillaImpresora(null);
            }

        } catch (SQLException e) {
            plantillas.clear();
            this.setPlantillaImpresora(null);
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

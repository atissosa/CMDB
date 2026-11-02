package com.atissosa.cmdb_backend.persistency;

import com.atissosa.cmdb_backend.domain.PlantillaPc;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MapeadorPlantillaPcSqlServer extends MapeadorPlantillaPc {

    public MapeadorPlantillaPcSqlServer(PlantillaPc plantillaPc) {
        super(plantillaPc);
    }

    public MapeadorPlantillaPcSqlServer() {
        super();
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
        String sql = "SELECT * FROM VPlantillasPcs";
        String condicion = "";
        if (this.getPlantillaPc().getId() != null) {
            condicion += " Id = ?";
        }
        if (this.getPlantillaPc().getMarca() != null) {
            if (condicion.endsWith("?")) {
                condicion += " AND";
            }
            condicion += " marca = ?";
        }
        if (this.getPlantillaPc().getModelo() != null) {
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
        if (this.getPlantillaPc().getId() != null) {
            parametros.add(this.getPlantillaPc().getId());
        }
        if (this.getPlantillaPc().getMarca() != null) {
            parametros.add(this.getPlantillaPc().getMarca());
        }
        if (this.getPlantillaPc().getModelo() != null) {
            parametros.add(this.getPlantillaPc().getModelo());
        }
        return parametros;
    }

    @Override
    public ArrayList leer(ResultSet rs) throws ExcepcionConexion {
        ArrayList<PlantillaPc> plantillas = new ArrayList();
        PlantillaPc plantillaPc;
        try {
            while (rs.next()) {
                plantillaPc = new PlantillaPc();
                plantillaPc.setId(rs.getInt("Id"));
                plantillaPc.setMarca(rs.getString("marca"));
                plantillaPc.setModelo(rs.getString("modelo"));
                plantillaPc.setMesesGarantia(rs.getInt("mesesGarantia"));
                plantillaPc.setMemoriaRam(rs.getInt("memoriaRam"));
                plantillaPc.setPrecioCompra(rs.getDouble("precioDeCompra"));
                plantillaPc.setProcesador(rs.getString("procesador"));
                plantillaPc.setTamanoñoDisco(rs.getInt("tamanoDisco"));
                plantillas.add(plantillaPc);
            }
            if (plantillas.size() == 1) {
                PlantillaPc p = plantillas.get(0);
                this.getPlantillaPc().setId(p.getId());
                this.getPlantillaPc().setMarca(p.getMarca());
                this.getPlantillaPc().setModelo(p.getModelo());
                this.getPlantillaPc().setMesesGarantia(p.getMesesGarantia());
                this.getPlantillaPc().setMemoriaRam(p.getMemoriaRam());
                this.getPlantillaPc().setPrecioCompra(p.getPrecioCompra());
                this.getPlantillaPc().setProcesador(p.getProcesador());
                this.getPlantillaPc().setTamanoñoDisco(p.getTamanoñoDisco());
            } else {
                this.setPlantillaPc(null);
            }
        } catch (SQLException e) {
            plantillas.clear();
            this.setPlantillaPc(null);
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

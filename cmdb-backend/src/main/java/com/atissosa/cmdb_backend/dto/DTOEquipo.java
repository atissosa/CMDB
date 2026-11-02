package com.atissosa.cmdb_backend.dto;

import com.atissosa.cmdb_backend.domain.Equipo;
import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.Lugar;
import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.PC;
import com.atissosa.cmdb_backend.domain.Validadora;
import java.text.SimpleDateFormat;

public class DTOEquipo {
    
    private Integer id;
    private String codigo;
    private String numeroSerie;
    private String marca;
    private String modelo;
    private Double precioDeCompra;
    private String fechaAlta;
    private String descripcion;
    private Integer mesesGarantia;
    private Boolean esPrestamo;
    private Integer numeroPrecinto;
    private String estado;
    private String lugarPropietario;
    private Integer idLugarPropietario;
    private String lugarActual;
    private Integer idLugarActual;
    private String usuario;
    private String tipo;
    private Boolean esConfigurable;
    private Integer idEquipoPrestamo;
    private String numeroSerieEquipoPrestamo;
    private String categoriaMantenimiento;
    private Integer cantidadMesesEnPrestamo;
    private Integer cantidadVecesPrestado;
    private Double depreciacionMensual;
    private Double costoVentaUsado;
    
    public DTOEquipo() {
    }
    
    public DTOEquipo(Equipo equipo) {
        this.id = equipo.getId();
        this.codigo = equipo.getCodigo();
        this.numeroSerie = equipo.getNumeroDeSerie();
        this.marca = equipo.getMarca();
        this.modelo = equipo.getModelo();
        this.precioDeCompra = equipo.getPrecioDeCompra();
        this.setFechaAlta((new SimpleDateFormat("yyyy-MM-dd")).format(equipo.getFechaAlta()));
        this.descripcion = equipo.getDescripcion();
        this.mesesGarantia = equipo.getMesesGarantia();
        this.esPrestamo = equipo.getEsPrestamo();
        this.numeroPrecinto = equipo.getNumeroPrecinto();
        this.estado = equipo.getEstado().toString();
        this.idLugarPropietario = equipo.getIdLugar();
        if(equipo.getUsuario() != null)
            this.usuario = equipo.getUsuario().getUsuario();
        this.tipo = equipo.getTipo();
        if(equipo.getLugar() != null)
            this.lugarPropietario = equipo.getLugar().getNombre();
        this.lugarActual = equipo.getNombreLugarActual();
        Lugar actual = equipo.getLugarActual();
        if(actual != null){
            this.idLugarActual = actual.getId();
            this.lugarActual = actual.getNombre();
        }
        this.esConfigurable = equipo.esConfigurable();
        if(equipo.getEquipoPrestamo() != null){
            this.idEquipoPrestamo = equipo.getEquipoPrestamo().getId();
            this.numeroSerieEquipoPrestamo = equipo.getEquipoPrestamo().getNumeroDeSerie();
        }
    }
    
    
    public static DTOEquipo nuevoDTO(Equipo e) {
        DTOEquipo dto = null;
        if(e != null){
            switch (e.getTipo()) {
                case "PC":
                    dto = new DTOPC((PC) e);
                    break;
                case "IMPRESORA":
                    dto = new DTOImpresora((Impresora) e);
                    break;
                case "VALIDADORA":
                    dto = new DTOValidadora((Validadora) e);
                    break;
                default:
                    dto = new DTOOtroEquipo((OtroEquipo) e);
                    break;
            }
        }
        return dto;
    }

    public Double getDepreciacionMensual() {
        return depreciacionMensual;
    }

    public void setDepreciacionMensual(Double depreciacionMensual) {
        this.depreciacionMensual = depreciacionMensual;
    }

    public Double getCostoVentaUsado() {
        return costoVentaUsado;
    }

    public void setCostoVentaUsado(Double costoVentaUsado) {
        this.costoVentaUsado = costoVentaUsado;
    }

    public Integer getCantidadMesesEnPrestamo() {
        return cantidadMesesEnPrestamo;
    }

    public void setCantidadMesesEnPrestamo(Integer cantidadMesesEnPrestamo) {
        this.cantidadMesesEnPrestamo = cantidadMesesEnPrestamo;
    }

    public Integer getCantidadVecesPrestado() {
        return cantidadVecesPrestado;
    }

    public void setCantidadVecesPrestado(Integer cantidadVecesPrestado) {
        this.cantidadVecesPrestado = cantidadVecesPrestado;
    }

    public String getCategoriaMantenimiento() {
        return categoriaMantenimiento;
    }

    public void setCategoriaMantenimiento(String categoriaMantenimiento) {
        this.categoriaMantenimiento = categoriaMantenimiento;
    }
    
    
    public Integer getId() { return this.id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getCodigo() { return this.codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNumeroSerie() { return this.numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    
    public String getMarca() { return this.marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    public String getModelo() { return this.modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public Double getPrecioDeCompra() { return this.precioDeCompra; }
    public void setPrecioDeCompra(Double precioDeCompra) { this.precioDeCompra = precioDeCompra; }
    
    public String getFechaAlta() { return this.fechaAlta; }
    public void setFechaAlta(String fechaAlta) { this.fechaAlta = fechaAlta; }
    
    public String getDescripcion() { return this.descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Integer getMesesGarantia() { return this.mesesGarantia; }
    public void setMesesGarantia(Integer mesesGarantia) { this.mesesGarantia = mesesGarantia; }
    
    public Boolean getEsPrestamo() { return this.esPrestamo; }
    public void setEsPrestamo(Boolean esPrestamo) { this.esPrestamo = esPrestamo; }
    
    public Integer getNumeroPrecinto() { return this.numeroPrecinto; }
    public void setNumeroPrecinto(Integer numeroPrecinto) { this.numeroPrecinto = numeroPrecinto; }
    
    public String getEstado() { return this.estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getLugar() { return this.lugarPropietario; }
    public void setLugar(String lugarPropietario) { this.lugarPropietario = lugarPropietario; }
    
    public Integer getidLugar() { return this.idLugarPropietario; }
    public void setidLugar(Integer idLugarPropietario) { this.idLugarPropietario = idLugarPropietario; }
    
    public String getLugarActual() { return this.lugarActual; }
    public void setLugarActual(String lugarActual) { this.lugarActual = lugarActual; }
    
    public Integer getidLugarActual() { return this.idLugarActual; }
    public void setidLugarActual(Integer idLugarActual) { this.idLugarActual = idLugarActual; }
    
    public String getUsuario() { return this.usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getTipo() { return this.tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Boolean esConfigurable() { return this.esConfigurable; }
    
    public Integer getIdEquipoPrestamo() { return this.idEquipoPrestamo; }
    public void setIdEquipoPrestamo(Integer idEquipoPrestamo) { this.idEquipoPrestamo = idEquipoPrestamo; }
    
    public String getNumeroSerieEquipoPrestamo() { return this.numeroSerieEquipoPrestamo; }
    public void setNumeroSerieEquipoPrestamo(String numeroSerieEquipoPrestamo) { this.numeroSerieEquipoPrestamo = numeroSerieEquipoPrestamo; }
    
}

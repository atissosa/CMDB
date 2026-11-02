package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.domain.Equipo;
import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.Impresora.OpcionesImpresora;
import com.atissosa.cmdb_backend.domain.ItemHistoricoLugar;
import com.atissosa.cmdb_backend.domain.Lugar;
import com.atissosa.cmdb_backend.domain.OtroEquipo;
import com.atissosa.cmdb_backend.domain.OtroEquipo.TiposEquipo;
import com.atissosa.cmdb_backend.domain.PC;
import com.atissosa.cmdb_backend.domain.PlantillaEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaImpresora;
import com.atissosa.cmdb_backend.domain.PlantillaOtroEquipo;
import com.atissosa.cmdb_backend.domain.PlantillaPc;
import com.atissosa.cmdb_backend.domain.PlantillaValidadora;
import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.domain.SubAgencia;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.domain.Validadora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import com.atissosa.cmdb_backend.global.Propiedades;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.atissosa.cmdb_backend.persistency.MapeadorDepartamentoCentral;
import com.atissosa.cmdb_backend.persistency.MapeadorEquipo;
import com.atissosa.cmdb_backend.persistency.MapeadorImpresora;
import com.atissosa.cmdb_backend.persistency.MapeadorItemHistoricoLugar;
import com.atissosa.cmdb_backend.persistency.MapeadorOtroEquipo;
import com.atissosa.cmdb_backend.persistency.MapeadorPC;
import com.atissosa.cmdb_backend.persistency.MapeadorPlantillaEquipo;
import com.atissosa.cmdb_backend.persistency.MapeadorService;
import com.atissosa.cmdb_backend.persistency.MapeadorSubAgencia;
import com.atissosa.cmdb_backend.persistency.MapeadorValidadora;
import com.atissosa.cmdb_backend.persistency.Persistencia;

public class SubSistemaCMDB {

    private DepartamentoCentral departamentoDefecto;
    private ArrayList<PlantillaEquipo> plantillas;
    private static Integer ROJO_CANT_MIN_MESES_EN_PRESTAMO;
    private static Integer ROJO_CANT_MIN_VECES_PRESTADO;
    private static Integer AMARILLO_CANT_MIN_MESES_EN_PRESTAMO;
    private static Integer AMARILLO_CANT_MIN_VECES_PRESTADO;
    private static Integer VERDE_CANT_MAX_MESES_EN_PRESTAMO;
    private static Integer VERDE_CANT_MAX_VECES_PRESTADO;

    void formatearCodigosDeEquiposBD() {
        try {
            this.buscarEquipos().forEach(e -> {
                try {
                    this.modificarEquipo(e, null);
                } catch (Exception ex) {
                    Logger.getLogger(SubSistemaCMDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(SubSistemaCMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private enum Categorias_Mantenimiento_Prestamos {
        ROJO,
        AMARILLO,
        VERDE
    }

    protected SubSistemaCMDB() {
        this.cargarDepartamentoDefecto();
        this.cargarPlantillasBD();
        ROJO_CANT_MIN_MESES_EN_PRESTAMO = 24;
        ROJO_CANT_MIN_VECES_PRESTADO = 10;
        AMARILLO_CANT_MIN_MESES_EN_PRESTAMO = 12;
        AMARILLO_CANT_MIN_VECES_PRESTADO = 5;
        VERDE_CANT_MAX_MESES_EN_PRESTAMO = 12;
        VERDE_CANT_MAX_VECES_PRESTADO = 5;
    }

    protected DepartamentoCentral getDepartamentoDefecto() {
        return this.departamentoDefecto;
    }

    protected void cargarDepartamentoDefecto() {
        try {
            this.departamentoDefecto = this.buscarDepartamentoCentral(Propiedades.DEPARTAMENTO_DEFECTO);

        } catch (ExcepcionConexion ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Service altaService(String nombre, String descripcion, String direccion) throws ExcepcionConexion, Exception, Exception {
        Service s = null;
        if (this.buscarService(nombre) == null) {
            s = new Service(nombre, descripcion, direccion);
            MapeadorService mapeador = MapeadorService.nuevoMapeador(s);
            int id = Persistencia.getInstancia().insertarSP(mapeador);
            if (id <= 0) {
                s = null;
            } else {
                s.setId(id);
            }
        }
        return s;
    }

    protected Service buscarService(String nombre) throws ExcepcionConexion, Exception {
        Service resultado = null;
        Service service = new Service();
        service.setNombre(nombre);
        MapeadorService mapeador = MapeadorService.nuevoMapeador(service);
        ArrayList encontrados = Persistencia.getInstancia().buscar(mapeador);
        if (encontrados.size() > 0) {
            resultado = (Service) encontrados.get(0);
        }
        return resultado;
    }

    protected DepartamentoCentral buscarDepartamentoCentral(String nombre) throws ExcepcionConexion, Exception {
        DepartamentoCentral resultado = null;
        DepartamentoCentral departamento = new DepartamentoCentral();
        departamento.setNombre(nombre);
        MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(departamento);
        ArrayList encontrados = Persistencia.getInstancia().buscar(mapeador);
        if (encontrados.size() > 0) {
            resultado = (DepartamentoCentral) encontrados.get(0);
        }
        return resultado;
    }

    protected DepartamentoCentral altaDepartamentoCentral(String nombre, String descripcion) throws ExcepcionConexion, Exception {
        DepartamentoCentral d = null;
        if (this.buscarDepartamentoCentral(nombre) == null) {
            d = new DepartamentoCentral(nombre, descripcion);
            MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(d);
            int id = Persistencia.getInstancia().insertarSP(mapeador);
            if (id <= 0) {
                d = null;
            } else {
                d.setId(id);
            }
        }
        return d;
    }

    protected ArrayList<DepartamentoCentral> getDepartamentos() throws ExcepcionConexion, Exception {
        ArrayList<DepartamentoCentral> departamentos = new ArrayList();
        MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(null);
        for (Object departamento : Persistencia.getInstancia().buscar(mapeador)) {
            departamentos.add((DepartamentoCentral) departamento);
        }
        return departamentos;
    }

    protected DepartamentoCentral modificarDepartamentoCentral(Integer id, String nombre, String descripcion) throws ExcepcionConexion, Exception {
        DepartamentoCentral d = null;
        d = new DepartamentoCentral(nombre, descripcion);
        d.setId(id);
        MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(d);
        int res = Persistencia.getInstancia().actualizar(mapeador);
        if (res <= 0) {
            d = null;
        }
        return d;
    }

    protected Service modificarService(Integer id, String nombre, String descripcion, String direccion) throws ExcepcionConexion, Exception {
        Service s = null;
        s = new Service(nombre, descripcion, direccion);
        s.setId(id);
        MapeadorService mapeador = MapeadorService.nuevoMapeador(s);
        int res = Persistencia.getInstancia().actualizar(mapeador);
        if (res <= 0) {
            s = null;
        }
        return s;
    }

    protected boolean bajaDepartamentoCentral(Integer id) throws ExcepcionConexion, Exception {
        DepartamentoCentral d = new DepartamentoCentral();
        d.setId(id);
        boolean ok = true;
        MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(d);
        int res = Persistencia.getInstancia().eliminar(mapeador);
        if (res <= 0) {
            d = null;
            ok = false;
        }
        return ok;
    }

    protected boolean bajaService(Integer id) throws ExcepcionConexion, Exception {
        Service s = new Service();
        s.setId(id);
        boolean ok = true;
        MapeadorService mapeador = MapeadorService.nuevoMapeador(s);
        int res = Persistencia.getInstancia().eliminar(mapeador);
        if (res <= 0) {
            s = null;
            ok = false;
        }
        return ok;
    }

    protected ArrayList<Service> buscarServices(int id) throws ExcepcionConexion, Exception {
        ArrayList<Service> services = new ArrayList<>();
        Service s;
        if (id > 0) {
            s = new Service();
            s.setId(id);
        } else {
            s = null;
        }
        MapeadorService mapeador = MapeadorService.nuevoMapeador(s);
        services = Persistencia.getInstancia().buscar(mapeador);
        return services;
    }

    protected ArrayList<SubAgencia> buscarSubagencias(int numero, boolean id) throws ExcepcionConexion, Exception {
        ArrayList<SubAgencia> subagencias = new ArrayList<>();
        SubAgencia s;
        if (numero > 0) {
            s = new SubAgencia();
            if (id) {
                s.setId(numero);
            } else {
                s.setNumero(numero);
            }
        } else {
            s = null;
        }
        MapeadorSubAgencia mapeador = MapeadorSubAgencia.nuevoMapeador(s);
        subagencias = Persistencia.getInstancia().buscar(mapeador);
        return subagencias;
    }

    protected ArrayList<DepartamentoCentral> buscarDepartamentosCentral(int id) throws ExcepcionConexion, Exception {
        ArrayList<DepartamentoCentral> departamentos = new ArrayList<>();
        DepartamentoCentral d;
        if (id > 0) {
            d = new DepartamentoCentral();
            d.setId(id);
        } else {
            d = null;
        }
        MapeadorDepartamentoCentral mapeador = MapeadorDepartamentoCentral.nuevoMapeador(d);
        departamentos = Persistencia.getInstancia().buscar(mapeador);
        return departamentos;
    }

    protected Lugar buscarLugar(Integer idLugar) throws ExcepcionConexion, Exception {
        Lugar retorno = null;
        if (this.getDepartamentoDefecto().getId().equals(idLugar)) {
            retorno = this.getDepartamentoDefecto();
        } else {
            ArrayList<SubAgencia> listaSubagencia = this.buscarSubagencias(idLugar, true);
            if (listaSubagencia.size() > 0) {
                retorno = listaSubagencia.get(0);
            } else {
                ArrayList<DepartamentoCentral> listaDepartamentos = this.buscarDepartamentosCentral(idLugar);
                if (listaDepartamentos.size() > 0) {
                    retorno = listaDepartamentos.get(0);
                } else {
                    ArrayList<Service> listaServices = this.buscarServices(idLugar);
                    if (listaServices.size() > 0) {
                        retorno = listaServices.get(0);
                    }
                }
            }
        }
        return retorno;
    }

    protected PC altaPc(String numeroDeSerie, String marca, String modelo,
            Double precioDeCompra, String fechaAlta, String descripcion, Integer mesesGarantia, Boolean esPrestamo,
            Integer numeroPrecinto, Lugar l, Usuario usuario, String ip, String nombreHost, String procesador,
            Integer memoriaRam, Integer tamanoDisco, String tipo) throws ExcepcionConexion, Exception {
        PC pc = null;
        if (esPrestamo) {
            l = this.getDepartamentoDefecto();
        }
        if (this.buscarEquipo(numeroDeSerie) == null) {
            pc = new PC(ip, nombreHost, procesador, memoriaRam, tamanoDisco,
                    crearCodigoEquipo(l, tipo, numeroDeSerie, esPrestamo), numeroDeSerie, marca,
                    modelo, precioDeCompra, new SimpleDateFormat("yyyy-MM-dd").parse(fechaAlta), descripcion, mesesGarantia,
                    esPrestamo, numeroPrecinto, l, usuario);
            MapeadorPC mapeador = MapeadorPC.nuevoMapeador(pc);
            int id = Persistencia.getInstancia().insertarSP(mapeador);
            if (id <= 0) {
                pc = null;
            } else {
                pc.setId(id);
                ItemHistoricoLugar item = new ItemHistoricoLugar();
                item.setEquipo(pc);
                item.setLugar(this.getDepartamentoDefecto());
                item.setFecha(new Date());
                item.setUsuario(usuario);
                item.setEstado(pc.getEstado());
                pc.getHistoriaLugares().add(item);
                MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                Persistencia.getInstancia().insertarSP(mapI);
                if (pc.esConfigurable()) {
                    pc.setEstado(EstadosEquipos.INACTIVO);
                } else if (esPrestamo) {
                    pc.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
                } else {
                    pc.setEstado(EstadosEquipos.ACTIVO);
                }
                ItemHistoricoLugar item2 = new ItemHistoricoLugar();
                item2.setEquipo(pc);
                if (!pc.esConfigurable() && !esPrestamo) {
                    item2.setLugar(pc.getLugar());
                } else {
                    item2.setLugar(this.getDepartamentoDefecto());
                }
                item2.setFecha(new Date());
                item2.setUsuario(usuario);
                item2.setEstado(pc.getEstado());
                pc.getHistoriaLugares().add(item2);
                mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item2);
                Persistencia.getInstancia().insertarSP(mapI);
                this.modificarEquipo(pc, usuario);
            }
        }
        return pc;
    }

    protected Impresora altaImpresora(String numeroDeSerie, String marca, String modelo,
            Double precioDeCompra, String fechaAlta, String descripcion, Integer mesesGarantia,
            Boolean esPrestamo, Integer numeroPrecinto, Lugar l, Usuario u, String ip,
            String nombreHost, Boolean multifuncion, String conexion, String opcion, String tipo) throws ExcepcionConexion, Exception {
        Impresora imp = null;
        if (esPrestamo) {
            l = this.getDepartamentoDefecto();
        }
        if (this.buscarEquipo(numeroDeSerie) == null) {
            imp = new Impresora(Impresora.OpcionesImpresora.valueOf(opcion),
                    multifuncion, ip, nombreHost, Impresora.ConexionesImpresora.valueOf(conexion),
                    crearCodigoEquipo(l, tipo, numeroDeSerie, esPrestamo),
                    numeroDeSerie, marca, modelo, precioDeCompra, new SimpleDateFormat("yyyy-MM-dd").parse(fechaAlta), descripcion,
                    mesesGarantia, esPrestamo, numeroPrecinto, l, u);
            MapeadorImpresora map = MapeadorImpresora.nuevoMapeador(imp);
            int id = Persistencia.getInstancia().insertarSP(map);
            if (id <= 0) {
                imp = null;
            } else {
                imp.setId(id);
                ItemHistoricoLugar item = new ItemHistoricoLugar();
                item.setEquipo(imp);
                item.setLugar(this.getDepartamentoDefecto());
                item.setFecha(new Date());
                item.setUsuario(u);
                item.setEstado(imp.getEstado());
                imp.getHistoriaLugares().add(item);
                MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                Persistencia.getInstancia().insertarSP(mapI);
                if (imp.esConfigurable()) {
                    imp.setEstado(EstadosEquipos.INACTIVO);
                } else if (esPrestamo) {
                    imp.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
                } else {
                    imp.setEstado(EstadosEquipos.ACTIVO);
                }
                ItemHistoricoLugar item2 = new ItemHistoricoLugar();
                item2.setEquipo(imp);
                if (!imp.esConfigurable() && !esPrestamo) {
                    item2.setLugar(imp.getLugar());
                } else {
                    item2.setLugar(this.getDepartamentoDefecto());
                }
                item2.setFecha(new Date());
                item2.setUsuario(u);
                item2.setEstado(imp.getEstado());
                imp.getHistoriaLugares().add(item2);
                mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item2);
                Persistencia.getInstancia().insertarSP(mapI);
                this.modificarEquipo(imp, u);
            }
        }
        return imp;
    }

    protected Validadora altaValidadora(String numeroDeSerie, String marca,
            String modelo, Double precioDeCompra, String fechaAlta, String descripcion,
            Integer mesesGarantia, Boolean esPrestamo, Integer numeroPrecinto, Lugar l, Usuario u, String tipo) throws ExcepcionConexion, Exception {
        Validadora val = null;
        if (esPrestamo) {
            l = this.getDepartamentoDefecto();
        }
        if (this.buscarEquipo(numeroDeSerie) == null) {
            val = new Validadora(crearCodigoEquipo(l, tipo, numeroDeSerie, esPrestamo),
                    numeroDeSerie, marca, modelo, precioDeCompra,
                    new SimpleDateFormat("yyyy-MM-dd").parse(fechaAlta), descripcion, mesesGarantia, esPrestamo, numeroPrecinto, l, u);
            MapeadorValidadora map = MapeadorValidadora.nuevoMapeador(val);
            int id = Persistencia.getInstancia().insertarSP(map);
            if (id <= 0) {
                val = null;
            } else {
                val.setId(id);
                ItemHistoricoLugar item = new ItemHistoricoLugar();
                item.setEquipo(val);
                item.setLugar(this.getDepartamentoDefecto());
                item.setFecha(new Date());
                item.setUsuario(u);
                item.setEstado(val.getEstado());
                val.getHistoriaLugares().add(item);
                MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                Persistencia.getInstancia().insertarSP(mapI);
                if (val.esConfigurable()) {
                    val.setEstado(EstadosEquipos.INACTIVO);
                } else if (esPrestamo) {
                    val.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
                } else {
                    val.setEstado(EstadosEquipos.ACTIVO);
                }
                ItemHistoricoLugar item2 = new ItemHistoricoLugar();
                item2.setEquipo(val);
                if (!val.esConfigurable() && !esPrestamo) {
                    item2.setLugar(val.getLugar());
                } else {
                    item2.setLugar(this.getDepartamentoDefecto());
                }
                item2.setFecha(new Date());
                item2.setUsuario(u);
                item2.setEstado(val.getEstado());
                val.getHistoriaLugares().add(item2);
                mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item2);
                Persistencia.getInstancia().insertarSP(mapI);
                this.modificarEquipo(val, u);
            }
        }
        return val;
    }

    protected OtroEquipo altaOtroEquipo(String numeroDeSerie, String marca, String modelo,
            Double precioDeCompra, String fechaAlta, String descripcion, Integer mesesGarantia,
            Boolean esPrestamo, Integer numeroPrecinto, Lugar l, Usuario u, String tipo) throws ExcepcionConexion, Exception {
        OtroEquipo oEquipo = null;
        if (esPrestamo) {
            l = this.getDepartamentoDefecto();
        }
        if (this.buscarEquipo(numeroDeSerie) == null) {
            oEquipo = new OtroEquipo(OtroEquipo.TiposEquipo.valueOf(tipo.toUpperCase()),
                    crearCodigoEquipo(l, tipo, numeroDeSerie, esPrestamo), numeroDeSerie,
                    marca, modelo, precioDeCompra, new SimpleDateFormat("yyyy-MM-dd").parse(fechaAlta), descripcion, mesesGarantia, esPrestamo,
                    numeroPrecinto, l, u);
            MapeadorOtroEquipo mapeador = MapeadorOtroEquipo.nuevoMapeador(oEquipo);
            int id = Persistencia.getInstancia().insertarSP(mapeador);
            if (id <= 0) {
                oEquipo = null;
            } else {
                oEquipo.setId(id);
                ItemHistoricoLugar item = new ItemHistoricoLugar();
                item.setEquipo(oEquipo);
                item.setLugar(this.getDepartamentoDefecto());
                item.setFecha(new Date());
                item.setUsuario(u);
                item.setEstado(oEquipo.getEstado());
                oEquipo.getHistoriaLugares().add(item);
                MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                Persistencia.getInstancia().insertarSP(mapI);
                if (oEquipo.esConfigurable()) {
                    oEquipo.setEstado(EstadosEquipos.INACTIVO);
                } else if (esPrestamo) {
                    oEquipo.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
                } else {
                    oEquipo.setEstado(EstadosEquipos.ACTIVO);
                }
                ItemHistoricoLugar item2 = new ItemHistoricoLugar();
                item2.setEquipo(oEquipo);
                if (!oEquipo.esConfigurable() && !esPrestamo) {
                    item2.setLugar(oEquipo.getLugar());
                } else {
                    item2.setLugar(this.getDepartamentoDefecto());
                }
                item2.setFecha(new Date());
                item2.setUsuario(u);
                item2.setEstado(oEquipo.getEstado());
                oEquipo.getHistoriaLugares().add(item2);
                mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item2);
                Persistencia.getInstancia().insertarSP(mapI);
                this.modificarEquipo(oEquipo, u);
            }
        }
        return oEquipo;
    }

    protected ArrayList<Equipo> buscarEquipos(DTOFiltroEquipo filtro) throws ExcepcionConexion, Exception {
        ArrayList<Equipo> retorno = new ArrayList();
        try {
            if (!filtro.tieneTipo() || (filtro.tieneTipo() && filtro.getTipo().contains("pc"))) {
                MapeadorPC mapPc = MapeadorPC.nuevoMapeador(filtro);
                ArrayList pcS = Persistencia.getInstancia().buscar(mapPc);
                retorno.addAll((ArrayList<PC>) (pcS));
            }
            if (!filtro.tieneTipo() || (filtro.tieneTipo() && filtro.getTipo().contains("impresora"))) {
                MapeadorImpresora mapImp = MapeadorImpresora.nuevoMapeador(filtro);
                ArrayList imps = Persistencia.getInstancia().buscar(mapImp);
                retorno.addAll((ArrayList<Impresora>) (imps));
            }
            if (!filtro.tieneTipo() || (filtro.tieneTipo() && filtro.getTipo().contains("validadora"))) {
                MapeadorValidadora mapVal = MapeadorValidadora.nuevoMapeador(filtro);
                ArrayList vals = Persistencia.getInstancia().buscar(mapVal);
                retorno.addAll((ArrayList<Validadora>) (vals));
            }
            boolean cumpleOtrosEquipos = false;
            for (String t : filtro.getTipo()) {
                if (!t.equalsIgnoreCase("pc") && !t.equalsIgnoreCase("impresora") && !t.equalsIgnoreCase("validadora")) {
                    cumpleOtrosEquipos = true;
                }
            }
            if (!filtro.tieneTipo() || (filtro.tieneTipo() && cumpleOtrosEquipos)) {
                MapeadorOtroEquipo mapOtro = MapeadorOtroEquipo.nuevoMapeador(filtro);
                ArrayList otros = Persistencia.getInstancia().buscar(mapOtro);
                retorno.addAll((ArrayList<OtroEquipo>) (otros));
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(SubSistemaCMDB.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    protected ArrayList<Equipo> buscarEquipos() throws ExcepcionConexion, Exception {
        return this.buscarEquipos(new DTOFiltroEquipo());
    }

    protected Equipo cargarEquipoConItemsHistoricos(Equipo equipo) throws ExcepcionConexion, Exception {
        //Para el equipo se cargan relaciones
        //Histórico de lugares/estados
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setIdEquipo(equipo.getId());
        MapeadorItemHistoricoLugar mapItem = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        ArrayList<ItemHistoricoLugar> items = Persistencia.getInstancia().buscar(mapItem);
        DTOFiltroEquipo filtroEquipo = new DTOFiltroEquipo();
        for (ItemHistoricoLugar i : items) {
            i.setEquipo(equipo);
            i.setLugar(this.buscarLugar(i.getIdLugar()));
            //Equipo de préstamo asociado al item si lo hay
            if (i.getIdEquipoPrestamo() != null) {
                filtroEquipo.agregarId(i.getIdEquipoPrestamo());
            }
        }
        equipo.setHistoriaLugares(items);
        equipo.setLugar(this.buscarLugar(equipo.getIdLugar()));
        //Equipo de préstamo asociado al equipo si lo hay
        if (equipo.getIdEquipoPrestamo() != null) {
            filtroEquipo.agregarId(equipo.getIdEquipoPrestamo());
        }
        ArrayList<Equipo> equiposAsociados = this.buscarEquipos(filtroEquipo);
        int x = 0;
        boolean encontre = false;
        Equipo asociado = null;
        for (ItemHistoricoLugar i : items) {
            x = 0;
            encontre = false;
            asociado = null;
            while (x < equiposAsociados.size() && !encontre) {
                asociado = equiposAsociados.get(x);
                if (i.getIdEquipoPrestamo() != null && i.getIdEquipoPrestamo().equals(asociado.getId())) {
                    i.setEquipoPrestamo(asociado);
                    encontre = true;
                }
                x++;
            }
        }
        x = 0;
        encontre = false;
        asociado = null;
        if (equipo.getIdEquipoPrestamo() != null) {
            while (x < equiposAsociados.size() && !encontre) {
                asociado = equiposAsociados.get(x);
                if (equipo.getIdEquipoPrestamo().equals(asociado.getId())) {
                    equipo.setEquipoPrestamo(asociado);
                    asociado.setLugar(this.buscarLugar(asociado.getIdLugar()));
                    encontre = true;
                }
                x++;
            }
        }

        return equipo;
    }

    protected ArrayList retornarTiposEquipos() {
        ArrayList retorno = new ArrayList();
        retorno.addAll(Arrays.asList(TiposEquipo.values()));
        retorno
                .add(PC.class
                        .getSimpleName().toUpperCase());
        retorno
                .add(Impresora.class
                        .getSimpleName().toUpperCase());
        retorno
                .add(Validadora.class
                        .getSimpleName().toUpperCase());
        return retorno;
    }

    protected ArrayList retornarEstadosEquipos() {
        ArrayList retorno = new ArrayList();
        retorno.addAll(Arrays.asList(EstadosEquipos.values()));
        return retorno;
    }

    protected ArrayList retornarOpcionesImpresoras() {
        ArrayList retorno = new ArrayList();
        retorno.addAll(Arrays.asList(OpcionesImpresora.values()));
        return retorno;
    }

    protected ArrayList retornarConexionesImpresoras() {
        ArrayList retorno = new ArrayList();
        retorno.addAll(Arrays.asList(Impresora.ConexionesImpresora.values()));
        return retorno;
    }

    private String crearCodigoEquipo(Lugar l, String tipoEquipo, String numeroSerie, boolean esPrestamo) {
        String codigo = "";
        switch (tipoEquipo.toUpperCase()) {
            case "PC":
                codigo += "EQP-";
                break;
            case "IMPRESORA":
                codigo += "IMP-";
                break;
            case "VALIDADORA":
                codigo += "VAL-";
                break;
            case "LECTORA_BANDA_MAGNETICA":
                codigo += "LBT-";
                break;
            case "LECTORA_CHEQUES_TARJETAS":
                codigo += "LCH-";
                break;
            case "ROUTER":
                codigo += "VPN-";
                break;
            case "SCANNER":
                codigo += "SCN-";
                break;
            case "SWITCH":
                codigo += "SWT-";
                break;
            default:
                codigo += OtroEquipo.TiposEquipo.valueOf(tipoEquipo).toString().substring(0, 3) + "-";
                break;
        }
        switch (l.getTipo()) {
            case "SUBAGENCIA":
                if (((SubAgencia) l).getNumero() < 10) {
                    codigo += "AG00" + ((SubAgencia) l).getNumero();
                } else if (((SubAgencia) l).getNumero() < 100) {
                    codigo += "AG0" + ((SubAgencia) l).getNumero();
                } else {
                    codigo += "AG" + ((SubAgencia) l).getNumero();
                }
                break;
            case "DEPARTAMENTOCENTRAL":
                codigo += l.getNombre().substring(0, 3);
                break;
        }
        codigo += "-";
        codigo += numeroSerie.substring(numeroSerie.length() - 3, numeroSerie.length());
        if (esPrestamo) {
            codigo += "-PREST";
        }
        return codigo.toUpperCase();
    }

    protected Equipo buscarEquipo(String numeroSerie) throws ExcepcionConexion, Exception {
        DTOFiltroEquipo filtro = new DTOFiltroEquipo();
        filtro.agregarNumeroSerie(numeroSerie);
        ArrayList<Equipo> busqueda = this.buscarEquipos(filtro);
        Equipo retorno = null;
        if (busqueda.size() == 1) {
            retorno = busqueda.get(0);
        }
        if (retorno != null) {
            retorno = this.cargarEquipoConItemsHistoricos(retorno);
        }
        return retorno;
    }

    protected boolean bajaEquipo(String numeroSerie) throws ExcepcionConexion, Exception {
        Equipo e = this.buscarEquipo(numeroSerie);
        boolean salida = true;
        if (e != null) {
            e.setEstado(Equipo.EstadosEquipos.BAJA);
            MapeadorEquipo map = MapeadorEquipo.nuevoMapeadorEquipo(e);
            Persistencia.getInstancia().eliminar(map);
        } else {
            salida = false;
        }
        return salida;
    }

    protected boolean asignarEquipoParaPrestamo(String numeroSerie, Usuario usuario) throws ExcepcionConexion, Exception {
        boolean salida = false;
        Equipo e = this.buscarEquipo(numeroSerie);
        if (e != null && this.equipoPuedeSerPrestamo(e) && e.getLugar().getId() == this.getDepartamentoDefecto().getId()) {
            e.setLugar(this.buscarLugar(e.getIdLugar()));
            e.setEsPrestamo(true);
            e.setEstado(Equipo.EstadosEquipos.PRESTAMO_DISPONIBLE);
            e.setCodigo(crearCodigoEquipo(e.getLugar(), e.getTipo(), numeroSerie, true));
            MapeadorEquipo map = MapeadorEquipo.nuevoMapeadorEquipo(e);
            Persistencia.getInstancia().actualizar(map);
            ItemHistoricoLugar item = new ItemHistoricoLugar();
            item.setEquipo(e);
            item.setLugar(e.getLugarActual());
            item.setFecha(new Date());
            item.setUsuario(usuario);
            item.setEstado(e.getEstado());

            MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
            Persistencia.getInstancia().insertarSP(mapI);
            salida = true;
        }
        return salida;
    }

    private boolean equipoPuedeSerPrestamo(Equipo e) {
        boolean valido = false;
        if (e.getEstado().equals(Equipo.EstadosEquipos.ALTA)
                || e.getEstado().equals(Equipo.EstadosEquipos.ACTIVO)) {
            valido = true;
        }
        return valido;
    }

    private Validadora modificarValidadora(Validadora validadora, Usuario usuario, Equipo e) throws ExcepcionConexion, Exception {
        if (e == null) {
            validadora = null;
        } else {
            if (validadora.getEsPrestamo() && !validadora.getEsPrestamo().equals(e.getEsPrestamo())) {
                validadora.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
            } else if (!validadora.getEsPrestamo() && !validadora.getEsPrestamo().equals(e.getEsPrestamo())) {
                validadora.setEstado(EstadosEquipos.ACTIVO);
            }
            validadora.setCodigo(this.crearCodigoEquipo(validadora.getLugar(), validadora.getTipo(), validadora.getNumeroDeSerie(), validadora.getEsPrestamo()));
            MapeadorValidadora mapeador = MapeadorValidadora.nuevoMapeador(validadora);
            int res = Persistencia.getInstancia().actualizar(mapeador);
            if (res <= 0) {
                validadora = null;
            } else {
                if (!e.getIdLugar().equals(validadora.getIdLugar())) {
                    ItemHistoricoLugar item = new ItemHistoricoLugar();
                    item.setEquipo(validadora);
                    item.setLugar(validadora.getLugar());
                    item.setFecha(new Date());
                    item.setUsuario(usuario);
                    item.setEstado(validadora.getEstado());
                    MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                    Persistencia.getInstancia().insertarSP(mapI);
                    validadora.getHistoriaLugares().add(item);
                }
            }
        }
        return validadora;
    }

    private PC modificarPC(PC pc, Usuario usuario, Equipo e) throws ExcepcionConexion, Exception {
        if (e == null) {
            pc = null;
        } else {
            if (pc.getEsPrestamo() && !pc.getEsPrestamo().equals(e.getEsPrestamo())) {
                pc.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
            } else if (!pc.getEsPrestamo() && !pc.getEsPrestamo().equals(e.getEsPrestamo())) {
                pc.setEstado(EstadosEquipos.ACTIVO);
            }
            pc.setCodigo(this.crearCodigoEquipo(pc.getLugar(), pc.getTipo(), pc.getNumeroDeSerie(), pc.getEsPrestamo()));
            MapeadorPC mapeador = MapeadorPC.nuevoMapeador(pc);
            int res = Persistencia.getInstancia().actualizar(mapeador);
            if (res <= 0) {
                pc = null;
            } else {
                if (!e.getIdLugar().equals(pc.getIdLugar())) {
                    ItemHistoricoLugar item = new ItemHistoricoLugar();
                    item.setEquipo(pc);
                    item.setLugar(pc.getLugar());
                    item.setFecha(new Date());
                    item.setUsuario(usuario);
                    item.setEstado(pc.getEstado());
                    MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                    Persistencia.getInstancia().insertarSP(mapI);
                    pc.getHistoriaLugares().add(item);
                }
            }
        }
        return pc;
    }

    private OtroEquipo modificarOtroEquipo(OtroEquipo equipo, Usuario usuario, Equipo e) throws ExcepcionConexion, Exception {
        if (e == null) {
            equipo = null;
        } else {
            if (equipo.getEsPrestamo() && !equipo.getEsPrestamo().equals(e.getEsPrestamo())) {
                equipo.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
            } else if (!equipo.getEsPrestamo() && !equipo.getEsPrestamo().equals(e.getEsPrestamo())) {
                equipo.setEstado(EstadosEquipos.ACTIVO);
            }
            equipo.setCodigo(this.crearCodigoEquipo(equipo.getLugar(), equipo.getTipo(), equipo.getNumeroDeSerie(), equipo.getEsPrestamo()));
            MapeadorOtroEquipo mapeador = MapeadorOtroEquipo.nuevoMapeador(equipo);
            int res = Persistencia.getInstancia().actualizar(mapeador);
            if (res <= 0) {
                equipo = null;
            } else {
                if (!e.getIdLugar().equals(equipo.getIdLugar())) {
                    ItemHistoricoLugar item = new ItemHistoricoLugar();
                    item.setEquipo(equipo);
                    item.setLugar(equipo.getLugar());
                    item.setFecha(new Date());
                    item.setUsuario(usuario);
                    item.setEstado(equipo.getEstado());
                    MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                    Persistencia.getInstancia().insertarSP(mapI);
                    equipo.getHistoriaLugares().add(item);
                }
            }
        }
        return equipo;
    }

    private Impresora modificarImpresora(Impresora impresora, Usuario usuario, Equipo e) throws ExcepcionConexion, Exception {
        if (e == null) {
            impresora = null;
        } else {
            if (impresora.getEsPrestamo() && !impresora.getEsPrestamo().equals(e.getEsPrestamo())) {
                impresora.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
            } else if (!impresora.getEsPrestamo() && !impresora.getEsPrestamo().equals(e.getEsPrestamo())) {
                impresora.setEstado(EstadosEquipos.ACTIVO);
            }
            impresora.setCodigo(this.crearCodigoEquipo(impresora.getLugar(), impresora.getTipo(), impresora.getNumeroDeSerie(), impresora.getEsPrestamo()));
            MapeadorImpresora mapeador = MapeadorImpresora.nuevoMapeador(impresora);
            int res = Persistencia.getInstancia().actualizar(mapeador);
            if (res <= 0) {
                impresora = null;
            } else {
                if (!e.getIdLugar().equals(impresora.getIdLugar())) {
                    ItemHistoricoLugar item = new ItemHistoricoLugar();
                    item.setEquipo(e);
                    item.setLugar(impresora.getLugar());
                    item.setFecha(new Date());
                    item.setUsuario(usuario);
                    item.setEstado(e.getEstado());
                    MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
                    Persistencia.getInstancia().insertarSP(mapI);
                    impresora.getHistoriaLugares().add(item);
                }
                e.modificarEquipo(impresora);
            }
        }
        return (Impresora) e;
    }

    protected Equipo modificarEquipo(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        Equipo viejo = null;
        DTOFiltroEquipo filtro = new DTOFiltroEquipo();
        filtro.agregarNumeroSerie(equipo.getNumeroDeSerie());
        ArrayList<Equipo> busquedaViejo = this.buscarEquipos(filtro);
        if (busquedaViejo.size() == 1) {
            viejo = busquedaViejo.get(0);
            viejo.setLugar(this.buscarLugar(viejo.getIdLugar()));
        }
        if (equipo != null) {
            switch (equipo.getTipo()) {
                case "PC":
                    retorno = modificarPC((PC) equipo, usuario, viejo);
                    break;
                case "IMPRESORA":
                    retorno = modificarImpresora((Impresora) equipo, usuario, viejo);
                    break;
                case "VALIDADORA":
                    retorno = modificarValidadora((Validadora) equipo, usuario, viejo);
                    break;
                default:
                    retorno = modificarOtroEquipo((OtroEquipo) equipo, usuario, viejo);
                    break;
            }
        }
        return retorno;
    }

    protected Equipo prestarEquipoSubagencia(Equipo equipo, SubAgencia subagencia, Usuario usuario, Equipo averiado) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(subagencia);
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.PRESTADO);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        if (averiado != null) {
            equipo.setEquipoPrestamo(averiado);
            item.setEquipoPrestamo(averiado);
            this.asociarEquipoPrestamo(averiado, usuario, equipo);
        }
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        return retorno;
    }

    protected Equipo recuperarEquipoPrestado(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(equipo.getLugarActual());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.EN_RECUPERACION);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        return retorno;
    }

    protected Equipo devolverEquipoCentral(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(this.getDepartamentoDefecto());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.PRESTAMO_DISPONIBLE);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        Equipo averiado = equipo.getEquipoPrestamo();
        equipo.setEquipoPrestamo(null);
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        if (averiado != null) {
            averiado.setEquipoPrestamo(null);
            this.modificarEquipo(averiado, usuario);
        }
        retorno = equipo;
        return retorno;
    }

    protected Equipo enviarEquipoAReparar(Equipo equipo, Usuario usuario, Equipo prestado) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(equipo.getLugarActual());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.AVERIADO);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        if (prestado != null) {
            equipo.setEquipoPrestamo(prestado);
            item.setEquipoPrestamo(prestado);
            this.asociarEquipoPrestamo(prestado, usuario, equipo);
        }
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        return retorno;
    }

    protected void asociarEquipoPrestamo(Equipo equipo, Usuario usuario, Equipo otro) throws ExcepcionConexion, Exception {
        if (equipo.getEstado().equals(EstadosEquipos.PRESTAMO_DISPONIBLE)) {
            equipo.setEstado(EstadosEquipos.PRESTADO);
        }
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(equipo.getLugarActual());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(equipo.getEstado());
        equipo.getHistoriaLugares().add(item);
        equipo.setEquipoPrestamo(otro);
        item.setEquipoPrestamo(otro);
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
    }

    protected Equipo ingresarEquipoAReparar(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(this.getDepartamentoDefecto());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.EN_REPARACION);
        item.setEquipoPrestamo(equipo.getEquipoPrestamo());
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        return retorno;
    }

    protected Equipo devolverEquipoReparado(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(equipo.getLugar());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(EstadosEquipos.ACTIVO);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        Equipo prestamo = equipo.getEquipoPrestamo();
        equipo.setEquipoPrestamo(null);
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        if (prestamo != null) {
            prestamo = this.cargarEquipoConItemsHistoricos(prestamo);
            this.recuperarEquipoPrestado(prestamo, usuario);
        }
        return retorno;
    }

    protected Equipo despacharEquipo(Equipo equipo, Usuario usuario) throws ExcepcionConexion, Exception {
        Equipo retorno = null;
        ItemHistoricoLugar item = new ItemHistoricoLugar();
        item.setEquipo(equipo);
        item.setLugar(equipo.getEsPrestamo() ? this.getDepartamentoDefecto() : equipo.getLugar());
        item.setFecha(new Date());
        item.setUsuario(usuario);
        item.setEstado(equipo.getEsPrestamo() ? EstadosEquipos.PRESTAMO_DISPONIBLE : EstadosEquipos.ACTIVO);
        equipo.getHistoriaLugares().add(item);
        equipo.setEstado(item.getEstado());
        this.modificarEquipo(equipo, usuario);
        MapeadorItemHistoricoLugar mapI = MapeadorItemHistoricoLugar.nuevoMapeador(item);
        Persistencia.getInstancia().insertarSP(mapI);
        retorno = equipo;
        return retorno;
    }

    private void cargarPlantillasBD() {
        ArrayList<PlantillaEquipo> plantillasEquipo = new ArrayList<>();
        try {
            MapeadorPlantillaEquipo mapeador = MapeadorPlantillaEquipo.nuevoMapeadorPlantillaEquipo(new PlantillaPc());
            plantillasEquipo.addAll(Persistencia.getInstancia().buscar(mapeador));
            mapeador = MapeadorPlantillaEquipo.nuevoMapeadorPlantillaEquipo(new PlantillaImpresora());
            plantillasEquipo.addAll(Persistencia.getInstancia().buscar(mapeador));
            mapeador = MapeadorPlantillaEquipo.nuevoMapeadorPlantillaEquipo(new PlantillaValidadora());
            plantillasEquipo.addAll(Persistencia.getInstancia().buscar(mapeador));
            mapeador = MapeadorPlantillaEquipo.nuevoMapeadorPlantillaEquipo(new PlantillaOtroEquipo());
            plantillasEquipo.addAll(Persistencia.getInstancia().buscar(mapeador));
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(SubSistemaCMDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.plantillas = plantillasEquipo;
    }

    protected ArrayList<PlantillaEquipo> getPlantillasPc(String marca) throws ExcepcionConexion, Exception {
        ArrayList<PlantillaEquipo> plantillasEquipo = new ArrayList<>();
        if (marca == null || marca.isEmpty()) {
            this.plantillas.forEach(p -> {
                if (p instanceof PlantillaPc) {
                    plantillasEquipo.add(p);
                }
            });
        } else {
            this.plantillas.forEach(p -> {
                if (p.getMarca().equalsIgnoreCase(marca) && p instanceof PlantillaPc) {
                    plantillasEquipo.add(p);
                }
            });
        }
        return plantillasEquipo;
    }

    protected PlantillaPc buscarPlantillaPC(Integer id) throws ExcepcionConexion, Exception {
        PlantillaPc plantillaPc = null;
        int i = 0;
        while (i < this.plantillas.size() && plantillaPc == null) {
            if (Objects.equals(this.plantillas.get(i).getId(), id)) {
                plantillaPc = (PlantillaPc) this.plantillas.get(i);
            }
            i++;
        }
        return plantillaPc;
    }

    protected ArrayList<PlantillaEquipo> getPlantillasImpresoras(String marca) throws ExcepcionConexion, Exception {
        ArrayList<PlantillaEquipo> plantillasEquipo = new ArrayList<>();
        if (marca == null || marca.isEmpty()) {
            this.plantillas.forEach(p -> {
                if (p instanceof PlantillaImpresora) {
                    plantillasEquipo.add(p);
                }
            });
        } else {
            this.plantillas.forEach(p -> {
                if (p.getMarca().equalsIgnoreCase(marca) && p instanceof PlantillaImpresora) {
                    plantillasEquipo.add(p);
                }
            });
        }
        return plantillasEquipo;
    }

    protected PlantillaImpresora buscarPlantillaImpresora(Integer id) throws ExcepcionConexion, Exception {
        PlantillaImpresora plantillaImpresora = null;
        int i = 0;
        while (i < this.plantillas.size() && plantillaImpresora == null) {
            if (Objects.equals(this.plantillas.get(i).getId(), id)) {
                plantillaImpresora = (PlantillaImpresora) this.plantillas.get(i);
            }
            i++;
        }
        return plantillaImpresora;
    }

    protected ArrayList<PlantillaEquipo> getPlantillasValidadoras(String marca) throws ExcepcionConexion, Exception {
        ArrayList<PlantillaEquipo> plantillasEquipo = new ArrayList<>();
        if (marca == null || marca.isEmpty()) {
            this.plantillas.forEach(p -> {
                if (p instanceof PlantillaValidadora) {
                    plantillasEquipo.add(p);
                }
            });
        } else {
            this.plantillas.forEach(p -> {
                if (p.getMarca().equalsIgnoreCase(marca) && p instanceof PlantillaValidadora) {
                    plantillasEquipo.add(p);
                }
            });
        }
        return plantillasEquipo;
    }

    protected ArrayList<PlantillaEquipo> getPlantillasOtrosEquipos(String tipo, String marca) throws ExcepcionConexion, Exception {
        ArrayList<PlantillaEquipo> plantillasEquipo = new ArrayList<>();
        if ((tipo == null || tipo.isEmpty()) && (marca == null || marca.isEmpty())) {
            this.plantillas.forEach(p -> {
                if (p instanceof PlantillaOtroEquipo) {
                    plantillasEquipo.add(p);
                }
            });
        } else {
            this.plantillas.forEach(p -> {
                if ((marca != null && !marca.isEmpty() && p.getMarca().equalsIgnoreCase(marca))
                        || (marca == null || marca.isEmpty()) && p instanceof PlantillaOtroEquipo
                        && (tipo != null && !tipo.isEmpty() && ((PlantillaOtroEquipo) p).getTipo().toString().equalsIgnoreCase(tipo))
                        || (tipo == null || tipo.isEmpty())) {
                    plantillasEquipo.add(p);
                }
            });
        }
        return plantillasEquipo;
    }

    protected PlantillaValidadora buscarPlantillaValidadora(Integer id) throws ExcepcionConexion, Exception {
        PlantillaValidadora plantillaValidadora = null;
        int i = 0;
        while (i < this.plantillas.size() && plantillaValidadora == null) {
            if (Objects.equals(this.plantillas.get(i).getId(), id)) {
                plantillaValidadora = (PlantillaValidadora) this.plantillas.get(i);
            }
            i++;
        }
        return plantillaValidadora;
    }

    protected PlantillaOtroEquipo buscarPlantillaOtroEquipo(Integer id) throws ExcepcionConexion, Exception {
        PlantillaOtroEquipo plantillaOtroEquipo = null;
        int i = 0;
        while (i < this.plantillas.size() && plantillaOtroEquipo == null) {
            if (Objects.equals(this.plantillas.get(i).getId(), id)) {
                plantillaOtroEquipo = (PlantillaOtroEquipo) this.plantillas.get(i);
            }
            i++;
        }
        return plantillaOtroEquipo;
    }

    //calcula el costo de venta de un equipo usado de la siguiente manera
    //costoVenta = precioEquipo - (depreciacionMensual * mesesDeActividad)
    protected double costoDeVentaDeEquipo(Equipo e) {
        double costo = e.getPrecioDeCompra() - (this.depreciacionMensualDeEquipo(e) * this.cantidadDeMesesEntreFechas(e.getFechaAlta(), new Date()));
        if (costo < 0) {
            return 0;
        }
        return formatearDecimales(costo, 2);
    }

    //retorna todos los equipos de prestamos registrados
    protected ArrayList<Equipo> listadoMantenimientoPrestamos() throws ExcepcionConexion, Exception {
        DTOFiltroEquipo filtro = new DTOFiltroEquipo();
        filtro.setEsPrestamo(true);
        return this.buscarEquipos(filtro);
    }

    //Devuelve la cantidad de meses entre las fechas indicadas
    private Integer cantidadDeMesesEntreFechas(Date DateInicio, Date DateFin) {
        Calendar CalendarInicio = new GregorianCalendar();
        Calendar CalendarFin = new GregorianCalendar();
        CalendarInicio.setTime(DateInicio);
        CalendarFin.setTime(DateFin);
        int difAños = CalendarFin.get(Calendar.YEAR) - CalendarInicio.get(Calendar.YEAR);
        int meses = difAños * 12 + CalendarFin.get(Calendar.MONTH) - CalendarInicio.get(Calendar.MONTH);
        return meses;
    }

    protected int cantidadMesesEnPrestamo(Equipo e) {
        int mesesPrestados = 0;
        int idPrestado = -1;
        int i = 0;
        while (i <= e.getHistoriaLugares().size() - 1 && idPrestado == -1) {
            if (e.getHistoriaLugares().get(i).getEstado() == EstadosEquipos.PRESTAMO_DISPONIBLE) {
                idPrestado = i;
            }
            i++;
        }
        if (idPrestado != -1) {
            mesesPrestados = this.cantidadDeMesesEntreFechas(e.getHistoriaLugares().get(idPrestado).getFecha(), new Date());
        }
        return mesesPrestados;
    }

    protected int cantidadVecesPrestado(Equipo e) {
        int cant = 0;
        for (ItemHistoricoLugar h : e.getHistoriaLugares()) {
            if (h.getEstado() == EstadosEquipos.PRESTADO) {
                cant++;
            }
        }
        return cant;
    }

    protected String obtenerCategoriaEquipoPrestamo(Equipo e) {
        String categoria = "";
        int cantMesesEnPrestamo = this.cantidadMesesEnPrestamo(e);
        int cantVecesPrestado = this.cantidadVecesPrestado(e);
        if (cantMesesEnPrestamo > ROJO_CANT_MIN_MESES_EN_PRESTAMO || cantVecesPrestado > ROJO_CANT_MIN_VECES_PRESTADO) {
            categoria = Categorias_Mantenimiento_Prestamos.ROJO.toString();
        } else if (cantMesesEnPrestamo >= AMARILLO_CANT_MIN_MESES_EN_PRESTAMO || cantVecesPrestado >= AMARILLO_CANT_MIN_VECES_PRESTADO) {
            categoria = Categorias_Mantenimiento_Prestamos.AMARILLO.toString();
        } else if (cantMesesEnPrestamo < VERDE_CANT_MAX_MESES_EN_PRESTAMO || cantVecesPrestado < VERDE_CANT_MAX_VECES_PRESTADO) {
            categoria = Categorias_Mantenimiento_Prestamos.VERDE.toString();
        }
        return categoria;
    }

    //la depreciacion se calcula dep = (precioEquipo - valorDescuentoMensual)/mesesVidaUtil
    protected double depreciacionMensualDeEquipo(Equipo e) {
        return formatearDecimales((e.getPrecioDeCompra() - this.valorDescuentoMensual(e)) / Equipo.mesesDevidaUtil, 2);
    }

    //El valor mensual segun lo indagado será el 10% del costo inicial del equipo
    private double valorDescuentoMensual(Equipo e) {
        return formatearDecimales(e.getPrecioDeCompra() * 0.10, 2);
    }

    public static double formatearDecimales(double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
    }

    //metodo que setea los parametros de mantenimiento de prestamos
    public void modicarParametros(Integer RCantMesesEnPrestamo, Integer RCantVecesPrestado,
            Integer ACantMesesEnPrestamo, Integer ACantVecesPrestado,
            Integer VCantMesesEnPrestamo, Integer VCantVecesPrestado) {
        ROJO_CANT_MIN_MESES_EN_PRESTAMO = RCantMesesEnPrestamo;
        ROJO_CANT_MIN_VECES_PRESTADO = RCantVecesPrestado;
        AMARILLO_CANT_MIN_MESES_EN_PRESTAMO = ACantMesesEnPrestamo;
        AMARILLO_CANT_MIN_VECES_PRESTADO = ACantVecesPrestado;
        VERDE_CANT_MAX_MESES_EN_PRESTAMO = VCantMesesEnPrestamo;
        VERDE_CANT_MAX_VECES_PRESTADO = VCantVecesPrestado;
    }
    
    public Integer[] getParametrosMantenimiento(){
        Integer[] parametros = new Integer[6];
        parametros[0] = ROJO_CANT_MIN_MESES_EN_PRESTAMO;
        parametros[1] = ROJO_CANT_MIN_VECES_PRESTADO;
        parametros[2] =AMARILLO_CANT_MIN_MESES_EN_PRESTAMO;
        parametros[3] =AMARILLO_CANT_MIN_VECES_PRESTADO;
        parametros[4] =VERDE_CANT_MAX_MESES_EN_PRESTAMO;
        parametros[5] =VERDE_CANT_MAX_VECES_PRESTADO;
        return parametros;
    }
}

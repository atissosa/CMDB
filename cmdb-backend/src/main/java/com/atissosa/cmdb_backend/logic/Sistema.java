package com.atissosa.cmdb_backend.logic;

import com.atissosa.cmdb_backend.domain.DepartamentoCentral;
import com.atissosa.cmdb_backend.domain.Equipo;
import com.atissosa.cmdb_backend.domain.Equipo.EstadosEquipos;
import com.atissosa.cmdb_backend.domain.Impresora;
import com.atissosa.cmdb_backend.domain.Impresora.ConexionesImpresora;
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
import com.atissosa.cmdb_backend.domain.Rol;
import com.atissosa.cmdb_backend.domain.Service;
import com.atissosa.cmdb_backend.domain.SubAgencia;
import com.atissosa.cmdb_backend.domain.Usuario;
import com.atissosa.cmdb_backend.domain.Validadora;
import com.atissosa.cmdb_backend.dto.DTODepartamentoCentral;
import com.atissosa.cmdb_backend.dto.DTOEquipo;
import com.atissosa.cmdb_backend.dto.DTOImpresora;
import com.atissosa.cmdb_backend.dto.DTOItemHistoricoLugar;
import com.atissosa.cmdb_backend.dto.DTOLugar;
import com.atissosa.cmdb_backend.dto.DTOOtroEquipo;
import com.atissosa.cmdb_backend.dto.DTOPC;
import com.atissosa.cmdb_backend.dto.DTOPlantillaImpresora;
import com.atissosa.cmdb_backend.dto.DTOPlantillaPc;
import com.atissosa.cmdb_backend.dto.DTOPlantillaOtroEquipo;
import com.atissosa.cmdb_backend.dto.DTOPlantillaValidadora;
import com.atissosa.cmdb_backend.dto.DTOService;
import com.atissosa.cmdb_backend.dto.DTOSubAgencia;
import com.atissosa.cmdb_backend.dto.DTOUsuario;
import com.atissosa.cmdb_backend.dto.DTOValidadora;
import com.atissosa.cmdb_backend.dto.DTOFiltroEquipo;
import com.atissosa.cmdb_backend.dto.DTOParametroMantenimientoPrestamo;
import com.atissosa.cmdb_backend.dto.Respuesta;
import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.atissosa.cmdb_backend.controllers.RecursoDepartamentoCentral;
import com.atissosa.cmdb_backend.controllers.RecursoEquipo;
import com.atissosa.cmdb_backend.controllers.RecursoImpresora;
import com.atissosa.cmdb_backend.controllers.RecursoLogin;
import com.atissosa.cmdb_backend.controllers.RecursoOtroEquipo;
import com.atissosa.cmdb_backend.controllers.RecursoPC;
import com.atissosa.cmdb_backend.controllers.RecursoService;
import com.atissosa.cmdb_backend.controllers.RecursoSubAgencia;
import com.atissosa.cmdb_backend.controllers.RecursoValidadora;
import com.atissosa.cmdb_backend.utilities.Validador;

public class Sistema {

    private static Sistema instancia;
    private SubSistemaUsuarios sistemaUsuarios;
    private SubSistemaCMDB sistemaCMDB;

    private Sistema() {
        this.sistemaUsuarios = new SubSistemaUsuarios();
        this.sistemaCMDB = new SubSistemaCMDB();
    }

    public static Sistema getInstancia() {
        if (Sistema.instancia == null) {
            Sistema.instancia = new Sistema();
        }
        return Sistema.instancia;
    }

    public Respuesta login(String usr, String pass) {
        DTOUsuario retorno = null;
        Usuario usuario;
        int codigo = 2;
        if (usr != "" && pass != "") {
            try {
                if (!this.sistemaUsuarios.usuarioLogueado(usr)) {
                    usuario = this.sistemaUsuarios.login(usr, pass);
                    if (usuario != null) {
                        retorno = new DTOUsuario(usuario);
                        codigo = 1;
                    }
                } else {
                    codigo = 26;
                }
            } catch (ExcepcionConexion e) {
                e.printStackTrace(System.out);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoLogin.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public void logout(String user) {
        if (user != null && !user.isEmpty()) {
            try {
                this.sistemaUsuarios.logout(user);
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(Sistema.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception e) {
                Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public Respuesta getDepartamentos() {
        ArrayList<DTODepartamentoCentral> departamentos = new ArrayList();
        int codigo;
        try {
            for (DepartamentoCentral departamento : this.sistemaCMDB.getDepartamentos()) {
                departamentos.add(new DTODepartamentoCentral(departamento));
            }
            codigo = 4;
        } catch (ExcepcionConexion e) {
            e.printStackTrace(System.out);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, departamentos);
    }

    public Respuesta altaService(DTOService dtoService, String usuario) {
        DTOService retorno = null;
        int codigo = 11;
        if (dtoService != null && !usuario.isEmpty()) {
            try {
                if (dtoService.getNombre() != null
                        && dtoService.getDireccion() != null
                        && Validador.NombreValido(dtoService.getNombre())
                        && !dtoService.getDireccion().isEmpty()) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            Service s = this.sistemaCMDB.altaService(dtoService.getNombre(), dtoService.getDescripcion(), dtoService.getDireccion());
                            if (s != null) {
                                retorno = new DTOService(s);
                                codigo = 5;
                            } else {
                                codigo = 7;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta altaDepartamentoCentral(DTODepartamentoCentral dtoDepartamento, String usuario) {
        DTODepartamentoCentral retorno = null;
        int codigo = 11;
        if (dtoDepartamento != null && !usuario.isEmpty()) {
            try {
                if (dtoDepartamento.getNombre() != null
                        && (Validador.NombreValido(dtoDepartamento.getNombre()))) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            DepartamentoCentral d = this.sistemaCMDB.altaDepartamentoCentral(dtoDepartamento.getNombre(), dtoDepartamento.getDescripcion());
                            if (d != null) {
                                retorno = new DTODepartamentoCentral(d);
                                codigo = 5;
                            } else {
                                codigo = 7;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta modificarDepartamentoCentral(DTODepartamentoCentral dtoDepartamento, String usuario) {
        DTODepartamentoCentral retorno = null;
        int codigo = 11;
        if (dtoDepartamento != null && !usuario.isEmpty()) {
            try {
                if (dtoDepartamento.getId() != null && Validador.NombreValido(dtoDepartamento.getNombre())) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            DepartamentoCentral d = this.sistemaCMDB.modificarDepartamentoCentral(dtoDepartamento.getId(), dtoDepartamento.getNombre(), dtoDepartamento.getDescripcion());
                            if (d != null) {
                                retorno = new DTODepartamentoCentral(d);
                                codigo = 6;
                            } else {
                                codigo = 8;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta modificarService(DTOService dtoService, String usuario) {
        DTOService retorno = null;
        int codigo = 11;
        if (dtoService != null && !usuario.isEmpty()) {
            try {
                if (dtoService.getId() != null && Validador.NombreValido(dtoService.getNombre()) && !dtoService.getDireccion().isEmpty()) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            Service s = this.sistemaCMDB.modificarService(dtoService.getId(), dtoService.getNombre(), dtoService.getDescripcion(), dtoService.getDireccion());
                            if (s != null) {
                                retorno = new DTOService(s);
                                codigo = 6;
                            } else {
                                codigo = 8;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta bajaDepartamentoCentral(DTODepartamentoCentral dtoDepartamento, String usuario) {
        int codigo = 11;
        if (dtoDepartamento != null && !usuario.isEmpty()) {
            try {
                if (dtoDepartamento.getId() != null) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            if (this.sistemaCMDB.bajaDepartamentoCentral(dtoDepartamento.getId())) {
                                codigo = 9;
                            } else {
                                codigo = 10;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, null);
    }

    public Respuesta bajaService(DTOService service, String usuario) {
        int codigo = 11;
        if (service != null && !usuario.isEmpty()) {
            try {
                if (service.getId() != null) {
                    Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                            if (this.sistemaCMDB.bajaService(service.getId())) {
                                codigo = 9;
                            } else {
                                codigo = 10;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, null);
    }

    public Respuesta buscarServices(int id) {
        ArrayList<DTOService> retorno = new ArrayList<>();
        int codigo;

        try {
            ArrayList<Service> services = this.sistemaCMDB.buscarServices(id);
            if (services != null && services.size() > 0) {
                for (Service s : services) {
                    retorno.add(new DTOService(s));
                }
                codigo = 4;
            } else {
                codigo = 12;
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }

        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta buscarSubagencias(int numero) {
        ArrayList<DTOSubAgencia> retorno = new ArrayList<>();
        int codigo;
        try {
            ArrayList<SubAgencia> subagencias = this.sistemaCMDB.buscarSubagencias(numero, false);
            if (subagencias != null && subagencias.size() > 0) {
                for (SubAgencia s : subagencias) {
                    retorno.add(new DTOSubAgencia(s));
                }
                codigo = 4;
            } else {
                codigo = 12;
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoSubAgencia.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }

        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta buscarDepartamentosCentral(int id) {
        ArrayList<DTODepartamentoCentral> retorno = new ArrayList<>();
        int codigo;
        try {
            ArrayList<DepartamentoCentral> departamentos = this.sistemaCMDB.buscarDepartamentosCentral(id);
            if (departamentos != null && departamentos.size() > 0) {
                for (DepartamentoCentral d : departamentos) {
                    retorno.add(new DTODepartamentoCentral(d));
                }
                codigo = 4;
            } else {
                codigo = 12;
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoDepartamentoCentral.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta altaPc(DTOPC dtoPc) {
        DTOPC retorno = null;
        int codigo = 11;
        if (dtoPc != null) {
            try {
                if (/*      DATOS NO NULLOS         */dtoPc.getNumeroSerie() != null
                        && dtoPc.getMarca() != null
                        && dtoPc.getModelo() != null
                        && dtoPc.getPrecioDeCompra() != null
                        && dtoPc.getFechaAlta() != null
                        && dtoPc.getMesesGarantia() != null
                        && dtoPc.getEsPrestamo() != null
                        && dtoPc.getNumeroPrecinto() != null
                        && dtoPc.getUsuario() != null
                        && dtoPc.getIp() != null
                        && dtoPc.getMemoriaRam() != null
                        && dtoPc.getProcesador() != null
                        && dtoPc.getTamanoDisco() != null
                        && dtoPc.getNombreHost() != null
                        && dtoPc.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtoPc.getNumeroSerie().isEmpty()
                        && !dtoPc.getMarca().isEmpty()
                        && !dtoPc.getModelo().isEmpty()
                        && dtoPc.getPrecioDeCompra() >= 0
                        && dtoPc.getMesesGarantia() >= 0
                        && dtoPc.getNumeroPrecinto() > 0
                        && dtoPc.getidLugar() > 0
                        && !dtoPc.getUsuario().isEmpty()
                        && Validador.IpValida(dtoPc.getIp())
                        && dtoPc.getMemoriaRam() > 0
                        && !dtoPc.getProcesador().isEmpty()
                        && dtoPc.getTamanoDisco() > 0
                        && Validador.NombreValido(dtoPc.getNombreHost())
                        && !dtoPc.getTipo().isEmpty()
                        && !dtoPc.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(dtoPc.getUsuario());
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = null;
                            if (dtoPc.getidLugar() != null) {
                                l = this.sistemaCMDB.buscarLugar(dtoPc.getidLugar());
                            }
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtoPc.getUsuario());
                            if (u != null) {
                                PC pc = this.sistemaCMDB.altaPc(dtoPc.getNumeroSerie(),
                                        dtoPc.getMarca(), dtoPc.getModelo(),
                                        dtoPc.getPrecioDeCompra(), dtoPc.getFechaAlta(),
                                        dtoPc.getDescripcion(), dtoPc.getMesesGarantia(), dtoPc.getEsPrestamo(),
                                        dtoPc.getNumeroPrecinto(), l, u, dtoPc.getIp(),
                                        dtoPc.getNombreHost(), dtoPc.getProcesador(),
                                        dtoPc.getMemoriaRam(), dtoPc.getTamanoDisco(), dtoPc.getTipo());
                                if (pc != null) {
                                    retorno = new DTOPC(pc);
                                    codigo = 5;
                                } else {
                                    codigo = 7;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoPC.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta altaImpresora(DTOImpresora dtoImpresora) {
        DTOImpresora retorno = null;
        int codigo = 11;
        if (dtoImpresora != null) {
            try {
                if (/*      DATOS NO NULLOS         */dtoImpresora.getNumeroSerie() != null
                        && dtoImpresora.getMarca() != null
                        && dtoImpresora.getModelo() != null
                        && dtoImpresora.getPrecioDeCompra() != null
                        && dtoImpresora.getFechaAlta() != null
                        && dtoImpresora.getMesesGarantia() != null
                        && dtoImpresora.getEsPrestamo() != null
                        && dtoImpresora.getNumeroPrecinto() != null
                        && dtoImpresora.getUsuario() != null
                        && dtoImpresora.getIp() != null
                        && dtoImpresora.getNombreHost() != null
                        && dtoImpresora.getTipo() != null
                        && dtoImpresora.getMultifuncion() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtoImpresora.getNumeroSerie().isEmpty()
                        && !dtoImpresora.getMarca().isEmpty()
                        && !dtoImpresora.getModelo().isEmpty()
                        && dtoImpresora.getPrecioDeCompra() >= 0
                        && dtoImpresora.getMesesGarantia() >= 0
                        && dtoImpresora.getNumeroPrecinto() > 0
                        && dtoImpresora.getidLugar() > 0
                        && !dtoImpresora.getUsuario().isEmpty()
                        && Validador.IpValida(dtoImpresora.getIp())
                        && Validador.NombreValido(dtoImpresora.getNombreHost())
                        && !dtoImpresora.getTipo().isEmpty()
                        && !dtoImpresora.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(dtoImpresora.getUsuario());
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = null;
                            if (dtoImpresora.getidLugar() != null) {
                                l = this.sistemaCMDB.buscarLugar(dtoImpresora.getidLugar());
                            }
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtoImpresora.getUsuario());
                            if (u != null) {
                                Impresora imp = this.sistemaCMDB.altaImpresora(dtoImpresora.getNumeroSerie(),
                                        dtoImpresora.getMarca(), dtoImpresora.getModelo(),
                                        dtoImpresora.getPrecioDeCompra(), dtoImpresora.getFechaAlta(),
                                        dtoImpresora.getDescripcion(), dtoImpresora.getMesesGarantia(), dtoImpresora.getEsPrestamo(),
                                        dtoImpresora.getNumeroPrecinto(), l, u, dtoImpresora.getIp(), dtoImpresora.getNombreHost(),
                                        dtoImpresora.getMultifuncion(), dtoImpresora.getConexion(), dtoImpresora.getOpcion(), dtoImpresora.getTipo());
                                if (imp != null) {
                                    retorno = new DTOImpresora(imp);
                                    codigo = 5;
                                } else {
                                    codigo = 7;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoImpresora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta buscarEquipos(String numeroSerie) {
        ArrayList<DTOEquipo> retorno = new ArrayList<>();
        int codigo;
        try {
            String serie = numeroSerie;
            if (numeroSerie == null) {
                serie = "";
            }
            DTOFiltroEquipo filtro = new DTOFiltroEquipo();
            filtro.agregarNumeroSerie(numeroSerie);
            ArrayList<Equipo> equipos = this.sistemaCMDB.buscarEquipos(filtro);
            if (equipos.size() > 0) {
                for (Equipo e : equipos) {
                    retorno.add(DTOEquipo.nuevoDTO(e));
                }
                codigo = 4;
            } else {
                codigo = 12;
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoEquipo.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta buscarEquipos(DTOFiltroEquipo dtoFiltroEquipo) {
        ArrayList<DTOEquipo> retorno = new ArrayList<>();
        int codigo;
        try {
            for (Equipo e : this.sistemaCMDB.buscarEquipos(dtoFiltroEquipo)) {
                retorno.add(DTOEquipo.nuevoDTO(e));
            }
            codigo = (retorno.size() > 0) ? 4 : 12;
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
            codigo = 3;
        } catch (Exception e) {
            Logger.getLogger(RecursoEquipo.class.getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta buscarEquipo(String numeroSerie) {
        DTOEquipo dto = null;
        Equipo equipo;
        int codigo = 11;
        if (numeroSerie != null && numeroSerie != "") {
            try {
                equipo = this.sistemaCMDB.buscarEquipo(numeroSerie);
                if (equipo != null) {
                    dto = DTOEquipo.nuevoDTO(equipo);
                    codigo = 4;
                } else {
                    codigo = 12;
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoEquipo.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dto);
    }

    public Respuesta altaValidadora(DTOValidadora dtoValidadora) {
        DTOValidadora retorno = null;
        int codigo = 11;
        if (dtoValidadora != null) {
            try {
                if (/*      DATOS NO NULLOS         */dtoValidadora.getNumeroSerie() != null
                        && dtoValidadora.getMarca() != null
                        && dtoValidadora.getModelo() != null
                        && dtoValidadora.getPrecioDeCompra() != null
                        && dtoValidadora.getFechaAlta() != null
                        && dtoValidadora.getMesesGarantia() != null
                        && dtoValidadora.getEsPrestamo() != null
                        && dtoValidadora.getNumeroPrecinto() != null
                        && dtoValidadora.getUsuario() != null
                        && dtoValidadora.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtoValidadora.getNumeroSerie().isEmpty()
                        && !dtoValidadora.getMarca().isEmpty()
                        && !dtoValidadora.getModelo().isEmpty()
                        && dtoValidadora.getPrecioDeCompra() >= 0
                        && dtoValidadora.getFechaAlta() != null
                        && dtoValidadora.getMesesGarantia() >= 0
                        && dtoValidadora.getEsPrestamo() != null
                        && dtoValidadora.getNumeroPrecinto() > 0
                        && dtoValidadora.getidLugar() > 0
                        && !dtoValidadora.getUsuario().isEmpty()
                        && !dtoValidadora.getTipo().isEmpty()
                        && !dtoValidadora.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(dtoValidadora.getUsuario());
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = null;
                            if (dtoValidadora.getidLugar() != null) {
                                l = this.sistemaCMDB.buscarLugar(dtoValidadora.getidLugar());
                            }
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtoValidadora.getUsuario());
                            if (u != null) {
                                Validadora val = this.sistemaCMDB.altaValidadora(dtoValidadora.getNumeroSerie(), dtoValidadora.getMarca(), dtoValidadora.getModelo(),
                                        dtoValidadora.getPrecioDeCompra(), dtoValidadora.getFechaAlta(), dtoValidadora.getDescripcion(), dtoValidadora.getMesesGarantia(), dtoValidadora.getEsPrestamo(),
                                        dtoValidadora.getNumeroPrecinto(), l, u, dtoValidadora.getTipo());
                                if (val != null) {
                                    retorno = new DTOValidadora(val);
                                    codigo = 5;
                                } else {
                                    codigo = 7;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoValidadora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta altaOtroEquipo(DTOOtroEquipo dtoOtroEquipo) {
        DTOOtroEquipo retorno = null;
        int codigo = 11;
        if (dtoOtroEquipo != null) {
            try {
                if (/*      DATOS NO NULLOS         */dtoOtroEquipo.getNumeroSerie() != null
                        && dtoOtroEquipo.getMarca() != null
                        && dtoOtroEquipo.getModelo() != null
                        && dtoOtroEquipo.getPrecioDeCompra() != null
                        && dtoOtroEquipo.getFechaAlta() != null
                        && dtoOtroEquipo.getMesesGarantia() != null
                        && dtoOtroEquipo.getEsPrestamo() != null
                        && dtoOtroEquipo.getNumeroPrecinto() != null
                        && dtoOtroEquipo.getUsuario() != null
                        && dtoOtroEquipo.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtoOtroEquipo.getNumeroSerie().isEmpty()
                        && !dtoOtroEquipo.getMarca().isEmpty()
                        && !dtoOtroEquipo.getModelo().isEmpty()
                        && dtoOtroEquipo.getPrecioDeCompra() >= 0
                        && dtoOtroEquipo.getFechaAlta() != null
                        && dtoOtroEquipo.getMesesGarantia() >= 0
                        && dtoOtroEquipo.getEsPrestamo() != null
                        && dtoOtroEquipo.getNumeroPrecinto() > 0
                        && dtoOtroEquipo.getidLugar() > 0
                        && !dtoOtroEquipo.getUsuario().isEmpty()
                        && !dtoOtroEquipo.getTipo().isEmpty()
                        && !dtoOtroEquipo.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(dtoOtroEquipo.getUsuario());
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = null;
                            if (dtoOtroEquipo.getidLugar() != null) {
                                l = this.sistemaCMDB.buscarLugar(dtoOtroEquipo.getidLugar());
                            }
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtoOtroEquipo.getUsuario());
                            if (u != null) {
                                OtroEquipo oEquipo = this.sistemaCMDB.altaOtroEquipo(dtoOtroEquipo.getNumeroSerie(), dtoOtroEquipo.getMarca(), dtoOtroEquipo.getModelo(),
                                        dtoOtroEquipo.getPrecioDeCompra(), dtoOtroEquipo.getFechaAlta(), dtoOtroEquipo.getDescripcion(), dtoOtroEquipo.getMesesGarantia(), dtoOtroEquipo.getEsPrestamo(),
                                        dtoOtroEquipo.getNumeroPrecinto(), l, u, dtoOtroEquipo.getTipo());
                                if (oEquipo != null) {
                                    retorno = new DTOOtroEquipo(oEquipo);
                                    codigo = 5;
                                } else {
                                    codigo = 7;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoOtroEquipo.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta retornaTiposEquipos() {
        return Respuesta.nuevaRespuesta(4, this.sistemaCMDB.retornarTiposEquipos());
    }

    public Respuesta retornarEstadosEquipos() {
        return Respuesta.nuevaRespuesta(4, this.sistemaCMDB.retornarEstadosEquipos());
    }

    public Respuesta retornarOpcionesImpresoras() {
        return Respuesta.nuevaRespuesta(4, this.sistemaCMDB.retornarOpcionesImpresoras());
    }

    public Respuesta retornarConexionesImpresoras() {
        return Respuesta.nuevaRespuesta(4, this.sistemaCMDB.retornarConexionesImpresoras());
    }

    public Respuesta darEquipoDeBaja(DTOFiltroEquipo dtoFiltroEquipo) {
        int codigo = 11;
        if (dtoFiltroEquipo != null && dtoFiltroEquipo.getNumeroSerie() != null && dtoFiltroEquipo.getUsuario() != null
                && !dtoFiltroEquipo.getNumeroSerie().isEmpty() && !dtoFiltroEquipo.getNumeroSerie().get(0).isEmpty() && !dtoFiltroEquipo.getUsuario().isEmpty()) {
            try {
                Usuario user = this.sistemaUsuarios.buscarUsuario(dtoFiltroEquipo.getUsuario().get(0));
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                        if (this.sistemaCMDB.bajaEquipo(dtoFiltroEquipo.getNumeroSerie().get(0))) {
                            codigo = 9;
                        } else {
                            codigo = 10;
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoEquipo.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, null);
    }

    public Respuesta modificarPC(DTOPC dtopc, String usuario) {
        DTOPC retorno = null;
        int codigo = 11;
        if (dtopc != null && !usuario.isEmpty()) {
            try {
                if (/*      DATOS NO NULLOS         */dtopc.getNumeroSerie() != null
                        && dtopc.getMarca() != null
                        && dtopc.getModelo() != null
                        && dtopc.getPrecioDeCompra() != null
                        && dtopc.getFechaAlta() != null
                        && dtopc.getMesesGarantia() != null
                        && dtopc.getEsPrestamo() != null
                        && dtopc.getidLugar() != null
                        && dtopc.getUsuario() != null
                        && dtopc.getIp() != null
                        && dtopc.getMemoriaRam() != null
                        && dtopc.getProcesador() != null
                        && dtopc.getTamanoDisco() != null
                        && dtopc.getNombreHost() != null
                        && dtopc.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtopc.getNumeroSerie().isEmpty()
                        && !dtopc.getMarca().isEmpty()
                        && !dtopc.getModelo().isEmpty()
                        && dtopc.getPrecioDeCompra() >= 0
                        && dtopc.getMesesGarantia() >= 0
                        && dtopc.getidLugar() > 0
                        && !dtopc.getUsuario().isEmpty()
                        && Validador.IpValida(dtopc.getIp())
                        && dtopc.getMemoriaRam() > 0
                        && !dtopc.getProcesador().isEmpty()
                        && dtopc.getTamanoDisco() > 0
                        && Validador.NombreValido(dtopc.getNombreHost())
                        && !dtopc.getTipo().isEmpty()
                        && !dtopc.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = this.sistemaCMDB.buscarLugar(dtopc.getidLugar());
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtopc.getUsuario());
                            if (l != null && u != null) {
                                PC pc = new PC();
                                pc.setId(dtopc.getId());
                                pc.setCodigo(dtopc.getCodigo());
                                pc.setNumeroDeSerie(dtopc.getNumeroSerie());
                                pc.setMarca(dtopc.getMarca());
                                pc.setModelo(dtopc.getModelo());
                                pc.setPrecioDeCompra(dtopc.getPrecioDeCompra());
                                pc.setFechaAlta((new SimpleDateFormat("yyyy-MM-dd")).parse(dtopc.getFechaAlta()));
                                pc.setDescripcion(dtopc.getDescripcion());
                                pc.setMesesGarantia(dtopc.getMesesGarantia());
                                pc.setEsPrestamo(dtopc.getEsPrestamo());
                                pc.setNumeroPrecinto(dtopc.getNumeroPrecinto());
                                pc.setEstado(Equipo.EstadosEquipos.valueOf(dtopc.getEstado()));
                                pc.setLugar(l);
                                pc.setUsuario(u);
                                pc.setIp(dtopc.getIp());
                                pc.setNombreHost(dtopc.getNombreHost());
                                pc.setProcesador(dtopc.getProcesador());
                                pc.setMemoriaRam(dtopc.getMemoriaRam());
                                pc.setTamanoDisco(dtopc.getTamanoDisco());
                                pc = (PC) this.sistemaCMDB.modificarEquipo(pc, u);
                                if (pc != null) {
                                    retorno = new DTOPC(pc);
                                    codigo = 6;
                                } else {
                                    codigo = 8;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoValidadora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta modificarImpresora(DTOImpresora dtoImpresora, String usuario) {
        DTOImpresora retorno = null;
        int codigo = 11;
        if (dtoImpresora != null && usuario != null && !usuario.isEmpty()) {
            try {
                if (/*      DATOS NO NULLOS         */dtoImpresora.getNumeroSerie() != null
                        && dtoImpresora.getMarca() != null
                        && dtoImpresora.getModelo() != null
                        && dtoImpresora.getPrecioDeCompra() != null
                        && dtoImpresora.getFechaAlta() != null
                        && dtoImpresora.getMesesGarantia() != null
                        && dtoImpresora.getEsPrestamo() != null
                        && dtoImpresora.getidLugar() != null
                        && dtoImpresora.getUsuario() != null
                        && dtoImpresora.getIp() != null
                        && dtoImpresora.getNombreHost() != null
                        && dtoImpresora.getTipo() != null
                        && dtoImpresora.getMultifuncion() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !dtoImpresora.getNumeroSerie().isEmpty()
                        && !dtoImpresora.getMarca().isEmpty()
                        && !dtoImpresora.getModelo().isEmpty()
                        && dtoImpresora.getPrecioDeCompra() >= 0
                        && dtoImpresora.getMesesGarantia() >= 0
                        && dtoImpresora.getidLugar() > 0
                        && !dtoImpresora.getUsuario().isEmpty()
                        && Validador.IpValida(dtoImpresora.getIp())
                        && Validador.NombreValido(dtoImpresora.getNombreHost())
                        && !dtoImpresora.getTipo().isEmpty()
                        && !dtoImpresora.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = this.sistemaCMDB.buscarLugar(dtoImpresora.getidLugar());
                            Usuario u = this.sistemaUsuarios.buscarUsuario(dtoImpresora.getUsuario());
                            if (l != null && u != null) {
                                Impresora imp = new Impresora();
                                imp.setId(dtoImpresora.getId());
                                imp.setNumeroDeSerie(dtoImpresora.getNumeroSerie());
                                imp.setMarca(dtoImpresora.getMarca());
                                imp.setModelo(dtoImpresora.getModelo());
                                imp.setPrecioDeCompra(dtoImpresora.getPrecioDeCompra());
                                imp.setFechaAlta((new SimpleDateFormat("yyyy-MM-dd")).parse(dtoImpresora.getFechaAlta()));
                                imp.setDescripcion(dtoImpresora.getDescripcion());
                                imp.setMesesGarantia(dtoImpresora.getMesesGarantia());
                                imp.setEsPrestamo(dtoImpresora.getEsPrestamo());
                                imp.setNumeroPrecinto(dtoImpresora.getNumeroPrecinto());
                                imp.setEstado(Equipo.EstadosEquipos.valueOf(dtoImpresora.getEstado()));
                                imp.setLugar(l);
                                imp.setUsuario(u);
                                imp.setIp(dtoImpresora.getIp());
                                imp.setNombreHost(dtoImpresora.getNombreHost());
                                imp.setOpcion(OpcionesImpresora.valueOf(dtoImpresora.getOpcion()));
                                imp.setMultifuncion(dtoImpresora.getMultifuncion());
                                imp.setConexion(ConexionesImpresora.valueOf(dtoImpresora.getConexion()));
                                imp = (Impresora) this.sistemaCMDB.modificarEquipo(imp, u);
                                if (imp != null) {
                                    retorno = new DTOImpresora(imp);
                                    codigo = 6;
                                } else {
                                    codigo = 8;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoValidadora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta modificarValidadora(DTOValidadora validadora, String usuario) {
        DTOValidadora retorno = null;
        int codigo = 11;
        if (validadora != null && !usuario.isEmpty()) {
            try {
                if (/*      DATOS NO NULLOS         */validadora.getNumeroSerie() != null
                        && validadora.getMarca() != null
                        && validadora.getModelo() != null
                        && validadora.getPrecioDeCompra() != null
                        && validadora.getFechaAlta() != null
                        && validadora.getMesesGarantia() != null
                        && validadora.getEsPrestamo() != null
                        && validadora.getidLugar() != null
                        && validadora.getUsuario() != null
                        && validadora.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !validadora.getNumeroSerie().isEmpty()
                        && !validadora.getMarca().isEmpty()
                        && !validadora.getModelo().isEmpty()
                        && validadora.getPrecioDeCompra() >= 0
                        && validadora.getFechaAlta() != null
                        && validadora.getMesesGarantia() >= 0
                        && validadora.getEsPrestamo() != null
                        && validadora.getidLugar() > 0
                        && !validadora.getUsuario().isEmpty()
                        && !validadora.getTipo().isEmpty()
                        && !validadora.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = this.sistemaCMDB.buscarLugar(validadora.getidLugar());
                            Usuario u = this.sistemaUsuarios.buscarUsuario(validadora.getUsuario());
                            if (l != null && u != null) {
                                Validadora val = new Validadora();
                                val.setId(validadora.getId());
                                val.setNumeroDeSerie(validadora.getNumeroSerie());
                                val.setMarca(validadora.getMarca());
                                val.setModelo(validadora.getModelo());
                                val.setPrecioDeCompra(validadora.getPrecioDeCompra());
                                val.setFechaAlta((new SimpleDateFormat("yyyy-MM-dd")).parse(validadora.getFechaAlta()));
                                val.setDescripcion(validadora.getDescripcion());
                                val.setMesesGarantia(validadora.getMesesGarantia());
                                val.setEsPrestamo(validadora.getEsPrestamo());
                                val.setNumeroPrecinto(validadora.getNumeroPrecinto());
                                val.setEstado(Equipo.EstadosEquipos.valueOf(validadora.getEstado()));
                                val.setLugar(l);
                                val.setUsuario(u);
                                val = (Validadora) this.sistemaCMDB.modificarEquipo(val, u);
                                if (val != null) {
                                    retorno = new DTOValidadora(val);
                                    codigo = 6;
                                } else {
                                    codigo = 8;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoValidadora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta modificarOtroEquipo(DTOOtroEquipo equipo, String usuario) {
        DTOOtroEquipo retorno = null;
        int codigo = 11;
        if (equipo != null && !usuario.isEmpty()) {
            try {
                if (/*      DATOS NO NULLOS         */equipo.getNumeroSerie() != null
                        && equipo.getMarca() != null
                        && equipo.getModelo() != null
                        && equipo.getPrecioDeCompra() != null
                        && equipo.getFechaAlta() != null
                        && equipo.getMesesGarantia() != null
                        && equipo.getEsPrestamo() != null
                        && equipo.getidLugar() != null
                        && equipo.getUsuario() != null
                        && equipo.getTipo() != null
                        /*     PARAMETROS CORRECTOS     */
                        && !equipo.getNumeroSerie().isEmpty()
                        && !equipo.getMarca().isEmpty()
                        && !equipo.getModelo().isEmpty()
                        && equipo.getPrecioDeCompra() >= 0
                        && equipo.getFechaAlta() != null
                        && equipo.getMesesGarantia() >= 0
                        && equipo.getEsPrestamo() != null
                        && equipo.getidLugar() > 0
                        && !equipo.getUsuario().isEmpty()
                        && !equipo.getTipo().isEmpty()
                        && !equipo.getFechaAlta().isEmpty()) {
                    Usuario user = this.sistemaUsuarios.buscarUsuario(usuario);
                    String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                    if (user != null && this.sistemaUsuarios.usuarioLogueado(user.getUsuario())) {
                        if (this.accesoPermitido(nombreMetodo, user.getRol())) {
                            Lugar l = this.sistemaCMDB.buscarLugar(equipo.getidLugar());
                            Usuario u = this.sistemaUsuarios.buscarUsuario(equipo.getUsuario());
                            if (l != null && u != null) {
                                OtroEquipo eq = new OtroEquipo();
                                eq.setId(equipo.getId());
                                eq.setNumeroDeSerie(equipo.getNumeroSerie());
                                eq.setMarca(equipo.getMarca());
                                eq.setModelo(equipo.getModelo());
                                eq.setPrecioDeCompra(equipo.getPrecioDeCompra());
                                eq.setFechaAlta((new SimpleDateFormat("yyyy-MM-dd")).parse(equipo.getFechaAlta()));
                                eq.setDescripcion(equipo.getDescripcion());
                                eq.setMesesGarantia(equipo.getMesesGarantia());
                                eq.setEsPrestamo(equipo.getEsPrestamo());
                                eq.setNumeroPrecinto(equipo.getNumeroPrecinto());
                                eq.setEstado(Equipo.EstadosEquipos.valueOf(equipo.getEstado()));
                                eq.setLugar(l);
                                eq.setUsuario(u);
                                eq.setTipo(TiposEquipo.valueOf(equipo.getTipo()));
                                eq = (OtroEquipo) this.sistemaCMDB.modificarEquipo(eq, u);
                                if (eq != null) {
                                    retorno = new DTOOtroEquipo(eq);
                                    codigo = 6;
                                } else {
                                    codigo = 8;
                                }
                            } else {
                                codigo = 16;
                            }
                        } else {
                            codigo = 13;
                        }
                    } else {
                        codigo = 14;
                    }
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoValidadora.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    public Respuesta asignarEquipoParaPrestamo(DTOFiltroEquipo dtoFiltroEquipo) {
        int codigo = 11;
        if (dtoFiltroEquipo != null && dtoFiltroEquipo.getUsuario() != null
                && !dtoFiltroEquipo.getUsuario().isEmpty()) {
            try {
                Usuario usuario = this.sistemaUsuarios.buscarUsuario(dtoFiltroEquipo.getUsuario().get(0));
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (usuario != null && this.sistemaUsuarios.usuarioLogueado(usuario.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, usuario.getRol())) {
                        if (this.sistemaCMDB.asignarEquipoParaPrestamo(dtoFiltroEquipo.getNumeroSerie().get(0), usuario)) {
                            codigo = 17;
                        } else {
                            codigo = 8;
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
                codigo = 3;
            } catch (Exception e) {
                Logger.getLogger(RecursoEquipo.class.getName()).log(Level.SEVERE, null, e);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, null);
    }

    public Respuesta getLugares() {
        int codigo = 4;
        ArrayList<DTOLugar> lugares = new ArrayList();
        try {
            for (DepartamentoCentral d : this.sistemaCMDB.buscarDepartamentosCentral(0)) {
                lugares.add(new DTODepartamentoCentral(d));
            }
            for (Service s : this.sistemaCMDB.buscarServices(0)) {
                lugares.add(new DTOService(s));
            }
            for (SubAgencia a : this.sistemaCMDB.buscarSubagencias(0, false)) {
                lugares.add(new DTOSubAgencia(a));

            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception e) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, lugares);
    }

    public Respuesta prestarEquipoSubagencia(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if (equipo != null && equipo.getNumeroSerie() != null && equipo.getidLugarActual() != null
                && (!equipo.getNumeroSerie().isEmpty() && equipo.getidLugarActual() > 0) && !usuario.isEmpty()) {
            try {
                codigo = 15;
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            Lugar busquedaL = this.sistemaCMDB.buscarLugar(equipo.getidLugarActual());
                            if (busquedaL != null && busquedaL instanceof SubAgencia) {
                                Equipo averiado = null;
                                if (equipo.getNumeroSerieEquipoPrestamo() != null) {
                                    averiado = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerieEquipoPrestamo());
                                    codigo = 27;
                                }
                                if ((averiado != null && averiado.getTipo().equals(e.getTipo()) && averiado.getEstado() == EstadosEquipos.AVERIADO
                                        || averiado == null)
                                        && e.getEsPrestamo() && e.getEstado() == Equipo.EstadosEquipos.PRESTAMO_DISPONIBLE) {
                                    e = this.sistemaCMDB.prestarEquipoSubagencia(e, (SubAgencia) busquedaL, u, averiado);
                                    if (e != null) {
                                        dtoEquipo = DTOEquipo.nuevoDTO(e);
                                        codigo = 17;
                                    }
                                } else {
                                    codigo = 18;
                                }
                            }

                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }

        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta recuperarEquipoPrestado(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if ((equipo != null && equipo.getNumeroSerie() != null)
                && (!equipo.getNumeroSerie().isEmpty()) && !usuario.isEmpty()) {
            try {
                codigo = 15;
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            codigo = 29;
                            if (e.getLugarActual() instanceof SubAgencia && e.getEstado() == Equipo.EstadosEquipos.PRESTADO) {
                                codigo = 0;
                                e = this.sistemaCMDB.recuperarEquipoPrestado(e, u);
                                if (e != null) {
                                    dtoEquipo = DTOEquipo.nuevoDTO(e);
                                    codigo = 17;

                                }
                            }
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta devolverEquipoCentral(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if ((equipo != null && equipo.getNumeroSerie() != null)
                && (!equipo.getNumeroSerie().isEmpty()) && !usuario.isEmpty()) {
            try {
                codigo = 15;
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            codigo = 19;
                            if (e.getLugarActual().getId() != this.sistemaCMDB.getDepartamentoDefecto().getId() && (e.getEstado() == Equipo.EstadosEquipos.PRESTADO || e.getEstado() == Equipo.EstadosEquipos.EN_RECUPERACION)) {
                                codigo = 0;
                                e = this.sistemaCMDB.devolverEquipoCentral(e, u);
                                if (e != null) {
                                    dtoEquipo = DTOEquipo.nuevoDTO(e);
                                    codigo = 17;

                                }
                            }
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta enviarEquipoAReparar(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if (equipo != null && equipo.getNumeroSerie() != null && !usuario.isEmpty()) {
            try {
                codigo = 15;
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            Equipo prestado = null;
                            if (equipo.getNumeroSerieEquipoPrestamo() != null) {
                                prestado = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerieEquipoPrestamo());
                            }
                            if (!(equipo.getNumeroSerieEquipoPrestamo() != null && prestado == null)) {
                                codigo = 28;
                                if (!(prestado != null && !prestado.getTipo().equals(e.getTipo()) && prestado.getEstado() != EstadosEquipos.PRESTAMO_DISPONIBLE)) {
                                    codigo = 20;
                                    if (e.getLugarActual() instanceof SubAgencia && e.getEstado() == Equipo.EstadosEquipos.ACTIVO) {
                                        codigo = 0;
                                        e = this.sistemaCMDB.enviarEquipoAReparar(e, u, prestado);
                                        if (e != null) {
                                            dtoEquipo = DTOEquipo.nuevoDTO(e);
                                            codigo = 17;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;
                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta ingresarEquipoAReparar(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if (equipo != null && equipo.getNumeroSerie() != null && !usuario.isEmpty()
                && !equipo.getNumeroSerie().isEmpty()) {
            try {
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            codigo = 21;
                            if (e.getLugarActual() instanceof SubAgencia && e.getEstado() == Equipo.EstadosEquipos.AVERIADO) {
                                codigo = 0;
                                e = this.sistemaCMDB.ingresarEquipoAReparar(e, u);
                                if (e != null) {
                                    dtoEquipo = DTOEquipo.nuevoDTO(e);
                                    codigo = 17;

                                }
                            }
                        } else {
                            codigo = 15;
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta devolverEquipoReparado(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if (equipo != null && equipo.getNumeroSerie() != null
                && !usuario.isEmpty() && !equipo.getNumeroSerie().isEmpty()) {
            try {
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {

                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            codigo = 22;
                            if (e.getLugarActual().getId() == this.sistemaCMDB.getDepartamentoDefecto().getId() && e.getEstado() == Equipo.EstadosEquipos.EN_REPARACION) {
                                codigo = 0;
                                e = this.sistemaCMDB.devolverEquipoReparado(e, u);
                                if (e != null) {
                                    dtoEquipo = DTOEquipo.nuevoDTO(e);
                                    codigo = 17;

                                }
                            }
                        } else {
                            codigo = 15;
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta despacharEquipo(DTOEquipo equipo, String usuario) {
        int codigo = 11;
        DTOEquipo dtoEquipo = null;
        if (equipo != null && equipo.getNumeroSerie() != null && !usuario.isEmpty()) {
            try {
                codigo = 15;
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        Equipo e = this.sistemaCMDB.buscarEquipo(equipo.getNumeroSerie());
                        if (e != null) {
                            codigo = 25;
                            if (e.getLugarActual().getId() == this.sistemaCMDB.getDepartamentoDefecto().getId() && e.getEstado() == Equipo.EstadosEquipos.INACTIVO) {
                                codigo = 0;
                                e = this.sistemaCMDB.despacharEquipo(e, u);
                                if (e != null) {
                                    dtoEquipo = DTOEquipo.nuevoDTO(e);
                                    codigo = 17;

                                }
                            }
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dtoEquipo);
    }

    public Respuesta lugaresHomologados() {
        Respuesta r = this.getLugares();
        ArrayList<DTOLugar> lugaresHomologados = (ArrayList<DTOLugar>) r.getRetorno();
        lugaresHomologados.removeIf(l -> !l.getActivo());
        return Respuesta.nuevaRespuesta(r.getCodigo(), lugaresHomologados);
    }

    //retorna las marcas de pc registradas en caso de que marca sea vacia
    // si la marca no es vacia retorna los modelos de la marca solicitada
    public Respuesta getPlantillasPc(String marca) {
        ArrayList resultado = new ArrayList();
        ArrayList<DTOPlantillaPc> dtoPlantillasPCs = new ArrayList();
        int codigo;
        try {
            ArrayList<PlantillaEquipo> plantillas = this.sistemaCMDB.getPlantillasPc(marca);
            if (plantillas.size() > 0) {
                plantillas.forEach((p) -> {
                    dtoPlantillasPCs.add(new DTOPlantillaPc((PlantillaPc) p));
                });
                if (marca == null || marca.isEmpty()) {
                    ArrayList lista = new ArrayList();
                    for (PlantillaEquipo p : plantillas) {
                        if (!lista.contains(p.getMarca())) {
                            lista.add(p.getMarca());
                        }
                    }
                    resultado = lista;
                } else {
                    resultado = dtoPlantillasPCs;
                }
                codigo = 4;
            } else {
                codigo = 12;

            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception ex) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, resultado);
    }

    //retorna la plantilla del modelo especificado
    public Respuesta buscarPlantillaPC(DTOPlantillaPc dtoPlantillaEquipo) {
        DTOPlantillaPc dto = null;
        int codigo = 11;
        if (dtoPlantillaEquipo != null && dtoPlantillaEquipo.getId() != null && dtoPlantillaEquipo.getId() >= 0) {
            try {
                PlantillaPc plantilla = this.sistemaCMDB.buscarPlantillaPC(dtoPlantillaEquipo.getId());
                if (plantilla != null) {
                    dto = new DTOPlantillaPc(plantilla);
                    codigo = 4;
                } else {
                    codigo = 12;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dto);
    }

    public Respuesta getPlantillasImpresoras(String marca) {
        ArrayList resultado = new ArrayList();
        ArrayList<DTOPlantillaImpresora> dtoPlantillasImpresoras = new ArrayList();
        int codigo;
        try {
            ArrayList<PlantillaEquipo> plantillas = this.sistemaCMDB.getPlantillasImpresoras(marca);
            if (plantillas.size() > 0) {
                plantillas.forEach((p) -> {
                    dtoPlantillasImpresoras.add(new DTOPlantillaImpresora((PlantillaImpresora) p));
                });
                if (marca == null || marca.isEmpty()) {
                    ArrayList lista = new ArrayList();
                    for (PlantillaEquipo p : plantillas) {
                        if (!lista.contains(p.getMarca())) {
                            lista.add(p.getMarca());
                        }
                    }
                    resultado = lista;
                } else {
                    resultado = dtoPlantillasImpresoras;
                }
                codigo = 4;
            } else {
                codigo = 12;

            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception ex) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, resultado);
    }

    public Respuesta buscarPlantillaImpresora(DTOPlantillaImpresora dtoPlantillaEquipo) {
        DTOPlantillaImpresora dto = null;
        int codigo = 11;
        if (dtoPlantillaEquipo != null && dtoPlantillaEquipo.getId() != null && dtoPlantillaEquipo.getId() >= 0) {
            try {
                PlantillaImpresora plantilla = this.sistemaCMDB.buscarPlantillaImpresora(dtoPlantillaEquipo.getId());
                if (plantilla != null) {
                    dto = new DTOPlantillaImpresora(plantilla);
                    codigo = 4;
                } else {
                    codigo = 12;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dto);
    }

    public Respuesta buscarPlantillaValidadora(DTOPlantillaValidadora dtoPlantillaEquipo) {
        DTOPlantillaValidadora dto = null;
        int codigo = 11;
        if (dtoPlantillaEquipo != null && dtoPlantillaEquipo.getId() != null && dtoPlantillaEquipo.getId() >= 0) {
            try {
                PlantillaValidadora plantilla = this.sistemaCMDB.buscarPlantillaValidadora(dtoPlantillaEquipo.getId());
                if (plantilla != null) {
                    dto = new DTOPlantillaValidadora(plantilla);
                    codigo = 4;
                } else {
                    codigo = 12;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dto);
    }

    public Respuesta getPlantillasValidadoras(String marca) {
        ArrayList resultado = new ArrayList();
        ArrayList<DTOPlantillaValidadora> dtoPlantillasValidadoras = new ArrayList();
        int codigo;
        try {
            ArrayList<PlantillaEquipo> plantillas = this.sistemaCMDB.getPlantillasValidadoras(marca);
            if (plantillas.size() > 0) {
                plantillas.forEach((p) -> {
                    dtoPlantillasValidadoras.add(new DTOPlantillaValidadora((PlantillaValidadora) p));
                });
                if (marca == null || marca.isEmpty()) {
                    ArrayList lista = new ArrayList();
                    for (PlantillaEquipo p : plantillas) {
                        if (!lista.contains(p.getMarca())) {
                            lista.add(p.getMarca());
                        }
                    }
                    resultado = lista;
                } else {
                    resultado = dtoPlantillasValidadoras;
                }
                codigo = 4;
            } else {
                codigo = 12;

            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception ex) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, resultado);
    }

    public Respuesta getPlantillasOtroEquipo(String tipo, String marca) {
        ArrayList resultado = new ArrayList();
        ArrayList<DTOPlantillaOtroEquipo> dtoPlantillasOtroEquipo = new ArrayList();
        int codigo;
        try {
            ArrayList<PlantillaEquipo> plantillas = this.sistemaCMDB.getPlantillasOtrosEquipos(tipo, marca);
            if (plantillas.size() > 0) {
                plantillas.forEach((p) -> {
                    dtoPlantillasOtroEquipo.add(new DTOPlantillaOtroEquipo((PlantillaOtroEquipo) p));
                });
                if (marca == null || marca.isEmpty()) {
                    ArrayList lista = new ArrayList();
                    for (PlantillaEquipo p : plantillas) {
                        if (!lista.contains(p.getMarca())) {
                            lista.add(p.getMarca());
                        }
                    }
                    resultado = lista;
                } else {
                    resultado = dtoPlantillasOtroEquipo;
                }
                codigo = 4;
            } else {
                codigo = 12;

            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception ex) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, resultado);
    }

    public Respuesta buscarPlantillaOtroEquipo(DTOPlantillaOtroEquipo dtoPlantillaEquipo) {
        DTOPlantillaOtroEquipo dto = null;
        int codigo = 11;
        if (dtoPlantillaEquipo != null && dtoPlantillaEquipo.getId() != null && dtoPlantillaEquipo.getId() >= 0) {
            try {
                PlantillaOtroEquipo plantilla = this.sistemaCMDB.buscarPlantillaOtroEquipo(dtoPlantillaEquipo.getId());
                if (plantilla != null) {
                    dto = new DTOPlantillaOtroEquipo(plantilla);
                    codigo = 4;
                } else {
                    codigo = 12;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, dto);
    }

    public Respuesta listadoMantenimientoEquiposDePrestamo(String usuario) {
        ArrayList resultado = new ArrayList();
        int codigo = 11;
        if (usuario != null && !usuario.isEmpty()) {
            try {
                Usuario u = this.sistemaUsuarios.buscarUsuario(usuario);
                String nombreMetodo = Thread.currentThread().getStackTrace()[1].getMethodName();
                if (u != null && this.sistemaUsuarios.usuarioLogueado(u.getUsuario())) {
                    if (this.accesoPermitido(nombreMetodo, u.getRol())) {
                        ArrayList<Equipo> lista = this.sistemaCMDB.listadoMantenimientoPrestamos();
                        if (lista.size() > 0) {
                            DTOEquipo dto;
                            for (Equipo e : lista) {
                                e = this.sistemaCMDB.cargarEquipoConItemsHistoricos(e);
                                dto = DTOEquipo.nuevoDTO(e);
                                dto.setCantidadMesesEnPrestamo(this.sistemaCMDB.cantidadMesesEnPrestamo(e));
                                dto.setCantidadVecesPrestado(this.sistemaCMDB.cantidadVecesPrestado(e));
                                dto.setDepreciacionMensual(this.sistemaCMDB.depreciacionMensualDeEquipo(e));
                                dto.setCostoVentaUsado(this.sistemaCMDB.costoDeVentaDeEquipo(e));
                                dto.setCategoriaMantenimiento(this.sistemaCMDB.obtenerCategoriaEquipoPrestamo(e));
                                resultado.add(dto);
                            }
                            codigo = 4;
                        } else {
                            codigo = 12;
                        }
                    } else {
                        codigo = 13;
                    }
                } else {
                    codigo = 14;

                }
            } catch (ExcepcionConexion ex) {
                Logger.getLogger(RecursoService.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 3;

            } catch (Exception ex) {
                Logger.getLogger(RecursoEquipo.class
                        .getName()).log(Level.SEVERE, null, ex);
                codigo = 0;
            }
        }
        return Respuesta.nuevaRespuesta(codigo, resultado);
    }

    public Respuesta buscarHistoricoEquipos(String numeroSerie) {
        ArrayList<DTOItemHistoricoLugar> retorno = new ArrayList<>();
        int codigo;
        try {
            if (numeroSerie == null) {
                numeroSerie = "";
            }
            DTOItemHistoricoLugar dto;
            Equipo equipoConItemsHistoricos = this.sistemaCMDB.buscarEquipo(numeroSerie);
            for (ItemHistoricoLugar i : equipoConItemsHistoricos.getHistoriaLugares()) {
                i.setUsuario(this.sistemaUsuarios.buscarUsuario(i.getUsuario().getId()));
                dto = new DTOItemHistoricoLugar(i);
                //Se agrega para moder mostrar el numero de sugagencia
                //en el historico cuando es SUBAGENCIA
                if (i.getLugar().getTipo() == "SUBAGENCIA") {
                    dto.setIdLugar(((SubAgencia) i.getLugar()).getNumero());
                }
                retorno.add(dto);
            }
            codigo = (retorno.size() > 0) ? 4 : 12;

        } catch (ExcepcionConexion ex) {
            Logger.getLogger(RecursoService.class
                    .getName()).log(Level.SEVERE, null, ex);
            codigo = 3;

        } catch (Exception e) {
            Logger.getLogger(RecursoEquipo.class
                    .getName()).log(Level.SEVERE, null, e);
            codigo = 0;
        }
        return Respuesta.nuevaRespuesta(codigo, retorno);
    }

    private boolean accesoPermitido(String nombreMetodo, Rol rol) {
        return this.sistemaUsuarios.accesoPermitido(nombreMetodo, rol);
    }

    public Respuesta modificarParametrosMantenimientoPrestamos(DTOParametroMantenimientoPrestamo dto) {
        Integer codigo = 11;
        if (dto.getRCantMesesEnPrestamo() != null && dto.getRCantVecesPrestado() != null
                && dto.getACantMesesEnPrestamo() != null && dto.getACantVecesPrestado() != null
                && dto.getVCantMesesEnPrestamo() != null && dto.getVCantVecesPrestado() != null
                && dto.getRCantMesesEnPrestamo() > 0 && dto.getRCantVecesPrestado() > 0
                && dto.getACantMesesEnPrestamo() > 0 && dto.getACantVecesPrestado() > 0
                && dto.getVCantMesesEnPrestamo() > 0 && dto.getVCantVecesPrestado() > 0) {
            this.sistemaCMDB.modicarParametros(dto.getRCantMesesEnPrestamo(),
                    dto.getRCantVecesPrestado(), dto.getACantMesesEnPrestamo(), dto.getACantVecesPrestado(),
                    dto.getVCantMesesEnPrestamo(), dto.getVCantVecesPrestado());
            codigo = 6;
        }
        return Respuesta.nuevaRespuesta(codigo, null);
    }

    public void formatearCodigosDeEquiposBD() {
        this.sistemaCMDB.formatearCodigosDeEquiposBD();
    }
    
    public Respuesta getParametrosMantenimiento(){
        return Respuesta.nuevaRespuesta(4, this.sistemaCMDB.getParametrosMantenimiento());
    }
}

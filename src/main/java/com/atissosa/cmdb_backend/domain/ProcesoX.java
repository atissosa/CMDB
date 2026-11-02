package com.atissosa.cmdb_backend.domain;

import com.atissosa.cmdb_backend.errors.ExcepcionConexion;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.atissosa.cmdb_backend.logic.SubSistemaUsuarios;

public class ProcesoX extends Observable implements Runnable {

    private static int tiempoMaximo = 1200;
    private String nombre;
    private Thread hilo;
    private boolean correr;
    private boolean finalizado;
    private int x;
    private String usuario;
    private SubSistemaUsuarios sistemaUsuarios;

    public void continuar() {
        iniciar();
    }

    public static void setTiempoMaximo(int valor) {
        tiempoMaximo = valor;
    }

    public void reset() {
        if (!finalizado) {
            x = 1;
        }
    }

    public void terminar() {
        finalizado = true;
        detener();
    }

    public ProcesoX(String nombreHilo, String nombreUsuario, SubSistemaUsuarios s) {
        this.nombre = nombreHilo;
        this.usuario = nombreUsuario;
        this.sistemaUsuarios = s;
    }

    public int getValor() {
        return this.x;
    }

    public void iniciar() {
        if (!correr && !finalizado) {
            correr = true;
            this.x = tiempoMaximo;
            hilo = new Thread(this);
            hilo.start();
        }
    }

    public void detener() {
        if (correr) {
            correr = false;
            hilo.interrupt();
        }
    }

    @Override
    public void run() {
        try {
            if (correr) {
                for (; x > 0 && correr; x--) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException ex) {
                        System.out.println("Proceso terminado");
                        this.terminar();
                    }
                }
                correr = false;
                this.terminar();
                this.sistemaUsuarios.logout(this.usuario);
            }
        } catch (ExcepcionConexion ex) {
            Logger.getLogger(SubSistemaUsuarios.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}

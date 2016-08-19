package com.trucoargento.modelo;

import java.util.ArrayList;
import java.util.List;
import javax.websocket.Session;


/**
 * Clase que representa un jugador, el cual tiene un nombre y un maximo de 3 cartas.
 * @author FacundoTaramasco
 */
public class Jugador {
    
    private String nombre;
    private List<Carta> cartas = new ArrayList<>();
    private int indiceCarta = 0;
    private Session sesion;
    
    private boolean cantoEnvido;
    private boolean cantoEnvidoEnvido;
    private boolean cantoRealEnvido;
    private boolean cantoFaltaEnvido;
    
    private boolean aceptoEnvido;
    private boolean aceptoEnvidoEnvido;
    private boolean aceptoRealEnvido;
    private boolean aceptoFaltaEnvido;
    
    // Constructor
    public Jugador() {
        this("");
    }

    public Jugador(Session s) {
    }
    
    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    // Getters
    public String getNombre() { return nombre; }
    public List<Carta> getCartas() { return cartas; }
    public Session getSesion() { return sesion; }
    public boolean cantoEnvido() { return cantoEnvido; }
    public boolean cantoEnvidoEnvido() { return cantoEnvidoEnvido; }
    public boolean cantoRealEnvido() { return cantoRealEnvido; }
    public boolean cantoFaltaEnvido() { return cantoFaltaEnvido; }
    public boolean aceptoEnvido() { return aceptoEnvido; }
    public boolean aceptoEnvidoEnvido() { return aceptoEnvidoEnvido; }
    public boolean aceptoRealEnvido() { return aceptoRealEnvido; }
    public boolean aceptoFaltaEnvido() { return aceptoFaltaEnvido; }
    
    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSesion(Session sesion) { this.sesion = sesion; }
    public void setCantoEnvido(boolean cantoEnvido) { this.cantoEnvido = cantoEnvido; }
    public void setCantoEnvidoEnvido(boolean cantoEnvidoEnvido) { this.cantoEnvidoEnvido = cantoEnvidoEnvido; }
    public void setCantoRealEnvido(boolean cantoRealEnvido) { this.cantoRealEnvido = cantoRealEnvido; }
    public void setCantoFaltaEnvido(boolean cantoFaltaEnvido) { this.cantoFaltaEnvido = cantoFaltaEnvido; }
    public void setAceptoEnvido(boolean aceptoEnvido) { this.aceptoEnvido = aceptoEnvido; }
    public void setAceptoEnvidoEnvido(boolean aceptoEnvidoEnvido) { this.aceptoEnvidoEnvido = aceptoEnvidoEnvido; }
    public void setAceptoRealEnvido(boolean aceptoRealEnvido) { this.aceptoRealEnvido = aceptoRealEnvido; }
    public void setAceptoFaltaEnvido(boolean aceptoFaltaEnvido) { this.aceptoFaltaEnvido = aceptoFaltaEnvido; }
    
    // Customs

    public void recibirCarta( Carta cartaDelMazo) {
        if (indiceCarta == 3) return;
        cartas.add(cartaDelMazo);
        indiceCarta++;
    }

    public List<Carta> entregarCargas() {
        List<Carta> tmp = this.getCartas();
        this.cartas = new ArrayList<>();
        indiceCarta = 0;
        return tmp;
    }

    

    
    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", cartas=" + cartas +
                '}';
    }
}

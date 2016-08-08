package com.trucoargento.modelo;

import java.util.Arrays;
import javax.websocket.Session;


/**
 * Clase que representa un jugador, el cual tiene un nombre y un maximo de ITruco.CARTASXJUGADOR cartas.
 * @author FacundoTaramasco
 */
public class Jugador {
    
    private String nombre;
    private Carta[] cartas = new Carta[ITruco.CARTASXJUGADOR];
    private int indiceCarta = 0;
    private Session sesion;
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
    public Carta[] getCartas() { return cartas; }
    public Session getSesion() { return sesion; }
    
    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSesion(Session sesion) { this.sesion = sesion; }
    
    // Customs

    public void recibirCarta( Carta cartaDelMazo) {
        if (indiceCarta == ITruco.CARTASXJUGADOR) return;
        cartas[indiceCarta] = cartaDelMazo;
        indiceCarta++;
    }

    public Carta[] entregarCargas() {
        Carta[] tmp = this.getCartas();
        this.cartas = null;
        indiceCarta = 0;
        return tmp;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", cartas=" + Arrays.toString(cartas) +
                '}';
    }
}

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
    
    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSesion(Session sesion) { this.sesion = sesion; }
    
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

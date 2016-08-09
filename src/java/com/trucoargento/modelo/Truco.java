package com.trucoargento.modelo;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author FacundoTaramasco
 */
public class Truco {
    
    private List<Carta> mazoCartas                    = new ArrayList<Carta>();
    private final Map<Carta, Integer> jerarquiaCartas = new HashMap<Carta, Integer>();

    private Jugador jugadorUno;
    private Jugador jugadorDos;

    private boolean estadoJuego;

    private static final Truco truco = new Truco();
    
    public static Truco getInstance() { return truco; }
    
    // Constructor
    private Truco() {
        init();
    }

    /**
     * Metodo que inicializa el juego
     */
    private void init() {
        
        System.out.println("%%% INICIANDO JUEGO %%%");
        System.out.println("Generando mazo...");
        this.generarMazo();

        System.out.println("Mazo generado : ");
        this.mostrarMazoCustom();
        System.out.println("*******************************************************\n");

        System.out.println("Estableciendo jerarquia de cartas...");
        this.establecerJerarquiaCartas();
        System.out.println("Jerarquia de cartas : ");
        this.mostrarJerarquiaCartas();
        System.out.println("*******************************************************\n");

        System.out.println("Abarajando mazo...");
        this.abarajarMazo();
        System.out.println("Mazo abarajado : ");
        this.mostrarMazoCustom();
        System.out.println("*******************************************************\n");

        jugadorUno = new Jugador();
        jugadorDos = new Jugador();
        
        /*
        jugadorUno = new Jugador("Facu");
        jugadorDos = new Jugador("IA");

        System.out.println("Jugadores : ");
        System.out.println(jugadorUno);
        System.out.println(jugadorDos);
        System.out.println("*******************************************************\n");
        */
    }
    
    // Getters
    public Jugador getJugadorUno() {
        return jugadorUno;
    }
    
    public Jugador getJugadorDos() {    
        return jugadorDos;
    }

    // Setters
    public void setJugadorUno(Jugador jugadorUno) {
        this.jugadorUno = jugadorUno;
    }

    public void setJugadorDos(Jugador jugadorDos) {
        this.jugadorDos = jugadorDos;
    }
    
    
    
   
    // Customs
    /**
     * A jugar!
     
    public void jugar() {
        
        estadoJuego = true;

        System.out.println("Entregando cartas a los jugadores...");
        this.darCartasJugador(jugadorUno);
        this.darCartasJugador(jugadorDos);

        System.out.println("Recibiendo cartas de todos los jugadores...");

        this.recibirCartasJugadores();
        this.mostrarMazoCustom();

    }
    */
    
    /**
     * Metodo que genera el mazo de cartas
     */
    private void generarMazo() {
        for (Palos p : Palos.values()) { // COPA, ORO, BASTO, ESPADA
            for (NumeroCarta n : NumeroCarta.values()) { // UNO, DOS, TRES, CUATRO, CINCO, SEIS, SIETE, DIEZ, ONCE, DOCE
                mazoCartas.add(new Carta(p, n));
            }
        }
    }

    /**
     * Metodo que establece el orden jerarquico del mazo
     */
    private void establecerJerarquiaCartas() {

        jerarquiaCartas.put( mazoCartas.get(30), 1 ); // 1 espada
        jerarquiaCartas.put( mazoCartas.get(20), 2 ); // 1 basto
        jerarquiaCartas.put( mazoCartas.get(36), 3 ); // 7 espada
        jerarquiaCartas.put( mazoCartas.get(16), 4 ); // 7 oro

        // todos los 3
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 3)  jerarquiaCartas.put(c, 5);
        // todos los 2
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 2) jerarquiaCartas.put(c, 6);
        // todos los 1 (menos el de espada y basto)
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 1 && c.getPalo() != Palos.BASTO && c.getPalo() != Palos.ESPADA) jerarquiaCartas.put(c, 7);
        // todos los 12
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 12) jerarquiaCartas.put(c, 8);
        // todos los 11
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 11) jerarquiaCartas.put(c, 9);
        // todos los 10
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 10) jerarquiaCartas.put(c, 10);
        // todos los 7 (menos el de espada y oro)
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 7 && c.getPalo() != Palos.ESPADA && c.getPalo() != Palos.ORO) jerarquiaCartas.put(c, 11);
        // todos los 6
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 6) jerarquiaCartas.put(c, 12);
        // todos los 5
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 5) jerarquiaCartas.put(c, 13);
        // todos los 4
        for (Carta c : mazoCartas)
            if ( c.getValor().getValor() == 4) jerarquiaCartas.put(c,14);
    }

    /**
     * Metodo que abaraja el mazo (lo desordena)
     */
    public void abarajarMazo() {
        Collections.shuffle(mazoCartas);
    }

    /**
     * Metodo que le entrega ITruco.CARTASXJUGADOR cartas random del mazo al jugador especificado.
     * @param j Jugador que recibe las cartas.
     */
    public void darCartasJugador(Jugador j) {
        Random r = new Random();
        Carta carta;
        int indice;
        for (int i = 0; i < ITruco.CARTASXJUGADOR; i++) {
            indice = r.nextInt(mazoCartas.size());
            carta = mazoCartas.get(indice);
            j.recibirCarta(carta);

            System.out.println("Se entrego " + carta + " a jugador : " + j);
            mazoCartas.remove(indice);
        }
    }

    /**
     * Metodo que recibe todas las cartas de todos los jugadores y las agrega al mazo nuevamente
     */
    private void recibirCartasJugadores() {
        mazoCartas.addAll(Arrays.asList( jugadorUno.entregarCargas() ));
        mazoCartas.addAll(Arrays.asList( jugadorDos.entregarCargas() ));
    }


    private void mostrarMazoCustom() {
        for (Carta c : mazoCartas) {
            System.out.println(c);
        }
    }

    private void mostrarJerarquiaCartas() {
        Iterator it = jerarquiaCartas.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry)it.next();
            System.out.println(e.getKey() + " " + e.getValue());
        }
    }
    
    
}

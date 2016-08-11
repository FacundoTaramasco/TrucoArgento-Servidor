package com.trucoargento.modelo;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author FacundoTaramasco
 */
public class Truco {
    
    private List<Carta> mazoCartas                    = new ArrayList<Carta>();
    private final Map<Carta, Integer> jerarquiaCartas = new HashMap<Carta, Integer>();

    private Jugador jugadorUno;
    private Jugador jugadorDos;

    private Jugador jugadorTurno;
    
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
        
        
        jugadorTurno = jugadorUno;
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

    public Jugador getJugadorTurno() {
        return jugadorTurno;
    }

    
    
    // Setters
    public void setJugadorUno(Jugador jugadorUno) {
        this.jugadorUno = jugadorUno;
    }

    public void setJugadorDos(Jugador jugadorDos) {
        this.jugadorDos = jugadorDos;
    }
    
    
    
   
    // Customs
    
    public void cambiarTurno() {
        jugadorTurno = (jugadorTurno == jugadorUno ? jugadorDos : jugadorUno);
    }
    
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
            if ( c.getValor().getValor() == 4) jerarquiaCartas.put(c, 14);
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

    /*
    public TreeMap<Palos, Integer> determinarEnvidoJugador(Jugador j) {
        int dosCartasMismoPaloORO    = 0;
        int dosCartasMismoPaloBASTO  = 0;
        int dosCartasMismoPaloESPADA = 0;
        int dosCartasMismoPaloCOPA   = 0;
        
        int cartaNegraBASTO  = 0;
        int cartaNegraORO    = 0;
        int cartaNegraESPADA = 0;
        int cartaNegraCOPA   = 0;
        
        TreeMap<Palos, Integer> envido = new TreeMap();
        TreeMap<Palos, Integer> ret    = new TreeMap();
        
        envido.put(Palos.ORO, 0);
        envido.put(Palos.BASTO, 0);
        envido.put(Palos.ESPADA, 0);
        envido.put(Palos.COPA, 0);
        
        for (Carta c : j.getCartas()) {          
            System.out.println("calculando envido de carta : " + c +  " " + c.getValor().getValorEnvido() );
            switch(c.getPalo()) {
                case BASTO:
                    envido.put(Palos.BASTO, envido.get(Palos.BASTO) + c.getValor().getValorEnvido() );
                    dosCartasMismoPaloBASTO += 1;
                    if (c.getValor() == NumeroCarta.DIEZ || c.getValor() == NumeroCarta.ONCE || c.getValor() == NumeroCarta.DOCE)
                        cartaNegraBASTO += 1;
                    if (dosCartasMismoPaloBASTO == 2 && cartaNegraBASTO == 0)
                        envido.put(Palos.BASTO, envido.get(Palos.BASTO) + 20 );
                    if (dosCartasMismoPaloBASTO == 2 && cartaNegraBASTO == 1)
                        envido.put(Palos.BASTO, envido.get(Palos.BASTO) + 10);
                    break;
                case ORO:
                    envido.put(Palos.ORO, envido.get(Palos.ORO) + c.getValor().getValorEnvido() );
                    dosCartasMismoPaloORO += 1;
                    if (c.getValor() == NumeroCarta.DIEZ || c.getValor() == NumeroCarta.ONCE || c.getValor() == NumeroCarta.DOCE)
                        cartaNegraORO +=1;
                    if (dosCartasMismoPaloORO == 2 && cartaNegraORO == 0)
                        envido.put(Palos.ORO, envido.get(Palos.ORO) + 20 );
                    if (dosCartasMismoPaloORO == 2 && cartaNegraORO == 1)
                        envido.put(Palos.ORO, envido.get(Palos.ORO) + 10 );
                    break;
                case COPA:
                    envido.put(Palos.COPA, envido.get(Palos.COPA) + c.getValor().getValorEnvido() );
                    dosCartasMismoPaloCOPA += 1;
                    if (c.getValor() == NumeroCarta.DIEZ || c.getValor() == NumeroCarta.ONCE || c.getValor() == NumeroCarta.DOCE)
                        cartaNegraCOPA +=1;
                    if (dosCartasMismoPaloCOPA == 2 && cartaNegraCOPA == 0)
                        envido.put(Palos.COPA, envido.get(Palos.COPA) + 20 );
                    if (dosCartasMismoPaloCOPA == 2 && cartaNegraCOPA == 1)
                        envido.put(Palos.COPA, envido.get(Palos.COPA) + 10 );
                    break;
                case ESPADA:
                    envido.put(Palos.ESPADA, envido.get(Palos.ESPADA) + c.getValor().getValorEnvido() );
                    dosCartasMismoPaloESPADA += 1;
                    if (c.getValor() == NumeroCarta.DIEZ || c.getValor() == NumeroCarta.ONCE || c.getValor() == NumeroCarta.DOCE)
                        cartaNegraESPADA += 1;
                    if (dosCartasMismoPaloESPADA == 2 && cartaNegraESPADA == 0)
                        envido.put(Palos.ESPADA, envido.get(Palos.ESPADA) + 20 );
                    if (dosCartasMismoPaloESPADA == 2 && cartaNegraESPADA == 1)
                        envido.put(Palos.ESPADA, envido.get(Palos.ESPADA) + 10 );
            }            
        }
        
        int acc = envido.get(envido.firstKey());
        Palos p = envido.firstKey();
        
        for (Map.Entry<Palos, Integer> entry : envido.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
            if (entry.getValue() > acc) {
                acc = entry.getValue();
                p   = entry.getKey();
            }
        }
        ret.put(p, acc);
        return ret;
    }
    */
    
    /*
    private boolean tieneFlor(Jugador j) {
        int accBasto = 0;
        int accOro = 0;
        int accEspada = 0;
        int accCopa = 0;
        for (Carta c : j.getCartas()) {
            switch(c.getPalo()) {
                case BASTO:
                    accBasto++;
                    break;
                case ORO:
                    accOro++;
                    break;
                case ESPADA:
                    accEspada++;
                    break;
                case COPA :
                    accCopa++;
            }
        }
        return ( accBasto == j.getCartas().size() || 
                  accCopa == j.getCartas().size() || 
                accEspada == j.getCartas().size() ||
                   accOro == j.getCartas().size() );
    }
    */
    
    private boolean tieneFlor(Jugador j) {
        return ( j.getCartas().get(0).getPalo() == j.getCartas().get(1).getPalo() && j.getCartas().get(1).getPalo() == j.getCartas().get(2).getPalo() );
    }
    
    /**
     * Metodo que retorna booleano si la carta pasada por parametro es una 
     * 'carta negra' (10, 11 o 12)
     * @param c
     * @return 
     */
    private boolean cartaNegra(Carta c) {
        return (c.getValor() == NumeroCarta.DIEZ || c.getValor() == NumeroCarta.ONCE || c.getValor() == NumeroCarta.DOCE);
    }
        
    public void determinarEnvidoJugadorD(Jugador j) {
        int accEnvido = 0;
        int valorCartaUno = 0;
        int valorCartaDos = 0;
        List<Carta> l;
        // 3 cartas del mismo palo
        if (tieneFlor(j)) {
            System.out.println("Tenes flor capo, tomatelas");
            System.out.println("La mejor combinacion para envido es : " + valorEnvidoConFlor(j));
            return;
        }
        l = dosCartasMismoPalo(j);
        // tiene dos cartas del mismo palo
        if (!l.isEmpty()) {
            // las dos cartas son 'negras' (10, 11 o 12)
            if ( cartaNegra(l.get(0)) && cartaNegra(l.get(1)) ) {
                accEnvido = 20;
            } else { // o ninguna o alguna de las dos es carta negra
                   valorCartaUno = cartaNegra(l.get(0)) ? 0 : l.get(0).getValor().getValorEnvido();
                   valorCartaDos = cartaNegra(l.get(1)) ? 0 : l.get(1).getValor().getValorEnvido(); 
                   accEnvido     = valorCartaUno + valorCartaDos + 20;
            }
        } else { // lista vacia, las 3 cartas son distintas
            accEnvido = valorEnvidoCartaMasAlta(j);
        }
        System.out.println("El envido vale : " + accEnvido);
    }

    /**
     * Metodo que retorna una lista con las dos cartas que sean del mismo palo.
     * En el caso de que no existan dos cartas iguales la lista se retorna vacia.
     * @param j
     * @return 
     */
    private List<Carta> dosCartasMismoPalo(Jugador j) {
        List<Carta> lista = new ArrayList();
        if (j.getCartas().get(0).getPalo() == j.getCartas().get(1).getPalo()) {
            lista.add(j.getCartas().get(0));
            lista.add(j.getCartas().get(1));
        }
        if (j.getCartas().get(0).getPalo() == j.getCartas().get(2).getPalo() ) {
            lista.add(j.getCartas().get(0));
            lista.add(j.getCartas().get(2)); 
        }
        if (j.getCartas().get(1).getPalo() == j.getCartas().get(2).getPalo() ) {
            lista.add(j.getCartas().get(1));
            lista.add(j.getCartas().get(2));     
        }
        return lista;
    }
    
    /**
     * Si no tiene flor ni dos cartas iguales del mismo palo se toma la carta
     * mas alta de valor del envido.
     * @param j
     * @return 
     */
    private int valorEnvidoCartaMasAlta(Jugador j) {
        int max = j.getCartas().get(0).getValor().getValorEnvido();
        for (Carta c : j.getCartas()) {
            if (c.getValor().getValorEnvido() > max)
                max = c.getValor().getValorEnvido();
        }
        return max;
    }
    
    private int valorEnvidoConFlor(Jugador j) {
        int combUno, combDos, combTres = 0;
        
        /*
        3 cartas negras -> 20 de envido
        2 cartas negras -> 1 negra + la que resta + 20
        
        1 carta negra   -> la dos que restan + 20 
        
        ninguna negra -> buscar la mejor combinacion de las 3
        */
        
        if (cartaNegra(j.getCartas().get(0)) && cartaNegra(j.getCartas().get(1)) && cartaNegra(j.getCartas().get(2))) {
            return 20;
        }
        
        if (cartaNegra(j.getCartas().get(0)) && cartaNegra(j.getCartas().get(1)) ) {
            return j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(2).getValor().getValorEnvido() + 10;
        }
        if (cartaNegra(j.getCartas().get(0)) && cartaNegra(j.getCartas().get(2)) ) {
            return j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(1).getValor().getValorEnvido() + 10;
        }
        if (cartaNegra(j.getCartas().get(1)) && cartaNegra(j.getCartas().get(2)) ) {
            return j.getCartas().get(1).getValor().getValorEnvido() + j.getCartas().get(0).getValor().getValorEnvido() + 10;
        }
        
        if (cartaNegra(j.getCartas().get(0)) )
            return j.getCartas().get(1).getValor().getValorEnvido() + j.getCartas().get(2).getValor().getValorEnvido() + 20;
        if (cartaNegra(j.getCartas().get(1)) )
            return j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(2).getValor().getValorEnvido() + 20;
        if (cartaNegra(j.getCartas().get(2)) )
            return j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(1).getValor().getValorEnvido() + 20;
        
        combUno  = j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(1).getValor().getValorEnvido() + 20;
        combDos  = j.getCartas().get(0).getValor().getValorEnvido() + j.getCartas().get(2).getValor().getValorEnvido() + 20;
        combTres = j.getCartas().get(1).getValor().getValorEnvido() + j.getCartas().get(2).getValor().getValorEnvido() + 20;
        
        if (combUno >= combDos && combUno >= combTres) {
            return combUno;
        } else if (combDos >= combUno && combDos >= combTres) {
            return combDos;
        } else {
            return combTres;
        }

    }
    
    /**
     * Metodo que recibe todas las cartas de j y las agrega al mazo nuevamente
     */
    public void recibirCartasJugador(Jugador j) {
        if (j.getCartas() != null) {
            System.out.println(j.getNombre() + " esta entregando sus cartas");
            mazoCartas.addAll( j.entregarCargas() );
        }
        //mazoCartas.addAll(Arrays.asList( jugadorDos.entregarCargas() ));
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

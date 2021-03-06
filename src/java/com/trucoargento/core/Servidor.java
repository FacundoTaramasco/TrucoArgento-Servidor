package com.trucoargento.core;

import com.trucoargento.excepciones.ExcepcionEnvido;
import com.trucoargento.modelo.EnumEnvido;
import com.trucoargento.modelo.Jugador;
import com.trucoargento.modelo.Truco;
import java.io.IOException;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.trucoargento.util.Utileria;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FacundoTaramasco
 */
@ServerEndpoint("/TrucoArgento")
public class Servidor {
    
    private static final Logger LOGGER = Logger.getLogger(Servidor.class.getName());
    private final Truco t;

    public Servidor() {
        t = Truco.getInstance();
    }

    /** #########################################################################  **/
    /** #########################################################################  **/
    
    @OnOpen
    public void onOpen(Session s) {
        // se conecto jugador uno
        if (t.getJugadorUno().getSesion() == null) {
            this.jugadorUnoConectado(s);
        // se conecto jugador dos
        } else if (t.getJugadorDos().getSesion() == null) {
            this.jugadorDosConectado(s);
        } else {
            LOGGER.info("No se aceptan mas jugadores!");
        }
    }

    @OnMessage
    public void onMessage(String mensaje, Session s) {
        try (JsonReader reader = Json.createReader(new StringReader(mensaje))) {
            LOGGER.log(Level.INFO, "RECIBIENDO : {0}", mensaje);
            JsonObject mensajeJson = reader.readObject();
            
            // mensaje de jugador uno
            if (t.getJugadorUno().getSesion() == s) {
                switch (mensajeJson.getString("accion")) {
                    case "entraJuego":
                        // seteando nombre del jugador
                        t.getJugadorUno().setNombre( mensajeJson.getString("nombre") );
                        LOGGER.log(Level.INFO, "Jugador uno se llama : {0}", t.getJugadorUno().getNombre());
                        break;
                    case "cantoEnvido":
                        if (t.getJugadorUno() != t.getJugadorTurno()) {
                            // NO ES EL TURNO DE JUGADOR UNO!
                        }
                        int intEnv = Integer.parseInt(mensajeJson.getString("tipo"));
                        this.atenderCantoEnvido(t.getJugadorUno(), t.getJugadorDos(), intEnv);
                        break;
                    case "envidoAceptado":
                        this.mensajeAjugador(t.getJugadorUno(), t.resultadoJsonEnvido().get("jugadorUno").toString());
                        this.mensajeAjugador(t.getJugadorDos(), t.resultadoJsonEnvido().get("jugadorDos").toString());
                        break;
                    case "irseMazo":
                        LOGGER.info("Jugador uno se fue al mazo");
                        this.jugadoresDevuelvenCartas();
                        this.darCartasJugadores();
                        t.cambiarMano();
                        this.avisarInicioMano(t.getJugadorUno());
                        this.avisarInicioMano(t.getJugadorDos());
                        break;
                    default:
                        break;
                }
            }

            // mensaje de jugador dos
            if (t.getJugadorDos().getSesion() == s) {
                switch (mensajeJson.getString("accion")) {
                    case "entraJuego":
                        // seteando nombre del jugador
                        t.getJugadorDos().setNombre( mensajeJson.getString("nombre") );
                        LOGGER.log(Level.INFO, "Jugador dos se llama : {0}", t.getJugadorDos().getNombre());
                        // se inicia la partida
                        this.comenzarPartida();
                        break;
                    case "cantoEnvido":
                        int intEnv     = Integer.parseInt(mensajeJson.getString("tipo"));
                        this.atenderCantoEnvido(t.getJugadorDos(), t.getJugadorUno(), intEnv);
                        break;
                    case "envidoAceptado":
                        this.mensajeAjugador(t.getJugadorUno(), t.resultadoJsonEnvido().get("jugadorUno").toString());
                        this.mensajeAjugador(t.getJugadorDos(), t.resultadoJsonEnvido().get("jugadorDos").toString());
                        break;
                    case "irseMazo":
                        LOGGER.info("Jugador dos se fue al mazo");
                        this.jugadoresDevuelvenCartas();
                        this.darCartasJugadores();
                        t.cambiarMano();
                        this.avisarInicioMano(t.getJugadorUno());
                        this.avisarInicioMano(t.getJugadorDos());
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    @OnClose
    public void close(Session s) {
        if (t.getJugadorUno().getSesion() == s) {
            LOGGER.info("Jugador uno salio");
            t.getJugadorUno().setSesion(null);
            this.avisoRelogPagina(t.getJugadorDos());
        }
        if (t.getJugadorDos().getSesion() == s) {
            LOGGER.info("Jugados dos salio");
            t.getJugadorDos().setSesion(null);
            this.avisoRelogPagina(t.getJugadorUno());
            
        }
        // OJO QUE SE LLAMA DOS VECES POR EL RELOG
        this.jugadoresDevuelvenCartas();
    }

    @OnError
    public void onError(Throwable error) {
        LOGGER.log(Level.INFO, "ERROR {0}", error);

    }
     
    /** #########################################################################  **/
    /** #########################################################################  **/
    
    /*
    Metodo invocado al momento de conectarse el jugador uno
    */
    private void jugadorUnoConectado(Session s) {
        t.getJugadorUno().setSesion(s);
        LOGGER.info("Entro jugador uno");
        this.mensajeEsperaJugadorUno();
    }
    
    /*
    cuando se conecta el jugador uno se le envia un mensaje indicando
    la espera por la conexion del jugador dos
    */
    private void mensajeEsperaJugadorUno() {
        JsonProvider provider = JsonProvider.provider();
        JsonObject mensajeJson = provider.createObjectBuilder()
            .add("accion", "espera")
            .add("mensaje", "esperando jugador Dos")
            .build();
        mensajeAjugador(t.getJugadorUno(), mensajeJson.toString());
    }
    
    /*
    Metodo invocado al momento de conectarse el jugador dos
    */
    private void jugadorDosConectado(Session s) {
        t.getJugadorDos().setSesion(s);
        LOGGER.info("Entro jugador dos");
    }
    
    /*
    Metodo invocado cuando se encuentran conectados los dos jugadores y se da 
    inicio a la partida
    */
    private void comenzarPartida() {
        t.turnoPrimeraMano();
        this.darCartasJugadores();
        t.reiniciarEnvido();
        
        // ya entregadas las cartas a los jugadores (servidor/modelo) se le 
        // envian dichas cartas a cada jugador (cliente) en formato json
        mensajeInicial(t.getJugadorUno());
        mensajeInicial(t.getJugadorDos());
    }
    
    /**
     * Metodo que le envia mensaje json al jugador especificado indicando
     * que el juego comenzara y etc.
     * @param j 
     */
    private void mensajeInicial(Jugador j) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject mensajeJson = provider.createObjectBuilder()
            .add("accion",        "a_jugar")
            .add("href",          "pantallaJuego.html")
            .add("tuTurno",       t.getJugadorTurno() == j)
            .add("nombreJugador", j.getNombre())
            .add("nombreTurno",   t.getJugadorTurno().getNombre())
            .add("cartas",        Utileria.cartasToJson(j))
            .build();
        mensajeAjugador(j, mensajeJson.toString());
    }
    
    /*
    Metodo que le avisa al jugador el comienzo de la mano
    */
    private void avisarInicioMano(Jugador j) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject mensajeJson = provider.createObjectBuilder()
            .add("accion",      "iniciaMano")
            .add("tuTurno",     t.getJugadorTurno() == j)
            .add("nombreTurno", t.getJugadorTurno().getNombre())
            .add("cartas",      Utileria.cartasToJson(j))
            .build();
        mensajeAjugador(j, mensajeJson.toString());
    }
   
    /*
    Se le entregan las cartas a los jugadores (servidor/modelo)
    */
    private void darCartasJugadores() {
        t.darCartasJugador(t.getJugadorUno());
        t.darCartasJugador(t.getJugadorDos());
        LOGGER.info("Cartas entregadas a los jugadores");
    }
    
    /*
    Todos los jugadores devuelven las cartas y se ponen en el mazo
    */
    private void jugadoresDevuelvenCartas() {
        t.recibirCartasJugador( t.getJugadorUno() );
        t.recibirCartasJugador( t.getJugadorDos() );
        LOGGER.info("Todos los jugadores devolvieron las cartas");
    }
   
    
    private void atenderCantoEnvido(Jugador origen, Jugador destino, int tipoEnvido) {
        EnumEnvido env = null;
         switch(tipoEnvido) {
             case 1:
                 env = EnumEnvido.ENVIDO;
                 break;
             case 2:
                 env = EnumEnvido.ENVIDO_ENVIDO;
                 break;
             case 3:
                 env = EnumEnvido.REAL_ENVIDO;
                 break;
             case 4:
                 env = EnumEnvido.FALTA_ENVIDO;
         }  
         try {
            t.jugadorCantaEnvido(env);
            LOGGER.info("Jugador: " + origen.getNombre() + " canta: " + env);
             t.cambiarTurno();
             LOGGER.info("Se cambio de turno");
             this.avisoCambioTurno(t.getJugadorUno());
             this.avisoCambioTurno(t.getJugadorDos());
             avisoInfoEnvido("infoEnvido", origen, "Esperando respuesta de: " + destino.getNombre());
             
             avisoInfoEnvido("logEnvido", origen, "Vos cantaste " + env);
             avisoInfoEnvido("logEnvido", destino, "El canto " + env);
             avisoJugadorCantoEnvido(origen, destino, tipoEnvido, env);
         } catch(ExcepcionEnvido ee) {
             avisoInfoEnvido("errorEnvido", origen, ee.getMessage());
         }
    }
    
    
    /**
     * Metodo que le envia un mensaje json al jugador indicando que el otro jugador
     * canto envido.
     * @param origen
     * @param destino 
     */
    private void avisoJugadorCantoEnvido(Jugador origen, Jugador destino, int intEnv, EnumEnvido env) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgJ       = provider.createObjectBuilder()
            .add("accion",  "cantoEnvido")
            .add("tipoEnvido", intEnv)
            .add("mensaje", origen.getNombre() + " canto "+ env.toString().toLowerCase()  +", ¿aceptas?")
            .build();
        mensajeAjugador(destino, msgJ.toString());
    }
    
    private void avisoInfoEnvido(String accion, Jugador j, String s) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgJ       = provider.createObjectBuilder()
            .add("accion",  accion)
            .add("mensaje", s)
            .build();
        mensajeAjugador(j, msgJ.toString());
    }
    
    private void avisoCambioTurno(Jugador j) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgJ       = provider.createObjectBuilder()
            .add("accion",      "cambioTurno")
            .add("nombreTurno", t.getJugadorTurno().getNombre())
            .add("tuTurno", t.getJugadorTurno() == j)
            .build();
        mensajeAjugador(j, msgJ.toString());
        //mensajeAjugador(t.getJugadorDos(), msgJ.toString());
    }
    private void avisoRelogPagina(Jugador j) {
        if (j.getSesion() == null) return;
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgJ       = provider.createObjectBuilder()
            .add("accion", "relog")
            .build();
        mensajeAjugador(j, msgJ.toString());
    }
 

    /**
     * Metodo que le envia un mensaje en formato json a un jugador
     * @param j
     * @param msgJson 
     */
    private void mensajeAjugador(Jugador j, String msgJson) {
        try {
           j.getSesion().getBasicRemote().sendText(msgJson);
            LOGGER.log(Level.INFO, "ENVIANDO : {0}", msgJson);
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

package com.trucoargento.core;

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
        //System.out.println("Iniciando servidor...");
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
            //this.jugadoresConectados();
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
                if (mensajeJson.getString("accion").equals("entraJuego")) {
                    // seteando nombre del jugador
                    t.getJugadorUno().setNombre( mensajeJson.getString("nombre") );
                    LOGGER.log(Level.INFO, "jugador uno se llama : {0}", mensajeJson.getString("nombre"));
                }
                if (mensajeJson.getString("accion").equals("cantoEnvido")) {
                    LOGGER.info("jugador uno canta envido!");
                    t.cambiarTurno();
                    avisoJugadorCantoEnvido(t.getJugadorUno(), t.getJugadorDos());
                }
                if (mensajeJson.getString("accion").equals("envidoAceptado")) {
                    this.atenderEnvidoAceptado();
                }
                
            }

            // mensaje de jugador dos
            if (t.getJugadorDos().getSesion() == s) {
                if (mensajeJson.getString("accion").equals("entraJuego")) {
                    // seteando nombre del jugador
                    t.getJugadorDos().setNombre( mensajeJson.getString("nombre") );
                    LOGGER.log(Level.INFO, "jugador dos se llama : {0}", mensajeJson.getString("nombre"));
                    
                    this.jugadoresConectados();
                }
                if (mensajeJson.getString("accion").equals("cantoEnvido")) {
                    LOGGER.info("jugador dos canta envido!");
                    t.cambiarTurno();
                    avisoJugadorCantoEnvido(t.getJugadorDos(), t.getJugadorUno());
                }
                if (mensajeJson.getString("accion").equals("envidoAceptado")) {
                    this.atenderEnvidoAceptado();
                }
            }
        }
    }
    
    @OnClose
    public void close(Session s) {
        if (t.getJugadorUno().getSesion() == s) {
            LOGGER.info("Jugador uno salio");
            t.getJugadorUno().setSesion(null);
        }
        if (t.getJugadorDos().getSesion() == s) {
            LOGGER.info("Jugados dos salio");
            t.getJugadorDos().setSesion(null);
        }
        // todos los jugadores devuelven sus cartas
        t.recibirCartasJugador( t.getJugadorUno() );
        t.recibirCartasJugador( t.getJugadorDos() );
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
    se le entregan las cartas a los jugadores (servidor/modelo)
    */
    private void darCartasJugadores() {
        t.darCartasJugador(t.getJugadorUno());
        t.darCartasJugador(t.getJugadorDos());
        LOGGER.info("Cartas entregadas a los jugadores");
    }
    
    /*
    Metodo invocado cuando se encuentran conectados los dos jugadores
    */
    private void jugadoresConectados() {
        this.darCartasJugadores();
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
            .add("accion", "a_jugar")
            .add("href", "pantallaJuego.html")
            .add("tuTurno", t.getJugadorTurno() == j)
            .add("nombreJugador", j.getNombre())
            .add("nombreTurno", t.getJugadorTurno().getNombre())
            .add("cartas", Utileria.cartasToJson(j))
            //.add("mensaje", "el juego comenzara!")
            .build();
        mensajeAjugador(j, mensajeJson.toString());
    }
    
    /**
     * Metodo que le envia un mensaje json al jugador indicando que el otro jugador
     * canto envido.
     * @param origen
     * @param destino 
     */
    private void avisoJugadorCantoEnvido(Jugador origen, Jugador destino) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msgJ       = provider.createObjectBuilder()
            .add("accion", "cantoEnvido")
            .add("mensaje", origen.getNombre() + " canto envido, ¿aceptas?")
            .add("tuTurno", t.getJugadorTurno() == destino) // irrelevante, debe ser siempre true esto
            .add("nombreTurno", t.getJugadorTurno().getNombre()) // lo mismo... es destino.getNombre()
            .build();
        mensajeAjugador(destino, msgJ.toString());
    }
    
    /**
     * Metodo que le envia mensajes a los dos jugadores informando el resultado
     * del envido.
     */
    private void atenderEnvidoAceptado() {
        int envidoJugadorUno = t.determinarEnvidoJugadorD( t.getJugadorUno() );
        int envidoJugadorDos = t.determinarEnvidoJugadorD( t.getJugadorDos() );

        if (envidoJugadorUno > envidoJugadorDos) {
            mensajeAjugador(t.getJugadorUno(), jsonResultadoEnvidoJugador(true, t.getJugadorDos(), envidoJugadorUno, envidoJugadorDos).toString() );
            mensajeAjugador(t.getJugadorDos(), jsonResultadoEnvidoJugador(false, t.getJugadorUno(), envidoJugadorDos, envidoJugadorUno).toString() );
        } else if(envidoJugadorDos > envidoJugadorUno) {
            mensajeAjugador(t.getJugadorUno(), jsonResultadoEnvidoJugador(false, t.getJugadorDos(), envidoJugadorUno, envidoJugadorDos).toString() );
            mensajeAjugador(t.getJugadorDos(), jsonResultadoEnvidoJugador(true, t.getJugadorUno(), envidoJugadorDos, envidoJugadorUno).toString() );
        } else { // envidos iguales
            if (t.getJugadorMano() == t.getJugadorUno()) { // es mano jugador uno
                // gano jugador uno
                mensajeAjugador(t.getJugadorUno(), jsonResultadoEnvidoJugador(true, t.getJugadorDos(), envidoJugadorUno, envidoJugadorDos).toString() );
                mensajeAjugador(t.getJugadorDos(), jsonResultadoEnvidoJugador(false, t.getJugadorUno(), envidoJugadorDos, envidoJugadorUno).toString() );                 
            } else {
                // gano jugador dos
                mensajeAjugador(t.getJugadorUno(), jsonResultadoEnvidoJugador(false, t.getJugadorDos(), envidoJugadorUno, envidoJugadorDos).toString() );
                mensajeAjugador(t.getJugadorDos(), jsonResultadoEnvidoJugador(true, t.getJugadorUno(), envidoJugadorDos, envidoJugadorUno).toString() );
            }
        }
    }
    
    /**
     * Metodo que retorna un json con informacion del resultado del envido.
     * @param resultado
     * @param elOtroJugador
     * @param envidoVos
     * @param envidoEl
     * @return 
     */
    private JsonObject jsonResultadoEnvidoJugador(boolean resultado, Jugador elOtroJugador, int envidoVos, int envidoEl) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject json       = provider.createObjectBuilder()
            .add("accion", "determinarEnvido")
            .add("resultado", msgGenericoEnvido( (resultado ? "Ganaste" : "Perdiste"), elOtroJugador, envidoVos, envidoEl) )
            .build();
        return json;
    }
    
    /**
     * Metodo de utileria que formatea y retorna un String que representa un
     * mensaje para un jugador.
     * @param resultado
     * @param elOtro
     * @param envidoJ1
     * @param envidoJ2
     * @return 
     */
    private String msgGenericoEnvido(String resultado, Jugador elOtro, int envidoJ1, int envidoJ2) {
        return resultado + " envido con: " + envidoJ1 + ", " + elOtro.getNombre() + " tenia: " + envidoJ2;
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

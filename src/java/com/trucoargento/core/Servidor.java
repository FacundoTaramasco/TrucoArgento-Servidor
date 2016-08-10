/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

import com.fasterxml.jackson.core.JsonProcessingException;
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
            this.jugadoresConectados();
            
        } else {
            LOGGER.info("No se aceptan mas jugadores!");
        }
    }

    @OnMessage
    public void onMessage(String mensaje, Session s) {
        try (JsonReader reader = Json.createReader(new StringReader(mensaje))) {
            JsonObject mensajeJson = reader.readObject();

            // mensaje de jugador uno
            if (t.getJugadorUno().getSesion() == s) {
                if (mensajeJson.getString("accion").equals("entraJuego")) {
                    // seteando nombre del jugador
                    t.getJugadorUno().setNombre( mensajeJson.getString("nombre") );
                    LOGGER.log(Level.INFO, "jugador uno se llama : {0}", mensajeJson.getString("nombre"));
                }
                
                if (mensajeJson.getString("accion").equals("cantoEnvido")) {
                    LOGGER.info("jugador dos canta envido!");
                }

            }

            // mensaje de jugador dos
            if (t.getJugadorDos().getSesion() == s) {
                if (mensajeJson.getString("accion").equals("entraJuego")) {
                    // seteando nombre del jugador
                    t.getJugadorDos().setNombre( mensajeJson.getString("nombre") );
                    LOGGER.log(Level.INFO, "jugador dos se llama : {0}", mensajeJson.getString("nombre"));
                }
                
                if (mensajeJson.getString("accion").equals("cantoEnvido")) {
                    LOGGER.info("jugador dos canta envido!");
                    
                    JsonProvider provider = JsonProvider.provider();
                    JsonObject msgJ       = provider.createObjectBuilder()
                        .add("accion", "cantoEnvido")
                        .add("mensaje", "jugador dos canto envido, ¿aceptas?")
                        .build();
                    mensajeAjugador(t.getJugadorUno(), msgJ.toString());
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
    se le entregan las cartas a los jugadores (servidor)
    */
    private void darCartasJugadores() {
        t.darCartasJugador(t.getJugadorUno());
        t.darCartasJugador(t.getJugadorDos());
    }
    
    /*
    Metodo invocado cuando se encuentran conectados los dos jugadores
    */
    private void jugadoresConectados() {
        this.darCartasJugadores();

        // ya entregadas las cartas a los jugadores (servidor) se le 
        // envia dichas cartas a cada jugador (cliente) en formato json

        JsonProvider provider = JsonProvider.provider();
        JsonObject mensajeJsonJ1 = provider.createObjectBuilder()
            .add("accion", "a_jugar")
            .add("href", "pantallaJuego.html")
            .add("cartas", Utileria.cartasToJson(t.getJugadorUno()))
            //.add("mensaje", "el juego comenzara!")
            .build();

        JsonObject mensajeJsonJ2 = provider.createObjectBuilder()
            .add("accion", "a_jugar")
            .add("href", "pantallaJuego.html")
            .add("cartas", Utileria.cartasToJson(t.getJugadorDos()))
            //.add("mensaje", "el juego comenzara!")
            .build();

        mensajeAjugador(t.getJugadorUno(), mensajeJsonJ1.toString());
        mensajeAjugador(t.getJugadorDos(), mensajeJsonJ2.toString());
        //t.getJugadorUno().getSesion().getBasicRemote().sendText(mensajeJsonJ1.toString());
        //t.getJugadorDos().getSesion().getBasicRemote().sendText(mensajeJsonJ2.toString());

    }
    
    private void mensajeAjugador(Jugador j, String msgJson) {
        try {
           j.getSesion().getBasicRemote().sendText(msgJson); 
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
}

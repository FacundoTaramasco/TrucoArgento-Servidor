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
import java.math.BigDecimal;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trucoargento.modelo.Carta;
import com.trucoargento.modelo.NumeroCarta;
import com.trucoargento.modelo.Palos;
import com.trucoargento.util.Utileria;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FacundoTaramasco
 */
@ServerEndpoint("/TrucoArgento")
public class Servidor {
    
    private Truco t;
  
    public Servidor() {
        //System.out.println("Iniciando servidor...");
        t = Truco.getInstance();

    }

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
            System.out.println("No se aceptan mas jugadores!");
        }
    }

    @OnMessage
    public String onMessage(String mensaje, Session s) {
        
        // mensaje de jugador uno
        if (t.getJugadorUno().getSesion() == s) {
            
            JsonReader reader = Json.createReader(new StringReader(mensaje));
            JsonObject mensajeJson = reader.readObject();
            if (mensajeJson.getString("accion").equals("entraJuego")) {
                // seteando nombre del jugador
                t.getJugadorUno().setNombre( mensajeJson.getString("nombre") );
                System.out.println("jugador uno se llama : " +  mensajeJson.getString("nombre"));
            }

        }
        
        // mensaje de jugador dos
        if (t.getJugadorDos().getSesion() == s) {
            JsonReader reader = Json.createReader(new StringReader(mensaje));
            JsonObject mensajeJson = reader.readObject();
            if (mensajeJson.getString("accion").equals("entraJuego")) {
                // seteando nombre del jugador
                t.getJugadorDos().setNombre( mensajeJson.getString("nombre") );
                System.out.println("jugador dos se llama : " +  mensajeJson.getString("nombre"));
            }
        }
        return "";
    }
    
    @OnClose
    public void close(Session s) {
        if (t.getJugadorUno().getSesion() == s) {
            System.out.println("Jugador uno salio");
            t.getJugadorUno().setSesion(null);
        }
        if (t.getJugadorDos().getSesion() == s) {
            System.out.println("Jugador dos salio");
            t.getJugadorDos().setSesion(null);
        }
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("ERROR " + error);
    }
     
    
    /*
    Metodo invocado al momento de conectarse el jugador uno
    */
    private void jugadorUnoConectado(Session s) {
        t.getJugadorUno().setSesion(s);
        System.out.println("Entro jugador uno");
        this.mensajeEsperaJugadorUno();
    }
    
    /*
    cuando se conecta el jugador uno se le envia un mensaje indicando
    la espera por la conexion del jugador dos
    */
    private void mensajeEsperaJugadorUno() {
        try {                
            JsonProvider provider = JsonProvider.provider();
            JsonObject mensajeJson = provider.createObjectBuilder()
                .add("accion", "espera")
                .add("mensaje", "esperando jugador Dos")
                .build();
            t.getJugadorUno().getSesion().getBasicRemote().sendText(mensajeJson.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
    Metodo invocado al momento de conectarse el jugador dos
    */
    private void jugadorDosConectado(Session s) {
        t.getJugadorDos().setSesion(s);
        System.out.println("Entro jugador dos");
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
        try {
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

            t.getJugadorUno().getSesion().getBasicRemote().sendText(mensajeJsonJ1.toString());
            t.getJugadorDos().getSesion().getBasicRemote().sendText(mensajeJsonJ2.toString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}

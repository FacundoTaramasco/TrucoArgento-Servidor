/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

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
            t.getJugadorUno().setSesion(s);
            System.out.println("Entro jugador uno");
            try {                
                JsonProvider provider = JsonProvider.provider();
                JsonObject mensajeJson = provider.createObjectBuilder()
                    .add("accion", "espera")
                    .add("mensaje", "esperando jugador Dos")
                    .build();
                s.getBasicRemote().sendText(mensajeJson.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        // se conecto jugador dos
        } else if (t.getJugadorDos().getSesion() == null) {
            t.getJugadorDos().setSesion(s);
            System.out.println("Entro jugador dos");
            try {
                JsonProvider provider = JsonProvider.provider();
                JsonObject mensajeJson = provider.createObjectBuilder()
                    .add("accion", "a_jugar")
                    .add("href", "pantallaJuego.html")
                    //.add("mensaje", "el juego comenzara!")
                    .build();

                t.getJugadorUno().getSesion().getBasicRemote().sendText(mensajeJson.toString());
                t.getJugadorDos().getSesion().getBasicRemote().sendText(mensajeJson.toString());
            } catch(IOException e) {
                e.printStackTrace();
            }

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
}

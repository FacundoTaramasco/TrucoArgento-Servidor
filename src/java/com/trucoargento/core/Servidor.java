/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

import com.trucoargento.modelo.Jugador;
import com.trucoargento.modelo.Truco;
import java.io.IOException;
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
                s.getBasicRemote().sendText("Esperando al jugador dos...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        // se conecto jugador dos
        } else if (t.getJugadorDos().getSesion() == null) {
            t.getJugadorDos().setSesion(s);
            System.out.println("Entro jugador dos");
            try {
                t.getJugadorUno().getSesion().getBasicRemote().sendText("El juego comenzará");
                t.getJugadorDos().getSesion().getBasicRemote().sendText("El juego comenzará");
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
        if (t.getJugadorUno().getSesion() != null && t.getJugadorUno().getSesion() == s) {
            
        }
        
        // mensaje de jugador dos
        if (t.getJugadorDos().getSesion() != null && t.getJugadorDos().getSesion() == s) {
            
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

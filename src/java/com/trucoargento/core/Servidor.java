/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

import com.trucoargento.modelo.Truco;
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


       
  
    public Servidor() {
        //System.out.println("Iniciando servidor...");
        Truco t = Truco.getInstance();
        //Truco t = new Truco();
        
        //t.jugar();
    }
    
    @OnOpen
    public void onOpen(Session s) {
        System.out.println("sesion  : " + s + " connectada.");
        
        
    }

    @OnMessage
    public String onMessage(String mensaje, Session s) {
        System.out.println("sesion : " + s + " envia : " + mensaje);
        return "recibido";
    }
    
    @OnClose
    public void close(Session session) {
        System.out.println(session + " cerrada");
    }

    @OnError
    public void onError(Throwable error) {
        System.out.println("ERROR " + error);
    }
}

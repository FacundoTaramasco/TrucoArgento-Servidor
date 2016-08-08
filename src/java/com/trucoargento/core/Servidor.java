/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

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

    @OnMessage
    public String onMessage(Session s, String mensaje) {
        System.out.println("sesion : " + s + " envia : " + mensaje);
        
        return "recibido";
    }
    
    @OnOpen
    public void onOpen(Session s) {
        System.out.println("sesion  : " + s + " connectada.");
    }
    
}

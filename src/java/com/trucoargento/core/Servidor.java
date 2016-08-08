/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trucoargento.core;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Usuario
 */
@ServerEndpoint("/TrucoArgento")
public class Servidor {

    @OnMessage
    public String onMessage(String message) {
        return null;
    }
    

    
}

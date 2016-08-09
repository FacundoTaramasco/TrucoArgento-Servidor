package com.trucoargento.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trucoargento.core.Servidor;
import com.trucoargento.modelo.Jugador;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FacundoTaramasco
 */
public class Utileria {
    
    public static String cartasToJson(Jugador j) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString( j.getCartas());
           return jsonInString;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}

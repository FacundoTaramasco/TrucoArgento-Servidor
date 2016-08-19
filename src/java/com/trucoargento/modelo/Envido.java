package com.trucoargento.modelo;

import com.trucoargento.excepciones.ExcepcionEnvido;
import java.util.LinkedList;

/**
 *
                                    Puntos si se quiere     Puntos si no se quiere
Envido                                          2               1
Real envido                                     3               1
Falta envido                                    *               1
Envido, envido                                  4               2
Envido, real envido                             5               2
Envido, falta envido                            *               2
Real Envido, falta envido                       *               3
Envido, envido, falta envido                    *               4
Envido, envido, real envido                     7               4
Envido, real envido, falta envido       	*               5
Envido, envido, real envido, falta envido	*               7

 * @author FacundoTaramasco
 */
public class Envido {
    
    private LinkedList<EnumEnvido> l;
    
    public Envido() {
        l = new LinkedList();
    }
    
    public void agregarEnvido(EnumEnvido ie) throws ExcepcionEnvido {
        if (l.size() == 4)
            throw new ExcepcionEnvido("No se puede cantar mas, existe un maximo de 4 cantos.");
        
        if (l.isEmpty()) {
            // no puede agregar ENVIDO_ENVIDO solo en primer lugar
            if (ie == EnumEnvido.ENVIDO_ENVIDO)
                throw new ExcepcionEnvido("No puede cantar envido envido, para eso debe cantar envido primero.");
        } else {
            // no se puede tener por ejemplo : real envido, envido
            if (l.getLast().getRango() > ie.getRango() )
                throw new ExcepcionEnvido("El Envido debe seguir un orden. Ej : envido, envido envido, real envido, falta envido.");
        }
        if (l.contains(ie))
            throw new ExcepcionEnvido(ie.toString() + " ya fue cantado.");
        l.add(ie);
    }
    
    public void reiniciarEnvido() {
        l.clear();
    }
    
    public int calcularPuntajeAceptado() {
        int valor = 0;
        for (EnumEnvido e : l) {
            valor += e.getValorEnvido();
        }
        return valor;
    }
    
    public int calcularPuntajeRechazado() {
        int valor = 0;
        if (l.isEmpty()) return valor;
        if (l.size() == 1) {
            if (l.getFirst() == EnumEnvido.ENVIDO || 
                l.getFirst() == EnumEnvido.REAL_ENVIDO || 
                l.getFirst() == EnumEnvido.FALTA_ENVIDO)
                valor = 1;
        } else if (l.size() == 2) {
            if ( (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.ENVIDO_ENVIDO) ||
                 (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.REAL_ENVIDO)   ||   
                 (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.FALTA_ENVIDO) )
                valor = 2;
            if (l.get(0) == EnumEnvido.REAL_ENVIDO && l.get(1) == EnumEnvido.FALTA_ENVIDO) 
                valor = 3;   
        } else if (l.size() == 3) {
            if ( (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.ENVIDO_ENVIDO && l.get(2) == EnumEnvido.REAL_ENVIDO) ||
                 (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.ENVIDO_ENVIDO && l.get(2) == EnumEnvido.FALTA_ENVIDO) )
                valor = 4;
            if (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.REAL_ENVIDO && l.get(2) == EnumEnvido.FALTA_ENVIDO)
                valor = 5;
        } else if (l.size() == 4) {
            if (l.get(0) == EnumEnvido.ENVIDO && l.get(1) == EnumEnvido.ENVIDO_ENVIDO && 
                l.get(2) == EnumEnvido.REAL_ENVIDO && l.get(3) == EnumEnvido.FALTA_ENVIDO )
                valor = 7;
        }
        return valor;
    }
}

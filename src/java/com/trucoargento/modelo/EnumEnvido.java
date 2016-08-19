package com.trucoargento.modelo;

/**
 *
 * @author FacundoTaramasco
 */
public enum EnumEnvido {

    ENVIDO         (1, 2),
    ENVIDO_ENVIDO  (2, 2),
    REAL_ENVIDO    (3, 3),
    FALTA_ENVIDO   (4, 30);
    
    private final int rango;
    private final int valorEnvido;
    
    private EnumEnvido(int r, int valor) {
        this.rango = r;
        this.valorEnvido = valor;
    }

    public int getRango() {
        return rango;
    }

    public int getValorEnvido() {
        return valorEnvido;
    }
    
}

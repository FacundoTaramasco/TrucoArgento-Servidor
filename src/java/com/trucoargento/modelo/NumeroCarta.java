package com.trucoargento.modelo;

/**
 *
 * @author FacundoTaramasco
 */
public enum NumeroCarta {
    
    UNO    (1, 1),
    DOS    (2, 2),
    TRES   (3, 3),
    CUATRO (4, 4),
    CINCO  (5, 5),
    SEIS   (6, 6),
    SIETE  (7, 7),
    DIEZ   (10, 10),
    ONCE   (11, 10),
    DOCE   (12, 10);

    private final int valor;
    private final int valorEnvido;

    // Constructor
    NumeroCarta(int valor, int valorEnvido) {
        this.valor = valor;
        this.valorEnvido = valorEnvido;
    }

    // Getter
    public int getValor() {
        return valor;
    }
    
    public int getValorEnvido() {
        return valorEnvido;
    }
}

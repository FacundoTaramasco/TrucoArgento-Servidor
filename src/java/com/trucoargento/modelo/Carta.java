package com.trucoargento.modelo;

/**
 *
 * @author FacundoTaramasco
 */
public class Carta {
    
    private Palos palo;
    private NumeroCarta valor;

    public Carta() {
        this(null, null);
    }

    // Constructor
    public Carta(Palos palo, NumeroCarta valor) {
        this.palo = palo;
        this.valor = valor;
    }

    // Getters
    public Palos getPalo() { return palo; }
    public NumeroCarta getValor() { return valor; }

    // Setters
    public void setPalo(Palos palo) { this.palo = palo; }

    public void setValor(NumeroCarta valor) { this.valor = valor; }

    // Customs

    @Override
    public String toString() {
        return "Carta{" +
                "palo=" + palo +
                ", valorStr=" + valor +
                ", valor=" + valor.getValor() +
                '}';
    }
}

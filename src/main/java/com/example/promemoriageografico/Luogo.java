/*
 *  Classe che rappresenta un luogo
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *      -getter e setter;
 */
package com.example.promemoriageografico;

public class Luogo {
    private String nome;
    private String descrizione;
    private double latitudine;
    private double longitudine;
    private double raggioNotifica;
    private int id;

    public Luogo(String nome, String descrizione, double latitudine, double longitudine, int id, double raggioNotifica){
        this.nome = nome;
        this.descrizione = descrizione;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.id = id;
        this.raggioNotifica = raggioNotifica;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getRaggioNotifica() {
        return raggioNotifica;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setRaggioNotifica(double raggioNotifica) {
        this.raggioNotifica = raggioNotifica;
    }
}

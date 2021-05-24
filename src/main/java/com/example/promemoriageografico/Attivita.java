/*
 *  Classe che rappresenta un'attività
 *  Autore: Ferola giovanni
 *
 *   indice:
 *       -costruttore;
 *      -getter e setter;
 */
package com.example.promemoriageografico;

public class Attivita {
    int id;
    String nome;
    String descrizione;
    boolean fatto;

    public Attivita(String nome, String descrizione, int id, boolean fatto){
        this.nome = nome;
        this.descrizione = descrizione;
        this.id = id;
        this.fatto = fatto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setFatto(boolean fatto) {
        this.fatto = fatto;
    }

    public boolean isFatto() {
        return fatto;
    }
}

package br.edu.ifpr.tcc.atividade.frase;

import io.realm.RealmObject;

/**
 * Created by diego on 05/08/2016.
 */
public class Frase extends RealmObject {

    private int id;
    private String texto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}

package br.edu.ifpr.tcc.atividade.musica;

import java.io.File;

import io.realm.RealmObject;

/**
 * Created by Enzo on 05/08/2016.
 */
public class Musica extends RealmObject {

    private int id;
    private String caminho;
    private String nome;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}

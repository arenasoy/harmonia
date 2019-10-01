package br.edu.ifpr.tcc.atividade.imagem;

import android.media.Image;

import io.realm.RealmObject;

/**
 * Created by diego on 05/08/2016.
 */
public class Imagem extends RealmObject {

    private int id;
    private byte[] foto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}

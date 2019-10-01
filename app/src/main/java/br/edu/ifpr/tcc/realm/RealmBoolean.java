package br.edu.ifpr.tcc.realm;

import io.realm.RealmObject;

/**
 * Created by diego on 12/09/2016.
 */
public class RealmBoolean extends RealmObject{

    private boolean boleano;

    public boolean isBoleano() {
        return boleano;
    }

    public void setBoleano(boolean boleano) {
        this.boleano = boleano;
    }
}

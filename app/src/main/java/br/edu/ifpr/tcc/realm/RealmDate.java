package br.edu.ifpr.tcc.realm;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by diego on 14/06/2016.
 */
public class RealmDate extends RealmObject {

    private Date data;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}

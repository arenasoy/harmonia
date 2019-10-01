package br.edu.ifpr.tcc.remedio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.remedio.alarme.AlarmReceiver;
import br.edu.ifpr.tcc.remedio.views.HoraDoseForm;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Admin on 12/09/2016.
 */
public class HoraDose extends RealmObject {

    //TODO alarme de 1 minuto dar ruim --> aqui n

    @PrimaryKey
    private int id;

    private Date hora;
    private String dose;

    private boolean alarmeCriado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public boolean isAlarmeCriado() {
        return alarmeCriado;
    }

    public void setAlarmeCriado(boolean alarmeCriado) {
        this.alarmeCriado = alarmeCriado;
    }

}

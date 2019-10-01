package br.edu.ifpr.tcc.realm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.Trace;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.alarme.AlarmReceiver;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by diego on 07/11/2016.
 */

public class BootService extends IntentService {

    private Realm realm;

    public BootService() {
        super("BootService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        criarAlarmes();
        Intent service = new Intent(this, BootService.class);
        stopService(service);
    }

    public void criarAlarmes()
    {
        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));
        RealmResults<Remedio> remediosResults = realm.where(Remedio.class).equalTo("ligado", true).findAll();
        List<Remedio> remedios = new ArrayList<Remedio>();
        remedios.addAll(remediosResults);
        for (Remedio r:
             remedios) {
            if (r.isLigado())
            for (HoraDose h:
                 r.getHoraDoses()) {
                criarPrimeiroAlarme(h, r);
                Log.i("Criou horaDose", h.getId()+"");
            }
        }
    }

    private void criarPrimeiroAlarme(HoraDose horaDose, Remedio r)
    {
        AlarmManager alarm_manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        Calendar c = Calendar.getInstance();
        c.setTime(horaDose.getHora());

        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        c.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DAY_OF_MONTH, verificarValor(horaDose, r));

        realm.beginTransaction();
        horaDose.setHora(c.getTime());
        realm.commitTransaction();

            my_intent.putExtra("extra", "alarm on");
            my_intent.putExtra("idHoraDose", horaDose.getId());
            my_intent.putExtra("whale_choice", 0);

            PendingIntent pending_intent = PendingIntent.getBroadcast(getApplicationContext(), horaDose.getId(),
                    my_intent, PendingIntent.FLAG_ONE_SHOT);

            // set the alarm manager
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    pending_intent);

            Log.i("Criou pending intent", horaDose.getId()+"");

            realm.beginTransaction();
            horaDose.setAlarmeCriado(true);
            realm.commitTransaction();

    }

    private int verificarValor(HoraDose horaDose, Remedio remedio)
    {
        Calendar c = Calendar.getInstance();
        if (remedio.getIntervalo().equals("Uma vez")) {
            return 0;
        }
        else if(remedio.getIntervalo().equals("Diariamente"))
        {
            return 0;
        }
        else if (remedio.getIntervalo().equals("Semanalmente"))
        {
            int hoje = c.get(Calendar.DAY_OF_WEEK);

            Log.e("Hoje", hoje+"");
            if (remedio.getDiasSemana().get(hoje-1).isBoleano())
            {
                return 0;
            }

            if (hoje < 7 && remedio.getDiasSemana().get(hoje).isBoleano()) {
                return 1;
            }
            if ((hoje < 6 && remedio.getDiasSemana().get(hoje+1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(1).isBoleano()))
            {
                return 2;
            }
            if ((hoje < 5 && remedio.getDiasSemana().get(hoje+2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 1 && remedio.getDiasSemana().get(2).isBoleano()))
            {
                return 3;
            }
            if ((hoje < 4 && remedio.getDiasSemana().get(hoje+3).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(3).isBoleano()))
            {
                return 4;
            }
            if ((hoje < 3 && remedio.getDiasSemana().get(hoje+4).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(4).isBoleano()))
            {
                return 5;
            }
            if ((hoje < 2 && remedio.getDiasSemana().get(hoje+6).isBoleano())
                    || (hoje == 2 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(4).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(5).isBoleano()))
            {
                return 6;
            }


            return 0;


        }
        else if (remedio.getIntervalo().equals("Mensalmente"))
        {
            int inicio = c.get(Calendar.DAY_OF_MONTH);
            int soma = 0;

            if (remedio.getDiasMes().get(inicio+1).isBoleano())
                return 0;

            for (int i = inicio; i < 31; i++)
            {
                if (remedio.getDiasMes().get(i).isBoleano())
                {
                    inicio = Math.abs(i - c.get(Calendar.DAY_OF_MONTH));
                    return inicio+1;
                }
                else {
                    soma++;
                }
            }


            for (int i = 1; i < inicio; i++)
            {
                if (remedio.getDiasMes().get(i).isBoleano())
                {
                    inicio = soma + i;
                    return inicio;
                }
            }
            Log.e("Mensalmente qnts dias", inicio+"");
        }
        else if (remedio.getIntervalo().equals("Cada X Dias"))
        {
            return 0;
        }
        else if (remedio.getIntervalo().equals("Cada X Horas"))
        {
            return 0;
        }
        return 0;
    }

}
package br.edu.ifpr.tcc.remedio.alarme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import io.realm.Realm;

/**
 * Created by Admin on 26/09/2016.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    private int idHoraDose;
    private Realm realm;
    private HoraDose horaDose;
    private Remedio remedio;

    @Override
    public void onReceive(Context context, Intent intent) {

        String get_your_string = intent.getExtras().getString("extra");
        idHoraDose = intent.getExtras().getInt("idHoraDose");

        Integer get_your_whale_choice = intent.getExtras().getInt("whale_choice");

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        service_intent.putExtra("extra", get_your_string);

        service_intent.putExtra("whale_choice", get_your_whale_choice);
        service_intent.putExtra("idHoraDose", idHoraDose);

        // start the ringtone service
        context.startService(service_intent);


    }

}

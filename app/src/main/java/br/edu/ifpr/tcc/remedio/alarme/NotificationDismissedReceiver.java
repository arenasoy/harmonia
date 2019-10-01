package br.edu.ifpr.tcc.remedio.alarme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by diego on 22/11/2016.
 */

public class NotificationDismissedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int idHoraDose = intent.getExtras().getInt("idHoraDose");
        int idRemedio = intent.getExtras().getInt("idRemedio");

        String get_your_string = "alarm off";

        //create an intent to the ringtone service
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        service_intent.putExtra("extra", get_your_string);

        service_intent.putExtra("whale_choice", 0);
        service_intent.putExtra("idHoraDose", idHoraDose);
        service_intent.putExtra("idRemedio", idRemedio);

        // start the ringtone service
        context.startService(service_intent);
    }
}
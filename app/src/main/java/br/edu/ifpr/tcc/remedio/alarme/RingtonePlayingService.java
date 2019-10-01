package br.edu.ifpr.tcc.remedio.alarme;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import io.realm.Realm;

/**
 * Created by diego on 12/10/2016.
 */

public class RingtonePlayingService extends Service{

    private MediaPlayer media_song;
    private int startId;
    private boolean isRunning;

    private HoraDose horaDose;
    private Remedio remedio;

    private Realm realm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));

        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // fetch the extra string from the alarm on/alarm off values
        String state = intent.getExtras().getString("extra");
        int idHoraDose = intent.getExtras().getInt("idHoraDose");
        Integer whale_sound_choice = intent.getExtras().getInt("whale_choice");

        if (idHoraDose > 0) {
            horaDose = realm.where(HoraDose.class).equalTo("id", idHoraDose).findFirst();
            remedio = realm.where(Remedio.class).equalTo("horaDoses.id", horaDose.getId()).findFirst();
        }

        Log.e("Ringtone extra is ", state);
        Log.e("Whale choice is ", whale_sound_choice.toString());

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                if (media_song.isPlaying()) {
                    media_song.stop();
                    media_song.reset();
                    this.isRunning = false;
                    this.startId = 0;
                }
                realm.beginTransaction();
                criarAlarme(horaDose);
                realm.commitTransaction();
                break;
            default:
                startId = 0;
                break;
        }

        if (remedio != null && horaDose != null && remedio.isLigado()) {
            Calendar agora = Calendar.getInstance();
            agora.set(Calendar.SECOND, 0);
            Calendar horaTocar = Calendar.getInstance();
            horaTocar.setTime(horaDose.getHora());
            horaTocar.set(Calendar.SECOND, 0);
            if (startId == 1) {

                NotificationManager notify_manager = (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);

                Intent intent_tocando = new Intent(this.getApplicationContext(), AlarmeTocando.class);
                intent_tocando.putExtra("idHoraDose", idHoraDose);

                PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, idHoraDose,
                        intent_tocando, 0);

                Notification notification_popup = new Notification.Builder(this)
                        .setContentTitle("Hora do remédio!")
                        .setContentText(remedio.getNome() + ": " + horaDose.getDose())
                        .setSmallIcon(R.drawable.ic_local_pharmacy_black_24dp)
                        .setContentIntent(pending_intent_main_activity)
                        .setAutoCancel(true)
                        .setDeleteIntent(deletar())
                        .build();

                if (!this.isRunning && startId == 1) {
                    Log.e("there is no music, ", "and you want start");
                    Log.e("Agora:" + agora.get(Calendar.MINUTE), "Hora tocar:" + horaTocar.get(Calendar.MINUTE));
                    this.isRunning = true;
                    this.startId = 0;

                    notify_manager.notify(horaDose.getId(), notification_popup);

                    // play the whale sound depending on the passed whale choice id

                    media_song = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
                    media_song.start();

                    realm.beginTransaction();
                    horaDose.setAlarmeCriado(false);
                    realm.commitTransaction();
                } else if (this.isRunning && startId == 1) {

                } else {

                    media_song.stop();
                    media_song.reset();
                    this.isRunning = false;
                    this.startId = 0;

                }
            }
        }


        return START_NOT_STICKY;
    }


    public PendingIntent deletar()
    {
        Intent intent = new Intent(getApplicationContext(), NotificationDismissedReceiver.class);
        intent.putExtra("idHoraDose", horaDose.getId());
        intent.putExtra("idRemedio", remedio.getId());

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        horaDose.getId(), intent, 0);
        return pendingIntent;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Log.e("on Destroy called", "ta da");

        super.onDestroy();
        this.isRunning = false;
    }


    private void criarAlarme(HoraDose horaDose)
    {
        AlarmManager alarm_manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        Calendar c = Calendar.getInstance();
        c.setTime(horaDose.getHora());
        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        c.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        if (remedio.getIntervalo().equals("Uma vez")) {
            remedio.setLigado(false);
            horaDose.setAlarmeCriado(false);
        }
        else {

            if (remedio.getIntervalo().equals("Diariamente")) {
                c.add(Calendar.DAY_OF_MONTH, 1);
            } else if (remedio.getIntervalo().equals("Semanalmente")) {
                c.add(Calendar.DAY_OF_MONTH, verificarValor(horaDose));
            } else if (remedio.getIntervalo().equals("Mensalmente")) {
                c.add(Calendar.DAY_OF_MONTH, verificarValor(horaDose));
            } else if (remedio.getIntervalo().equals("Cada X Dias")) {
                c.add(Calendar.DAY_OF_MONTH, remedio.getRepetir());
            } else if (remedio.getIntervalo().equals("Cada X Horas")) {
                c.add(Calendar.HOUR_OF_DAY, remedio.getRepetirHoras());
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Log.e("Próximo alarme", simpleDateFormat.format(c.getTime()));
            horaDose.setHora(c.getTime());

            my_intent.putExtra("extra", "alarm on");
            my_intent.putExtra("idHoraDose", horaDose.getId());
            my_intent.putExtra("whale_choice", 0);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), horaDose.getId(),
                    my_intent, PendingIntent.FLAG_ONE_SHOT);

            // set the alarm manager
            alarm_manager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                    pendingIntent);

            horaDose.setAlarmeCriado(true);
        }

    }

    private int verificarValor(HoraDose horaDose)
    {
        Calendar c = Calendar.getInstance();
        if (remedio.getIntervalo().equals("Uma vez")) {
            remedio.setLigado(false);
        }
        else if(remedio.getIntervalo().equals("Diariamente"))
        {
            return 1;
        }
        else if (remedio.getIntervalo().equals("Semanalmente"))
        {
            int hoje = c.get(Calendar.DAY_OF_WEEK);

            Log.e("Hoje", hoje+"");

            if (hoje < 7 && remedio.getDiasSemana().get(hoje).isBoleano()) {
                return 1;
            }
            else if ((hoje < 6 && remedio.getDiasSemana().get(hoje+1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(1).isBoleano()))
            {
                return 2;
            }
            else if ((hoje < 5 && remedio.getDiasSemana().get(hoje+2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 1 && remedio.getDiasSemana().get(2).isBoleano()))
            {
                return 3;
            }
            else if ((hoje < 4 && remedio.getDiasSemana().get(hoje+3).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(3).isBoleano()))
            {
                return 4;
            }
            else if ((hoje < 3 && remedio.getDiasSemana().get(hoje+4).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(4).isBoleano()))
            {
                return 5;
            }
            else if ((hoje < 2 && remedio.getDiasSemana().get(hoje+6).isBoleano())
                    || (hoje == 2 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(4).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(5).isBoleano()))
            {
                return 6;
            }
            else if (remedio.getDiasSemana().get(hoje-1).isBoleano())
            {
                return 7;
            }

            return 0;


        }
        else if (remedio.getIntervalo().equals("Mensalmente"))
        {
            int inicio = c.get(Calendar.DAY_OF_MONTH);
            int soma = 0;

            for (int i = inicio+1; i < 31; i++)
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

            soma++;

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
            return remedio.getRepetir();
        }
        else if (remedio.getIntervalo().equals("Cada X Horas"))
        {
            return remedio.getRepetirHoras();
        }
        return 0;
    }
}

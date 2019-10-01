package br.edu.ifpr.tcc.remedio.alarme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import io.realm.Realm;

public class AlarmeTocando extends BaseDrawerActivity {

    private Realm realm;
    private HoraDose horaDose;
    private Remedio remedio;

    private TextView nomeRemedioTV;
    private TextView doseRemedioTV;
    private TextView horaRemedioTV;
    private ImageView fotoRemedioIV;

    private SimpleDateFormat sdf;

    private PendingIntent pendingIntent;

    //TODO ah num ta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_alarme_tocando, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Hora do Remédio!");

        mudarItem(R.id.gerenciar_remedios);

        sdf = new SimpleDateFormat("HH:mm");

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        nomeRemedioTV = (TextView) findViewById(R.id.nomeRemedioTV);
        doseRemedioTV = (TextView) findViewById(R.id.doseRemedioTV);
        horaRemedioTV = (TextView) findViewById(R.id.horaRemedioTV);
        fotoRemedioIV = (ImageView) findViewById(R.id.fotoRemedioIV);


        Bundle params = getIntent().getExtras();
        int idHoraDose = params.getInt("idHoraDose");

        horaDose = realm.where(HoraDose.class).equalTo("id", idHoraDose).findFirst();
        remedio = realm.where(Remedio.class).equalTo("horaDoses.id", idHoraDose).findFirst();

        nomeRemedioTV.setText("Remédio: " + remedio.getNome());
        if (horaDose.getDose() != null && !horaDose.getDose().equals("")) {
            doseRemedioTV.setText("Dose: " + horaDose.getDose());
            doseRemedioTV.setVisibility(View.VISIBLE);
        } else {
            doseRemedioTV.setVisibility(View.INVISIBLE);
        }
        String hora = sdf.format(horaDose.getHora());
        horaRemedioTV.setText("Horário: " +hora);

        mostrarFoto();
    }

    public void mostrarFoto()
    {
        if (remedio.getImagem() != null) {
            fotoRemedioIV.setImageBitmap(BitmapFactory.decodeStream(
                    new ByteArrayInputStream(remedio.getImagem().getFoto())));
        }
    }

    public void pararAlarme(View view)
    {
        Intent intent = new Intent(this, RingtonePlayingService.class);

        intent.putExtra("extra", "alarm off");
        startService(intent);
        stopService(intent);

        if (remedio != null)
        {
            realm.beginTransaction();
            criarAlarme(horaDose);
            realm.commitTransaction();
        }

        this.finish();
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

            PendingIntent.getBroadcast(getApplicationContext(), horaDose.getId(), getIntent(), PendingIntent.FLAG_ONE_SHOT).cancel();
            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), horaDose.getId(),
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

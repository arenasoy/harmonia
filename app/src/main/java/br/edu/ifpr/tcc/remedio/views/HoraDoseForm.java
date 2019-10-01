package br.edu.ifpr.tcc.remedio.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.alarme.AlarmReceiver;
import io.realm.Realm;

public class HoraDoseForm extends BaseDrawerActivity {

    private Realm realm;

    private Remedio remedio;
    private Date hora;
    private HoraDose horaDose;

    private TimePickerDialog timePickerDialog;
    private Calendar calendar;
    private TextView horaTV;
    private EditText doseTF;

    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_hora_dose_form, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de horários e doses");

        mudarItem(R.id.gerenciar_remedios);

        remedio = ((Global) this.getApplication()).getRemedio();

        sdf = new SimpleDateFormat("HH:mm");

        horaTV = (TextView) findViewById(R.id.horaTV);
        doseTF = (EditText) findViewById(R.id.doseTF);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        calendar = Calendar.getInstance();

        Bundle params = getIntent().getExtras();

        hora = calendar.getTime();

        if (params != null)
        {
            int idHoraDose = params.getInt("idHoraDose");
            horaDose = realm.where(HoraDose.class).equalTo("id", idHoraDose).findFirst();
            horaTV.setText(sdf.format(horaDose.getHora()));
            doseTF.setText(horaDose.getDose());
        }

        else
        {
            horaTV.setText(sdf.format(calendar.getTime()));
        }

    }

    public void abrirTimePicker(View view)
    {
        timePickerDialog = new TimePickerDialog(HoraDoseForm.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeCalendar.set(Calendar.MINUTE, minute);

                hora = timeCalendar.getTime();

                String timeString = DateUtils.formatDateTime(HoraDoseForm.this,
                        timeCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);

                horaTV.setText(timeString);

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(HoraDoseForm.this));
        timePickerDialog.show();
    }

    public void salvarHoraDose(View view)
    {
        realm.beginTransaction();

        if (horaDose == null) {
            horaDose = realm.createObject(HoraDose.class);
            horaDose.setId(realm.where(HoraDose.class).max("id").intValue() + 1);
            remedio.getHoraDoses().add(horaDose);
        }
        horaDose.setDose(doseTF.getText().toString());
        try {
            horaDose.setHora(sdf.parse(horaTV.getText().toString()));
            this.finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Não foi possível cadastrar", Toast.LENGTH_SHORT);
        }
        realm.commitTransaction();

    }


}

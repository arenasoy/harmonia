package br.edu.ifpr.tcc.remedio.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.views.HoraDoseForm;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by diego on 13/09/2016.
 */
public class HoraDoseAdapter extends RealmBaseAdapter<HoraDose> implements ListAdapter {

    private Realm realm;
    private SimpleDateFormat sdf;

    public HoraDoseAdapter(Context context, RealmResults<HoraDose> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
        sdf = new SimpleDateFormat("HH:mm");
        }


@Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final HoraDose horaDose = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.hora_dose_row, null);

        TextView horaTV = (TextView)view.findViewById(R.id.horaTV);
        TextView doseTV = (TextView)view.findViewById(R.id.doseTV);

        horaTV.setText("Hora: "+ sdf.format(horaDose.getHora()));
        doseTV.setText("Dose: "+ horaDose.getDose());

        view.setTag(horaDose.getId());

        Button remover = (Button) view.findViewById(R.id.removerHoraDoseBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            confirmarRemoverHoraDose(horaDose.getId());
            }
            });

        final Button editar = (Button) view.findViewById(R.id.editarHoraDoseBT);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarHoraDose(horaDose.getId());
            }
        });

        return view;
        }


    public void removerHoraDose(HoraDose horaDose)
    {
        realm.beginTransaction();
        horaDose.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverHoraDose(final int idHoraDose)
    {
        HoraDose horaDose = realm.where(HoraDose.class)
        .equalTo("id", idHoraDose)
        .findFirst();
        removerHoraDose(horaDose);

    }

    public void editarHoraDose(final int idHoraDose)
    {
        Intent intent = new Intent(context, HoraDoseForm.class);
        intent.putExtra("idHoraDose", idHoraDose);
        context.startActivity(intent);
    }
}

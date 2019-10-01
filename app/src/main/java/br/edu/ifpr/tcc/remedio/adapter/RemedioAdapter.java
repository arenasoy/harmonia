package br.edu.ifpr.tcc.remedio.adapter;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.views.CadastroRemedio;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by diego on 11/06/2016.
 */
public class RemedioAdapter extends RealmBaseAdapter<Remedio> implements ListAdapter {

    private Realm realm;
    private SimpleDateFormat format;
    private TextView nomeRemedioTV;
    private TextView horariosRemedioTV;
    private TextView diasTV;
    private Switch ligadoCB;

    public RemedioAdapter(Context context, RealmResults<Remedio> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Remedio remedio = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.remedio_row, null);

        nomeRemedioTV = (TextView) view.findViewById(R.id.nomeRemedioTV);
        horariosRemedioTV = (TextView) view.findViewById(R.id.horariosRemedioTV);
        diasTV = (TextView) view.findViewById(R.id.intervaloTV);
        ligadoCB = (Switch) view.findViewById(R.id.ligadoCB);

        format = new SimpleDateFormat("HH:mm");

        nomeRemedioTV.setText(remedio.getNome());
        ligadoCB.setChecked(remedio.isLigado());
        preencherHoraDose(remedio);

        if (remedio.getIntervalo().equals("Uma vez") || remedio.getIntervalo().equals("Diariamente")) {
            diasTV.setText(remedio.getIntervalo());
        } else if (remedio.getIntervalo().equals("Cada x Dias")) {
            diasTV.setText("Cada " + remedio.getRepetir() + " dias");
        } else if (remedio.getIntervalo().equals("Cada X Horas")) {
            diasTV.setText("Cada " + remedio.getRepetirHoras() + " horas");
        } else if (remedio.getIntervalo().equals("Semanalmente")) {
            diasTV.setText(verificarDias(remedio));
        } else if (remedio.getIntervalo().equals("Mensalmente")) {
            diasTV.setText(verificarDiasMes(remedio));
        }

        view.setTag(remedio.getId());


        Button editar = (Button) view.findViewById(R.id.editarRemedioBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editarRemedio(remedio.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerRemedioBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmarRemoverRemedio(remedio.getId());
            }
        });

        ligadoCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarSwitch(remedio);
            }
        });

        return view;
    }

    private void mudarSwitch(Remedio remedio)
    {
        realm.beginTransaction();
        remedio.setLigado(ligadoCB.isChecked());
        realm.commitTransaction();
    }

    private void editarRemedio(int id) {
        Intent cadastroRemedio = new Intent(context, CadastroRemedio.class);
        cadastroRemedio.putExtra("idRemedio", id);
        context.startActivity(cadastroRemedio);
    }

    public void removerRemedio(Remedio remedio) {
        realm.beginTransaction();
        remedio.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverRemedio(int idRemedio) {
        RealmObject rem = realm.where(Remedio.class)
                .equalTo("id", idRemedio)
                .findFirst();

        final Remedio remedio = (Remedio) rem;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover o remédio " + remedio.getNome() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerRemedio(remedio);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

    public void preencherHoraDose(Remedio remedio)
    {
        String horaDoses = "";
        if (remedio.getHoraDoses().size() > 0)
        {
            for (HoraDose h:
                    remedio.getHoraDoses()) {
                horaDoses += format.format(h.getHora())+"("+h.getDose()+")";
                if (h.getId() != remedio.getHoraDoses().last().getId())
                    horaDoses += "; ";
            }
        }
        horariosRemedioTV.setText(horaDoses);
    }

    private String verificarDias(Remedio remedio) {
        String dias = "";
        if (remedio.getDiasSemana().get(0).isBoleano())
            dias += ("Domingo ");
        if (remedio.getDiasSemana().get(1).isBoleano())
            dias += ("Segunda ");
        if (remedio.getDiasSemana().get(2).isBoleano())
            dias += ("Terça ");
        if (remedio.getDiasSemana().get(3).isBoleano())
            dias += ("Quarta ");
        if (remedio.getDiasSemana().get(4).isBoleano())
            dias += ("Quinta ");
        if (remedio.getDiasSemana().get(5).isBoleano())
            dias += ("Sexta ");
        if (remedio.getDiasSemana().get(6).isBoleano())
            dias += ("Sábado");

        return dias;
    }

    private String verificarDiasMes(Remedio remedio)
    {
        String dias = "";
        if (remedio.getDiasMes().get(0).isBoleano())
        {
            dias += ("1 ");
        }
        for (int i = 1; i < remedio.getDiasMes().size(); i++)
        {
            if (remedio.getDiasMes().get(i).isBoleano())
            {
                dias += ((i+1) + " ");
            }
        }
        return dias;
    }


}

package br.edu.ifpr.tcc.ataque.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.ataque.views.CadastroAtaque;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by diego on 04/11/2016.
 */

public class RegistrosAdapter extends RealmBaseAdapter<Ataque> implements ListAdapter {

    private Realm realm;
    private List<Integer> idAtaquesEnviar;

    public RegistrosAdapter (Context context, RealmResults<Ataque> realmResults,
                             boolean automaticUpdate, List<Integer> idAtaquesEnviar){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
        this.idAtaquesEnviar = idAtaquesEnviar;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Ataque ataque = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.ataque_enviar_row, null);
        view.setTag(ataque.getId());

        preencherDataAtaque(ataque, view, ataque.getId());

        return view;
    }

    private void preencherDataAtaque(Ataque ataque, View view, final int idAtaque) {

        TextView dataAtaque = (TextView)view.findViewById(R.id.dataAtaqueLabel);
        TextView ultimaVez = (TextView)view.findViewById(R.id.enviadoUltimaTV);

        Date dataAtaqueDate = ataque.getDataOcorrencia();
        if (dataAtaqueDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataAtaqueFormatada = sdf.format(dataAtaqueDate);
            dataAtaque.setText(dataAtaqueFormatada);
        }

        Date dataEnviado = ataque.getUltimoEnviado();
        if (dataEnviado != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataAtaqueFormatada = sdf.format(dataAtaqueDate);
            ultimaVez.setText(dataAtaqueFormatada);
        }

        CheckBox checkBox = (CheckBox)view.findViewById(R.id.selecionarAtaqueCB);
        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adicionarIdLista(idAtaque);
            }
        });
        if (idAtaquesEnviar.contains(idAtaque)) {
            checkBox.setChecked(true);
        }
    }

    private void adicionarIdLista(int idAtaque) {

        boolean adicionar = true;
        for (Integer integer:
             idAtaquesEnviar) {
            if (integer.intValue() == idAtaque) {
                adicionar = false;
                break;
            }
        }

        if (adicionar) {
            idAtaquesEnviar.add(idAtaque);
        } else {
            idAtaquesEnviar.remove(Integer.valueOf(idAtaque));
        }
    }

}

package br.edu.ifpr.tcc.ataque.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.ataque.views.CadastroAtaque;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by diego on 11/06/2016.
 */
public class AtaqueAdapter extends RealmBaseAdapter<Ataque> implements ListAdapter {

    private Realm realm;

    public AtaqueAdapter (Context context, RealmResults<Ataque> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Ataque ataque = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.ataque_row, null);
        view.setTag(ataque.getId());

        preencherDataAtaque(ataque, view, ataque.getId());

        Button editar = (Button) view.findViewById(R.id.editarImagemBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editarAtaque(ataque.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerAtaqueBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmarRemoverAtaque(ataque.getId());
            }
        });

        return view;
    }

    private void preencherDataAtaque(Ataque ataque, View view, final int idAtaque) {
        TextView dataAtaque = (TextView)view.findViewById(R.id.dataAtaqueLabel);

        Date dataAtaqueDate = ataque.getDataOcorrencia();
        if (dataAtaqueDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dataAtaqueFormatada = sdf.format(dataAtaqueDate);
            dataAtaque.setText(dataAtaqueFormatada);
        }

        dataAtaque.setClickable(true);
        dataAtaque.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                visualizarAtaque(idAtaque);
            }
        });

    }

    private void visualizarAtaque(int id) {
        Intent cadastroAtaque = new Intent(context, CadastroAtaque.class);
        cadastroAtaque.putExtra("estado", "visualizando");
        cadastroAtaque.putExtra("idAtaque", id);
        context.startActivity(cadastroAtaque);
    }

    private void editarAtaque(int id)
    {
        Intent cadastroAtaque = new Intent(context, CadastroAtaque.class);
        cadastroAtaque.putExtra("estado", "editando");
        cadastroAtaque.putExtra("idAtaque", id);
        context.startActivity(cadastroAtaque);
    }

    public void removerAtaque(Ataque ataque)
    {
        realm.beginTransaction();
        ataque.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverAtaque (int idAtaque)
    {
        RealmObject ataq = realm.where(Ataque.class)
                .equalTo("id", idAtaque)
                .findFirst();

        final Ataque ataque = (Ataque) ataq;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover este ataque ?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerAtaque(ataque);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

}

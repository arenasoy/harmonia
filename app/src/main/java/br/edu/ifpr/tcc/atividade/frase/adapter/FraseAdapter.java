package br.edu.ifpr.tcc.atividade.frase.adapter;

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

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.atividade.frase.views.CadastroFrase;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.views.CadastroContato;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by diego on 05/08/2016.
 */
public class FraseAdapter extends RealmBaseAdapter<Frase> implements ListAdapter {

    private Realm realm;

    public FraseAdapter(Context context, RealmResults<Frase> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Frase frase = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.frase_row, null);

        TextView fraseTV = (TextView)view.findViewById(R.id.fraseTV);

        fraseTV.setText(frase.getTexto());

        view.setTag(frase.getId());

        Button editar = (Button) view.findViewById(R.id.editarFraseBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editarFrase(frase.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerFraseBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmarRemoverFrase(frase.getId());
            }
        });

        fraseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context).setMessage(frase.getTexto()).create().show();

            }
        });

        return view;
    }

    private void editarFrase(int id)
    {
        Intent cadastroFrase = new Intent(context, CadastroFrase.class);
        cadastroFrase.putExtra("idFrase", id);
        context.startActivity(cadastroFrase);
    }

    public void removerFrase(Frase frase)
    {
        realm.beginTransaction();
        frase.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverFrase(final int idFrase)
    {
        RealmObject frase = realm.where(Frase.class)
                .equalTo("id", idFrase)
                .findFirst();

        final Frase f = (Frase) frase;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover a frase '" + f.getTexto() + "'?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerFrase(f);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }


}

package br.edu.ifpr.tcc.contato.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.views.CadastroContato;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by diego on 11/06/2016.
 */
public class ContatoAdapter extends RealmBaseAdapter<Contato> implements ListAdapter {

    private Realm realm;

    public ContatoAdapter(Context context, RealmResults<Contato> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Contato contato = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.contato_row, null);

        TextView nomeAjudanteTV = (TextView)view.findViewById(R.id.nomeAjudanteTV);
        TextView telefoneAjudanteTV = (TextView)view.findViewById(R.id.telefoneAjudanteTV);
        final CheckBox smsCB = (CheckBox) view.findViewById(R.id.smsCB);

        nomeAjudanteTV.setText(contato.getNome());
        telefoneAjudanteTV.setText(contato.getNumero());
        smsCB.setChecked(contato.isEnviarSMS());

        view.setTag(contato.getId());

        Button editar = (Button) view.findViewById(R.id.editarContatoBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editarContato(contato.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerContatoBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmarRemoverContato(contato.getId());
            }
        });

        smsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.beginTransaction();
                contato.setEnviarSMS(smsCB.isChecked());
                realm.commitTransaction();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(contato.getNome())
                        .setMessage("("+contato.getCodigoNumero()+") "+ contato.getNumero())
                        .create()
                        .show();
            }
        });

        return view;
    }

    private void editarContato(int id)
    {
        Intent cadastroContato = new Intent(context, CadastroContato.class);
        cadastroContato.putExtra("idContato", id);
        context.startActivity(cadastroContato);
    }

    public void removerContato(Contato contato)
    {
        realm.beginTransaction();
        contato.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverContato(final int idContato)
    {
        RealmObject contato = realm.where(Contato.class)
                .equalTo("id", idContato)
                .findFirst();

        final Contato cont = (Contato) contato;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover o " + cont.getNome() + "?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerContato(cont);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }


}

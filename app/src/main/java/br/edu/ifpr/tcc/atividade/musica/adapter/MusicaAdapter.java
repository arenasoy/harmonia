package br.edu.ifpr.tcc.atividade.musica.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.views.CadastroFrase;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.atividade.musica.views.CadastroMusica;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by Admin on 08/08/2016.
 */
public class MusicaAdapter extends RealmBaseAdapter<Musica> implements ListAdapter {

    private Realm realm;
    private TextView musicaTV;

    public MusicaAdapter(Context context, RealmResults<Musica> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Musica musica = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.musica_row, null);

        musicaTV = (TextView) view.findViewById(R.id.musicaTV);

        musicaTV.setText(musica.getNome());

        view.setTag(musica.getId());

        Button editar = (Button) view.findViewById(R.id.editarMusicaBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editarMusica(musica.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerMusicaBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmarRemoverMusica(musica.getId());
            }
        });

        return view;
    }

    private void editarMusica(int id)
    {
        Intent cadastroMusica = new Intent(context, CadastroMusica.class);
        cadastroMusica.putExtra("idMusica", id);
        context.startActivity(cadastroMusica);
    }

    public void removerMusica(Musica musica)
    {
        realm.beginTransaction();
        musica.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverMusica(final int idMusica)
    {
        RealmObject musica = realm.where(Musica.class)
                .equalTo("id", idMusica)
                .findFirst();

        final Musica msc = (Musica) musica;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover esta música?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerMusica(msc);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

}

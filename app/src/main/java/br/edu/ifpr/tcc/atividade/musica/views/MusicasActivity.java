package br.edu.ifpr.tcc.atividade.musica.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.atividade.musica.adapter.MusicaAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class MusicasActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Musica> musicas;
    private MusicaAdapter musicaAdapter;
    private ListView musicasLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_musicas, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Músicas");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaMusica();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        musicas = realm.allObjects(Musica.class);
        musicaAdapter = new MusicaAdapter(this, musicas, true);
        musicasLV = (ListView)findViewById(R.id.musicasLV);
        musicasLV.setAdapter(musicaAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }

    public void novaMusica()
    {
        startActivity(new Intent(this, CadastroMusica.class));
        this.finish();
    }


}

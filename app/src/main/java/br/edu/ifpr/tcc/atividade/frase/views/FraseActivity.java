package br.edu.ifpr.tcc.atividade.frase.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.atividade.frase.adapter.FraseAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class FraseActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Frase> frases;
    private FraseAdapter fraseAdapter;
    private ListView frasesLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_frase, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Frases");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaFrase();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        frases = realm.allObjects(Frase.class);
        fraseAdapter = new FraseAdapter(this, frases, true);
        frasesLV = (ListView)findViewById(R.id.frasesLV);
        frasesLV.setAdapter(fraseAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }

    public void novaFrase()
    {
        startActivity(new Intent(this, CadastroFrase.class));
        this.finish();
    }


}


package br.edu.ifpr.tcc.atividade.frase.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class MetodosFrase extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Frase> frases;
    private TextView fraseTV;
    private int fraseAtual = 0;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_metodos_frase, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Frases");

        mudarItem(R.id.acalme_me);

        fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proximaFrase(view);
            }
        });

        configurarFrases();


    }

    private void configurarFrases() {
        fraseTV = (TextView) findViewById(R.id.fraseTV);

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        frases = realm.allObjects(Frase.class);

        if (!frases.isEmpty()) {
            fraseTV.setText(frases.get(fraseAtual).getTexto());
        }
        else {
            fraseTV.setText("Nenhuma frase cadastrada");
            fab.setAlpha(.5f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.acalme_me);
    }

    public void proximaFrase(View view) {
        if (!frases.isEmpty()){
            fraseAtual++;
            if (frases.size() != fraseAtual) {
                fraseTV.setText(frases.get(fraseAtual).getTexto());
            } else {
                fraseAtual = 0;
                fraseTV.setText(frases.get(fraseAtual).getTexto());
            }
        }
    }
}

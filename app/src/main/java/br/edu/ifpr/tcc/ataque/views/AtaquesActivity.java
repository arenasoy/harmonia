package br.edu.ifpr.tcc.ataque.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.ataque.adapter.AtaqueAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

public class AtaquesActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Ataque> ataques;
    private AtaqueAdapter ataqueAdapter;
    private ListView ataquesLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_ataques, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Histórico de ataques");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabNovo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novoAtaque();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        ataques = realm.allObjects(Ataque.class);
        ataqueAdapter = new AtaqueAdapter(this, ataques, true);
        ataquesLV = (ListView)findViewById(R.id.ataquesLV);
        ataquesLV.setAdapter(ataqueAdapter);

        RealmResults<Ataque> ataques = realm.where(Ataque.class).findAll();
        for (Ataque ataque:
                ataques) {
            if (ataque.getDataOcorrencia() == null && ataque.getLocalAtaque() == null
                    && ataque.getAcaoAcalmar() == null)
            {
                realm.beginTransaction();
                ataque.removeFromRealm();
                realm.commitTransaction();
            }
        }

        if (this.ataques.isEmpty()) {
            Button enviarBt = (Button) findViewById(R.id.enviarRegistrosAtaquesBt);
            enviarBt.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.historico_ataques);
        if (!this.ataques.isEmpty()) {
            Button enviarBt = (Button) findViewById(R.id.enviarRegistrosAtaquesBt);
            enviarBt.setEnabled(true);
        }
    }

    public void novoAtaque()
    {
        Intent i = new Intent(this, CadastroAtaque.class);
        i.putExtra("estado", "novo");
        startActivity(i);
    }

    public void enviarRegistros(View view) {
        startActivity(new Intent(this, EnviarRegistrosActivity.class));
    }



}

package br.edu.ifpr.tcc.contato.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.adapter.ContatoAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class ContatosActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Contato> contatos;
    private ContatoAdapter contatoAdapter;
    private ListView contatosLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_contatos, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Contatos");

        mudarItem(R.id.configuracoes);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novoContato();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        contatos = realm.allObjects(Contato.class);
        contatoAdapter = new ContatoAdapter(this, contatos, true);
        contatosLV = (ListView)findViewById(R.id.contatosLV);
        contatosLV.setAdapter(contatoAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }

    public void novoContato()
    {
        startActivity(new Intent(this, CadastroContato.class));
        this.finish();
    }


}

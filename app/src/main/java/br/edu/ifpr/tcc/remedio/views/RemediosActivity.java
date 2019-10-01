package br.edu.ifpr.tcc.remedio.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.adapter.RemedioAdapter;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

public class RemediosActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Remedio> remedios;
    private RemedioAdapter remedioAdapter;
    private ListView remediosLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_remedios, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Remédios");

        mudarItem(R.id.gerenciar_remedios);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novoRemedio();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));


        for (Remedio remedio:
             realm.where(Usuario.class).findFirst().getRemedios()) {
            if (remedio.getNome() == null)
            {
                realm.beginTransaction();
                remedio.removeFromRealm();
                realm.commitTransaction();
            }
        }

        remedios = realm.allObjects(Remedio.class);
        remedioAdapter = new RemedioAdapter(this, remedios, true);
        remediosLV = (ListView)findViewById(R.id.remediosLV);
        remediosLV.setAdapter(remedioAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.gerenciar_remedios);
    }

    public void novoRemedio()
    {
        startActivity(new Intent(this, CadastroRemedio.class));
        this.finish();
    }

}

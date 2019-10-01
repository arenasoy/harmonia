package br.edu.ifpr.tcc.remedio.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.adapter.HoraDoseAdapter;
import io.realm.Realm;
import io.realm.RealmResults;

public class HoraDoseActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<HoraDose> horaDoses;
    private HoraDoseAdapter horaDoseAdapter;
    private ListView horaDosesLV;
    private Remedio remedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_hora_dose, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Horários e doses");

        mudarItem(R.id.gerenciar_remedios);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HoraDoseForm.class));
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fabSalvar);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fecharHoraDose();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        remedio = ((Global) this.getApplication()).getRemedio();
        horaDoses = remedio.getHoraDoses().where().findAll();
        horaDoseAdapter = new HoraDoseAdapter(this, horaDoses, true);
        horaDosesLV = (ListView)findViewById(R.id.horaDosesLV);
        horaDosesLV.setAdapter(horaDoseAdapter);
    }

    public void fecharHoraDose()
    {
        CadastroRemedio.preencherHoraDose();
        this.finish();
    }

}

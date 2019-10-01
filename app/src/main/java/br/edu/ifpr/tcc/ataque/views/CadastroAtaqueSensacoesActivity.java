package br.edu.ifpr.tcc.ataque.views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.realm.RealmString;
import io.realm.Realm;
import io.realm.RealmList;

public class CadastroAtaqueSensacoesActivity extends AppCompatActivity {

    private boolean isAntes;
    private Ataque ataque;
    private Realm realm;
    private List<CheckBox> checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_ataque_sensacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        Bundle params = getIntent().getExtras();
        isAntes = params.getBoolean("antes");
        int idAtaque = params.getInt("idAtaque");

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        ataque = realm.where(Ataque.class)
                .equalTo("id", idAtaque)
                .findAll()
                .first();

        checkboxes = new ArrayList();
        adicionarCheckboxes();

        RealmList<RealmString> sensacoesAtaque;

        if (!isAntes) {
            toolbar.setTitle("Sensações depois do ataque");
            sensacoesAtaque = configurarCasoDepois();
        } else {
            toolbar.setTitle("Sensações antes do ataque");
            sensacoesAtaque = configurarCasoAntes();
        }

        setSupportActionBar(toolbar);

        preencherCheckboxes (sensacoesAtaque);

    }

    @NonNull
    private RealmList<RealmString> configurarCasoAntes() {
        RealmList<RealmString> sensacoesAtaque;
        sensacoesAtaque = ataque.getSensacoesAntes();

        if (sensacoesAtaque == null) {
            sensacoesAtaque = new RealmList<RealmString>();
        }

        realm.beginTransaction();
        if (ataque.getSensacoesDepoisOutros() == null) {
            ataque.setSensacoesDepoisOutros("");
        }
        realm.commitTransaction();

        preencherEditText(ataque.getSensacoesDepoisOutros());
        return sensacoesAtaque;
    }

    private void preencherEditText(String sensacao) {
        EditText outrosET = (EditText) findViewById(R.id.sensacoesOutrosET);
        outrosET.setText(sensacao);
    }

    @NonNull
    private RealmList<RealmString> configurarCasoDepois() {
        RealmList<RealmString> sensacoesAtaque;
        sensacoesAtaque = ataque.getSensacoesDepois();

        if (sensacoesAtaque == null) {
            sensacoesAtaque = new RealmList<RealmString>();
        }

        realm.beginTransaction();
        if (ataque.getSensacoesAntesOutros() == null) {
            ataque.setSensacoesAntesOutros("");
        }
        realm.commitTransaction();

        preencherEditText(ataque.getSensacoesAntesOutros());

        TextView sensacoesTV = (TextView)findViewById(R.id.tituloSensacoesTV);
        sensacoesTV.setText("Sensações depois do ataque");
        return sensacoesAtaque;
    }

    private void adicionarCheckboxes() {
        ViewGroup parentView = (ViewGroup) findViewById(R.id.layoutCheckboxesSensacoes);

        // começa no 1 pra ignorar textview, termina em -1 pra ignorar edittext
        for(int i=0; i < parentView.getChildCount()-1; i++) {
            CheckBox cb = (CheckBox) parentView.getChildAt(i);
            checkboxes.add(cb);
        }
    }

    private void preencherCheckboxes(RealmList<RealmString> sensacoesAtaque) {
        System.out.println("preenchendo");
        for (CheckBox cb:
             checkboxes) {
            for (RealmString sensacao:
                    sensacoesAtaque) {
                if (cb.getText().toString().equals(sensacao.getTexto())) {
                    cb.setChecked(true);
                }
            }
        }
    }

    public void salvarRegistro(View view) {
        RealmList<RealmString> lista = new RealmList<RealmString>();

        criarListaSensacoes(lista);
        salvarCoisas(lista);
        this.finish();
    }

    private void salvarCoisas(RealmList<RealmString> lista) {
        realm.beginTransaction();

        EditText et = (EditText) findViewById(R.id.sensacoesOutrosET);

        if (isAntes) {
            ataque.setSensacoesAntes(lista);
            ataque.setSensacoesAntesOutros(et.getText().toString());
        } else {
            ataque.setSensacoesDepois(lista);
            ataque.setSensacoesDepoisOutros(et.getText().toString());
        }

        realm.commitTransaction();
    }

    private void criarListaSensacoes(RealmList<RealmString> lista) {
        for (CheckBox cb:
             checkboxes) {
            if (cb.isChecked()) {
                realm.beginTransaction();

                RealmString rs = realm.createObject(RealmString.class);
                rs.setTexto(cb.getText().toString());
                lista.add(rs);

                realm.commitTransaction();

            }
        }
    }

}

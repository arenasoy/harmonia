package br.edu.ifpr.tcc.ataque.views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
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

public class CadastroAtaqueSentimentosActivity extends AppCompatActivity {

    private boolean isAntes;
    private Ataque ataque;
    private Realm realm;
    private List<CheckBox> checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_ataque_sentimentos);
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

        RealmList<RealmString> sentimentosAtaque;

        if (!isAntes) {
            toolbar.setTitle("Sentimentos depois do ataque");
            sentimentosAtaque = configurarCasoDepois();
        } else {
            toolbar.setTitle("Sentimentos antes do ataque");
            sentimentosAtaque = configurarCasoAntes();
        }

        setSupportActionBar(toolbar);

        preencherCheckboxes (sentimentosAtaque);

    }

    @NonNull
    private RealmList<RealmString> configurarCasoAntes() {
        RealmList<RealmString> sentimentosAtaque;
        sentimentosAtaque = ataque.getSensacoesAntes();

        if (sentimentosAtaque == null) {
            sentimentosAtaque = new RealmList<RealmString>();
        }

        realm.beginTransaction();
        if (ataque.getSentimentosDepoisOutros() == null) {
            ataque.setSentimentosDepoisOutros("");
        }
        realm.commitTransaction();

        preencherEditText(ataque.getSentimentosDepoisOutros());
        return sentimentosAtaque;
    }

    private void preencherEditText(String sentimento) {
        EditText outrosET = (EditText) findViewById(R.id.sentimentosOutrosET);
        outrosET.setText(sentimento);
    }

    @NonNull
    private RealmList<RealmString> configurarCasoDepois() {
        RealmList<RealmString> sentimentosAtaque;
        sentimentosAtaque = ataque.getSentimentosDepois();

        if (sentimentosAtaque == null) {
            sentimentosAtaque = new RealmList<RealmString>();
        }

        realm.beginTransaction();
        if (ataque.getSentimentosAntesOutros() == null) {
            ataque.setSentimentosAntesOutros("");
        }
        realm.commitTransaction();

        preencherEditText(ataque.getSentimentosAntesOutros());

        TextView sensacoesTV = (TextView)findViewById(R.id.tituloSentimentosTV);
        sensacoesTV.setText("Sensações depois do ataque");
        return sentimentosAtaque;
    }

    private void adicionarCheckboxes() {
        ViewGroup parentView = (ViewGroup) findViewById(R.id.layoutCheckboxesSentimentos);

        // começa no 1 pra ignorar textview, termina em -1 pra ignorar edittext
        for(int i=0; i < parentView.getChildCount()-1; i++) {
            CheckBox cb = (CheckBox) parentView.getChildAt(i);
            checkboxes.add(cb);
        }
    }

    private void preencherCheckboxes(RealmList<RealmString> sentimentosAtaque) {
        System.out.println("preenchendo");
        for (CheckBox cb:
                checkboxes) {
            for (RealmString sentimento:
                    sentimentosAtaque) {
                if (cb.getText().toString().equals(sentimento.getTexto())) {
                    cb.setChecked(true);
                }
            }
        }
    }

    public void salvarRegistro(View view) {
        RealmList<RealmString> lista = new RealmList<RealmString>();

        criarListaSentimentos(lista);
        salvarCoisas(lista);
        this.finish();
    }

    private void salvarCoisas(RealmList<RealmString> lista) {
        realm.beginTransaction();

        EditText et = (EditText) findViewById(R.id.sentimentosOutrosET);

        if (isAntes) {
            ataque.setSentimentosAntes(lista);
            ataque.setSentimentosAntesOutros(et.getText().toString());
        } else {
            ataque.setSentimentosDepois(lista);
            ataque.setSentimentosDepoisOutros(et.getText().toString());
        }

        realm.commitTransaction();
    }

    private void criarListaSentimentos(RealmList<RealmString> lista) {
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
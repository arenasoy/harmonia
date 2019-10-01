package br.edu.ifpr.tcc.ataque.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.realm.RealmString;
import br.edu.ifpr.tcc.remedio.views.HoraDoseForm;
import io.realm.Realm;
import io.realm.RealmList;

public class CadastroAtaque extends AppCompatActivity {

    private Ataque ataque;
    private Realm realm;
    private String estado;

    private SimpleDateFormat sdf;
    private TextView dataAtaqueTv;
    private EditText localAtaqueEt;
    private TextView sentimentosAntesTv;
    private TextView sentimentosDepoisTv;
    private TextView sensacoesAntesTv;
    private TextView sensacoesDepoisTv;
    private EditText pensamentosAntesEt;
    private EditText pensamentosDepoisEt;
    private EditText metodoAcalmarEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_ataque);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        configurarCampos();

        Bundle params = getIntent().getExtras();
        estado = params.getString("estado");

        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));

        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        montarAtaque();

        if (estado.equals("editando")) {
            toolbar.setTitle("Editar ataque");
        } else if (estado.equals("novo")) {
            toolbar.setTitle("Novo ataque");
        } else {
            toolbar.setTitle("Visualizar ataque");
            bloquearCampos();
        }

        preencherTela();

        setSupportActionBar(toolbar);
    }

    private void bloquearCampos() {
        dataAtaqueTv.setClickable(false);

        localAtaqueEt.setFocusable(false);
        localAtaqueEt.setClickable(false);

        sentimentosAntesTv.setClickable(false);
        sentimentosAntesTv.setText("Sentimentos não cadastrados!");
        sentimentosDepoisTv.setClickable(false);
        sentimentosDepoisTv.setText("Sentimentos não cadastrados!");
        sensacoesAntesTv.setClickable(false);
        sensacoesAntesTv.setText("Sensações não cadastradas!");
        sensacoesDepoisTv.setClickable(false);
        sensacoesDepoisTv.setText("Sensações não cadastradas!");

        pensamentosAntesEt.setFocusable(false);
        pensamentosAntesEt.setClickable(false);
        pensamentosDepoisEt.setFocusable(false);
        pensamentosDepoisEt.setClickable(false);
        metodoAcalmarEt.setFocusable(false);
        metodoAcalmarEt.setClickable(false);

    }

    private void montarAtaque() {
        if (estado.equals("novo")) {
            realm.beginTransaction();

            ataque = realm.createObject(Ataque.class);
            int id = realm.where(Ataque.class).max("id").intValue() + 1;
            ataque.setId(id);

            realm.commitTransaction();
        } else {
            ataque = realm.where(Ataque.class)
                    .equalTo("id", getIntent().getIntExtra("idAtaque", 0))
                    .findAll()
                    .first();
        }
    }

    private void configurarCampos() {
        dataAtaqueTv = (TextView) findViewById(R.id.dataAtaqueTV);
        localAtaqueEt = (EditText) findViewById(R.id.localAtaqueET);
        sentimentosAntesTv = (TextView) findViewById(R.id.sentimentosAntesTV);
        sentimentosDepoisTv = (TextView) findViewById(R.id.sentimentosDepoisTV);
        sensacoesAntesTv = (TextView) findViewById(R.id.sensacoesAntesTV);
        sensacoesDepoisTv = (TextView) findViewById(R.id.sensacoesDepoisTV);
        pensamentosAntesEt = (EditText) findViewById(R.id.pensamentosAntesET);
        pensamentosDepoisEt = (EditText) findViewById(R.id.pensamentosDepoisET);
        metodoAcalmarEt = (EditText) findViewById(R.id.metodoAcalmarET);

        dataAtaqueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDatePicker();
            }
        });

    }

    public void abrirDatePicker()
    {
        final Calendar c = Calendar.getInstance();

        c.setTime(abrirTimePicker(c).getTime());
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                realm.beginTransaction();
                c.set(i, i1, i2);
                ataque.setDataOcorrencia(c.getTime());
                dataAtaqueTv.setText(sdf.format(ataque.getDataOcorrencia()));
                realm.commitTransaction();
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance()
                    .get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();


    }

    public Calendar abrirTimePicker(final Calendar c)
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(CadastroAtaque.this,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                realm.beginTransaction();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                ataque.setDataOcorrencia(c.getTime());
                dataAtaqueTv.setText(sdf.format(ataque.getDataOcorrencia()));
                realm.commitTransaction();

            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(CadastroAtaque.this));
        timePickerDialog.show();
        return c;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        preencherTela();
    }

    private void preencherTela() {
        preencherDataAtaque();
        preencherLocalAtaque();
        preencherSentimentosAntes();
        preencherSentimentosDepois();
        preencherSensacoesAntes();
        preencherSensacoesDepois();
        preencherPensamentosAntes();
        preencherPensamentosDepois();
        preencherMetodoAcalmar();
    }

    private void preencherDataAtaque() {
        Date dataAtaqueDate = ataque.getDataOcorrencia();
        if (dataAtaqueDate != null) {
            String dataAtaqueFormatada = sdf.format(dataAtaqueDate);
            dataAtaqueTv.setText(dataAtaqueFormatada);
        }
    }

    private void preencherLocalAtaque() {
        String localAtaqueString = ataque.getLocalAtaque();
        if (localAtaqueString != null) {
            if (!localAtaqueString.equals("")) {
                localAtaqueEt.setText(localAtaqueString);
            }
        }
    }

    private void preencherSentimentosAntes() {
        RealmList<RealmString> sentimentosAntesList = ataque.getSentimentosAntes();
        String sentimentosAntesOutros = ataque.getSentimentosAntesOutros();
        if (sentimentosAntesList != null || sentimentosAntesOutros != null) {
            String texto = "";
            if (sentimentosAntesList != null) {
                if (!sentimentosAntesList.isEmpty()){
                    texto += transformarListaString(sentimentosAntesList);
                }
            }
            if (sentimentosAntesOutros != null) {
                if (!sentimentosAntesOutros.equals("")) {
                    texto += sentimentosAntesOutros;
                }
            }
            if (!texto.equals("")) {
                sentimentosAntesTv.setText(texto);
            }
        }
    }

    private void preencherSentimentosDepois() {
        RealmList<RealmString> sentimentosDepoisList = ataque.getSentimentosDepois();
        String sentimentosDepoisOutros = ataque.getSentimentosDepoisOutros();
        if (sentimentosDepoisList != null || sentimentosDepoisOutros != null) {
            String texto = "";
            if (sentimentosDepoisList != null) {
                if (!sentimentosDepoisList.isEmpty()){
                    texto += transformarListaString(sentimentosDepoisList);
                }
            }
            if (sentimentosDepoisOutros != null) {
                if (!sentimentosDepoisOutros.equals("")) {
                    texto += sentimentosDepoisOutros;
                }
            }
            if (!texto.equals("")) {
                sentimentosDepoisTv.setText(texto);
            }
        }
    }


    private void preencherSensacoesAntes() {
        RealmList<RealmString> sensacoesAntesList = ataque.getSensacoesAntes();
        String sensacoesAntesOutros = ataque.getSensacoesAntesOutros();
        if (sensacoesAntesList != null || sensacoesAntesOutros != null) {
            String texto = "";
            if (sensacoesAntesList != null) {
                if (!sensacoesAntesList.isEmpty()){
                    texto += transformarListaString(sensacoesAntesList);
                }
            }
            if (sensacoesAntesOutros != null) {
                if (!sensacoesAntesOutros.equals("")) {
                    texto += sensacoesAntesOutros;
                }
            }

            if (!texto.equals("")) {
                sensacoesAntesTv.setText(texto);
            }
        }
    }

    private void preencherSensacoesDepois() {
        RealmList<RealmString> sensacoesDepoisList = ataque.getSensacoesDepois();
        String sensacoesDepoisOutros = ataque.getSensacoesDepoisOutros();
        if (sensacoesDepoisList != null || sensacoesDepoisOutros != null) {
            String texto = "";

            if (sensacoesDepoisList != null) {
                if (!sensacoesDepoisList.isEmpty()){
                    texto += transformarListaString(sensacoesDepoisList);
                }
            }

            if (sensacoesDepoisOutros != null) {
                if (!sensacoesDepoisOutros.equals("")) {
                    texto += sensacoesDepoisOutros;
                }
            }

            if (!texto.equals("")) {
                sensacoesDepoisTv.setText(texto);
            }
        }
    }

    private void preencherPensamentosAntes() {
        String pensamentosAntesString = ataque.getPensamentoAntes();
        if (pensamentosAntesString != null) {
            if (!pensamentosAntesString.equals("")) {
                pensamentosAntesEt.setText(pensamentosAntesString);
            }
        }
    }

    private void preencherPensamentosDepois() {
        String pensamentosDepoisString = ataque.getPensamentoDepois();
        if (pensamentosDepoisString != null) {
            if (!pensamentosDepoisString.equals("")) {
                pensamentosDepoisEt.setText(pensamentosDepoisString);
            }
        }
    }

    private void preencherMetodoAcalmar() {
        String metodoAcalmarString = ataque.getAcaoAcalmar();
        if (metodoAcalmarString != null) {
            if (!metodoAcalmarString.equals("")) {
                metodoAcalmarEt.setText(metodoAcalmarString);
            }
        }
    }

    public String transformarListaString(RealmList<RealmString> lista) {
        String temp = "";
        for (RealmString rs:
             lista) {
            temp += rs.getTexto() + "\n";
        }
        return temp;
    }

    public void editarSentimentosAntes(View view) {
        Intent i = new Intent(this, CadastroAtaqueSentimentosActivity.class);
        i.putExtra("antes", true);
        i.putExtra("idAtaque", ataque.getId());
        startActivity(i);
    }

    public void editarSentimentosDepois(View view) {
        Intent i = new Intent(this, CadastroAtaqueSentimentosActivity.class);
        i.putExtra("depois", true);
        i.putExtra("idAtaque", ataque.getId());
        startActivity(i);
    }

    public void editarSensacoesAntes(View view) {
        Intent i = new Intent(this, CadastroAtaqueSensacoesActivity.class);
        i.putExtra("antes", true);
        i.putExtra("idAtaque", ataque.getId());
        startActivity(i);
    }

    public void editarSensacoesDepois(View view) {
        Intent i = new Intent(this, CadastroAtaqueSensacoesActivity.class);
        i.putExtra("depois", true);
        i.putExtra("idAtaque", ataque.getId());
        startActivity(i);
    }

    public void salvarRegistro(View view) {
        realm.beginTransaction();
        ataque.setLocalAtaque(localAtaqueEt.getText().toString().trim());
        ataque.setPensamentoAntes(pensamentosAntesEt.getText().toString().trim());
        ataque.setPensamentoDepois(pensamentosDepoisEt.getText().toString().trim());
        ataque.setAcaoAcalmar(metodoAcalmarEt.getText().toString().trim());
        realm.commitTransaction();
        this.finish();
    }

}

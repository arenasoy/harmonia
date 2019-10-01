package br.edu.ifpr.tcc.atividade.frase.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;

public class CadastroFrase extends BaseDrawerActivity {

    private Realm realm;
    private Frase frase;
    private EditText fraseTF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_frase, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de frase");

        mudarItem(R.id.configuracoes);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        fraseTF = (EditText)findViewById(R.id.fraseTF);

        Bundle params = getIntent().getExtras();

        if (params != null)
        {
            Integer idFrase = params.getInt("idFrase");
            frase = realm.where(Frase.class).equalTo("id", idFrase).findFirst();
            fraseTF.setText(frase.getTexto());

        }

    }

    public void salvarFrase(View view)
    {

        Usuario usuario = ((Global)getApplication()).getUsuario();

        if (fraseTF.getText() == null || fraseTF.getText().toString().equals(""))
            fraseTF.setError("Digite uma frase");
        else {

            realm.beginTransaction();

            if (frase == null) {
                frase = realm.createObject(Frase.class);
                int id = realm.where(Frase.class).max("id").intValue() + 1;
                frase.setId(id);

            }

            frase.setTexto(fraseTF.getText().toString());
            usuario.getFrases().add(frase);

            realm.commitTransaction();

            abrirFrases();
        }
    }

    public void abrirFrases()
    {
        Intent frases = new Intent(this, FraseActivity.class);
        startActivity(frases);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }
}

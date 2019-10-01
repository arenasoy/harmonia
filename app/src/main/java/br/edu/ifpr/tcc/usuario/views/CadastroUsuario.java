package br.edu.ifpr.tcc.usuario.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.solicitarAjuda.SolicitarAjuda;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

public class CadastroUsuario extends BaseDrawerActivity {

    private Realm realm;
    private Usuario usuario;
    private EditText nomeTF;
    private EditText emailTF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_usuario, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Configurações");

        mudarItem(R.id.configuracoes);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        nomeTF = (EditText) findViewById(R.id.nomeTF);
        emailTF = (EditText) findViewById(R.id.emailTF);

        configEditando();
    }

    private void configEditando() {
        RealmResults<Usuario> usuarios = realm.allObjects(Usuario.class);

        if (usuarios.size() > 0)
        {
            nomeTF.setText(usuarios.first().getNome());
            emailTF.setText(usuarios.first().getEmail());
        }
        else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void salvar(View view)
    {
        if (verificarEmail()) {
            realm.beginTransaction();

            verificarUsuario();
            setDadosUsuario();

            realm.commitTransaction();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            finalizarLogin();
        } else {
            mostrarErroEmail();
        }
    }

    private void setDadosUsuario() {
        usuario = realm.allObjects(Usuario.class).first();

        usuario.setNome(nomeTF.getText().toString());
        usuario.setEmail(emailTF.getText().toString());
    }

    private void verificarUsuario() {
        if (usuario == null) {
            usuario = realm.createObject(Usuario.class);
            int id = realm.where(Usuario.class).max("id").intValue() + 1;
            usuario.setId(id);
        }
    }

    private void mostrarErroEmail() {
        Toast.makeText(getApplicationContext(), "Email inválido!",
                Toast.LENGTH_LONG).show();
    }

    private boolean verificarEmail() {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(emailTF.getText().toString()).matches();
    }

    public void finalizarLogin()
    {
        Intent paginaInicial = new Intent(this, SolicitarAjuda.class);
        startActivity(paginaInicial);
        this.finish();
    }

}

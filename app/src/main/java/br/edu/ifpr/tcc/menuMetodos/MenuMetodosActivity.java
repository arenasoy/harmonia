package br.edu.ifpr.tcc.menuMetodos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.views.MetodosFrase;
import br.edu.ifpr.tcc.atividade.imagem.views.MetodosImagem;
import br.edu.ifpr.tcc.atividade.musica.views.MetodosMusica;
import br.edu.ifpr.tcc.atividade.musica.views.MusicasActivity;
import br.edu.ifpr.tcc.atividade.views.AtividadeActivity;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.views.CadastroContato;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.solicitarAjuda.SolicitarAjuda;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MenuMetodosActivity extends BaseDrawerActivity {

    private ImageButton discagemBT;
    private Usuario usuario;
    private RealmList<Contato> contatos;
    private List<Contato> discagem;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_menu_metodos, null, false);
        drawerLayout.addView(contentView, 0);

        mudarItem(R.id.acalme_me);

        mudarTitulo("Menu de métodos");

        discagemBT = (ImageButton) findViewById(R.id.discagemBT);

        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));

        discagemBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogContatosEmergencia();
            }
        });
        usuario = ((Global)getApplication()).getUsuario();

    }

    public void abrirDialogContatosEmergencia()
    {

        RealmResults<Contato> contatos = realm.where(Contato.class).equalTo("discagemRapida", true).findAll();
        if (contatos.size() > 0) {
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.discagem_rapida, null);
            ListView discagemRapidaLV = (ListView) dialoglayout.findViewById(R.id.discagemRapidaLV);
            ContatoEmergenciaAdapter contatoEmergenciaAdapter = new ContatoEmergenciaAdapter(this, contatos, true);
            discagemRapidaLV.setAdapter(contatoEmergenciaAdapter);
            Dialog contatosEmg = new Dialog(this);
            contatosEmg.setContentView(dialoglayout);
            contatosEmg.show();
        }
        else {
            verificarUsuario();
        }
    }

    private void verificarUsuario() {
        new AlertDialog.Builder(MenuMetodosActivity.this)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setTitle("Aviso!")
                .setMessage("Não há nenhum contato cadastrado")
                .setNeutralButton("Cadastrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), CadastroContato.class));
                    }

                })
                .setPositiveButton("Cancelar", null)
                .show();
    }

    public void discar(String numero)
    {
        Intent chamar = new Intent(Intent.ACTION_CALL);
        chamar.setData(Uri.parse("tel:"+numero));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MenuMetodosActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, 0);
        }
        startActivity(chamar);
    }


    public void abrirRespiracao(View view)
    {
        startActivity(new Intent(this, AtividadeActivity.class));
    }

    public void abrirImagens(View view)
    {
        startActivity(new Intent(this, MetodosImagem.class));
    }

    public void abrirFrases(View view)
    {
        startActivity(new Intent(this, MetodosFrase.class));
    }

    public void abrirMusicas(View view)
    {
        startActivity(new Intent(this, MetodosMusica.class));
    }
}

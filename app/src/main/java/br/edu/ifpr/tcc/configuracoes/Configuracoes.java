package br.edu.ifpr.tcc.configuracoes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.frase.views.FraseActivity;
import br.edu.ifpr.tcc.atividade.imagem.views.ImagensActivity;
import br.edu.ifpr.tcc.atividade.musica.views.MusicasActivity;
import br.edu.ifpr.tcc.contato.views.ContatosActivity;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.usuario.views.CadastroUsuario;

public class Configuracoes extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_configuracoes, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Configurações");

        mudarItem(R.id.configuracoes);
    }

    public void abrirFrases(View view) {
        startActivity(new Intent(this, FraseActivity.class));
    }
    public void abrirImagens(View view) {
        startActivity(new Intent(this, ImagensActivity.class));
    }
    public void abrirMusicas(View view) {
        startActivity(new Intent(this, MusicasActivity.class));
    }
    public void abrirContatos(View view) {
        startActivity(new Intent(this, ContatosActivity.class));
    }
}

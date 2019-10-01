package br.edu.ifpr.tcc.atividade.imagem.views;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.atividade.imagem.adapter.ImagemAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

/*
ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(imagem.getFoto());
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);

        imageView.setImageBitmap(bitmap);
*/
public class ImagensActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Imagem> imagens;
    private ImagemAdapter imagemAdapter;
    private ListView imagensLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_imagens, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Imagens");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                novaImagem();
            }
        });

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        imagens = realm.where(Usuario.class).findFirst().getImagens().where().findAll();
        imagemAdapter = new ImagemAdapter(this, imagens, true);
        imagensLV = (ListView)findViewById(R.id.imagensLV);
        imagensLV.setAdapter(imagemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }

    public void novaImagem()
    {
        startActivity(new Intent(this, CadastroImagem.class));
        this.finish();
    }

}

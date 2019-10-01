package br.edu.ifpr.tcc.atividade.imagem.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmResults;

public class MetodosImagem extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Imagem> imagens;
    private List<Imagem> imagensList;
    private ViewPager viewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    private TextView deslizeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_metodos_imagem, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Imagens");

        mudarItem(R.id.acalme_me);

        deslizeTV = (TextView) findViewById(R.id.deslizeTV);

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        imagens = realm.where(Usuario.class).findFirst().getImagens().where().findAll();

        if (imagens.isEmpty()) {
            imagensList = new ArrayList<Imagem>();
        } else {
            imagensList = imagens.subList(0 ,imagens.size());
        }

        Bitmap bmp[] = new Bitmap[imagensList.size()];

        if (imagensList.size() > 0) {

            for (int i = 0; i < imagensList.size(); i++) {
                bmp[i] = BitmapFactory.decodeByteArray(imagensList.get(i).getFoto(), 0, imagensList.get(i).getFoto().length);
            }

            viewPager = (ViewPager) findViewById(R.id.viewPager);
            customSwipeAdapter = new CustomSwipeAdapter(this, bmp);
            viewPager.setAdapter(customSwipeAdapter);

            deslizeTV.setText("Deslize para passar as imagens");
        }

        else {
            deslizeTV.setText("Não há imagens cadastradas");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.acalme_me);
    }

}

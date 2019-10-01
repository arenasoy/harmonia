package br.edu.ifpr.tcc;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;

public class SobreActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_sobre, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Sobre");

        mudarItem(R.id.sobre);
    }
}

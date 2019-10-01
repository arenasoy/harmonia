package br.edu.ifpr.tcc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.solicitarAjuda.SolicitarAjuda;

public class ComoUtilizarActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_como_utilizar, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Como utilizar");

        mudarItem(R.id.comoUtilizar);
    }

    public void fechar(View view) {
        Intent intent = new Intent(this, SolicitarAjuda.class);
        startActivity(intent);
        this.finish();
    }
}

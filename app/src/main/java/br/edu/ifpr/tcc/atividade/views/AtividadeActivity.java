package br.edu.ifpr.tcc.atividade.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;

public class AtividadeActivity extends BaseDrawerActivity {

    private Realm realm;
    private String numero1;
    private String numero2;/*
    private Button discagem1;
    private Button discagem2;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_atividade, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Atividades");

        realm = Realm.getInstance(RealmConfig.getConfig(this));
/*
        discagem1 = (Button) findViewById(R.id.discagem1BT);
        discagem2 = (Button) findViewById(R.id.discagem2BT);

        discagem1.setVisibility(View.GONE);
        discagem2.setVisibility(View.GONE);

        Usuario usuario = realm.where(Usuario.class).findFirst();
        numero1 = "";
        numero2 = "";
        int id = 0;
        int cont = 0;

        for (int i = 0; i < usuario.getContatos().size(); i++) {
            if (usuario.getContatos().get(i).isDiscagemRapida() && i == 0)
            {
                discagem1.setVisibility(View.VISIBLE);
                discagem1.setText("Ligar para "+ usuario.getContatos().get(i).getNome());
                numero1 = usuario.getContatos().get(i).getNumero();
                id = usuario.getContatos().get(i).getId();
                cont++;

            }
            else if (usuario.getContatos().get(i).isDiscagemRapida() && i > 0 && usuario.getContatos().get(i).getId() != id && cont < 2)
            {
                discagem2.setVisibility(View.VISIBLE);
                discagem2.setText("Ligar para "+ usuario.getContatos().get(i).getNome());
                numero2 = usuario.getContatos().get(i).getNumero();
                cont++;
            }
            else if (i >= 2)
            {
                break;
            }
        }

*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.acalme_me);
    }

    public void realizarChamada(View view)
    {
        Intent chamar = new Intent(Intent.ACTION_CALL);
        chamar.setData(Uri.parse("tel:"+numero1));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(chamar);
    }

    public void realizarChamada2(View view)
    {
        Intent chamar = new Intent(Intent.ACTION_CALL);
        chamar.setData(Uri.parse("tel:"+numero2));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(chamar);
    }


}

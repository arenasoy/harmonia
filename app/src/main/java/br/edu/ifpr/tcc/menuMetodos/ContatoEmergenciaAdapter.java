package br.edu.ifpr.tcc.menuMetodos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.contato.Contato;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by Admin on 03/11/2016.
 */

public class ContatoEmergenciaAdapter extends RealmBaseAdapter<Contato> implements ListAdapter {

    private Context context;

    public ContatoEmergenciaAdapter(Context context, RealmResults<Contato> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Contato contato = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.contato_emergencia_row, null);

        TextView nomeContatoTV = (TextView)
                view.findViewById(R.id.nomeContatoTV);

        nomeContatoTV.setText(contato.getNome());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefone = String.valueOf(contato.getNumero());
                Intent chamar = new Intent(Intent.ACTION_CALL);
                chamar.setData(Uri.parse("tel:" + telefone));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                }
                context.startActivity(chamar);

            }
        });

        ImageButton imgBt = (ImageButton) view.findViewById(R.id.imageButton2);
        imgBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telefone = String.valueOf(contato.getNumero());
                Intent chamar = new Intent(Intent.ACTION_CALL);
                chamar.setData(Uri.parse("tel:" + telefone));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                }
                context.startActivity(chamar);
            }
        });

        view.setTag(contato.getId());
        return view;
    }
}
package br.edu.ifpr.tcc.atividade.imagem.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.atividade.imagem.views.CadastroImagem;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.views.CadastroContato;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by diego on 11/06/2016.
 */
public class ImagemAdapter extends RealmBaseAdapter<Imagem> implements ListAdapter {

    private Realm realm;
    private ImageView imageView;

    public ImagemAdapter(Context context, RealmResults<Imagem> realmResults, boolean automaticUpdate){
        super(context, realmResults, automaticUpdate);
        realm = Realm.getInstance(RealmConfig.getConfig(context));
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Imagem imagem = realmResults.get(position);
        LayoutInflater layout = LayoutInflater.from(context);
        View view = layout.inflate(R.layout.imagem_row, null);

        imageView = (ImageView) view.findViewById(R.id.imageViewLista);

        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(imagem.getFoto()));
        int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
        imageView.setImageBitmap(scaled);

        view.setTag(imagem.getId());

        ImageButton abrir = (ImageButton) view.findViewById(R.id.imageViewLista);
        abrir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                abrirImagem(imagem);
            }
        });

        Button editar = (Button) view.findViewById(R.id.editarImagemBT);
        editar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editarImagem(imagem.getId());
            }
        });

        Button remover = (Button) view.findViewById(R.id.removerImagemBT);
        remover.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                confirmarRemoverImagem(imagem.getId());
            }
        });

        return view;
    }

    public void editarImagem(int id)
    {
        Intent editar = new Intent(context, CadastroImagem.class);
        editar.putExtra("idImagem", id);
        context.startActivity(editar);

    }

    public void abrirImagem(Imagem imagem)
    {
        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(imagem.getFoto()));
        ImageView view = new ImageView(context);
        view.setImageBitmap(bitmap);
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setView(view)
                .show();
    }

    public void removerImagem(Imagem imagem)
    {
        realm.beginTransaction();
        imagem.removeFromRealm();
        realm.commitTransaction();
    }

    public void confirmarRemoverImagem(final int idImagem)
    {
        RealmObject imagem = realm.where(Imagem.class)
                .equalTo("id", idImagem)
                .findFirst();

        final Imagem img = (Imagem) imagem;
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_menu_help)
                .setTitle("Confirmação")
                .setMessage("Deseja realmente remover esta imagem?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerImagem(img);
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }


}

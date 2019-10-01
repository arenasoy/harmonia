package br.edu.ifpr.tcc.atividade.imagem.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;

public class CadastroImagem extends BaseDrawerActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Realm realm;
    private Usuario usuario;
    private ImageView imageView;
    private Imagem imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_imagem, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de imagem");

        mudarItem(R.id.configuracoes);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        usuario = ((Global)getApplication()).getUsuario();

        imageView = (ImageView) findViewById(R.id.imageView);

        Bundle params = getIntent().getExtras();

        if (params != null)
        {
            Integer idImagem = params.getInt("idImagem");
            imagem = realm.where(Imagem.class).equalTo("id", idImagem).findFirst();
            imageView.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(imagem.getFoto())));

        }
        else {
            realm.beginTransaction();
            imagem = new Imagem();
            imagem.setId(usuario.getImagens().size() + 1);
            realm.commitTransaction();
        }


        usuario = realm.where(Usuario.class).findFirst();
    }

    public void abrirGaleria(View view)
    {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                imageView.setImageBitmap(scaled);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

        }

    }

    public void salvarImagem(View view)
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if ( imageView.getDrawable() == null) {
            Toast.makeText(this, "Favor selecionar uma imagem válida", Toast.LENGTH_SHORT).show();
        }
        else {
            Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            realm.beginTransaction();

            imagem.setFoto(byteArray);
            usuario.getImagens().add(imagem);

            realm.commitTransaction();

            abrirImagens();
        }
    }

    public void abrirCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void abrirImagens()
    {
        Intent imagens = new Intent(this, ImagensActivity.class);
        startActivity(imagens);
        this.finish();
    }



}

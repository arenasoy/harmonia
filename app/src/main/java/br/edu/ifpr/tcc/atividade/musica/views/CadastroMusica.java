package br.edu.ifpr.tcc.atividade.musica.views;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;

public class CadastroMusica extends BaseDrawerActivity {

    private Realm realm;
    private Usuario usuario;
    private String uri = "";
    private Musica musica;
    private TextView caminhoTV;
    private String nome = "";
    private String artista = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_musica, null, false);
        drawerLayout.addView(contentView, 0);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        caminhoTV = (TextView) findViewById(R.id.caminhoTV);

        mudarTitulo("Cadastro de música");

        mudarItem(R.id.configuracoes);

        Bundle params = getIntent().getExtras();

        if (params != null) {
            Integer idMusica = params.getInt("idMusica");
            musica = realm.where(Musica.class).equalTo("id", idMusica).findFirst();
            caminhoTV.setText(musica.getCaminho());
        }

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        usuario = ((Global)getApplication()).getUsuario();
    }


    public void salvarMusica(View view) {

        if (uri == null || uri.equals("")) {
            Toast.makeText(this, "É preciso selecionar uma música", Toast.LENGTH_SHORT).show();
        }
        else {
            realm.beginTransaction();

            if (musica == null) {
                musica = realm.createObject(Musica.class);
                musica.setId(realm.where(Musica.class).max("id").intValue() + 1);
            }
            musica.setCaminho(uri);
            musica.setNome(nome);
            realm.commitTransaction();

            abrirMusicas();
        }

    }

    public void abrirMusicas()
    {
        Intent musicas = new Intent(this, MusicasActivity.class);
        startActivity(musicas);
        this.finish();
    }

    public void abrirMusicas(View view)
    {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                }

                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 10) {
            Uri uriSound = data.getData();
            uri = uriSound.toString();

            nome = "Não foi possível identificar o nome da música";
            String scheme = uriSound.getScheme();
            if (scheme.equals("file")) {
                nome = uriSound.getLastPathSegment();
            }
            else if (scheme.equals("content")) {
                String[] proj = { MediaStore.Images.Media.TITLE };
                Cursor cursor = getApplicationContext().getContentResolver().query(uriSound, proj, null, null, null);
                if (cursor != null && cursor.getCount() != 0) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                    cursor.moveToFirst();
                    nome = cursor.getString(columnIndex);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }

            caminhoTV.setText(nome);
        }
    }

    private String getRealPathFromURI(Uri uri) {
        File myFile = new File(uri.getPath().toString());
        String s = myFile.getAbsolutePath();
        return s;
    }
}
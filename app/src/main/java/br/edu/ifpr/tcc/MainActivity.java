package br.edu.ifpr.tcc;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.solicitarAjuda.SolicitarAjuda;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmList;

import static android.R.attr.bitmap;

public class MainActivity extends Activity {

    private Realm realm;
    private Usuario usuario;
    private List<String> permissoesNaoConcedidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));

        permissoesNaoConcedidas = new ArrayList<String>();

        verificarPermissoes();

        if (permissoesNaoConcedidas.size() > 0) {

            Dialog dialog = abrirDialog();
            dialog.show();

        } else {
            verificarCadastro();
        }
    }

    @NonNull
    private Dialog abrirDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.permissoes_dialog, null);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogLayout);

        final TextView permissoes = (TextView) dialogLayout.findViewById(R.id.textViewPermsList);
        permissoes.setText(processarPerms(permissoesNaoConcedidas, permissoes.getText().toString()));
        Button ok = (Button) dialogLayout.findViewById(R.id.fecharDialogBt);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        return dialog;
    }

    private CharSequence processarPerms(List<String> permissoesNaoConcedidas, String base) {
        String temp = "";

        for (String s:
             permissoesNaoConcedidas) {
            base += "\n" + s;
        }

        return base;
    }

    public void verificarPermissoes()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("Armazenamento");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("Contatos");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("Câmera");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("Local");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("SMS");
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissoesNaoConcedidas.add("Telefone");
        }

        for (String s:
             permissoesNaoConcedidas) {
            Log.e("Permissão", s);
        }
    }

    public void cadastrar()
    {
        realm.beginTransaction();
        usuario = realm.createObject(Usuario.class);
        usuario.setId(realm.where(Usuario.class).max("id").intValue() + 1);

        usuario.setAtaques(new RealmList<Ataque>());
        usuario.setContatos(new RealmList<Contato>());
        usuario.setFrases(new RealmList<Frase>());
        usuario.setImagens(new RealmList<Imagem>());
        usuario.setMusicas(new RealmList<Musica>());
        usuario.setRemedios(new RealmList<Remedio>());

        //TODO alarme
        //TODO trocar icone remedio
        cadastrarFrases();
        cadastrarMusicas();
        //cadastrarImagens();

        realm.commitTransaction();

        Intent utilizar = new Intent(getApplicationContext(), ComoUtilizarActivity.class);
        startActivity(utilizar);
    }

    private void cadastrarImagens() {

            Imagem imagem = realm.createObject(Imagem.class);
            imagem.setId(realm.where(Imagem.class).max("id").intValue() + 1);

            Bitmap bitmap = (BitmapFactory.decodeResource(getResources(), R.raw.imagem1));

            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] byteArray = byteBuffer.array();

            imagem.setFoto(byteArray);

            Imagem imagem2 = realm.createObject(Imagem.class);
            imagem2.setId(realm.where(Imagem.class).max("id").intValue() + 1);

            bitmap = (BitmapFactory.decodeResource(getResources(), R.raw.imagem2));

            size = bitmap.getRowBytes() * bitmap.getHeight();
            byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byteArray = byteBuffer.array();

            imagem2.setFoto(byteArray);

            Imagem imagem3 = realm.createObject(Imagem.class);
            imagem3.setId(realm.where(Imagem.class).max("id").intValue() + 1);

            bitmap = (BitmapFactory.decodeResource(getResources(), R.raw.imagem3));

            size = bitmap.getRowBytes() * bitmap.getHeight();
            byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byteArray = byteBuffer.array();

            imagem3.setFoto(byteArray);

    }

    private void cadastrarMusicas() {
        Musica musica = realm.createObject(Musica.class);
        musica.setId(realm.where(Musica.class).max("id").intValue() + 1);

        musica.setCaminho((Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.calm)).toString());
        musica.setNome("Calm - Silent Partner");

        Musica musica2 = realm.createObject(Musica.class);
        musica2.setId(realm.where(Musica.class).max("id").intValue() + 1);

        musica2.setCaminho((Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.faroutman)).toString());
        musica2.setNome("Far Out Man - Jingle Punks");

        Musica musica3 = realm.createObject(Musica.class);
        musica3.setId(realm.where(Musica.class).max("id").intValue() + 1);

        musica3.setCaminho((Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.stalemate)).toString());
        musica3.setNome("Stale Mate - Jingle Punks");
    }

    private void cadastrarFrases() {
        Frase frase = realm.createObject(Frase.class);
        frase.setTexto("Conte de trás pra frente, de três em três, começando do 100");
        frase.setId(realm.where(Frase.class).max("id").intValue() + 1);

        Frase frase2 = realm.createObject(Frase.class);
        frase2.setTexto("Faça uma lista com os nomes dos presidentes em ordem cronológica");
        frase2.setId(realm.where(Frase.class).max("id").intValue() + 1);

        Frase frase3 = realm.createObject(Frase.class);
        frase3.setTexto("Recite o seu poema favorito");
        frase3.setId(realm.where(Frase.class).max("id").intValue() + 1);

        Frase frase4 = realm.createObject(Frase.class);
        frase4.setTexto("Recite sua música favorita");
        frase4.setId(realm.where(Frase.class).max("id").intValue() + 1);

    }

    public void verificarCadastro()
    {
        usuario = realm.where(Usuario.class).findFirst();

        if ( usuario == null )
        {
            cadastrar();
        }
        else {
            entrar();
        }
        ((Global) this.getApplication()).setUsuario(usuario);

    }

    public void entrar()
    {
        Intent solicitarAjuda = new Intent(this, SolicitarAjuda.class);
        startActivity(solicitarAjuda);
        this.finish();
    }

}

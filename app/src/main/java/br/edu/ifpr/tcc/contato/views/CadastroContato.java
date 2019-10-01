package br.edu.ifpr.tcc.contato.views;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.menuMetodos.MenuMetodosActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmList;

public class CadastroContato extends BaseDrawerActivity {

    private Realm realm;
    private Contato contato;
    private EditText nomeContatoTF;
    private EditText telefoneContatoTF;
    private CheckBox enviarSms;
    private CheckBox discagemRapida;
    private CountryCodePicker codigoNumero;
    private String contactID;
    private Uri uriContact;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_contato, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de contato");

        mudarItem(R.id.configuracoes);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        usuario = ((Global)getApplication()).getUsuario();

        configurarCampos();

        Bundle params = getIntent().getExtras();
        if (params != null) {
            configurarEditando(params);
        }

    }

    public void abrirAgenda(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void configurarEditando(Bundle params) {
        Integer idContato = params.getInt("idContato");
        contato = realm.where(Contato.class).equalTo("id", idContato).findFirst();
        nomeContatoTF.setText(contato.getNome());
        codigoNumero.setCountryForNameCode(contato.getCodigoNumero());
        telefoneContatoTF.setText(contato.getNumero());
        if (contato.isEnviarSMS()) {
            enviarSms.setChecked(true);
        } else {
            enviarSms.setChecked(false);
        }

        if (contato.isDiscagemRapida()) {
            discagemRapida.setChecked(true);
        } else {
            discagemRapida.setChecked(false);
        }
    }

    private void configurarCampos() {
        nomeContatoTF = (EditText) findViewById(R.id.nomeContatoTF);
        telefoneContatoTF = (EditText) findViewById(R.id.telefoneContatoTF);
        enviarSms = (CheckBox) findViewById(R.id.checkBoxEnviarSMS);
        discagemRapida = (CheckBox) findViewById(R.id.checkBoxDiscagemRapida);
        codigoNumero = (CountryCodePicker) findViewById(R.id.codigoNumero);
    }

    public void salvarContato(View view) {

        verificarPermissoes();

        realm.beginTransaction();

        setContato();

        realm.commitTransaction();
    }


    private void verificarPermissoes() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {

            }
            requestPermissions(new String[] {Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            } else
            {
                Toast.makeText(this, "Permissão é necessária", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setContato() {

        if (nomeContatoTF.getText().toString().equals("") || telefoneContatoTF.getText().toString().equals(""))
        {
            if (nomeContatoTF.getText().toString().equals("")) {
                nomeContatoTF.setError("Nome é obrigatório");
            }
            if (telefoneContatoTF.getText().toString().equals("")) {
                telefoneContatoTF.setError("Telefone é obrigatório");
            }
        }
        else {

            if (contato == null) {
                contato = realm.createObject(Contato.class);
                int id = realm.where(Contato.class).max("id").intValue() + 1;
                contato.setId(id);
            }

            contato.setNome(nomeContatoTF.getText().toString());
            contato.setCodigoNumero(codigoNumero.getSelectedCountryCode());
            contato.setNumero(telefoneContatoTF.getText().toString());
            contato.setEnviarSMS(enviarSms.isChecked());
            contato.setDiscagemRapida(discagemRapida.isChecked());

            abrirContatos();
        }
    }

    public void abrirContatos() {
        Intent contatos = new Intent(this, ContatosActivity.class);
        startActivity(contatos);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mudarItem(R.id.configuracoes);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        uriContact = data.getData();

        switch (reqCode) {
            case (1):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
                    if (c.moveToFirst()) {
                        String nome = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        nomeContatoTF.setText(nome);
                        c.close();
                        retrieveContactNumber();
                    }
                    break;
                }

        }

    }

    private void retrieveContactNumber() {

        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            telefoneContatoTF.setText(contactNumber);
        }

        cursorPhone.close();
    }


}
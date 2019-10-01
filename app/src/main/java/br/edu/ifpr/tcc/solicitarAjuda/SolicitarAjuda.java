package br.edu.ifpr.tcc.solicitarAjuda;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.contato.views.CadastroContato;
import br.edu.ifpr.tcc.localizacao.Localizacao;
import br.edu.ifpr.tcc.menuMetodos.MenuMetodosActivity;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class SolicitarAjuda extends BaseDrawerActivity {

    private Realm realm;
    //TODO pedir permissões
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_ajuda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mudarTitulo("Solicitar ajuda");

        mudarItem(0);

        realm = Realm.getInstance(RealmConfig.getConfig(getApplicationContext()));
    }

    public void solicitarAjuda(View view)
    {
        if (!hasContatos()) {
            verificarUsuario();
        } else {
            abrirAtividades();
            new EnviarMensagem().execute("");
        }
        registrarAtaque();
    }

    public void registrarAtaque()
    {
        realm.beginTransaction();
        Ataque ataque = realm.createObject(Ataque.class);
        int id = realm.where(Ataque.class).max("id").intValue() + 1;
        ataque.setId(id);
        ataque.setDataOcorrencia(Calendar.getInstance().getTime());
        realm.commitTransaction();
    }

    private void verificarUsuario() {
        new AlertDialog.Builder(SolicitarAjuda.this)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setTitle("Aviso!")
                .setMessage("Não há nenhum contato cadastrado")
                .setNeutralButton("Cadastrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), CadastroContato.class));
                    }

                })
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        abrirAtividades();
                    }
                })
                .show();
    }

    public boolean hasContatos() {
        RealmResults<Contato> contatos = realm.allObjects(Contato.class);
        if (contatos.size() == 0) {
            return false;
        }
        return true;
    }

    public void abrirAtividades()
    {
        startActivity(new Intent(getApplicationContext(), MenuMetodosActivity.class));
        this.finish();
    }

    public String localizar()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location location;

        Double latitude;
        Double longitude;

        try {
            location = getLocation(locationManager);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (Exception e) {
            latitude = 0.0;
            longitude = 0.0;
            e.printStackTrace();
            Log.e("latitude", latitude+"");
            Log.e("longitude", longitude+"");
        }

        if (Localizacao.getEndereco(latitude, longitude) != null) {
            return "Preciso de ajuda! Estou em: " + Localizacao.getEndereco(latitude, longitude) + ". http://maps.google.com/?q=" + latitude + "," + longitude + "";
        }
        return "Preciso de ajuda! Estou aqui: http://maps.google.com/?q=" + latitude + "," + longitude + "";
    }

    private Location getLocation(LocationManager locationManager) {
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {  }

        Location location;
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        return location;
    }

    private class EnviarMensagem extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            enviarMensagens();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void enviarMensagens()
    {
        realm = Realm.getInstance(RealmConfig.getConfig(this));
        RealmResults<Contato> contatos = realm.allObjects(Contato.class);
        String mensagem = localizar();
        for (Contato contato : contatos) {
            if (contato.isEnviarSMS()) {
                enviarMensagem(contato.getCodigoNumero() + contato.getNumero(), mensagem);
            }
        }
    }


    public void enviarMensagem(String phoneNumber, String message)
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);


    }
}

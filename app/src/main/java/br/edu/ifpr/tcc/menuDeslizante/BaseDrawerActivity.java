package br.edu.ifpr.tcc.menuDeslizante;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import br.edu.ifpr.tcc.ComoUtilizarActivity;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.SobreActivity;
import br.edu.ifpr.tcc.ataque.views.AtaquesActivity;
import br.edu.ifpr.tcc.configuracoes.Configuracoes;
import br.edu.ifpr.tcc.menuMetodos.MenuMetodosActivity;
import br.edu.ifpr.tcc.solicitarAjuda.SolicitarAjuda;
import br.edu.ifpr.tcc.remedio.views.RemediosActivity;

/**
 * Created by diego on 15/06/2016.
 */
public class BaseDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    protected DrawerLayout drawerLayout;
    protected Toolbar toolbar;
    protected FrameLayout frameLayout;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.content_frame);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void mudarTitulo(String titulo)
    {
        setTitle(titulo);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*to prevent current item select over and over
        if (item.isChecked()){
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }*/
        if (id == R.id.solicitar_ajuda) {
            startActivity(new Intent(getApplicationContext(), SolicitarAjuda.class));
        } else if (id == R.id.acalme_me) {
            startActivity(new Intent(getApplicationContext(), MenuMetodosActivity.class));
        } else if (id == R.id.gerenciar_remedios) {
            startActivity(new Intent(getApplicationContext(), RemediosActivity.class));
        } else if (id == R.id.historico_ataques) {
            startActivity(new Intent(getApplicationContext(), AtaquesActivity.class));
        } else if (id == R.id.configuracoes) {
            startActivity(new Intent(getApplicationContext(), Configuracoes.class));
        } else if (id == R.id.comoUtilizar) {
            startActivity(new Intent(getApplicationContext(), ComoUtilizarActivity.class));
        } else if (id == R.id.sobre) {
            startActivity(new Intent(getApplicationContext(), SobreActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void mudarItem(int id)
    {
        navigationView.setCheckedItem(id);
    }
}
package br.edu.ifpr.tcc;

import android.app.Application;

import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.usuario.Usuario;

/**
 * Created by diego on 18/09/2016.
 */
public class Global extends Application {

    private Usuario usuario;
    private Remedio remedio;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Remedio getRemedio() {
        return remedio;
    }

    public void setRemedio(Remedio remedio) {
        this.remedio = remedio;
    }

}
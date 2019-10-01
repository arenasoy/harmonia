package br.edu.ifpr.tcc.contato;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by diego on 08/06/2016.
 */
public class Contato extends RealmObject {

    @PrimaryKey
    private int id;

    private String nome;
    private String numero;
    private boolean enviarSMS;
    private boolean discagemRapida;
    private String codigoNumero;

    public boolean isEnviarSMS() {
        return enviarSMS;
    }

    public void setEnviarSMS(boolean enviarSMS) {
        this.enviarSMS = enviarSMS;
    }

    public boolean isDiscagemRapida() {
        return discagemRapida;
    }

    public void setDiscagemRapida(boolean discagemRapida) {
        this.discagemRapida = discagemRapida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodigoNumero() {
        return codigoNumero;
    }

    public void setCodigoNumero(String codigoNumero) {
        this.codigoNumero = codigoNumero;
    }
}
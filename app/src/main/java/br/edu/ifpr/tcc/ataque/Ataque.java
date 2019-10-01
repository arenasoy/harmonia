package br.edu.ifpr.tcc.ataque;

import java.util.Date;
import java.util.List;

import br.edu.ifpr.tcc.realm.RealmString;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Enzo on 08/06/2016.
 */
public class Ataque extends RealmObject {

    @PrimaryKey
    private int id;

    private Date dataOcorrencia;
    private Date ultimoEnviado;
    private String localAtaque;
    private String pensamentoAntes;
    private String pensamentoDepois;
    private RealmList<RealmString> sensacoesAntes;
    private String sensacoesAntesOutros;
    private RealmList<RealmString> sensacoesDepois;
    private String sensacoesDepoisOutros;
    private RealmList<RealmString> sentimentosAntes;
    private String sentimentosAntesOutros;
    private RealmList<RealmString> sentimentosDepois;
    private String sentimentosDepoisOutros;
    private String acaoAcalmar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Date getUltimoEnviado() {
        return ultimoEnviado;
    }

    public void setUltimoEnviado(Date ultimoEnviado) {
        this.ultimoEnviado = ultimoEnviado;
    }

    public RealmList<RealmString> getSensacoesAntes() {
        return sensacoesAntes;
    }

    public void setSensacoesAntes(RealmList<RealmString> sensacoesAntes) {
        this.sensacoesAntes = sensacoesAntes;
    }

    public String getSensacoesAntesOutros() {
        return sensacoesAntesOutros;
    }

    public void setSensacoesAntesOutros(String sensacoesAntesOutros) {
        this.sensacoesAntesOutros = sensacoesAntesOutros;
    }

    public RealmList<RealmString> getSensacoesDepois() {
        return sensacoesDepois;
    }

    public void setSensacoesDepois(RealmList<RealmString> sensacoesDepois) {
        this.sensacoesDepois = sensacoesDepois;
    }

    public String getSensacoesDepoisOutros() {
        return sensacoesDepoisOutros;
    }

    public void setSensacoesDepoisOutros(String sensacoesDepoisOutros) {
        this.sensacoesDepoisOutros = sensacoesDepoisOutros;
    }

    public RealmList<RealmString> getSentimentosAntes() {
        return sentimentosAntes;
    }

    public void setSentimentosAntes(RealmList<RealmString> sentimentosAntes) {
        this.sentimentosAntes = sentimentosAntes;
    }

    public String getSentimentosAntesOutros() {
        return sentimentosAntesOutros;
    }

    public void setSentimentosAntesOutros(String sentimentosAntesOutros) {
        this.sentimentosAntesOutros = sentimentosAntesOutros;
    }

    public RealmList<RealmString> getSentimentosDepois() {
        return sentimentosDepois;
    }

    public void setSentimentosDepois(RealmList<RealmString> sentimentosDepois) {
        this.sentimentosDepois = sentimentosDepois;
    }

    public String getSentimentosDepoisOutros() {
        return sentimentosDepoisOutros;
    }

    public void setSentimentosDepoisOutros(String sentimentosDepoisOutros) {
        this.sentimentosDepoisOutros = sentimentosDepoisOutros;
    }

    public String getLocalAtaque() {
        return localAtaque;
    }

    public void setLocalAtaque(String localAtaque) {
        this.localAtaque = localAtaque;
    }

    public String getPensamentoAntes() {
        return pensamentoAntes;
    }

    public void setPensamentoAntes(String pensamentoAntes) {
        this.pensamentoAntes = pensamentoAntes;
    }

    public String getPensamentoDepois() {
        return pensamentoDepois;
    }

    public void setPensamentoDepois(String pensamentoDepois) {
        this.pensamentoDepois = pensamentoDepois;
    }

    public String getAcaoAcalmar() {
        return acaoAcalmar;
    }

    public void setAcaoAcalmar(String acaoAcalmar) {
        this.acaoAcalmar = acaoAcalmar;
    }
}

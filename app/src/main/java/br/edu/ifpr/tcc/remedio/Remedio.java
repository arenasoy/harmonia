package br.edu.ifpr.tcc.remedio;

import java.util.Date;

import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.realm.RealmBoolean;
import br.edu.ifpr.tcc.realm.RealmDate;
import br.edu.ifpr.tcc.realm.RealmString;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by diego on 08/06/2016.
 */
public class Remedio extends RealmObject {

    @PrimaryKey
    private int id;

    private Imagem imagem;

    private String nome;
    private RealmList<RealmBoolean> diasSemana;
    private RealmList<HoraDose> horaDoses;
    private RealmList<RealmBoolean> diasMes;

    private String intervalo;
    private Date dataInicio;
    private int repetir;
    private int repetirHoras;
    private boolean ligado;


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

    public RealmList<RealmBoolean> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(RealmList<RealmBoolean> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Imagem getImagem() {
        return imagem;
    }

    public void setImagem(Imagem imagem) {

        this.imagem = imagem;
    }

    public RealmList<RealmBoolean> getDiasMes() {
        return diasMes;
    }

    public void setDiasMes(RealmList<RealmBoolean> diasMes) {
        this.diasMes = diasMes;
    }

    public int getRepetir() {
        return repetir;
    }

    public void setRepetir(int repetir) {
        this.repetir = repetir;
    }

    public int getRepetirHoras() {
        return repetirHoras;
    }

    public void setRepetirHoras(int repetirHoras) {
        this.repetirHoras = repetirHoras;
    }

    public RealmList<HoraDose> getHoraDoses() {
        return horaDoses;
    }

    public void setHoraDoses(RealmList<HoraDose> horaDoses) {
        this.horaDoses = horaDoses;
    }

    public boolean isLigado() {
        return ligado;
    }

    public void setLigado(boolean ligado) {
        this.ligado = ligado;
    }
}

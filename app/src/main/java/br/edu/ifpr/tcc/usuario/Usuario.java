package br.edu.ifpr.tcc.usuario;

import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.remedio.Remedio;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by diego on 08/06/2016.
 */
public class Usuario extends RealmObject{

    @PrimaryKey
    private int id;

    private String nome;
    private String email;

    private RealmList<Remedio> remedios;
    private RealmList<Ataque> ataques;
    private RealmList<Contato> contatos;
    private RealmList<Frase> frases;
    private RealmList<Imagem> imagens;
    private RealmList<Musica> musicas;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RealmList<Remedio> getRemedios() {
        return remedios;
    }

    public void setRemedios(RealmList<Remedio> remedios) {
        this.remedios = remedios;
    }

    public RealmList<Ataque> getAtaques() {
        return ataques;
    }

    public void setAtaques(RealmList<Ataque> ataques) {
        this.ataques = ataques;
    }

    public RealmList<Contato> getContatos() {
        return contatos;
    }

    public void setContatos(RealmList<Contato> contatos) {
        this.contatos = contatos;
    }

    public RealmList<Frase> getFrases() {
        return frases;
    }

    public void setFrases(RealmList<Frase> frases) {
        this.frases = frases;
    }

    public RealmList<Imagem> getImagens() {
        return imagens;
    }

    public void setImagens(RealmList<Imagem> imagens) {
        this.imagens = imagens;
    }

    public RealmList<Musica> getMusicas() {
        return musicas;
    }

    public void setMusicas(RealmList<Musica> musicas) {
        this.musicas = musicas;
    }
}

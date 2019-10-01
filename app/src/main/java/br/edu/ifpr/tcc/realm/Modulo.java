package br.edu.ifpr.tcc.realm;

import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.atividade.frase.Frase;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.contato.Contato;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.annotations.RealmModule;

/**
 * Created by diego on 08/06/2016., Remedio.class, Ataque.class, Contato.class
 */
@RealmModule(classes = {Usuario.class, Remedio.class, Ataque.class, Contato.class, RealmString.class, RealmDate.class, Frase.class, Imagem.class, Musica.class, RealmBoolean.class, HoraDose.class})
public class Modulo {

}

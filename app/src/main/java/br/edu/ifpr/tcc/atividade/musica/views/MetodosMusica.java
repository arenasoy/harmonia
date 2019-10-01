package br.edu.ifpr.tcc.atividade.musica.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.musica.MediaPlayerDataSourceSetter;
import br.edu.ifpr.tcc.atividade.musica.Musica;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import io.realm.Realm;
import io.realm.RealmResults;

public class MetodosMusica extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Musica> musicasRealm;
    private List<Musica> musicas;

    private MediaPlayer mediaPlayer;
    private Handler mHandler = new Handler();
    private Button play, pause, forward, backward;
    private SeekBar seekBar;
    private int musicaAtual;
    private boolean iniciou = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_metodos_musica, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Músicas");

        mudarItem(R.id.acalme_me);

        play = (Button) findViewById(R.id.play);
        backward = (Button) findViewById(R.id.backward);
        backward.setEnabled(false);
        forward = (Button) findViewById(R.id.forward);
        forward.setEnabled(false);
        pause = (Button) findViewById(R.id.pause);
        pause.setEnabled(false);

        realm = Realm.getInstance(RealmConfig.getConfig(this));
        musicas = converterLista(realm.allObjects(Musica.class));

        if (!(musicas.size() > 0)) {
            play.setEnabled(false);
            mostrarToast();
        }

        TextView quantidadeTV = (TextView) findViewById(R.id.quantidadeMusicasTV);

        quantidadeTV.setText("Quantidade de músicas cadastradas: " + musicas.size());
    }

    private List<Musica> converterLista(RealmResults<Musica> results) {
        List<Musica> musicas = new ArrayList<Musica>();
        for (Musica mus:
                results) {
            musicas.add(mus);
        }
        return musicas;
    }

    private void prepararMusica() {
        if (checarMusicas()) {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

            if (musicas.get(musicaAtual).getId() <= 3) {
                if (musicas.get(musicaAtual).getId() == 1) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.calm);
                } else if (musicas.get(musicaAtual).getId() == 2) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.faroutman);
                } else if (musicas.get(musicaAtual).getId() == 3) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.stalemate);
                }
            } else {
                String audioFile = musicas.get(musicaAtual).getCaminho();
                mediaPlayer = new MediaPlayer();
                try {
                    MediaPlayerDataSourceSetter.setMediaPlayerDataSource(this, mediaPlayer, audioFile);
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            TextView nomeMusica = (TextView) findViewById(R.id.nomeMusicaPlayerTv);
            nomeMusica.setText(musicas.get(musicaAtual).getNome());
        } else {
            mostrarToast();
        }
    }

    private void mostrarToast() {
        Toast.makeText(this, "Nenhuma música cadastrada", Toast.LENGTH_LONG).show();
    }

    private boolean checarMusicas() {
        if (musicas.size()>0) {
            return true;
        }
        return false;
    }

    private void musicaSeguinte() {
        if (musicaAtual+1 >= musicas.size()) {
            musicaAtual=0;
        } else {
            musicaAtual++;
        }
        prepararMusica();
        tocarMusica();
    }

    private void musicaAnterior() {
        if (musicaAtual-1 <= 0) {
            musicaAtual=musicas.size()-1;
        } else {
            musicaAtual--;
        }
        prepararMusica();
        tocarMusica();
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(mediaPlayer != null) {
                int mDuration = mediaPlayer.getDuration();
                seekBar.setMax(mDuration);

                //update total time text view
                TextView totalTime = (TextView) findViewById(R.id.totalTime);
                totalTime.setText(getTimeString(mDuration));

                //set progress to current position
                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(mCurrentPosition);

                //update current time text view
                TextView currentTime = (TextView) findViewById(R.id.currentTime);
                currentTime.setText(getTimeString(mCurrentPosition));

                //handle drag on seekbar
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer != null && fromUser){
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });
            }
            mHandler.postDelayed(this, 10);
        }
    };

    public void play(View view){
        if (!iniciou) {
            musicaAtual = 0;
            prepararMusica();
            iniciou = true;
            backward.setEnabled(true);
            forward.setEnabled(true);
            pause.setEnabled(true);
            setSeekbar();
        }
        if (checarMusicas()) {
            tocarMusica();
        } else {
            mostrarToast();
        }
    }

    private void tocarMusica() {
        mediaPlayer.start();
        play.setEnabled(false);
        pause.setEnabled(true);
    }

    private void setSeekbar() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(final MediaPlayer mp) {
                mp.start();
                seekBar = (SeekBar) findViewById(R.id.seekBar);
                mRunnable.run();
            }
        });
    }

    public void pause(View view){
        if (checarMusicas()) {
            mediaPlayer.pause();
            play.setEnabled(true);
            pause.setEnabled(false);
        } else {
            mostrarToast();
        }
    }

    public void seekForward(View view){
        musicaSeguinte();
    }

    public void seekBackward(View view){
        if (checarMusicas()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if(currentPosition <= 3000){
                musicaAnterior();
            } else {
                mediaPlayer.seekTo(0);
            }
        }
    }

    public void onBackPressed(){
        super.onBackPressed();

        desativarMedia();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        desativarMedia();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (musicas.size() > 0) {
            musicaAtual = 0;
            prepararMusica();
        }
    }

    private void desativarMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000*60*60);
        long minutes = ( millis % (1000*60*60) ) / (1000*60);
        long seconds = ( ( millis % (1000*60*60) ) % (1000*60) ) / 1000;

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

}
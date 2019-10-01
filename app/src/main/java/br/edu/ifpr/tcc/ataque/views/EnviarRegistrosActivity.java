package br.edu.ifpr.tcc.ataque.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.ataque.Ataque;
import br.edu.ifpr.tcc.ataque.adapter.AtaqueAdapter;
import br.edu.ifpr.tcc.ataque.adapter.RegistrosAdapter;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.realm.RealmString;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class EnviarRegistrosActivity extends BaseDrawerActivity {

    private Realm realm;
    private RealmResults<Ataque> ataques;
    private RegistrosAdapter ataqueAdapter;
    private ListView ataquesEnviarLV;
    private SimpleDateFormat formatoAssunto;
    private List<Integer> idAtaquesEnviar;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_enviar_registros, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Envio de histórico");

        mudarItem(R.id.historico_ataques);

        idAtaquesEnviar = new ArrayList<Integer>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabEnviar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarRegistros();
            }
        });

        formatoAssunto = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        ataques = realm.where(Ataque.class).findAllSorted("dataOcorrencia", Sort.DESCENDING);

        ataqueAdapter = new RegistrosAdapter(this, ataques, true, idAtaquesEnviar);

        ataquesEnviarLV = (ListView)findViewById(R.id.ataquesEnviarLV);
        ataquesEnviarLV.setAdapter(ataqueAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void enviarRegistros() {

        if (idAtaquesEnviar.size() == 0)
        {
            Toast.makeText(this, "Nenhum ataque selecionado", Toast.LENGTH_SHORT).show();
        }
        else {
            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_pedir_email, null);

            final Dialog dialogEmail = new Dialog(this);
            dialogEmail.setContentView(dialoglayout);

            final EditText emailDialogET = (EditText) dialoglayout.findViewById(R.id.emailDialogET);



            final Button enviar = (Button) dialoglayout.findViewById(R.id.enviarEmailBT);
            enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    realm.beginTransaction();
                    RealmResults<Ataque> ataquesResult = realm.allObjects(Ataque.class);
                    List<Ataque> ataques = new ArrayList<Ataque>();
                    ataques.addAll(ataquesResult);
                    List<Ataque> ataquesEnviar = new ArrayList<Ataque>();
                    for (Ataque a :
                            ataques) {
                        for (Integer i :
                                idAtaquesEnviar) {
                            if (a.getId() == i.intValue()) {
                                ataquesEnviar.add(a);
                                a.setUltimoEnviado(Calendar.getInstance().getTime());
                            }
                        }
                    }

                    realm.commitTransaction();
                    email = emailDialogET.getText().toString();
                    gerarPDF(ataquesEnviar);
                    dialogEmail.dismiss();
                }
            });

            dialogEmail.show();
        }

    }

    public void selecionarUmDia(View view) {
        int dias = -1;

        Date diaAnterior = getPrazo(dias);
        marcarOpcoes(diaAnterior);
    }


    public void selecionarSeteDias(View view) {
        int dias = -7;

        Date diaAnterior = getPrazo(dias);
        marcarOpcoes(diaAnterior);
    }

    public void selecionarTrintaDias(View view) {
        int dias = -30;

        Date diaAnterior = getPrazo(dias);
        marcarOpcoes(diaAnterior);
    }


    private Date getPrazo(int dias) {
        //pegar data atual - 1
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    private void marcarOpcoes(Date diaMin) {
        Calendar cal;// pegar dia seguinte
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date diaSeguinte = cal.getTime();

        //verificar ataques da lista que tem data igual
        RealmResults<Ataque> ataques = getAtaques(diaMin, diaSeguinte);

        for (Ataque ataque:
                ataques) {
            if (!verificarLista(ataque.getId())) {
                idAtaquesEnviar.add(ataque.getId());
            }
        }

        List<View> rows = pegarRows();
        marcarCheckboxes(ataques, rows);
    }

    private RealmResults<Ataque> getAtaques(Date diaAnterior, Date diaSeguinte) {
        return realm.where(Ataque.class).greaterThanOrEqualTo("dataOcorrencia", diaAnterior)
                .lessThan("dataOcorrencia", diaSeguinte).findAll();
    }

    @NonNull
    private List<View> pegarRows() {
        List<View> rows = new ArrayList<View>();
        for (int i = 0; i < ataquesEnviarLV.getChildCount(); i++) {
            rows.add(ataquesEnviarLV.getChildAt(i));
        }
        return rows;
    }

    private void marcarCheckboxes(RealmResults<Ataque> ataques, List<View> rows) {
        for (View v:
                rows) {
            for (Ataque ataque:
                    ataques) {
                if (((Integer)v.getTag()).intValue() == ataque.getId()) {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.selecionarAtaqueCB);
                    cb.setChecked(true);
                }
            }
        }
    }

    private boolean verificarLista(int id) {
        for (Integer integer:
                idAtaquesEnviar) {
            if (integer.intValue() == id) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public void gerarPDF(List<Ataque> ataques) {
        Document doc = new Document();

        SimpleDateFormat sdfCaminho = new SimpleDateFormat("dd-MM-yyyy-HH-mm");

        String outpath = Environment.getExternalStorageDirectory() + "/historico"
                + sdfCaminho.format(Calendar.getInstance().getTime()) + ".pdf";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }

        try {

            PdfWriter.getInstance(doc, new FileOutputStream(outpath));

            doc.open();

            Font fonteTitulo = new Font();
            fonteTitulo.setColor(BaseColor.BLACK);
            fonteTitulo.setStyle(Font.BOLD);

            Font f = new Font();
            f.setStyle(Font.ITALIC);

            doc.add(new Paragraph("HISTÓRICO DE ATAQUES DE PÂNICO", fonteTitulo));
            doc.add(new Paragraph("Criado no dia " +
                    sdf.format(Calendar.getInstance().getTime()) + " pelo aplicativo Harmonia", f));

            LineSeparator ls = new LineSeparator();
            doc.add(new Chunk(ls));

            for (Ataque a :
                    ataques) {

                Paragraph p;
                Font s = new Font();
                s.setStyle(Font.ITALIC);
                doc.add(new Paragraph("Data do ataque: " + sdf.format(a.getDataOcorrencia())));
                doc.add(new Paragraph("Local: " + verificarLocal(a)));
                doc.add(new Paragraph("Sentimentos antes do ataque: "));
                p = new Paragraph(gerarSentimentosAntes(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Sentimentos depois do ataque: "));
                p = new Paragraph(gerarSentimentosDepois(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Sensações antes do ataque: "));
                p = new Paragraph(gerarSensacoesAntes(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Sensações depois do ataque: "));
                p = new Paragraph(gerarSensacoesDepois(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Pensamentos antes do ataque: "));
                p = new Paragraph(gerarPensamentosAntes(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Pensamentos depois do ataque: "));
                p = new Paragraph(gerarPensamentosDepois(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                doc.add(new Paragraph("Métodos utilizados para se acalmar: "));
                p = new Paragraph(gerarMetodosUtilizados(a), s);
                p.setIndentationLeft(25);
                doc.add(p);

                LineSeparator l = new LineSeparator();
                doc.add(new Chunk(l));
            }


            doc.close();

            enviarEmail(outpath);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Problema ao gerar o pdf", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void enviarEmail(String path)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Histórico de ataques");
        intent.putExtra(Intent.EXTRA_TEXT,
                "O arquivo anexado é um histórico de ataques gerado pelo aplicativo " +
                "Harmonia em " + formatoAssunto.format(Calendar.getInstance().getTime()));
        Uri uri = Uri.fromFile(new File(path));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Enviar via..."));
    }

    public String verificarLocal(Ataque a)
    {
        if (a.getLocalAtaque() != null && !a.getLocalAtaque().equals("")) {
            return a.getLocalAtaque();
        }
        return "Não informado";
    }

    public String gerarSensacoesAntes(Ataque a)
    {
        String sensacoesAntes = "";
        for (RealmString sensacaoAntes:
                a.getSensacoesAntes()) {
            sensacoesAntes += sensacaoAntes.getTexto() + "\n";
        }
        if (a.getSensacoesAntesOutros() != null)
            sensacoesAntes += a.getSensacoesAntesOutros();
        if (sensacoesAntes != "")
        {
            return "Não informado";
        }

        return "Não informado";
    }

    public String gerarSensacoesDepois(Ataque a)
    {
        String sensacoesDepois = "";
        for (RealmString sensacaoDepois:
                a.getSensacoesDepois()) {
            sensacoesDepois += sensacaoDepois.getTexto() + "\n";
        }
        if (a.getSensacoesDepoisOutros() != null)
            sensacoesDepois += a.getSensacoesDepoisOutros();
        if (sensacoesDepois != "")
        {
            return sensacoesDepois;
        }

        return "Não informado";
    }

    public String gerarPensamentosAntes(Ataque a)
    {
        if (a.getPensamentoAntes() != null && !a.getPensamentoAntes().equals(""))
        {
            return a.getPensamentoAntes();
        }

        return "Não informado";
    }

    public String gerarPensamentosDepois(Ataque a)
    {
        if (a.getPensamentoDepois() != null && !a.getPensamentoDepois().equals(""))
        {
            return a.getPensamentoDepois();
        }

        return "Não informado";
    }

    public String gerarMetodosUtilizados(Ataque a)
    {
        if (a.getAcaoAcalmar() != null && !a.getAcaoAcalmar().equals(""))
        {
            return a.getAcaoAcalmar();
        }

        return "Não informado";
    }

    public String gerarSentimentosAntes(Ataque a)
    {
        String sentimentosAntes = "";
        for (RealmString sentimentoAntes:
                a.getSentimentosAntes()) {
            sentimentosAntes += sentimentoAntes.getTexto() + "\n";
        }
        if (a.getSentimentosAntesOutros() != null)
            sentimentosAntes += a.getSentimentosAntesOutros();
        if (sentimentosAntes != "")
        {
            return sentimentosAntes;
        }

        return "Não informado";
    }

    public String gerarSentimentosDepois(Ataque a)
    {
        String sentimentosDepois = "";
        for (RealmString sentimentoDepois:
                a.getSentimentosDepois()) {
            sentimentosDepois += sentimentoDepois.getTexto() + "\n";
        }
        if (a.getSensacoesDepoisOutros() != null)
            sentimentosDepois += a.getSentimentosDepoisOutros();
        if (sentimentosDepois != "")
        {
            return sentimentosDepois;
        }

        return "Não informado";
    }
}





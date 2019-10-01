package br.edu.ifpr.tcc.remedio.views;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ifpr.tcc.Global;
import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.atividade.imagem.Imagem;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmBoolean;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.HoraDose;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.remedio.alarme.AlarmReceiver;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmList;

public class CadastroRemedio extends BaseDrawerActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    //Tela de cadastro
    private EditText nomeRemedioTF;
    private static TextView horaDoseTV;
    private TextView intervaloTV;
    private ImageView fotoRemedio;
    private Usuario usuario;

    //Dialog de intervalos
    private Dialog dialog;
    private Dialog dialogDias;
    private Dialog dialogHoras;
    private RadioButton umaVezBT;
    private RadioButton diariamenteBT;
    private RadioButton semanalmenteBT;
    private RadioButton mensalmenteBT;
    private RadioButton cadaDiasBT;
    private RadioButton cadaHorasBT;

    private String repetirCada;

    private SimpleDateFormat sdf;
    private static SimpleDateFormat sdfHora;

    private Realm realm;
    private static Remedio remedio;

    private Calendar inicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_cadastro_remedio, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de remédio");

        mudarItem(R.id.gerenciar_remedios);

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        sdfHora = new SimpleDateFormat("HH:mm");

        usuario = ((Global)getApplication()).getUsuario();

        inicio = Calendar.getInstance();

        nomeRemedioTF = (EditText) findViewById(R.id.nomeRemedioET);
        horaDoseTV = (TextView) findViewById(R.id.horaDoseTV);
        intervaloTV = (TextView) findViewById(R.id.intervaloTV);
        fotoRemedio = (ImageView) findViewById(R.id.fotoRemedio);
        fotoRemedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView imageView = (ImageView) findViewById(R.id.fotoRemedioIV);
                if (imageView != null) {

                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ImageView v = new ImageView(getApplicationContext());
                    v.setImageBitmap(bitmap);
                    new AlertDialog.Builder(getApplicationContext())
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setView(v)
                            .show();

                }
            }
        });

        Bundle params = getIntent().getExtras();

        if (params != null)
        {
            int id  = params.getInt("idRemedio");
            remedio = realm.where(Remedio.class).equalTo("id", id).findFirst();
            nomeRemedioTF.setText(remedio.getNome());
            intervaloTV.setText(remedio.getIntervalo());
            if (remedio.getIntervalo() == null)
                intervaloTV.setText("Uma vez");
            if (remedio.getImagem() != null)
            {
                fotoRemedio.setImageBitmap(BitmapFactory.decodeStream(new ByteArrayInputStream(remedio.getImagem().getFoto())));
            }

            preencherHoraDose();

        }
        else
        {
            realm.beginTransaction();
            remedio = realm.createObject(Remedio.class);
            int id = realm.where(Remedio.class).max("id").intValue() + 1;
            remedio.setId(id);
            remedio.setIntervalo("Uma vez");

            gerarListDiasMes();
            gerarDiasSemana();

            usuario.getRemedios().add(remedio);
            realm.commitTransaction();
        }

        ((Global) this.getApplication()).setRemedio(remedio);


    }


    private void gerarListDiasMes() {
        RealmList<RealmBoolean> diasMes = new RealmList<RealmBoolean>();

        for (int i = 0; i < 31; i++)
        {
            RealmBoolean realmBoolean = realm.createObject(RealmBoolean.class);
            realmBoolean.setBoleano(false);
            diasMes.add(realmBoolean);
        }

        remedio.setDiasMes(diasMes);

    }

    public void abrirIntervalos(View view)
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.activity_intervalo, null);

        dialog = new Dialog(this);
        dialog.setContentView(dialoglayout);

        umaVezBT = (RadioButton) dialog.findViewById(R.id.umaVezBT);
        diariamenteBT = (RadioButton) dialog.findViewById(R.id.diariamenteBT);
        semanalmenteBT = (RadioButton) dialog.findViewById(R.id.semanalmenteBT);
        mensalmenteBT = (RadioButton) dialog.findViewById(R.id.mensalmenteBT);
        cadaDiasBT = (RadioButton) dialog.findViewById(R.id.cadaDiasBT);
        cadaHorasBT = (RadioButton) dialog.findViewById(R.id.cadaHorasBT);

        if (remedio.getIntervalo().equals("Uma vez") || remedio.getIntervalo() == null)
            umaVezBT.setChecked(true);
        else if (remedio.getIntervalo().equals("Diariamente"))
            diariamenteBT.setChecked(true);
        else if (remedio.getIntervalo().equals("Semanalmente"))
            semanalmenteBT.setChecked(true);
        else if (remedio.getIntervalo().equals("Mensalmente"))
            mensalmenteBT.setChecked(true);
        else if (remedio.getIntervalo().equals("Cada X Dias"))
            cadaDiasBT.setChecked(true);
        else if (remedio.getIntervalo().equals("Cada X Horas"))
            cadaHorasBT.setChecked(true);

        semanalmenteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogDiasSemana();
            }
        });

        mensalmenteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogMes();
            }
        });

        cadaDiasBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogCadaDias();
            }
        });

        cadaHorasBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogCadaHoras();
            }
        });

        dialog.show();

    }

    public void mostrarDialogCadaDias()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.repetir_layout, null);

        dialogDias = new Dialog(this);
        dialogDias.setContentView(dialoglayout);

        final EditText cadaDiasTF = (EditText) dialoglayout.findViewById(R.id.cadaDiasTF);
        final TextView cadaDiasTV = (TextView) dialoglayout.findViewById(R.id.cadaDiasTV);
        cadaDiasTV.setText("Cada X Dias");
        Button ok = (Button) dialoglayout.findViewById(R.id.cadaDiasOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetirCada = cadaDiasTF.getText().toString();
                realm.beginTransaction();
                remedio.setRepetir(Integer.valueOf(repetirCada));
                realm.commitTransaction();
                dialogDias.dismiss();
                cadaDiasBT.setText("Cada " + repetirCada + " dias");
            }
        });

        dialogDias.show();
    }

    public void mostrarDialogCadaHoras()
    {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.repetir_layout, null);

        dialogHoras = new Dialog(this);
        dialogHoras.setContentView(dialoglayout);

        final EditText cadaHorasTF = (EditText) dialoglayout.findViewById(R.id.cadaDiasTF);
        final TextView cadaHorasTV = (TextView) dialoglayout.findViewById(R.id.cadaDiasTV);
        cadaHorasTV.setText("Cada X Horas");
        Button ok = (Button) dialoglayout.findViewById(R.id.cadaDiasOk);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repetirCada = cadaHorasTF.getText().toString();
                realm.beginTransaction();
                remedio.setRepetirHoras(Integer.valueOf(repetirCada));
                realm.commitTransaction();
                dialogHoras.dismiss();
                cadaHorasBT.setText("Cada " + repetirCada + " horas");
            }
        });

        dialogHoras.show();
    }

    public void mostrarDialogMes()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        String[] dias = new String[31];

        for (int i = 0; i < dias.length; i++)
        {
            dias[i] = ""+(i+1);
        }

        final boolean[] checkedDias = new boolean[31];

        for (int i = 0; i < checkedDias.length; i++)
        {
            checkedDias[i] = remedio.getDiasMes().get(i).isBoleano();
        }

        final List<String> diasList = Arrays.asList(dias);

        builder.setMultiChoiceItems(dias, checkedDias, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                checkedDias[which] = isChecked;
                String currentItem = diasList.get(which);
            }
        });

        builder.setCancelable(false);

        builder.setTitle("Dias do mês");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                realm.beginTransaction();
                for (int i = 0; i < checkedDias.length; i++)
                {
                    remedio.getDiasMes().get(i).setBoleano(checkedDias[i]);
                }
                realm.commitTransaction();
                dialog.dismiss();
            }
        });


        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void salvarIntervalo(View view)
    {

        realm.beginTransaction();
        if (umaVezBT.isChecked()) {
            intervaloTV.setText("Uma vez");
            remedio.setIntervalo("Uma vez");
            gerarListDiasMes();
            gerarDiasSemana();
        } else if (diariamenteBT.isChecked()) {
            intervaloTV.setText("Diariamente");
            remedio.setIntervalo("Diariamente");
            gerarListDiasMes();
            gerarDiasSemana();
        } else if (semanalmenteBT.isChecked()) {
            intervaloTV.setText("Semanalmente");
            remedio.setIntervalo("Semanalmente");
            gerarListDiasMes();
        } else if (mensalmenteBT.isChecked()) {
            intervaloTV.setText("Mensalmente");
            remedio.setIntervalo("Mensalmente");
            gerarDiasSemana();
        } else if (cadaDiasBT.isChecked()) {
            intervaloTV.setText("Cada " + remedio.getRepetir() + " dias");
            remedio.setIntervalo("Cada X Dias");
            gerarDiasSemana();
            gerarListDiasMes();
        } else if (cadaHorasBT.isChecked()) {
            intervaloTV.setText("Cada " + remedio.getRepetirHoras() + " horas");
            remedio.setIntervalo("Cada X Horas");
            gerarListDiasMes();
            gerarDiasSemana();
        }
        realm.commitTransaction();
        dialog.dismiss();
    }

    private int verificarValor(HoraDose horaDose)
    {
        Calendar c = Calendar.getInstance();
        if (remedio.getIntervalo().equals("Uma vez")) {
            return 0;
        }
        else if(remedio.getIntervalo().equals("Diariamente"))
        {
            return 0;
        }
        else if (remedio.getIntervalo().equals("Semanalmente"))
        {
            int hoje = c.get(Calendar.DAY_OF_WEEK);

            Log.e("Hoje", hoje+"");
            if (remedio.getDiasSemana().get(hoje-1).isBoleano())
            {
                return 0;
            }

            if (hoje < 7 && remedio.getDiasSemana().get(hoje).isBoleano()) {
                return 1;
            }
            if ((hoje < 6 && remedio.getDiasSemana().get(hoje+1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(1).isBoleano()))
            {
                return 2;
            }
            if ((hoje < 5 && remedio.getDiasSemana().get(hoje+2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 1 && remedio.getDiasSemana().get(2).isBoleano()))
            {
                return 3;
            }
            if ((hoje < 4 && remedio.getDiasSemana().get(hoje+3).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(3).isBoleano()))
            {
                return 4;
            }
            if ((hoje < 3 && remedio.getDiasSemana().get(hoje+4).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(4).isBoleano()))
            {
                return 5;
            }
            if ((hoje < 2 && remedio.getDiasSemana().get(hoje+6).isBoleano())
                    || (hoje == 2 && remedio.getDiasSemana().get(0).isBoleano())
                    || (hoje == 3 && remedio.getDiasSemana().get(1).isBoleano())
                    || (hoje == 4 && remedio.getDiasSemana().get(2).isBoleano())
                    || (hoje == 5 && remedio.getDiasSemana().get(3).isBoleano())
                    || (hoje == 6 && remedio.getDiasSemana().get(4).isBoleano())
                    || (hoje == 7 && remedio.getDiasSemana().get(5).isBoleano()))
            {
                return 6;
            }


            return 0;


        }
        else if (remedio.getIntervalo().equals("Mensalmente"))
        {
            int inicio = c.get(Calendar.DAY_OF_MONTH);
            int soma = 0;

            if (remedio.getDiasMes().get(inicio+1).isBoleano())
                return 0;

            for (int i = inicio; i < 31; i++)
            {
                if (remedio.getDiasMes().get(i).isBoleano())
                {
                    inicio = Math.abs(i - c.get(Calendar.DAY_OF_MONTH));
                    return inicio+1;
                }
                else {
                    soma++;
                }
            }


            for (int i = 1; i < inicio; i++)
            {
                if (remedio.getDiasMes().get(i).isBoleano())
                {
                    inicio = soma + i;
                    return inicio;
                }
            }
            Log.e("Mensalmente qnts dias", inicio+"");
        }
        else if (remedio.getIntervalo().equals("Cada X Dias"))
        {
            return 0;
        }
        else if (remedio.getIntervalo().equals("Cada X Horas"))
        {
            return 0;
        }
        return 0;
    }

    private void gerarDiasSemana()
    {
        RealmList<RealmBoolean> diasSemana = new RealmList<RealmBoolean>();

        for (int i = 0; i < 7; i++)
        {
            RealmBoolean realmBoolean = realm.createObject(RealmBoolean.class);
            realmBoolean.setBoleano(false);
            diasSemana.add(realmBoolean);
        }

        remedio.setDiasSemana(diasSemana);
    }

    public void abrirQuantidadeDose(View view)
    {
        startActivity(new Intent(this, HoraDoseActivity.class));
    }

    public void salvarRemedio(View view)
    {
        if (nomeRemedioTF.getText().toString().equals("") || remedio.getHoraDoses().size() == 0) {

            if (nomeRemedioTF.getText().toString().equals("")) {
                nomeRemedioTF.setError("Nome é obrigatório");
            }
            if (remedio.getHoraDoses().size() == 0) {
                Toast.makeText(this, "Horário é obrigatório", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            realm.beginTransaction();
            remedio.setNome(nomeRemedioTF.getText().toString());
            remedio.setLigado(true);
            ImageView imageView = (ImageView) findViewById(R.id.fotoRemedio);

            if (hasImage(imageView)) {

                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Imagem imagem = realm.createObject(Imagem.class);
                imagem.setFoto(byteArray);
                remedio.setImagem(imagem);
            }


            for (HoraDose horaDose :
                    remedio.getHoraDoses()) {

                criarPrimeiroAlarme(horaDose);
            }

            //realm.where(Usuario.class).findFirst().getRemedios().add(remedio);
            realm.commitTransaction();

        /*Intent intent = new Intent(getApplicationContext(), Alarme.class);
        startActivity(intent);
        */
            abrirRemedios();
            this.finish();
        }
    }

    private void criarPrimeiroAlarme(HoraDose horaDose)
    {
        AlarmManager alarm_manager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent my_intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        Calendar c = Calendar.getInstance();
        c.setTime(horaDose.getHora());

        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        c.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DAY_OF_MONTH, verificarValor(horaDose));

        horaDose.setHora(c.getTime());

        Log.i("Soma dias", verificarValor(horaDose) + "");
        my_intent.putExtra("extra", "alarm on");
        my_intent.putExtra("idHoraDose", horaDose.getId());
        my_intent.putExtra("whale_choice", 0);

        PendingIntent pending_intent = PendingIntent.getBroadcast(getApplicationContext(), horaDose.getId(),
                my_intent, PendingIntent.FLAG_ONE_SHOT);

        // set the alarm manager
        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                pending_intent);

        horaDose.setAlarmeCriado(true);
    }

    private boolean hasImage(ImageView view)
    {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public void abrirRemedios()
    {
        startActivity(new Intent(this, RemediosActivity.class));
    }

    public void mostrarDialogDiasSemana()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        String[] dias = new String[]{
                "Domingo",
                "Segunda",
                "Terça",
                "Quarta",
                "Quinta",
                "Sexta",
                "Sábado"
        };

        final boolean[] checkedDias = new boolean[7];

        for (int i = 0; i < checkedDias.length; i++)
        {
            checkedDias[i] = remedio.getDiasSemana().get(i).isBoleano();
        }

        final List<String> diasList = Arrays.asList(dias);

        builder.setMultiChoiceItems(dias, checkedDias, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                checkedDias[which] = isChecked;
                String currentItem = diasList.get(which);
            }
        });

        builder.setCancelable(false);

        builder.setTitle("Dias da semana");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                realm.beginTransaction();
                for (int i = 0; i < checkedDias.length; i++)
                {
                    remedio.getDiasSemana().get(i).setBoleano(checkedDias[i]);
                }
                realm.commitTransaction();
                dialog.dismiss();
            }
        });


        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void abrirGaleria(View view)
    {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                int nh = (int) ( bitmap.getHeight() * (120.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 120, nh, true);
                fotoRemedio.setImageBitmap(scaled);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            fotoRemedio.setImageBitmap(imageBitmap);

        }

    }

    public void salvarImagem(View view)
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        realm.beginTransaction();

        Imagem imagem = new Imagem();
        imagem.setId(remedio.getId());
        imagem.setFoto(byteArray);
        remedio.setImagem(imagem);

        realm.commitTransaction();

    }

    public void abrirCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public static void preencherHoraDose()
    {
        String horaDoses = "";
        if (remedio.getHoraDoses().size() > 0)
        {
            for (HoraDose h:
                    remedio.getHoraDoses()) {
                horaDoses += sdfHora.format(h.getHora())+"("+h.getDose()+")";
                if (h.getId() != remedio.getHoraDoses().last().getId())
                    horaDoses += "; ";
            }
        }

        if (horaDoses == "")
        {
            horaDoses = "Clique para adicionar";
        }

        horaDoseTV.setText(horaDoses);
    }
}
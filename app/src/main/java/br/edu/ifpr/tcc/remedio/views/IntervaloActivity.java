package br.edu.ifpr.tcc.remedio.views;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import java.util.Arrays;
import java.util.List;

import br.edu.ifpr.tcc.R;
import br.edu.ifpr.tcc.menuDeslizante.BaseDrawerActivity;
import br.edu.ifpr.tcc.realm.RealmConfig;
import br.edu.ifpr.tcc.remedio.Remedio;
import br.edu.ifpr.tcc.usuario.Usuario;
import io.realm.Realm;

public class IntervaloActivity extends BaseDrawerActivity {

    private RadioButton umaVezBT;
    private RadioButton diariamenteBT;
    private RadioButton semanalmenteBT;
    private RadioButton mensalmenteBT;

    private ClipData.Item[] dias;
    private boolean[] diasSelecionados;

    private Remedio remedio;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_intervalo, null, false);
        drawerLayout.addView(contentView, 0);

        mudarTitulo("Cadastro de remédio");

        mudarItem(R.id.gerenciar_remedios);

        Bundle params = getIntent().getExtras();

        int idRemedio = params.getInt("idRemedio");

        realm = Realm.getInstance(RealmConfig.getConfig(this));

        remedio = realm.where(Usuario.class).findFirst().getRemedios().get(idRemedio);

        umaVezBT = (RadioButton) findViewById(R.id.umaVezBT);
        diariamenteBT = (RadioButton) findViewById(R.id.diariamenteBT);
        semanalmenteBT = (RadioButton) findViewById(R.id.semanalmenteBT);

        semanalmenteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogDias();
            }
        });

        mensalmenteBT = (RadioButton) findViewById(R.id.mensalmenteBT);

    }

    public void salvarIntervalo(View view) {

        if (umaVezBT.isChecked()) {
            remedio.setIntervalo("Uma vez");
        } else if (diariamenteBT.isChecked()) {
            remedio.setIntervalo("Diariamente");
        } else if (semanalmenteBT.isChecked()) {

            mostrarDialogDias();

        } else if (mensalmenteBT.isChecked()) {
            //Abrir dias do mês
        }
    }

    public void mostrarDialogDias()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] dias = new String[]{
                "Domingo",
                "Segunda",
                "Terça",
                "Quarta",
                "Quinta",
                "Sexta",
                "Sábado"
        };

        // Boolean array for initial selected items
        final boolean[] checkedDias = new boolean[7];

        for (int i = 0; i < checkedDias.length; i++)
        {
            checkedDias[i] = remedio.getDiasSemana().get(i).isBoleano();
        }

        // Convert the color array to list
        final List<String> diasList = Arrays.asList(dias);

        builder.setMultiChoiceItems(dias, checkedDias, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Update the current focused item's checked status
                checkedDias[which] = isChecked;

                // Get the current focused item
                String currentItem = diasList.get(which);
            }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog
        builder.setTitle("Dias da semana");

        // Set the positive/yes button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button
                realm.beginTransaction();
                for (int i = 0; i < checkedDias.length; i++)
                {
                    remedio.getDiasSemana().get(i).setBoleano(checkedDias[i]);
                }
                realm.commitTransaction();
                dialog.dismiss();
            }
        });


        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

}


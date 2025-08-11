package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import dao.DBAdapter;
import dao.Estado;
import dao.Limite;

public class ActLimiteNacional extends AppCompatActivity {

    final Context ctx = this;
    static DBAdapter db;
    static ListView listView;
    Spinner spinnerEstados;
    private LimiteAdapterNacional adapterLimites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_limite_nacional);

        db = new DBAdapter(ctx);

        inicializarComponentes();

        buscarEstadosELimites();

    }


    public void buscarEstadosELimites() {

        db.open();

        List<Estado> estados = db.getEstados();

        EstadoAdapter estadoAdapter = new EstadoAdapter(ctx, R.layout.row_estado,
                estados);

        spinnerEstados.setAdapter(estadoAdapter);
        spinnerEstados.setSelection(10);

        //Obtem item selecionado
        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                final String uf = ((TextView) view.findViewById(R.id.lbl_uf)).getText().toString();

                if (uf.equals("")) {

                    if (adapterLimites != null) {
                        adapterLimites.clear();
                        adapterLimites.notifyDataSetChanged();
                    }

                    Toast.makeText(getApplicationContext(),"Selecione o Estado!", Toast.LENGTH_LONG).show();

                }else {

                    buscarLimite(uf);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    public void buscarLimite(String uf) {

        List<Limite> limites1 = db.getLimite(uf, 1);
        List<Limite> limites2 = db.getLimite(uf, 2);
        List<Limite> limitesFinal = new ArrayList<>();

        for ( Limite limite1 : limites1 ) {

            if (limite1.getIdCargo() == 1 || limite1.getIdCargo() == 3) {

                for (Limite limite2 : limites2) {

                    if (limite1.getIdCargo() == limite2.getIdCargo()) {

                        limitesFinal.add(new Limite(limite1.getId(), limite1.getIdEleicao(), limite1.getIdCargo(),
                                limite1.getIdUE(), limite1.getValorMaximo(), limite1.getCaboMaximo(), limite2.getValorMaximo()));
                    }
                }
            } else
            {
                limitesFinal.add(new Limite(limite1.getId(), limite1.getIdEleicao(), limite1.getIdCargo(),
                        limite1.getIdUE(), limite1.getValorMaximo(), limite1.getCaboMaximo()));
            }

        }

        adapterLimites = new LimiteAdapterNacional(ctx, R.layout.row_limite_nacional,
                limitesFinal);

        listView = findViewById(R.id.listView);
        listView.setAdapter(adapterLimites);

        //Animacao na apresentacao dos dados
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0, 1);
        fadeInAnimation.setDuration(1000);
        fadeInAnimation.setFillAfter(true);
        listView.startAnimation(fadeInAnimation);

    }


    public void inicializarComponentes() {

        spinnerEstados = findViewById(R.id.spinnerEstados);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db.close();
        return;
    }

}

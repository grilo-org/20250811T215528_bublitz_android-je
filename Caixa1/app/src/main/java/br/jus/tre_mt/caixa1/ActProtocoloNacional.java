package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

import dao.DBAdapter;
import dao.GastoComp;

public class ActProtocoloNacional extends AppCompatActivity {

    final Context ctx = this;
    static DBAdapter db;
    static List<GastoComp> listaGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_protocolo);

        db = new DBAdapter(ctx);
        db.open();
        listaGasto = db.getGastosEleicaoNacional(true, true);
        db.close();

        if (listaGasto != null) {

            ProtNacionalAdapter protAdapter = new ProtNacionalAdapter(ctx, listaGasto);
            ListView listView = (ListView) findViewById(R.id.listViewProt);

            listView.setAdapter(protAdapter);
        }

    }
}

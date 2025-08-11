package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import dao.DBAdapter;
import dao.GastoComp;

public class ActProtocolo extends AppCompatActivity {

    final Context ctx = this;
    static DBAdapter db;
    static List<GastoComp> listaGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_protocolo);

        db = new DBAdapter(ctx);
        db.open();
        listaGasto = db.getGastos(true, true);
        db.close();

        if (listaGasto != null) {

            ProtAdapter protAdapter = new ProtAdapter(ctx, listaGasto);
            ListView listView = (ListView) findViewById(R.id.listViewProt);

            listView.setAdapter(protAdapter);
        }

    }
}

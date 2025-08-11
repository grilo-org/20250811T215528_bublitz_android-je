package br.jus.tre_mt.caixa1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import dao.DBAdapter;
import dao.Municipio;

public class ActLimite extends AppCompatActivity {

    final Context ctx = this;
    static DBAdapter db;
    static List<Municipio> municipios;
    static ListView listView;
    static EditText edtMunic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_limite);

        db = new DBAdapter(ctx);
        db.open();
        municipios = db.getMunicipios();
        db.close();

        final LimiteAdapter adapter = new LimiteAdapter(ctx, R.layout.row_limite,
                municipios);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


        edtMunic = (EditText) findViewById(R.id.editText);
        edtMunic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}

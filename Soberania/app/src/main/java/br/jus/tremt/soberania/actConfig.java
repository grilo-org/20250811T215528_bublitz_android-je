package br.jus.tremt.soberania;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Parametro;

public class actConfig extends AppCompatActivity {

    Switch swMun, swEst, swNac, swAut, swWifi;
    DBAdapter db;
    Parametro param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config);

        swMun = (Switch) findViewById(R.id.swMun);
        swEst = (Switch) findViewById(R.id.swEst);
        swNac = (Switch) findViewById(R.id.swNac);
        swAut = (Switch) findViewById(R.id.swAut);
        swWifi = (Switch) findViewById(R.id.swWifi);

        db = new DBAdapter(this);
        param = db.getParametros();
        swMun.setChecked(param.isPropMun());
        swEst.setChecked(param.isPropEst());
        swNac.setChecked(param.isPropNac());
        swAut.setChecked(param.isDownAut());
        swWifi.setChecked(param.isDownWifi());

    }

    public void salvar(View v) {
        db = new DBAdapter(this);
        param = new Parametro();
        param.setPropMun(swMun.isChecked());
        param.setPropEst(swEst.isChecked());
        param.setPropNac(swNac.isChecked());
        param.setDownAut(swAut.isChecked());
        param.setDownWifi(swWifi.isChecked());
        db.setParametros(param);
        Toast.makeText(actConfig.this, "Configurações salvas!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(actConfig.this, actMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

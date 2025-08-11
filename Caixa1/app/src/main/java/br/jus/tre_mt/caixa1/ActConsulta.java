package br.jus.tre_mt.caixa1;

import android.app.Dialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActConsulta extends AppCompatActivity {

    static WebView webView;
    static ImageButton btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_consulta);
        Bundle b = getIntent().getExtras();
        Long id = b.getLong("id");
        String url;
        if (id == 0)
            //url = "http://divulgacandcontas.tse.jus.br/divulga/#/estados/2016/2/MT/municipios";
            url = "http://divulgacandcontas.tse.jus.br/divulga/#/municipios/2018/2022802018/MT/cargos";
        else
            url = "http://divulgacandcontas.tse.jus.br/divulga/#/municipios/2016/2/"+id+"/candidatos";

        btnHome = (ImageButton) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

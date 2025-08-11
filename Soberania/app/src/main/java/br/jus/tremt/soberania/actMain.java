package br.jus.tremt.soberania;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.fonts.MeteoconsModule;
import com.joanzapata.iconify.fonts.SimpleLineIconsModule;
import com.joanzapata.iconify.fonts.TypiconsModule;
import com.joanzapata.iconify.fonts.WeathericonsModule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.adapter.PropostaAdapter;
import br.jus.tremt.soberania.modelo.Eleitor;
import br.jus.tremt.soberania.modelo.Parametro;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.modelo.RetornoErro;
import br.jus.tremt.soberania.modelo.RetornoQtde;
import br.jus.tremt.soberania.utils.AtualizaProposta;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.DialogSobre;
import br.jus.tremt.soberania.utils.Validacao;

public class actMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBAdapter db;
    ProgressDialog mDialog;
    PropostaAdapter adapter1, adapter2, adapter3;
    ListView lstProposta1, lstProposta2, lstProposta3;
    EditText edtPesquisar;
    TabHost tabHost;
    Eleitor eleitor;

    boolean temEleitor, cadastroOk;
    long idEleitor;

    Parametro param;
    static boolean jaAtualizou = false;

    List<Proposta> lista1 = null;
    List<Proposta> lista2 = null;
    List<Proposta> lista3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Iconify
                .with(new FontAwesomeModule())
                .with(new EntypoModule())
                .with(new TypiconsModule())
                .with(new MaterialModule())
                .with(new MaterialCommunityModule())
                .with(new MeteoconsModule())
                .with(new WeathericonsModule())
                .with(new SimpleLineIconsModule())
                .with(new IoniconsModule());

        setContentView(R.layout.act_main);

        db = new DBAdapter(getApplicationContext());
        param = db.getParametros();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        //Troca o titulo da ToolBar
        TextView txtTituloToolBar = toolbar.findViewById(R.id.tvIdTituloToolBarMain);
        txtTituloToolBar.setText("Soberano");
        txtTituloToolBar.setTextSize(18);
        txtTituloToolBar.setTextColor(Color.parseColor("#FFFFFF"));
        txtTituloToolBar.setTypeface(Typeface.DEFAULT, 1);

        //Troca o SubTitulo da ToolBar
        TextView txtSubTituloToolBar = toolbar.findViewById(R.id.tvIdSubTituloToolBarMain);
        txtSubTituloToolBar.setText("");
        txtSubTituloToolBar.setTextSize(14);
        txtSubTituloToolBar.setTextColor(Color.parseColor("#FFFFFF"));
        txtSubTituloToolBar.setTypeface(Typeface.DEFAULT, 0);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        atualizaIcons(navigationView);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Nacionais");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Nacionais");

        // para não exibir NACIONAIS, comente a linha abaixo:
        if (param.isPropNac()) {
            tabHost.addTab(spec);
        }

        //Tab 2
        spec = tabHost.newTabSpec("Estaduais");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Estaduais");
        if (param.isPropEst()) {
            tabHost.addTab(spec);
        }

        //Tab 3
        spec = tabHost.newTabSpec("Municipais");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Municipais");
        if (param.isPropMun()) {
            tabHost.addTab(spec);
        }

        //muda a cor das tabs
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            if (tabHost.getCurrentTab() == i) {
                TextView corTab = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                corTab.setTextColor(Color.parseColor("#007fff"));

            } else {
                TextView corTab3 = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                corTab3.setTextColor(Color.parseColor("#B0C4DE"));

            }
        }

        //evento que é disparado todas as vezes que muda de tab para mudar a cor
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String idTab) {
                //Log.i("exit",idTab);
                int tab = 0;

                if (idTab.equals("Nacionais")) {
                    tab = 0;
                } else if (idTab.equals("Estaduais")) {
                    tab = 1;
                } else if (idTab.equals("Municipais")) {
                    tab = 2;
                }


                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

                    if (tab == i) {

                        TextView corTab = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                        corTab.setTextColor(Color.parseColor("#007fff"));

                    } else {
                        TextView corTab = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                        corTab.setTextColor(Color.parseColor("#B0C4DE"));
                    }
                }
            }
        });


        lstProposta1 = (ListView) findViewById(R.id.lstProposta1);
        lstProposta1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String dataFim = lista1.get(position).getDataFim();
                Intent intent = verificarVigenciaProposta(dataFim);
                lista1 = null;
                Bundle b = new Bundle();
                b.putLong("id", id);
                b.putBoolean("votacao", true);
                b.putString("tipoAcao", "insert");
                intent.putExtras(b);
                actMain.this.startActivity(intent);
            }
        });

        lstProposta2 = (ListView) findViewById(R.id.lstProposta2);
        lstProposta2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String dataFim = lista2.get(position).getDataFim();
                Intent intent = verificarVigenciaProposta(dataFim);
                lista2 = null;
                Bundle b = new Bundle();
                b.putLong("id", id);
                b.putBoolean("votacao", true);
                b.putString("tipoAcao", "insert");
                intent.putExtras(b);
                actMain.this.startActivity(intent);
            }
        });

        lstProposta3 = (ListView) findViewById(R.id.lstProposta3);
        lstProposta3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String dataFim = lista3.get(position).getDataFim();
                Intent intent = verificarVigenciaProposta(dataFim);
                lista3 = null;
                Bundle b = new Bundle();
                b.putLong("id", id);
                b.putBoolean("votacao", true);
                b.putString("tipoAcao", "insert");
                intent.putExtras(b);
                actMain.this.startActivity(intent);
            }
        });


        edtPesquisar = (EditText) findViewById(R.id.edtPesquisar);
        edtPesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

                String text = arg0.toString().toLowerCase(Locale.getDefault());
                adapter1.filter(text);
                adapter2.filter(text);
                adapter3.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        atualizaLista();
        eleitor = db.getEleitor();

        if ((temEleitor) && (db.getEleitor().getMunicipio() != null) && (db.getEleitor().getUf() != null)) {
            txtSubTituloToolBar.setText(db.getEleitor().getMunicipio() + " - " + db.getEleitor().getUf());
        } else {
            txtSubTituloToolBar.setText("");
        }

    }

    private void atualizaIcons(NavigationView navigationView) {
        navigationView.getMenu().findItem(R.id.nav_home).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_home)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_atualizar).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_refresh)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_dados).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_user)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_acompanhar).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_file_text)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_votacao).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_eye)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_config).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_cogs)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
    }


    public Intent verificarVigenciaProposta(String dataFimVigenciaProposta) {
        Intent intent;
        //Verifica se ja encerrou a vigencia da votacao do projeto. Se venceu, chama tela
        //que exibe detalhes
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date dataFimFormatada = formato.parse(dataFimVigenciaProposta);
            Date dataAtual = new Date();
            String dataAtualString = formato.format(dataAtual);
            Date DateAtualFormatada = formato.parse(dataAtualString);
            if (dataFimFormatada.getTime() >= DateAtualFormatada.getTime()) {
                intent = new Intent(actMain.this, actDetalheProposta.class);
            } else {
                intent = new Intent(actMain.this, actDetalhePropostaEncerrada.class);
            }

        } catch (ParseException e) {
            intent = new Intent(actMain.this, actDetalheProposta.class);
        }

        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();

        atualizaLista();
        param = db.getParametros();

        if (temEleitor) {
            if (cadastroOk) {
                // Verifica se há atualizações
                if (!jaAtualizou) {
                    verificaNovasPropostas();
                }

                // Monta Lista com as propostas
                lstProposta1.setAdapter(adapter1);
                lstProposta2.setAdapter(adapter2);
                lstProposta3.setAdapter(adapter3);
            } else {
                // Apresenta tela para digitar código de validação
                Intent i = new Intent(this, actValidacao.class);
                startActivity(i);
            }
        } else {
            // Apresenta Tela para Cadastro
            Intent i = new Intent(this, actCadastro.class);
            startActivity(i);
        }
    }

    private void atualizaLista() {
        db = new DBAdapter(getApplicationContext());

        temEleitor = db.temEleitor();
        cadastroOk = db.isAtivado();
        idEleitor = db.idEleitor();

        db.open();
        if (lista1 == null) {
            lista1 = db.allProposta(false, true, 1, null);
            adapter1 = new PropostaAdapter(this, lista1);
        }
        if (lista2 == null) {
            lista2 = db.allProposta(false, true, 2, null);
            adapter2 = new PropostaAdapter(this, lista2);
        }
        if (lista3 == null) {
            lista3 = db.allProposta(false, true, 3, null);
            adapter3 = new PropostaAdapter(this, lista3);
        }
        db.close();
    }

    /*
    MENU DE CONFIGURAÇÃO A DIREITA RETIRADO A PEDIDO DO DARIENZO
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, actConfig.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(this, actMain.class);
            startActivity(i);

        } else if (id == R.id.nav_dados) {
            Intent i = new Intent(this, actDadosEleitor.class);
            startActivity(i);

        } else if (id == R.id.nav_votacao) {
            Intent i = new Intent(this, actMeusVotos.class);
            startActivity(i);

        } else if (id == R.id.nav_acompanhar) {
            Intent i = new Intent(this, actAcompanharProjetos.class);
            startActivity(i);

        } else if (id == R.id.nav_config) {
            Intent i = new Intent(this, actConfig.class);
            startActivity(i);

        } else if (id == R.id.nav_atualizar) {
            atualizaNovasPropostas();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*
            super.onBackPressed();
            Put up the Yes/No message box
            */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Encerrar Soberano")
                    .setMessage("Tem certeza??")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finishAffinity();
                                }
                            }).setNegativeButton("Não", null) // Do nothing on no
                    .show();
            return;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void verificaNovasPropostas() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(actMain.this,
                        "Buscando informações!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                return new ComunicaWS(actMain.this).qtdeProposta(idEleitor, param.getDataUltima());
            }

            @Override
            protected void onPostExecute(String res) {
                mDialog.dismiss();
                doneNovasPropostas(res);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doneNovasPropostas(String res) {
        jaAtualizou = true;
        Gson gson = new Gson();
        if (!res.contains("status")) {
            RetornoQtde rq = gson.fromJson(res, RetornoQtde.class);
            if (rq.getTotalPropostas() > 0) {
                ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!param.isDownAut() || (!wifiCheck.isConnected() && param.isDownWifi())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Existem atualizações nas propostas!")
                            .setMessage("Deseja atualizar agora??")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Sim",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            atualizaNovasPropostas();
                                        }
                                    }).setNegativeButton("Não", null) // Do nothing on no
                            .show();
                } else if (param.isDownAut()) {
                    atualizaNovasPropostas();
                }
            }
            //Intent intent = new Intent(actMain.this, actMain.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
        } else {
            try {
                RetornoErro msg = gson.fromJson(res, RetornoErro.class);
                String mens;
                if (msg.getMotivo() == null)
                    mens = msg.getStatus();
                else
                    mens = msg.getMotivo();
                new Validacao().msgbox(actMain.this, "Erro!!", mens);
            } catch (Exception e) {
                new Validacao().msgbox(actMain.this, "Erro!!", "Não foi possível verificar atualizações!");
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void atualizaNovasPropostas() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(actMain.this,
                        "Atualizando propostas!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                String prop = new ComunicaWS(actMain.this)
                        .retornaPropostas(db.getEleitor(), param.getDataUltima());
                String ret;
                if (prop.startsWith("[{\"status\":\"Nenhum registro foi encontrado!\"}]"))
                    ret = "nenhum";
                else
                    ret = new AtualizaProposta().gravar(actMain.this, prop);
                return ret;
            }

            @Override
            protected void onPostExecute(String res) {
                mDialog.dismiss();
                doneAtualizaPropostas(res);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void doneAtualizaPropostas(String res) {
        jaAtualizou = true;
        if (res.equals("ok")) {
            Toast.makeText(actMain.this, "Atualização realizada com sucesso!!!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(actMain.this, actMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (res.equals("nenhum"))
            new Validacao().msgbox(actMain.this, "Atenção!!", "Nenhuma proposta nova para atualizar!");
        else
            new Validacao().msgbox(actMain.this, "Erro!!", "Não foi possível atualizar propostas!");

    }

    public void sobre(View v) {
        new DialogSobre(actMain.this).show();
    }

}

package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import java.util.List;
import java.util.Locale;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.adapter.PropostaAdapter;
import br.jus.tremt.soberania.modelo.Parametro;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.utils.AtualizaProposta;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.DialogSobre;
import br.jus.tremt.soberania.utils.Validacao;

public class actAcompanharProjetos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Context ctx;
    EditText edtPesquisar;

    DBAdapter db;
    ProgressDialog mDialog;
    PropostaAdapter adapter1, adapter2, adapter3;
    long idEleitor;

    Parametro param;
    ListView lstProposta1, lstProposta2, lstProposta3;

    TabHost tabHost;

    List<Proposta> lista1 = null;
    List<Proposta> lista2 = null;
    List<Proposta> lista3 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = actAcompanharProjetos.this;

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

        setContentView(R.layout.act_acompanhar_projetos);

        db = new DBAdapter(ctx);
        param = db.getParametros();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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


        //Lista Propostas nacionais
        lstProposta1 = (ListView) findViewById(R.id.lstProposta1);
        lstProposta1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                lista1 = null;

                Intent intent = new Intent(ctx, actDetalhePropostaEncerrada.class);

                Bundle b = new Bundle();
                b.putLong("id", id);
                intent.putExtras(b);
                ctx.startActivity(intent);
            }
        });


        //Lista Propostas estaduais
        lstProposta2 = (ListView) findViewById(R.id.lstProposta2);
        lstProposta2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                lista2 = null;

                Intent intent = new Intent(ctx, actDetalhePropostaEncerrada.class);

                Bundle b = new Bundle();
                b.putLong("id", id);
                intent.putExtras(b);
                ctx.startActivity(intent);
            }
        });

        //Lista Propostas municipais
        lstProposta3 = (ListView) findViewById(R.id.lstProposta3);
        lstProposta3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                lista3 = null;

                Intent intent = new Intent(ctx, actDetalhePropostaEncerrada.class);

                Bundle b = new Bundle();
                b.putLong("id", id);
                intent.putExtras(b);
                ctx.startActivity(intent);
            }
        });

        edtPesquisar = findViewById(R.id.edtPesquisar);
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

    }

    @Override
    protected void onResume() {

        super.onResume();

        atualizaLista();

        // Monta Lista com as propostas
        lstProposta1.setAdapter(adapter1);
        lstProposta2.setAdapter(adapter2);
        lstProposta3.setAdapter(adapter3);
    }

    private void atualizaLista() {

        // Testa se já foi registrado
        db = new DBAdapter(ctx);

        param = db.getParametros();
        idEleitor = db.idEleitor();

        if (lista1 == null) {
            db.open();
            lista1 = db.allProposta(1);
            adapter1 = new PropostaAdapter(ctx, lista1);
            db.close();
        }

        if (lista2 == null) {
            db.open();
            lista2 = db.allProposta(2);
            adapter2 = new PropostaAdapter(ctx, lista2);
            db.close();
        }
        if (lista3 == null) {
            db.open();
            lista3 = db.allProposta(3);
            adapter3 = new PropostaAdapter(ctx, lista3);
            db.close();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(ctx, actMain.class);
            startActivity(i);

        } else if (id == R.id.nav_dados) {
            Intent i = new Intent(ctx, actDadosEleitor.class);
            startActivity(i);

        } else if (id == R.id.nav_votacao) {
            Intent i = new Intent(ctx, actMeusVotos.class);
            startActivity(i);

        } else if (id == R.id.nav_acompanhar) {
            Intent i = new Intent(ctx, actAcompanharProjetos.class);
            startActivity(i);

        } else if (id == R.id.nav_config) {
            Intent i = new Intent(ctx, actConfig.class);
            startActivity(i);

        } else if (id == R.id.nav_atualizar) {
            atualizaNovasPropostas();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void atualizaIcons(NavigationView navigationView) {
        navigationView.getMenu().findItem(R.id.nav_home).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_home)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_atualizar).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_refresh)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_dados).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_user)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_acompanhar).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_file_text)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_votacao).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_eye)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
        navigationView.getMenu().findItem(R.id.nav_config).setIcon(
                new IconDrawable(ctx, FontAwesomeIcons.fa_cogs)
                        .colorRes(R.color.black_overlay)
                        .actionBarSize());
    }


    @SuppressLint("StaticFieldLeak")
    private void atualizaNovasPropostas() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(ctx,
                        "Atualizando propostas!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                String prop = new ComunicaWS(ctx)
                        .retornaPropostas(db.getEleitor(), param.getDataUltima());
                String ret;
                if (prop.startsWith("[{\"status\":\"Nenhum registro foi encontrado!\"}]"))
                    ret = "nenhum";
                else
                    ret = new AtualizaProposta().gravar(actAcompanharProjetos.this, prop);
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
        if (res.equals("ok")) {
            Toast.makeText(ctx, "Atualização realizada com sucesso!!!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ctx, actMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (res.equals("nenhum"))
            new Validacao().msgbox(ctx, "Atenção!!", "Nenhuma proposta nova para atualizar!");
        else
            new Validacao().msgbox(ctx, "Erro!!", "Não foi possível atualizar propostas!");

    }

    public void sobre(View v) {
        new DialogSobre(ctx).show();
    }

}

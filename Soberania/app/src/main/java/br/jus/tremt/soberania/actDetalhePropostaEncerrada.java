package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.modelo.RetornoErro;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.Validacao;

/**
 * Created by 031066031880 on 19/01/2018.
 */

public class actDetalhePropostaEncerrada extends AppCompatActivity {


    DBAdapter db;
    ProgressDialog mDialog;
    public Proposta proposta, prop;
    TextView tvTituloProposta, tvdescricaoProposta, tvPeriodoVotacao,
            tvTotalAprovado, tvTotalRejeitado, tvProtocolo, tvdataProtocolo,
            tvResultado, tvAcompanharProjeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_detalhe_proposta_encerrada);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        inicializarComponentes();

        Bundle b = getIntent().getExtras();
        long codigo = b.getLong("id");

        db = new DBAdapter(getApplicationContext());
        db.open();
        proposta = db.getProposta(codigo);
        proposta.setLido("S");
        db.setProposta(proposta);
        db.close();

        tvTotalAprovado.setText("");
        tvTotalRejeitado.setText("");
        tvResultado.setText("-");

        atualizarTotalVotos();

        tvTituloProposta.setText(proposta.getNome());
        tvdescricaoProposta.setText(proposta.getDescricao());
        tvProtocolo.setText(proposta.getProtocolo());
        tvdataProtocolo.setText(Validacao.formatarData(proposta.getDataProtocolo()));
        tvPeriodoVotacao.setText(Validacao.formatarData(proposta.getDataInicio()) + " a " + Validacao.formatarData(proposta.getDataFim()));

        tvAcompanharProjeto.setText(proposta.getLinkAcompanharProjeto());

        tvTotalAprovado.setText(String.valueOf(proposta.getTotalAprovado()));
        tvTotalRejeitado.setText(String.valueOf(proposta.getTotalRejeitado()));

    }

    public void compartilhar(View v) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, proposta.toString());
        startActivity(Intent.createChooser(share, "Compartilhar Proposta - Soberano"));
    }

    @SuppressLint("StaticFieldLeak")
    public void inteiroTeor(View v) {
        try {
            new AsyncTask<String, Void, byte[]>() {
                @Override
                protected void onPreExecute() {
                    mDialog = ProgressDialog.show(actDetalhePropostaEncerrada.this,
                            "Baixando inteiro teor!", "Aguarde...",
                            true, false);
                    mDialog.setCancelable(false);
                }

                @Override
                protected byte[] doInBackground(String... params) {
                    return new ComunicaWS(actDetalhePropostaEncerrada.this)
                            .inteiroTeor(proposta);
                }


                @Override
                protected void onPostExecute(byte[] res) {
                    mDialog.dismiss();
                    doneAsyncPdf(res);

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Falha!",
                    "Não foi possível abrir o inteiro teor da proposta");
        }

    }

    public void doneAsyncPdf(byte[] res) {
        if (res != null) {
            try {
                String PATH = Environment.getExternalStorageDirectory().toString()
                        + "/"
                        + Environment.DIRECTORY_DOWNLOADS
                        + "/proposta_" + proposta.getId()
                        + ".pdf";

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(PATH));
                fos.write(res);
                fos.flush();
                fos.close();

                File file = new File(PATH);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                Intent intent = Intent.createChooser(target, "Abrir Proposta");
                startActivity(intent);
            } catch (IOException e) {
                new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Falha!",
                        "Não foi possível abrir o inteiro teor da proposta!");
            } catch (ActivityNotFoundException e) {
                new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Erro!",
                        "Não há um visualizador de PDF instalado!!");
            }
        } else
            new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Erro!",
                    "Não foi possível abrir o inteiro teor da proposta!");
    }

    public void inicializarComponentes() {
        tvTituloProposta = findViewById(R.id.tvIdTituloProjeto);
        tvdescricaoProposta = findViewById(R.id.tvIdDescricao);
        tvProtocolo = findViewById(R.id.tvIdProtocolo);
        tvdataProtocolo = findViewById(R.id.tvIdDataProtocoloOrgao);
        tvPeriodoVotacao = findViewById(R.id.tvIdPeriodoVotacao);
        tvResultado = findViewById(R.id.tvIdResultado);
        tvAcompanharProjeto = findViewById(R.id.tvIdAcompanhar);
        tvTotalAprovado = findViewById(R.id.tvIdAprovado);
        tvTotalRejeitado = findViewById(R.id.tvIdRejeitado);
    }

    @SuppressLint("StaticFieldLeak")
    public void atualizarTotalVotos() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(actDetalhePropostaEncerrada.this,
                        "Atualizando votação!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                return new ComunicaWS(actDetalhePropostaEncerrada.this).totalVotos(db.idEleitor(), proposta);
            }

            @Override
            protected void onPostExecute(String res) {
                mDialog.dismiss();
                doneAsync(res);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void doneAsync(String res) {
        Gson gson = new Gson();
        if (res.contains("idProposta")) {
            prop = gson.fromJson(res, Proposta.class);
            tvTotalAprovado.setText(prop.getTotalAprovado().toString());
            tvTotalRejeitado.setText(prop.getTotalRejeitado().toString());
            Long totalAprovado = prop.getTotalAprovado();
            Long totalRejeitado = prop.getTotalRejeitado();

            //se aprovado, a font sera verde, se nao vermelho
            if (totalAprovado > totalRejeitado) {
                tvResultado.setText("Aprovado");
                tvResultado.setTextColor(Color.GREEN);

            } else if (totalAprovado < totalRejeitado) {
                tvResultado.setText("Rejeitado");
                tvResultado.setTextColor(Color.RED);
            } else {
                tvResultado.setText("Empate");
            }

            Toast.makeText(actDetalhePropostaEncerrada.this, "Votação atualizada com sucesso!!!", Toast.LENGTH_LONG).show();
        } else {
            prop = null;
            try {
                RetornoErro msg = gson.fromJson(res, RetornoErro.class);
                String mens;
                if (msg.getMotivo() == null)
                    mens = msg.getStatus();
                else
                    mens = msg.getMotivo();
                new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Erro!!", mens);
            } catch (Exception e) {
                new Validacao().msgbox(actDetalhePropostaEncerrada.this, "Erro!!", "Não foi possível atualizar votação!");
            }
        }

    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
}

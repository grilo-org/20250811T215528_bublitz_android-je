package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.joanzapata.iconify.widget.IconButton;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.modelo.RetornoErro;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.Validacao;

public class actDetalheProposta extends AppCompatActivity {

    DBAdapter db;
    ProgressDialog mDialog;
    Proposta proposta;
    TextView tvTituloProposta, tvdescricaoProposta, tvInicio, tvTermino,
            tvTotalAprovado, tvTotalRejeitado;

    RelativeLayout rlFooter, rlHeader;
    ScrollView svContent;
    String tipoAcao = "";
    IconButton btnAprovar, btnRejeitar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_detalhe_proposta);

        if (shouldAskPermissions()) {
            askPermissions();
        }

        inicializarComponentes();

        Bundle b = getIntent().getExtras();
        long codigoProposta = b.getLong("id");
        tipoAcao = b.getString("tipoAcao");

        boolean votacao = b.getBoolean("votacao");

        //Se votacao for FALSE, os botoes serao ocultados
        if (!votacao) {

            rlFooter.setVisibility(View.GONE);
            //rlHeader.setVisibility(View.GONE);

            //Define nova margem para o ScrollView
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) svContent.getLayoutParams();
            layoutParams.setMargins(0, 100, 0, 0);
            svContent.setLayoutParams(layoutParams);
        }

        db = new DBAdapter(getApplicationContext());
        db.open();

        proposta = db.getProposta(codigoProposta);

        tvTituloProposta.setText(proposta.getNome());
        tvdescricaoProposta.setText(proposta.getDescricao());
        tvInicio.setText(Validacao.formatarData(proposta.getDataInicio()));
        tvTermino.setText(Validacao.formatarData(proposta.getDataFim()));
        //tvTotalAprovado.setText(String.valueOf(proposta.getTotalAprovado()));
        //tvTotalRejeitado.setText(String.valueOf(proposta.getTotalRejeitado()));
        tvTotalAprovado.setText("");
        tvTotalRejeitado.setText("");

        //Habilita ou desabilita o botao caso ja tenha votado
        if (tipoAcao.equalsIgnoreCase("update")) {

            if (proposta.getVoto().equalsIgnoreCase("A")) {

                btnAprovar = findViewById(R.id.btnAprovar);
                btnAprovar.setBackgroundResource(R.drawable.transparent_button);
                btnAprovar.setEnabled(false);

            } else {

                btnRejeitar = findViewById(R.id.btnReprovar);
                btnRejeitar.setBackgroundResource(R.drawable.transparent_button);
                btnRejeitar.setEnabled(false);
            }
        }

        //atualiza o status da proposta para lido
        if (proposta.getLido().equals("N")) {
            proposta.setLido("S");
            db.setProposta(proposta);
        }

        db.close();

    }

    public void inicializarComponentes() {

        tvTituloProposta = findViewById(R.id.tvIdTituloProjeto);
        tvdescricaoProposta = findViewById(R.id.tvIdDescricao);
        tvInicio = findViewById(R.id.tvIdInicio);
        tvTermino = findViewById(R.id.tvIdTermino);
        tvTotalAprovado = findViewById(R.id.tvIdAprovar);
        tvTotalRejeitado = findViewById(R.id.tvIdRejeitar);
        rlFooter = findViewById(R.id.llIdFooter);
        rlHeader = findViewById(R.id.llIdHeader);
        svContent = findViewById(R.id.svIdContent);
    }


    public void atualizarTotalVotos(View view) {
        asyncTotalVotos();
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncTotalVotos() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(actDetalheProposta.this,
                        "Atualizando votação!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                return new ComunicaWS(actDetalheProposta.this).totalVotos(db.idEleitor(), proposta);
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

            Proposta prop = gson.fromJson(res, Proposta.class);

            db.open();
            db.atualizarTotalVotos(proposta.getId(), prop.getTotalAprovado(), prop.getTotalRejeitado());

            Proposta p = db.getProposta(proposta.getId());

            db.close();

            tvTotalAprovado.setText(p.getTotalAprovado().toString());
            tvTotalRejeitado.setText(p.getTotalRejeitado().toString());


            Toast.makeText(actDetalheProposta.this, "Votação atualizada com sucesso!!!", Toast.LENGTH_LONG).show();
        } else {
            try {
                RetornoErro msg = gson.fromJson(res, RetornoErro.class);
                String mens;
                if (msg.getMotivo() == null)
                    mens = msg.getStatus();
                else
                    mens = msg.getMotivo();
                new Validacao().msgbox(actDetalheProposta.this, "Erro!!", mens);
            } catch (Exception e) {
                new Validacao().msgbox(actDetalheProposta.this, "Erro!!", "Não foi possível atualizar votação!");
            }
        }

    }

    public void compartilhar(View v) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/html");
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Compartilhar proposta:");
        share.putExtra(Intent.EXTRA_TEXT, proposta.toString());
        startActivity(Intent.createChooser(share, "Compartilhar Proposta - Soberano"));
    }

    @SuppressLint("StaticFieldLeak")
    public void inteiroTeor(View v) {
        try {
            new AsyncTask<String, Void, byte[]>() {
                @Override
                protected void onPreExecute() {
                    mDialog = ProgressDialog.show(actDetalheProposta.this,
                            "Baixando inteiro teor!", "Aguarde...",
                            true, false);
                    mDialog.setCancelable(false);
                }

                @Override
                protected byte[] doInBackground(String... params) {
                    return new ComunicaWS(actDetalheProposta.this)
                            .inteiroTeor(proposta);
                }


                @Override
                protected void onPostExecute(byte[] res) {
                    mDialog.dismiss();
                    doneAsyncPdf(res);

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            new Validacao().msgbox(actDetalheProposta.this, "Falha!",
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
                new Validacao().msgbox(actDetalheProposta.this, "Falha!",
                        "Não foi possível abrir o inteiro teor da proposta!");
            } catch (ActivityNotFoundException e) {
                new Validacao().msgbox(actDetalheProposta.this, "Falha!",
                        "Não há um visualizador de PDF instalado!!");
            }
        } else
            new Validacao().msgbox(actDetalheProposta.this, "Falha!",
                    "Não foi possível abrir o inteiro teor da proposta!");
    }

    public void votoAprovar(View v) {
        votarProposta("A");
    }

    public void votoReprovar(View v) {
        votarProposta("R");
    }

    public String tipo(String voto) {
        if (voto.equals("A"))
            return "Aprovação";
        else
            return "Reprovação";
    }

    private void votarProposta(final String voto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Votar pela " + tipo(voto) + " da proposta??")
                .setMessage("Confirma voto?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("StaticFieldLeak")
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                new AsyncTask<String, Void, String>() {
                                    @Override
                                    protected void onPreExecute() {
                                        mDialog = ProgressDialog.show(actDetalheProposta.this,
                                                "Enviando voto!", "Aguarde...",
                                                true, false);
                                        mDialog.setCancelable(false);
                                    }

                                    @Override
                                    protected String doInBackground(String... params) {
                                        return new ComunicaWS(actDetalheProposta.this)
                                                .votoProposta(new DBAdapter(actDetalheProposta.this).idEleitor(),
                                                        proposta.getId(), voto, tipoAcao);
                                    }

                                    @Override
                                    protected void onPostExecute(String res) {
                                        mDialog.dismiss();
                                        doneAsyncVotar(res, voto);

                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }).setNegativeButton("Não", null) // Do nothing on no
                .show();
        return;
    }

    public void doneAsyncVotar(String res, String voto) {
        if (res.contains("ok") || (res.contains("registrado"))) {

            String msg = "";

            // Grava resultado
            db.open();
            db.votarProposta(proposta.getId(), voto);
            db.close();

            if (tipoAcao.equalsIgnoreCase("update")) {
                msg = "atualizado";
                //Reload
                finish();
                startActivity(getIntent());
            } else {
                msg = "registrado";
            }

            Toast.makeText(actDetalheProposta.this, "Voto " + msg + " com sucesso!!!", Toast.LENGTH_LONG).show();

            if (tipoAcao.equalsIgnoreCase("insert")) {
                Intent intent = new Intent(actDetalheProposta.this, actMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                actDetalheProposta.this.startActivity(intent);
            } else {
                Intent intent = new Intent(actDetalheProposta.this, actMeusVotos.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                actDetalheProposta.this.startActivity(intent);
            }

        }
        else
        if(res.contains("encerrada")) {

            Toast.makeText(actDetalheProposta.this, "Votação encerrada!", Toast.LENGTH_LONG).show();
        }
        else {
            //String msg = res.substring(res.indexOf("motivo") + 9);
            //Toast.makeText(actCadastro.this, msg.substring(0, msg.indexOf("\"")), Toast.LENGTH_LONG).show();
            new Validacao().msgbox(actDetalheProposta.this, "Falha!",
                    "Não foi possível votar na proposta!");
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

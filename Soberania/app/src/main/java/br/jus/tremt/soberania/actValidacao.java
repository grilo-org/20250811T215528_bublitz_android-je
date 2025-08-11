package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.Validacao;

public class actValidacao extends AppCompatActivity {


    EditText etcodigoVerificacao;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_validacao);
        dbAdapter = new DBAdapter(getApplicationContext());
    }

    @SuppressLint("StaticFieldLeak")
    public void confirmar(View v) {

        inicializarComponentes();

        if (validarCampos()) {
            new AsyncTask<String, Void, String>() {
                ProgressDialog mDialog;
                String codigoVerificacao = etcodigoVerificacao.getText().toString().trim().replace("-", "");

                @Override
                protected void onPreExecute() {
                    mDialog = ProgressDialog.show(actValidacao.this,
                            "Validando código!", "Por favor, aguarde...", true, false);
                    mDialog.setCancelable(false);
                }

                @Override
                protected String doInBackground(String... params) {
                    return new ComunicaWS(actValidacao.this).verificaCodigo(dbAdapter.idEleitor(), codigoVerificacao);
                }

                @Override
                protected void onPostExecute(String res) {
                    mDialog.dismiss();
                    doneAsync(res);

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void doneAsync(String res) {
        if (res.contains("ok")) {
            new DBAdapter(this).activeCadastro();
            Toast.makeText(this, "Cadastro ativado com sucesso!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(actValidacao.this, actMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            actValidacao.this.startActivity(intent);
        } else {
            new Validacao().msgbox(actValidacao.this, "Código incorreto!", "Verifique o código enviado ao seu e-mail!");
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void reenviar(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setGravity(Gravity.CENTER);
        input.setWidth(200);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setText(dbAdapter.getEleitor().getEmail());
        builder.setTitle("Confirme seu e-mail!")
                .setMessage("Endereço de e-mail:")
                .setIcon(android.R.drawable.ic_dialog_email)
                .setView(input)
                .setPositiveButton("Enviar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final String email = input.getText().toString();
                                new AsyncTask<String, Void, String>() {

                                    ProgressDialog mDialog;

                                    @Override
                                    protected void onPreExecute() {
                                        mDialog = ProgressDialog.show(actValidacao.this,
                                                "Solicitando novo e-mail!", "Por favor, aguarde...", true, false);
                                        mDialog.setCancelable(false);
                                    }

                                    @Override
                                    protected String doInBackground(String... params) {
                                        return new ComunicaWS(actValidacao.this).reenviaEmail(dbAdapter.idEleitor(), email);
                                    }

                                    @Override
                                    protected void onPostExecute(String res) {
                                        mDialog.dismiss();
                                        if (!res.contains("Token inv"))
                                            Toast.makeText(actValidacao.this, "E-mail enviado com sucesso!", Toast.LENGTH_LONG).show();
                                        else {
                                            Toast.makeText(actValidacao.this, "Erro! Token inválido!", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                            }
                        }).setNegativeButton("Cancelar", null) // Do nothing on no
                .show();
        return;

    }

    public void inicializarComponentes() {
        etcodigoVerificacao = findViewById(R.id.etCodigoVerificacao);
    }

    public boolean validarCampos() {
        Validacao validacao = new Validacao();

        boolean ok = true;

        //Codigo verificacao
        if (validacao.validarCampo(etcodigoVerificacao.getText().toString())) {
            etcodigoVerificacao.setError(this.getResources().getString(R.string.msg_erro_act_validacao_codigoVazio));
            ok = false;
        } else
            //Verifica se todos os digitos foram preenchidos no campo
            if (etcodigoVerificacao.getText().toString().trim().replace("-", "").length() != 6) {
                etcodigoVerificacao.setError(this.getResources().getString(R.string.msg_erro_act_validacao_codigoIncompleto));
                ok = false;
            }

        return ok;
    }

}

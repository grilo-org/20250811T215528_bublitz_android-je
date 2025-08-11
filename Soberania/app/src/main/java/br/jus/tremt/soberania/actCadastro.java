package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Eleitor;
import br.jus.tremt.soberania.modelo.RetornoEleitor;
import br.jus.tremt.soberania.modelo.RetornoErro;
import br.jus.tremt.soberania.utils.ComunicaWS;
import br.jus.tremt.soberania.utils.Validacao;

public class actCadastro extends AppCompatActivity {


    EditText etNomeEleitor, etDataNasc, etTituloEleitor, etNomeMae,
            etEmail, etCelular;
    CheckBox cbNomeMae;
    ProgressDialog mDialog;
    Eleitor eleitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cadastro);

        inicializarComponentes();

        //CHECKBOX NOME DA MAE

        cbNomeMae.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etNomeMae.setText(R.string.nao_consta_maiusculo);
                    etNomeMae.setEnabled(false);
                    etNomeMae.setError(null);

                } else {
                    etNomeMae.setEnabled(true);
                    etNomeMae.setText("");
                }

            }
        });

    }

    public boolean validarCampos() {

        Validacao validacao = new Validacao();

        boolean ok = true;

        //Nome eleitor
        if (validacao.validarCampo(etNomeEleitor.getText().toString())) {
            etNomeEleitor.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_nomeEleitor));
            ok = false;
        }

        //Data Nascimento
        if (validacao.validarCampo(etDataNasc.getText().toString())) {
            etDataNasc.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_dataNasc));
            ok = false;
        } else if (!validacao.validarData(etDataNasc.getText().toString())) {
            etDataNasc.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_dataNascInvalida));
            ok = false;
        } else
            //Verifica se todos os digitos foram colocados no campo data nasc
            if (etDataNasc.getText().length() != 10) {
                etDataNasc.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_dataNascIncompleta));
                ok = false;
            }

        //titulo de eleitor
        if (validacao.validarCampo(etTituloEleitor.getText().toString())) {
            etTituloEleitor.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_TituloEleitor));
            ok = false;
        } else if (!validacao.ValidarTitulo(etTituloEleitor.getText().toString())) {
            etTituloEleitor.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_TituloEleitorInvalido));
            ok = false;
        }

        //nome da mae
        if (validacao.validarCampo(etNomeMae.getText().toString())) {
            etNomeMae.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_nomeMae));
            ok = false;
        }

        //E-mail
        if (validacao.validarCampo(etEmail.getText().toString())) {
            etEmail.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_Email));
            ok = false;
        } else if (!validacao.validarEmail(etEmail.getText().toString())) {
            etEmail.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_EmailInvalido));
            ok = false;
        }

        //celular
        if (validacao.validarCampo(etCelular.getText().toString())) {
            etCelular.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_Celular));
            ok = false;
        } else
            //Verifica se todos os digitos foram colocados no campo celular
            if (etCelular.getText().length() != 15) {
                etCelular.setError(this.getResources().getString(R.string.msg_erro_act_cadastro_Celular_incompleto));
                ok = false;
            }

        return ok;
    }

    @SuppressLint("StaticFieldLeak")
    public void cadastrar(View v) {
        if (validarCampos()) {

            eleitor = new Eleitor();
            String nomeMae = etNomeMae.getText().toString().trim().replace("Ã", "A");
            nomeMae = (nomeMae.equals("NAO CONSTA") ? "" : etNomeMae.getText().toString().trim());

            eleitor.setNome(etNomeEleitor.getText().toString().trim());
            eleitor.setDataNascto(etDataNasc.getText().toString().trim());
            String strTitulo = "000000000000" + etTituloEleitor.getText().toString().trim();
            eleitor.setTitulo(strTitulo.substring(strTitulo.length() - 12));
            eleitor.setNomeMae(nomeMae);
            eleitor.setNaoConstaMae(cbNomeMae.isChecked());
            eleitor.setEmail(etEmail.getText().toString().trim());
            eleitor.setTelefone(etCelular.getText().toString().trim());

            new AsyncTask<String, Void, String>() {
                @Override
                protected void onPreExecute() {
                    mDialog = ProgressDialog.show(actCadastro.this,
                            "Enviando dados!", "Aguarde...", true, false);
                    mDialog.setCancelable(false);
                }

                @Override
                protected String doInBackground(String... params) {
                    return new ComunicaWS(actCadastro.this).cadastraEleitor(eleitor);
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
        Log.i("saida", "retorno webservice: " + res);
        Gson gson = new Gson();

        if (res.contains("idEleitor")) {
            Toast.makeText(actCadastro.this, "Dados enviados com sucesso!!!", Toast.LENGTH_LONG).show();
            RetornoEleitor re = gson.fromJson(res, RetornoEleitor.class);
            eleitor.setId(re.getIdEleitor());
            eleitor.setMunicipio(re.getCidade());
            eleitor.setUf(re.getUf());
            eleitor.setLocalidadeId(re.getLocalidadeId());
            eleitor.setUfId(re.getUfId());
            new DBAdapter(this).setEleitor(eleitor);
            Intent intent = new Intent(actCadastro.this, actValidacao.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            try {
                RetornoErro msg = gson.fromJson(res, RetornoErro.class);
                String mens;
                if (msg.getMotivo() == null)
                    mens = msg.getStatus();
                else
                    mens = msg.getMotivo();
                new Validacao().msgbox(actCadastro.this, "Erro!!", mens);
            } catch (Exception e) {
                new Validacao().msgbox(actCadastro.this, "Erro!!", "Verifique sua conexão e tente novamente!");
            }
        }

    }

    public void inicializarComponentes() {

        etNomeEleitor = findViewById(R.id.etIdEleitor);
        etDataNasc = findViewById(R.id.etIdDataNasc);
        etTituloEleitor = findViewById(R.id.etIdTituloEleitor);
        etNomeMae = findViewById(R.id.etIdNomeMae);
        etEmail = findViewById(R.id.etIdEmail);
        etCelular = findViewById(R.id.etIdCelular);
        cbNomeMae = findViewById(R.id.cbIdNomeMae);

        //UPPERCASE
        etNomeEleitor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        etNomeMae.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
    }


}

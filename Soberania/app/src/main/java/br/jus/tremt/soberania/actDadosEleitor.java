package br.jus.tremt.soberania;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Eleitor;
import br.jus.tremt.soberania.utils.ComunicaWS;

public class actDadosEleitor extends AppCompatActivity {

    TextView tvTitulo, tvNome, tvDataNasc, tvNomeMae,
            tvEmail, tvCelular;
    DBAdapter dbAdapter;
    Eleitor eleitor;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dados_eleitor);

        inicializarComponentes();

        dbAdapter = new DBAdapter(getApplicationContext());

        eleitor = dbAdapter.getEleitor();
        if (eleitor != null) {
            //Alimentando componentes
            tvTitulo.setText(eleitor.getTitulo());
            tvNome.setText(eleitor.getNome());
            tvDataNasc.setText(eleitor.getDataNascto());
            tvNomeMae.setText(eleitor.getNomeMae());
            tvEmail.setText(eleitor.getEmail());
            tvCelular.setText(eleitor.getTelefone());
        }
    }

    public void trocarEleitor(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.act_dados_eleitor_msg_alert))
                .setMessage(this.getResources().getString(R.string.act_dados_eleitor_title_alert))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                limparEleitor();
                                //finish();
                            }
                        }).setNegativeButton("Não", null) // Do nothing on no
                .show();
        return;
    }

    public void inicializarComponentes() {
        tvTitulo = findViewById(R.id.tvIdTitulo);
        tvNome = findViewById(R.id.tvIdNome);
        tvDataNasc = findViewById(R.id.tvIdDataNasc);
        tvNomeMae = findViewById(R.id.tvIdNomeDaMae);
        tvEmail = findViewById(R.id.tvIdEmail);
        tvCelular = findViewById(R.id.tvIdCelular);
    }

    @SuppressLint("StaticFieldLeak")
    public void limparEleitor() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                mDialog = ProgressDialog.show(actDadosEleitor.this,
                        "Enviando dados!", "Aguarde...", true, false);
                mDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(String... params) {
                return new ComunicaWS(actDadosEleitor.this).limpaEleitor(eleitor.getId());
            }

            @Override
            protected void onPostExecute(String res) {
                mDialog.dismiss();
                doneAsync(res);

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void doneAsync(String res) {
        dbAdapter.deleteEleitor();
        if (res.contains("Token deletado")) {
            Toast.makeText(actDadosEleitor.this, "Dados apagados com sucesso!!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(actDadosEleitor.this, actMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            finish();
        }
    }

}

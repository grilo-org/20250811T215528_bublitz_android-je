package br.jus.tre_mt.caixa1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.CandPart;
import dao.DBAdapter;
import dao.Gasto;
import dao.Informante;
import dao.Midia;
import dao.Municipio;
import dao.RespGasto;

public class ActGasto extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int REQUEST_MIDIA_CAPTURE = 3;
    static final int REQUEST_PHOTO_INTENT = 4;

    final Context ctx = this;

    static long id_munic = 0, num_cand = 0;
    static String nom_munic, nom_cand;
    static List<Municipio> municipios = null;
    static List<CandPart> cands = null;

    static ImageButton btnVideo, btnSom;

    static BGridView gridMidia = null;
    static File imageFile, videoFile, midiaFile;
    static List<Midia> midias = null;
    static MidiaAdapter midiaadap;
    static Gasto gasto = null;
    static RespGasto rgasto;
    static AutoCompleteTextView edtMunic = null, edtCand = null;
    static EditText edtCPF = null;
    static DBAdapter db = null;
    static CandAdapter candadap;
    static Spinner spTipo;
    static EditText edtDescricao;

    String tipo_midia;
    ProgressDialog mDialog;
    List<RespGasto> resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gasto);

        db = new DBAdapter(ctx);

        gridMidia = (BGridView) findViewById(R.id.gridMidia);

        ArrayAdapter<CharSequence> tipoAdapter = ArrayAdapter.createFromResource(this, R.array.tipoGasto,
                R.layout.row_tipo);
        spTipo = (Spinner) findViewById(R.id.spTipo);
        spTipo.setAdapter(tipoAdapter);
        spTipo.setPrompt("Selecione um Tipo de Gasto");

        edtCPF = (EditText) findViewById(R.id.edtCPF);
        edtDescricao = (EditText) findViewById(R.id.edtDescricao);

        edtMunic = (AutoCompleteTextView) findViewById(R.id.edtMunic);
        edtMunic.clearListSelection();
        edtMunic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                id_munic = l;
                nom_munic = edtMunic.getText().toString();
                db.open();
                atualizaCand();
                //db.close();
            }
        });

        edtCand = (AutoCompleteTextView) findViewById(R.id.edtCand);
        edtCand.setEnabled(false);
        //edtCand.setHint(null);
        //edtCand.setError("Selecione um município!");
        edtCand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                num_cand = id;
                nom_cand = edtCand.getText().toString();
                db.open();
                db.saveGasto(gasto, num_cand);
                //db.close();
            }
        });

        db.open();

        gasto = db.novoGasto();
        num_cand = db.numCand(gasto);

        municipios = db.getMunicipios();

        btnVideo = (ImageButton) findViewById(R.id.btnVideo);
        btnSom = (ImageButton) findViewById(R.id.btnSom);

        atualizaMidia();



        MunicAdapter municAdapter = new MunicAdapter(ctx, R.layout.activity_main, R.id.lbl_name,
                municipios);
        edtMunic.setAdapter(municAdapter);

        try {
            if ((num_cand != 0)) { //&& (id_munic != 0)) {
                long old = num_cand;
                List<String> lista = new ArrayList<String>();
                lista = db.getNomeCand(num_cand, id_munic);
                nom_cand = lista.get(0);
                nom_munic = lista.get(1);
                id_munic = Long.valueOf(lista.get(2));
                edtMunic.setText(nom_munic);
                atualizaCand();
                edtCand.setText(nom_cand);
                num_cand = old;
            }
        } catch (Exception e) {

        }

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void atualizaMidia() {

        midias = db.getMidias(gasto.getId());

        btnVideo.setEnabled(true);
        btnSom.setEnabled(true);

        for (int i = 0; i < midias.size(); i++) {
            String ext = midias.get(i).getFilename().substring(midias.get(i).getFilename().lastIndexOf(".") + 1);
            String ftipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (ftipo.contains("video")) btnVideo.setEnabled(false);
            if (ftipo.contains("audio")) btnSom.setEnabled(false);
        }

        midiaadap = new MidiaAdapter(ctx, midias);
        gridMidia.setAdapter(midiaadap);
        gridMidia.setExpanded(true);
        gridMidia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle("Excluir mídia:")
                        .setMessage("Tem certeza??")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("Excluir",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        midias.remove(pos);
                                        salvaMidias();
                                    }
                                }).setNegativeButton("Não", null) // Do nothing on no
                        .show();
            }
        });
    }

    public void atualizaCand() {

        cands = db.getCandidatos(id_munic);
        num_cand = 0;
        db.saveGasto(gasto, num_cand);
        edtCand.setEnabled(true);
        edtCand.setError(null);
        edtCand.setHint("Nome ou Número");
        candadap = new CandAdapter(ctx, R.layout.activity_main, R.id.lbl_name, cands);
        edtCand.setAdapter(candadap);
        edtCand.clearListSelection();
    }

    public void novaFoto(View v) {
        if (midias.size() < 5) {
            //dispatchTakePictureIntent();
            Intent myIntent = new Intent(ActGasto.this, ActFoto.class);
            Bundle b = new Bundle();
            b.putLong("id", gasto.getId());
            b.putInt("v", midias.size());
            myIntent.putExtras(b);
            startActivityForResult(myIntent, REQUEST_PHOTO_INTENT);
        } else {
            Toast.makeText(ctx, "Número máximo de mídias atingido", Toast.LENGTH_LONG).show();
        }
    }

    public void novoAudioGaleria(View v) {

        if (midias.size() < 5) {
            dispatchTakeAudioGaleriaIntent();
        } else {
            Toast.makeText(ctx, "Número máximo de mídias atingido", Toast.LENGTH_LONG).show();
        }
    }

    public void novoVideo(View v) {
        if (midias.size() < 5) {
            //dispatchTakePictureIntent();
            Intent myIntent = new Intent(ActGasto.this, ActVideo.class);
            Bundle b = new Bundle();
            b.putLong("id", gasto.getId());
            b.putInt("v", midias.size());
            myIntent.putExtras(b);
            //ActGasto.this.startActivity(myIntent);
            startActivityForResult(myIntent, REQUEST_PHOTO_INTENT);
        } else {
            Toast.makeText(ctx, "Número máximo de mídias atingido", Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakeAudioGaleriaIntent() {

        tipo_midia = MediaStore.Audio.Media.DATA;
        Intent takeMidiaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //if (takeMidiaIntent.resolveActivity(getPackageManager()) != null) {

        startActivityForResult(Intent.createChooser(takeMidiaIntent.setType("audio/*"),
                "Selecione um audio"), REQUEST_MIDIA_CAPTURE);
        //}

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageName = "btv_f_" + gasto.getId() + "_" + Integer.toString(midias.size()) + ".png";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File image = new File(storageDir, imageName);
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            midias.add(new Midia(gasto, imageFile.getPath(), 1));
            salvaMidias();

        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            midias.add(new Midia(gasto, videoFile.getPath(), 1));
            salvaMidias();

        } else if (requestCode == REQUEST_MIDIA_CAPTURE && resultCode == RESULT_OK) {
            try {

                Uri uri = data.getData();
                String path = RealPathUtil.getRealPath(getApplicationContext(), uri, tipo_midia);
                midiaFile = new File(path);

                //midias.add(new Midia(null,gasto,1,path, null,null,"",latitude,longitude));
                salvaMidia(gasto.getId(),path);

            } catch (Exception e) {
                String str = e.getLocalizedMessage().toString();
                System.out.println(e);
            }
        }
        atualizaMidia();
    }

    private void galleryAddMidia(File midiaFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(midiaFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @SuppressLint("StaticFieldLeak")
    public void enviarAgora(View v) {
        if (num_cand == 0) {
            //Toast.makeText(ctx, "Selecione um candidato!!", Toast.LENGTH_LONG);
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione um candidato!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
        } else if (midias.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione uma mídia!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
        } else if (!Valida.isValidCPF(edtCPF.getText().toString().trim())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("CPF inválido!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
            edtCPF.setError("CPF Inválido!");
        } else {  // TESTAR TAMANHO DAS MIDIAS
            final String cpf = edtCPF.getText().toString();
            final String descricao = edtDescricao.getText().toString();
            final int tipo = spTipo.getSelectedItemPosition();
            final EnviarGasto envio = new EnviarGasto();
            final Timestamp data = new Timestamp(System.currentTimeMillis());
            try {
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        mDialog = ProgressDialog.show(ActGasto.this,
                                "Enviando gasto", "Aguarde...", true, true);
                        mDialog.setCancelable(false);
                    }

                    @Override
                    protected String doInBackground(String... params) {

                        String ret = "", res = null;
                        //String realUrl = "http://apps.tre-mt.jus.br/caixa1/api/gasto/candidato/"
                        String realUrl = " http://apps.tre-mt.jus.br/caixa1-dev/api/gasto/candidato/" +
                                db.getUrlCandidato(num_cand);
                        JSONObject json = new JSONObject();
                        gasto.setDescricao(descricao);
                        gasto.setCriadoEm(data);
                        gasto.setTipoDeGasto(tipo);
                        if (!cpf.equals("")) {
                            gasto.setInformante(new Informante(cpf));
                        }
                        try {
                            json.put("descricao", descricao);
                            json.put("criadoEm", System.currentTimeMillis());
                            json.put("tipoDeGasto", tipo);
                            String sjson = json.toString();
                            if (!cpf.equals("")) {
                                sjson = sjson.substring(0, sjson.lastIndexOf("}"));
                                sjson += ",\"informante\":{\"cpf\": \"" + cpf + "\"}}";
                            }
                            res = envio.requestGasto(realUrl, sjson);
                        } catch (Exception e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                        // manage exceptions
                        if (res.indexOf("motivo") == -1) {
                            ret += "G";
                            // enviar Mídias...
                            try {
                                JSONObject rjson = new JSONObject(res);
                                String protocolo = rjson.getString("protocolo");
                                Long recebidoEm = rjson.getLong("recebidoEm");
                                String token = rjson.getString("chaveParaEnvioDeMidia");
                                Date dt = new Timestamp(recebidoEm);
                                rgasto = new RespGasto(Long.valueOf(protocolo), dt, token);
                                String midiaUrl = "http://apps.tre-mt.jus.br/caixa1-dev";
                                //String midiaUrl = "http://apps.tre-mt.jus.br/caixa1";

                                outerloop:
                                for (int i = 0; i < midias.size(); i++) {

                                    res = envio.requestMidia(midiaUrl, protocolo,
                                            token, midias.get(i));


                                    if (res.indexOf("motivo") == -1) {
                                        JSONObject jmidia = new JSONObject(res);
                                        token = jmidia.getString("chaveParaEnvioDeMidia");

                                        midias.get(i).setToken(token);
                                        midias.get(i).setDh_envio(new Timestamp(jmidia.getLong("recebidoEm")));
                                        ret += "" + i;
                                    } else {
                                        ret = res;
                                        break outerloop;
                                    }
                                }
                            } catch (Exception e) {
                                ret = "\"motivo\":\"Erro ao enviar: Tente novamente!\"";
                            }
                        } else {
                            ret = res;
                        }
                        return ret;
                    }

                    @Override
                    protected void onPostExecute(String res) {
                        mDialog.dismiss();
                        if (res.length() == midias.size() + 1) {
                            encerrar();
                            Toast.makeText(ctx, "Dados enviados com sucesso!!!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            String msg;
                            if (res.length() > 10 )
                                msg = res.substring(res.indexOf("motivo")+9);
                            else
                                msg = res;
                            Toast.makeText(ctx, msg.substring(0, msg.indexOf("\"")), Toast.LENGTH_LONG).show();
                        }
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
            }
        }
    }

    public void encerrar() {

        db.saveGasto(gasto, num_cand, rgasto);
        db.salvaMidias(gasto, midias);
        db.newGasto();
        finish();

    }

    private void salvaMidias() {
        db.salvaMidias(gasto, midias);
        atualizaMidia();
    }

    private void salvaMidia(Long id, String nome) {
        db.salvaMidia(id, nome);
    }

    public void enviarDepois(View v) {
        if (num_cand == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione um candidato!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
        } else if (midias.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione uma mídia!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
        } else if (!Valida.isValidCPF(edtCPF.getText().toString().trim())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("CPF inválido!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();
            edtCPF.setError("CPF Inválido!");
        } else {
            final String cpf = edtCPF.getText().toString();
            final String descricao = edtDescricao.getText().toString();
            final int tipo = spTipo.getSelectedItemPosition();
            final EnviarGasto envio = new EnviarGasto();
            final Timestamp data = new Timestamp(System.currentTimeMillis());
            gasto.setDescricao(descricao);
            gasto.setCriadoEm(data);
            gasto.setTipoDeGasto(tipo);
            if (!cpf.equals("")) {
                gasto.setInformante(new Informante(cpf));
            }
            Toast.makeText(ctx, "Dados gravados com sucesso!!!", Toast.LENGTH_LONG).show();
            db.saveGasto(gasto, num_cand);
            db.salvaMidiasSemToken(gasto, midias);
            db.newGasto();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db.close();
        return;
    }

}
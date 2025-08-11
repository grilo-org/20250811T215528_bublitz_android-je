package br.jus.tre_mt.caixa1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.Candidato;
import dao.DBAdapter;
import dao.Estado;
import dao.Gasto;
import dao.Informante;
import dao.Midia;
import dao.RespGasto;
import dao.TipoDeGasto;
import dao.UnidadeEleitoral;
import utils.Utilitario;

public class ActGastoNacional extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int REQUEST_MIDIA_CAPTURE = 3;
    static final int REQUEST_PHOTO_INTENT = 4;

    final Context ctx = this;

    static long id_munic = 0, num_cand = 0;
    static String nom_munic, nom_cand;
    static List<UnidadeEleitoral> listUnidadeEleitoral = null;
    static List<Candidato> listCandidato = null;
    //static List<Candidato> cands = null;

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
    static EditText edtDescricao, etUrl;
    static LinearLayout llUrlSiteCampanha;
    TextView tvUrl;
    String url = "";
    String tipo_midia;
    ProgressDialog mDialog;
    List<RespGasto> resp;
    Integer idTipoGastoSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gasto_nacional);

        db = new DBAdapter(ctx);

        db.open();

        gridMidia = (BGridView) findViewById(R.id.gridMidia);

        //Tipo de gasto

        spTipo = findViewById(R.id.spTipo);

        List<TipoDeGasto> listTipoDeGasto = db.getTipoDeGasto();

        TipoDeGastoAdapter tipoDeGastoAdapter = new TipoDeGastoAdapter(ctx, R.layout.row_tipo_de_gasto,
                listTipoDeGasto);

        spTipo.setAdapter(tipoDeGastoAdapter);
        spTipo.setSelection(0);

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                idTipoGastoSelected = Integer.parseInt(((TextView) view.findViewById(R.id.lbl_id)).getText().toString());

                if (spTipo.getSelectedItemPosition() == 10) {

                    llUrlSiteCampanha.setVisibility(View.VISIBLE);

                } else {

                    etUrl.setText("");
                    llUrlSiteCampanha.setVisibility(View.GONE);

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //CPF
        edtCPF = findViewById(R.id.edtCPF);
        edtDescricao = findViewById(R.id.edtDescricao);

        //Cidades (Unidades Eleitorais)
        edtMunic = findViewById(R.id.actCidades);
        edtMunic.clearListSelection();
        edtMunic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                id_munic = l;
                //nom_munic = edtMunic.getText().toString();

            }

        });

        edtMunic.setOnFocusChangeListener(new AdapterView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (Utilitario.isNullOrEmpty(edtMunic.getText().toString().trim())){

                    id_munic = 0;

                }

            }
        });

        //Candidatos
        edtCand = findViewById(R.id.actCandidatos);
        //edtCand.clearListSelection();
        edtCand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView tvIdCandidato = view.findViewById(R.id.tvIdCandidato);

                num_cand = Long.parseLong(tvIdCandidato.getText().toString());
                //nom_cand = edtCand.getText().toString();

            }
        });


        edtCand.setOnFocusChangeListener(new AdapterView.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (Utilitario.isNullOrEmpty(edtCand.getText().toString().trim())){

                    num_cand = 0;

                }

            }
        });

        etUrl = findViewById(R.id.etUrl);

        gasto = db.novoGasto();
        num_cand = db.numCand(gasto);

        listUnidadeEleitoral = db.getUnidadeEleitoral("MT");

        listCandidato = db.getCandidatos("MT");

        //Busca os candidatos
        //atualizaCand();

        btnVideo = (ImageButton) findViewById(R.id.btnVideo);
        btnSom = (ImageButton) findViewById(R.id.btnSom);


        atualizaMidia();


        //Seta a lista de municipios (unidade eleitorais) no adaptador
        UnidadeEleitoralAdapter ueAdapter = new UnidadeEleitoralAdapter(ctx, R.layout.row_unidade_eleitoral,
                listUnidadeEleitoral);

        edtMunic.setAdapter(ueAdapter);


        //Seta a lista de candidatos no adaptador
        CandidatoAdapter candidatoAdapter = new CandidatoAdapter(this,
                listCandidato);

        edtCand.setAdapter(candidatoAdapter);


        etUrl = findViewById(R.id.etUrl);
        llUrlSiteCampanha = findViewById(R.id.llUrlCampanha);

        llUrlSiteCampanha.setVisibility(View.GONE);

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


    public void novaFoto(View v) {

        if (midias.size() < 5) {
            //dispatchTakePictureIntent();
            Intent myIntent = new Intent(ActGastoNacional.this, ActFoto.class);
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

        if (verificarPermissaoAcessoMidia() == 0) {

            if (midias.size() < 5) {
                dispatchTakeAudioGaleriaIntent();
            } else {
                Toast.makeText(ctx, "Número máximo de mídias atingido", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

    }

    public int verificarPermissaoAcessoMidia() {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        return permissionCheck;

    }

    public void novoVideo(View v) {

        if (midias.size() < 5) {
            //dispatchTakePictureIntent();
            Intent myIntent = new Intent(ActGastoNacional.this, ActVideo.class);
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

        final int tipo = idTipoGastoSelected;

        if (num_cand == 0) {
            //Toast.makeText(ctx, "Selecione um candidato!!", Toast.LENGTH_LONG);
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione um candidato!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();

        } else if (id_munic == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione uma cidade!")
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

        } else if (tipo == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Escolha o tipo de gasto!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();

        } else {  // TESTAR TAMANHO DAS MIDIAS

            final String cpf = edtCPF.getText().toString();
            final String descricao = edtDescricao.getText().toString();

            url = etUrl.getText().toString();
            final EnviarGasto envio = new EnviarGasto();
            final Timestamp data = new Timestamp(System.currentTimeMillis());

            try {
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        mDialog = ProgressDialog.show(ActGastoNacional.this,
                                "Enviando gasto", "Aguarde...", true, true);
                        mDialog.setCancelable(false);
                    }

                    @Override
                    protected String doInBackground(String... params) {

                        String ret = "", res = null;
                        //String realUrl = "http://apps.tre-mt.jus.br/caixa1/api/gasto/candidato/"
                        String realUrl = "http://apps.tre-mt.jus.br/caixa1-dev/api/gasto/candidato/" +
                                db.getUrlCandidato(num_cand);
                        JSONObject json = new JSONObject();

                        gasto .setDescricao(descricao);
                        gasto.setCriadoEm(data);
                        gasto.setTipoDeGasto(tipo);
                        gasto.setIdUe(id_munic);

                        if (!Utilitario.isNullOrEmpty(url)) {

                            gasto.setUrl(url);

                        }else {
                            gasto.setUrl("");
                        }

                        if (!cpf.equals("")) {
                            gasto.setInformante(new Informante(cpf));
                        }
                        try {
                            json.put("descricao", descricao);
                            json.put("criadoEm", System.currentTimeMillis());
                            json.put("tipoDeGasto", tipo);
                            json.put("ueId", id_munic);

                            if (!Utilitario.isNullOrEmpty(url)) {

                                json.put("url", url);

                            } else
                            {
                                json.put("url", "");
                            }

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
                                String processo = rjson.getString("processo");
                                Date dt = new Timestamp(recebidoEm);
                                rgasto = new RespGasto(Long.valueOf(protocolo), dt, token, processo);
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
                            if (res.length() > 10)
                                msg = res.substring(res.indexOf("motivo") + 9);
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
        gridMidia.clearChoices();
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

        final int tipo = idTipoGastoSelected;

        if (num_cand == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione um candidato!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();

        } else if (id_munic == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Selecione uma cidade!")
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

        } else if (tipo == 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Erro!")
                    .setMessage("Escolha o tipo de gasto!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Voltar", null)
                    .show();

        } else {

            final String cpf = edtCPF.getText().toString();
            final String descricao = edtDescricao.getText().toString();

            final EnviarGasto envio = new EnviarGasto();
            url = etUrl.getText().toString();
            final Timestamp data = new Timestamp(System.currentTimeMillis());
            gasto.setDescricao(descricao);
            gasto.setCriadoEm(data);
            gasto.setTipoDeGasto(tipo);
            gasto.setIdUe(id_munic);

            if (!Utilitario.isNullOrEmpty(url.trim())) {
                gasto.setUrl(url);
            }else
            {
                gasto.setUrl("");
            }

            if (!cpf.equals("")) {
                gasto.setInformante(new Informante(cpf));
            }
            Toast.makeText(ctx, "Dados gravados com sucesso!!!", Toast.LENGTH_LONG).show();
            db.saveGasto(gasto, num_cand);
            db.salvaMidiasSemToken(gasto, midias);
            db.newGasto();
            gridMidia.clearChoices();
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
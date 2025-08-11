package br.jus.tremt.soberania.utils;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

import br.jus.tremt.soberania.BuildConfig;
import br.jus.tremt.soberania.modelo.Eleitor;
import br.jus.tremt.soberania.modelo.Proposta;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jorgebublitz on 19/12/2017.
 */

public class ComunicaWS {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String url_tre = "https://apps3.tre-mt.jus.br/soberano/api/";
    //private static final String url_tre = "http://10.18.1.85:8180/soberano-homolog/api/";
    //private static final String url_tre = "http://10.0.2.2:8080/soberano/api/";
    private static final String url_cad_eleitor = url_tre + "CadastrarEleitor";
    private static final String url_verifica_cod = url_tre + "ValidarCodigoAtivacao";
    private static final String url_limpa_eleitor = url_tre + "ResetarCadastroToken";
    private static final String url_reenvia_email = url_tre + "ReenviaEmail";
    private static final String url_qtde_proposta = url_tre + "QtdProposta";
    private static final String url_retorna_propostas = url_tre + "RetornarPropostas";
    private static final String url_retorna_pdf = url_tre + "pdf";
    private static final String url_voto_proposta = url_tre + "VotoProposta";
    private static final String url_retorna_data = url_tre + "DataHoraServidor";
    private static final String url_retorna_total_votos = url_tre + "RetornarTotalVotos";

    private static OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();


    private static String token;
    private static String versionName;
    private Context ctx;

    public ComunicaWS(Context ctx) {
        this.ctx = ctx;
        try {
            this.token = SecurePreferences.generateAesKeyName(this.ctx);
            this.versionName = BuildConfig.VERSION_NAME;
        } catch (InvalidKeySpecException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    private static String requestData(String URL, String jsonObjSend) {
        RequestBody body = RequestBody.create(JSON, jsonObjSend);

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Accept", "application/json")
                .addHeader("Content-type", "application/json;charset=utf-8;")
                .addHeader("X-SOBERANIA-1-TOKEN", token)
                //.addHeader("X-SOBERANIA-1-VERSION", versionName)
                .post(body)
                .build();
        Response response = null;
        String ret = "";

        try {
            response = client.newCall(request).execute();
            ret = response.body().string();
        } catch (java.net.SocketTimeoutException e) {
            ret = "{\"motivo\":\"Erro ao conectar: Tempo excedido! Verifique sua conexão!\"}";
        } catch (Exception e) {
            String msg = e.getMessage();
            ret = "{\"motivo\":\"" + msg + "\"}";
        }
        return ret;
    }

    public String cadastraEleitor(Eleitor eleitor) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("titulo", eleitor.getTitulo());
            json.put("nomeEleitor", eleitor.getNome());
            json.put("nomeMae", eleitor.getNomeMae());
            //json.put("TemMae", eleitor.isNaoConstaMae());
            json.put("email", eleitor.getEmail());
            json.put("celular", eleitor.getTelefone());
            json.put("dataNasc", eleitor.getDataInt());
            json.put("hash", eleitor.getHash());
            ret = requestData(url_cad_eleitor, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String limpaEleitor(long idEleitor) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", idEleitor);
            ret = requestData(url_limpa_eleitor, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String verificaCodigo(long idEleitor, String codigo) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", idEleitor);
            json.put("codigoAtivacao", codigo);
            ret = requestData(url_verifica_cod, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String reenviaEmail(long idEleitor, String email) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", idEleitor);
            json.put("email", email);
            ret = requestData(url_reenvia_email, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String qtdeProposta(long idEleitor, String data) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("data", data);
            json.put("idEleitor", idEleitor);
            ret = requestData(url_qtde_proposta, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String totalVotos(long idEleitor, Proposta proposta) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", idEleitor);
            json.put("idProposta", proposta.getId());
            ret = requestData(url_retorna_total_votos, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String retornaPropostas(Eleitor eleitor, String data) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", eleitor.getId());
            json.put("idLocalidade", eleitor.getLocalidadeId());
            json.put("idUf", eleitor.getUfId());
            json.put("numeroPagina", 1);
            json.put("qtdRegistros", 1000);
            json.put("qtdAbrangencia", 3);
            json.put("abrangencia", 0);
            json.put("dataAtualizacao", data);
            ret = requestData(url_retorna_propostas, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String votoProposta(long idEleitor, long idProposta, String voto, String tipoAcao) {
        String ret = "";
        JSONObject json = new JSONObject();
        try {
            json.put("idEleitor", idEleitor);
            json.put("idProposta", idProposta);
            json.put("voto", voto);
            json.put("tipoAcao", tipoAcao);
            ret = requestData(url_voto_proposta, json.toString());
        } catch (JSONException e) {
        }
        return ret;
    }

    public String retornaDataHora(long idEleitor) {
        String ret = "";
        try {
            ret = requestData(url_retorna_data, "{}");
        } catch (Exception e) {
        }
        return ret;
    }

    public byte[] inteiroTeor(Proposta proposta) {
        byte[] ret;

        String url = url_retorna_pdf + "/" + String.valueOf(proposta.getId()) + "/";
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        //httpBuider.addQueryParameter("id", id);

        Request request = new Request.Builder()
                .url(httpBuider.build())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful())
                ret = response.body().bytes();
            else
                ret = null;

        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

}

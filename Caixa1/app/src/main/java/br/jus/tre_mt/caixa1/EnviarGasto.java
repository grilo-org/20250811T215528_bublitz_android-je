package br.jus.tre_mt.caixa1;


import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import dao.Midia;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jorgebublitz on 21/08/16.
 */

public class EnviarGasto {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String chave = "#TOKEN$DE$TESTE#";
    //public static final String chave = "e9vdZM2ke4MHp/Botu4fArp0IaAvQjE8VAn/JY/MWEY5aMIsh4Sw58LdB6cOt0/S";
    public static final String token_atu = "YFGqrXdD+VhzDl4SjB3WThGdu/ZIx9NGbHphWSOfnpOsq9ZxzuKkb/+OCX8QqCh/MyMN9Tkvd8atFGdFN/lonBVF/WjcogT4ZjKUY2y1GzQpM5v5irSHzGZXDmd6g45/o2l4P6T5GVunJF2f/Ev9pOGXoDxZZl6y4d6CtA2EYdVhgPhv/KYIFmzKFlvPB8pznE7jvWwyq8vU04CJAyt2WUZ7POPAD5F+";

    public static String requestGasto(String URL, String jsonObjSend) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, jsonObjSend);

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Accept", "application/json")
                .addHeader("Content-type", "application/json;charset=UTF-8;")
                .addHeader("X-CAIXA-1-TOKEN", chave)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
        }
        return "\"motivo\":\"Erro ao enviar dados! Tente novamente mais tarde!\"";
    }


    public static String requestMidia(String URL, String protocolo, String token, Midia midia) {
        String ret = "";
        File file1 = null;
        InputStream imagem = null;
        // testar tamanho
        try {
            file1 = new File(midia.getFilename());
            //if (file1.length() <= 50*1024*1024) {
            //imagem = new FileInputStream(file);
            //MyByteArrayOutputStream buffer = new MyByteArrayOutputStream();
            //int nRead;
            //byte[] data = new byte[1024];
            //while ((nRead = imagem.read(data, 0, data.length)) != -1) {
            //    buffer.write(data, 0, nRead);
            //}
            //imagem.close();
            //buffer.flush();
            //byte[] byteArray = buffer.toByteArray();

            String ext = midia.getFilename().substring(midia.getFilename().lastIndexOf(".") + 1);
            String ftipo = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            MediaType tipo = MediaType.parse(ftipo);

            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(tipo, file1);
            String realUrl = URL + "/api/gasto/" + protocolo + "/midia/";


            String latitude = "";
            String longitude = "";


            if (midia.getLatitude() != 0) {
                latitude = String.valueOf(midia.getLatitude());
            }

            if (midia.getLongitude() != 0) {
                longitude = String.valueOf(midia.getLongitude());
            }


            Request request = new Request.Builder()
                    .url(realUrl)
                    .addHeader("X-CAIXA-1-TOKEN", chave)
                    .addHeader("X-CAIXA-1-MEDIA-KEY", token)
                    .addHeader("dataRegistroMidia", midia.getDataRegistroMidia())
                    .addHeader("latitude", latitude)
                    .addHeader("longitude", longitude)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            ret = response.body().string();
            //} else { ret = "\"motivo\":\"Erro ao enviar: Arquivo muito grande!\""; }
        } catch (java.net.SocketTimeoutException e) {
            return "\"motivo\":\"Erro ao enviar: Tempo excedido! Verifique sua conexão!\"";
        } catch (Exception e) {
            return "\"motivo\":\"Erro ao enviar: "+e.getMessage()+"\"";
        }
        return ret;
    }

}

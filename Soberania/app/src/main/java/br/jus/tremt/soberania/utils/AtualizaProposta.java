package br.jus.tremt.soberania.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.jus.tremt.soberania.adapter.DBAdapter;
import br.jus.tremt.soberania.modelo.Parametro;
import br.jus.tremt.soberania.modelo.Proposta;

/**
 * Created by jorgebublitz on 21/02/2018.
 */

public class AtualizaProposta {
    public String gravar(Context ctx, String propostas) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String ret;
        Date d;
        DBAdapter db = new DBAdapter(ctx);
        Parametro param = db.getParametros();
        db.open();
        try {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<ArrayList<Proposta>>() {
            }.getType();
            List<Proposta> lista = gson.fromJson(propostas, tipoLista);
            for (int i = 0; i < lista.size(); i++) {
                Proposta p = lista.get(i);
                d = new Date(Long.valueOf(p.getDataFim()));
                p.setDataFim(df.format(d));
                d = new Date(Long.valueOf(p.getDataInicio()));
                p.setDataInicio(df.format(d));
                d = new Date(Long.valueOf(p.getDataProtocolo()));
                p.setDataProtocolo(df.format(d));
                if (p.getDataVoto() != null) {
                    d = new Date(Long.valueOf(p.getDataVoto()));
                    p.setDataVoto(df.format(d));
                }
                db.setProposta(p);
            }
            df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            param.setDataUltima(df.format(new Date()));
            db.setParametros(param);
            ret = "ok";
        } catch (Exception e) {
            ret = "error";
        }
        db.close();
        return ret;
    }
}

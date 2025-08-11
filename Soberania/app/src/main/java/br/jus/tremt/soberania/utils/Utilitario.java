package br.jus.tremt.soberania.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilitario {

    public static boolean isNullOrEmpty(@Nullable String string) {
        return  string == null || string.isEmpty();
    }


    public static boolean verificaConexao(Context ctx) {

        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


    public static Date timeStampToDate(Long time){

        if(time != null) {
            Timestamp stamp = new Timestamp(time);
            Date date = new Date(stamp.getTime());
            return date;
        }

        return null;
    }

    public static String dateToString(String tipoFormato, Date date) {

        if(isNullOrEmpty(tipoFormato)) {
            return null;
        }

        if(date == null) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(tipoFormato);

        String dataConvert = df.format(date);

        return dataConvert;
    }

    public static Date stringToDate(String tipoFormato, String dataString) throws ParseException {

        if(isNullOrEmpty(tipoFormato)) {
            return null;
        }

        if(isNullOrEmpty(dataString)) {
            return null;
        }

        SimpleDateFormat formato = new SimpleDateFormat(tipoFormato);
        Date date = formato.parse(dataString);

        return date;
    }

}

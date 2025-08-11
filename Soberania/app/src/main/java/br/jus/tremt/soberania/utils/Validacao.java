package br.jus.tremt.soberania.utils;

import android.app.AlertDialog;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 031066031880 on 20/12/2017.
 */

public class Validacao {


    public boolean validarCampo(String valorCampo) {

        if (valorCampo.isEmpty()) {
            return true;
        }

        return false;
    }

    //formado da data: yyyy-mm-dd
    public static String formatarData(String data) {

        try {

            String[] array = data.split("-");
            String dataFormatada = array[2] + "/" + array[1] + "/" + array[0];

            return dataFormatada;

        } catch (Exception e) {
        }


        return data;
    }

    public boolean validarEmail(String email) {

        String regex = "^[\\w-]+(?:\\.[\\w-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public boolean validarData(String data) {

        String padrao = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(padrao);

        //Captura erros de formatacao
        sdf.setLenient(false);

        try {

            sdf.parse(data);

            return true;

        } catch (ParseException e) {
        }

        return false;
    }

    public boolean ValidarTitulo(String strTitulo) {
        int dig1;
        int dig2;
        int dig3;
        int dig4;
        int dig5;
        int dig6;
        int dig7;
        int dig8;
        int dig9;
        int dig10;
        int dig11;
        int dig12;
        int dv1;
        int dv2;
        int qDig;


        if (strTitulo.length() < 12) { // Completar 12 dígitos
            strTitulo = "000000000000" + strTitulo;
            strTitulo = strTitulo.substring(strTitulo.length() - 12);
        } else if (strTitulo.length() > 12) {
            return false;
        }


        qDig = strTitulo.length(); // Total de caracteres

        dig1 = Integer.parseInt(Mid(strTitulo, qDig - 11, 1));
        dig2 = Integer.parseInt(Mid(strTitulo, qDig - 10, 1));
        dig3 = Integer.parseInt(Mid(strTitulo, qDig - 9, 1));
        dig4 = Integer.parseInt(Mid(strTitulo, qDig - 8, 1));
        dig5 = Integer.parseInt(Mid(strTitulo, qDig - 7, 1));
        dig6 = Integer.parseInt(Mid(strTitulo, qDig - 6, 1));
        dig7 = Integer.parseInt(Mid(strTitulo, qDig - 5, 1));
        dig8 = Integer.parseInt(Mid(strTitulo, qDig - 4, 1));
        dig9 = Integer.parseInt(Mid(strTitulo, qDig - 3, 1));
        dig10 = Integer.parseInt(Mid(strTitulo, qDig - 2, 1));
        dig11 = Integer.parseInt(Mid(strTitulo, qDig - 1, 1));
        dig12 = Integer.parseInt(Mid(strTitulo, qDig, 1));

        // Cálculo para o primeiro dígito verificador
        dv1 = (dig1 * 2) + (dig2 * 3) + (dig3 * 4) + (dig4 * 5) + (dig5 * 6)
                + (dig6 * 7) + (dig7 * 8) + (dig8 * 9);
        dv1 = dv1 % 11;

        if ((dig9 == 0) && ((dig10 == 1) || (dig11 == 2))) {// Se for SP ou MG
            if (dv1 == 0)
                dv1 = 1;
        }
        if (dv1 == 10) {
            dv1 = 0; // Se o resto for igual a 10, dv1 igual a zero
        }
        // Cálculo para o segundo dígito verificador
        dv2 = (dig9 * 7) + (dig10 * 8) + (dv1 * 9);
        dv2 = dv2 % 11;

        if ((dig9 == 0) && ((dig10 == 1) || (dig11 == 2))) {// Se for SP ou MG
            if (dv2 == 0)
                dv2 = 1;
        }
        if (dv2 == 10) {
            dv2 = 0; // Se o resto for igual a 10, dv1 igual a zero
        }

        // Validação dos dígitos verificadores
        if (dig11 == dv1 && dig12 == dv2) {
            return true;
        }

        return true;

    }

    public static String Mid(String texto, int inicio, int tamanho) {
        String strMid = texto.substring(inicio - 1, inicio + (tamanho - 1));
        return strMid;
    }

    public void msgbox(Context ctx, String titulo, String texto) {
        AlertDialog.Builder mensagem = new AlertDialog.Builder(ctx);
        mensagem.setTitle(titulo);
        mensagem.setMessage(texto);
        mensagem.setNeutralButton("Ok", null);
        mensagem.show();
    }
}

package br.jus.tremt.soberania.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.jus.tremt.soberania.modelo.Eleitor;
import br.jus.tremt.soberania.modelo.Parametro;
import br.jus.tremt.soberania.modelo.Proposta;
import br.jus.tremt.soberania.utils.SecurePreferences;

/**
 * Created by jorgebublitz on 18/12/2017.
 */

public class DBAdapter {
    private File file;
    private final Context ctx;
    private DatabaseHelper dbh;
    private SQLiteDatabase db1 = null;

    public DBAdapter(Context ctx) {
        this.ctx = ctx;
        file = ctx.getDatabasePath("tresoberania.db3");
        //file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tresoberania.db3");
        dbh = new DatabaseHelper(ctx);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context ctx) {
            super(ctx, "", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            onCreate(db);
        }
    }

    // ---abre banco---
    // se não existir copia de Assets
    public DBAdapter open() throws SQLException {
        String p = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/"));
        File direct = new File(p);
        if (!direct.exists()) {
            if (direct.mkdir())
                ; // se não existir a pasta é criada
        }
        File file = new File(this.file.getPath());
        if (!file.exists()) {
            try {
                CopyDB(this.ctx.getAssets().open(this.file.getName()),
                        new FileOutputStream(this.file.getPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (db1 == null) {
            db1 = SQLiteDatabase.openDatabase(this.file.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        } else if (!db1.isOpen()) {
            db1 = SQLiteDatabase.openDatabase(this.file.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        //provisório, para apagar propostas do período de testes
        db1.execSQL("delete from PROPOSTA where ID <> 4");

        return this;
    }

    // ---fecha o banco---
    public void close() {
        db1.close();
        dbh.close();
    }

    private void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public List<Proposta> allProposta(boolean jaVotada, boolean emVotacao, int abrangencia, String tipoVoto) {
        List<Proposta> lista = new ArrayList<Proposta>();

        String sql = "select ID, ABRANGENCIA, NOME, AUTOR, DESCRICAO, DATAINICIO, DATAFIM, "
                + "DATAVOTO, VOTO, APROVADO, REJEITADO, LINK, LIDO, PROTOCOLO, DATAPROTOCOLO, " +
                "LINKACOMPANHARPROJETO from PROPOSTA ";


        if (jaVotada)
            sql += "where VOTO = '" + tipoVoto + "' ";
        else {
            sql += "where (VOTO is null or VOTO='')  ";

            if (emVotacao)
                sql += "and DataFim >= date('now') ";
            else
                sql += "and DataFim < date('now') ";
        }
        if (abrangencia > 0)
            sql += "and ABRANGENCIA=" + Integer.toString(abrangencia);

        sql += " order by DATAINICIO desc";

        Cursor c = this.db1.rawQuery(sql, null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                lista.add(new Proposta(c.getLong(0), c.getInt(1),
                        c.getString(2), c.getString(3), c.getString(4),
                        c.getString(5), c.getString(6), c.getString(7),
                        c.getString(8), c.getLong(9), c.getLong(10),
                        c.getString(11), c.getString(12), c.getString(13),
                        c.getString(14), c.getString(15)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<Proposta> allProposta(int abrangencia) {

        List<Proposta> lista = new ArrayList<Proposta>();

        String sql = "SELECT ID, ABRANGENCIA, NOME, AUTOR, DESCRICAO, DATAINICIO, DATAFIM, "
                + "DATAVOTO, VOTO, APROVADO, REJEITADO, LINK, LIDO, PROTOCOLO, DATAPROTOCOLO, " +
                "LINKACOMPANHARPROJETO " +
                "FROM PROPOSTA " +
                "WHERE DATAFIM < date('now') ";

        if (abrangencia > 0)
            sql += "AND ABRANGENCIA=" + Integer.toString(abrangencia);

        sql += " ORDER BY DATAINICIO DESC";

        Cursor c = this.db1.rawQuery(sql, null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                lista.add(new Proposta(c.getLong(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4),
                        c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getLong(9), c.getLong(10), c.getString(11), c.getString(12), c.getString(13),
                        c.getString(14), c.getString(15)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public Proposta getProposta(long id) {
        Proposta p = new Proposta();
        Cursor c = this.db1.rawQuery("select ID, ABRANGENCIA, NOME, AUTOR, DESCRICAO, DATAINICIO, DATAFIM, "
                        + "DATAVOTO, VOTO, APROVADO, REJEITADO, LINK, LIDO, PROTOCOLO, DATAPROTOCOLO, LINKACOMPANHARPROJETO from PROPOSTA where ID=?",
                new String[]{Long.toString(id)});
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            p = new Proposta(c.getLong(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4),
                    c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                    c.getLong(9), c.getLong(10), c.getString(11), c.getString(12), c.getString(13),
                    c.getString(14), c.getString(15));
        } else {
            p = null;
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return p;
    }

    public void votarProposta(long id, String voto) {
        db1.beginTransaction();
        String sql = "update PROPOSTA set VOTO='" + voto + "', DATAVOTO=date('now') ";

        /*if (voto.equals("A"))
            sql += "APROVADO=APROVADO+1 ";
        else
            sql += "REJEITADO=REJEITADO+1 "; */

        sql += "where ID=" + String.valueOf(id);
        db1.execSQL(sql);
        db1.setTransactionSuccessful();
        db1.endTransaction();
    }

    public void atualizarTotalVotos(long idProposta, long totalAprovado, long totalRejeitado) {

        db1.beginTransaction();
        String sql = "UPDATE proposta "
                + " SET aprovado = " + totalAprovado + ","
                + " rejeitado = " + totalRejeitado
                + " WHERE id = " + idProposta;

        db1.execSQL(sql);
        db1.setTransactionSuccessful();
        db1.endTransaction();
    }

    public void setProposta(Proposta p) {
        ContentValues values = new ContentValues();
        values.put("ID", p.getId());
        values.put("NOME", p.getNome());
        values.put("AUTOR", p.getAutor());
        values.put("DESCRICAO", p.getDescricao());
        values.put("DATAINICIO", p.getDataInicio());
        values.put("DATAFIM", p.getDataFim());
        values.put("APROVADO", p.getTotalAprovado());
        values.put("REJEITADO", p.getTotalRejeitado());
        values.put("LINK", p.getLink());
        values.put("LIDO", p.getLido());
        values.put("ABRANGENCIA", p.getAbrangencia());
        values.put("PROTOCOLO", p.getProtocolo());
        values.put("DATAPROTOCOLO", p.getDataProtocolo());
        values.put("LINKACOMPANHARPROJETO", p.getLinkAcompanharProjeto());
        values.put("VOTO", p.getVoto());
        values.put("DATAVOTO", p.getDataVoto());

        Proposta p2 = getProposta(p.getId());
        if (p2 != null) {
            p.setVoto(p2.getVoto());
            p.setDataVoto(p2.getDataVoto());
            p.setLido(p2.getLido());
            values.put("LIDO", p.getLido());
        }

        db1.beginTransaction();
        // o método replace funciona como um "INSERT OR UPDATE"
        // ou seja, testa a chave primária (ID) para fazer
        // inserção ou atualização na tabela
        db1.replace("PROPOSTA", "Voto", values);
        db1.setTransactionSuccessful();
        db1.endTransaction();
    }

    public boolean temEleitor() {
        boolean ret = (idEleitor() > 0);
        return ret;
    }

    public boolean isAtivado() {
        boolean value = new SecurePreferences(ctx).getBoolean("isAtivado", false);
        return value;
    }

    public long idEleitor() {
        long value = new SecurePreferences(ctx).getLong("Id", -1L);
        return value;
    }

    public void activeCadastro() {
        new SecurePreferences(ctx).edit().putBoolean("isAtivado", true).commit();
    }

    public void setEleitor(Eleitor eleitor) {
        SecurePreferences sp = new SecurePreferences(ctx);
        sp.edit().putLong("Id", eleitor.getId()).commit();
        sp.edit().putString("Titulo", eleitor.getTitulo()).commit();
        sp.edit().putString("DataHora", java.text.DateFormat.getDateTimeInstance().format(Calendar
                .getInstance().getTime())).commit();
        sp.edit().putString("Nome", eleitor.getNome()).commit();
        sp.edit().putString("NomeMae", eleitor.getNomeMae()).commit();
        sp.edit().putBoolean("NaoMae", eleitor.isNaoConstaMae()).commit();
        sp.edit().putString("Nascto", eleitor.getDataNascto()).commit();
        sp.edit().putString("Email", eleitor.getEmail()).commit();
        sp.edit().putString("Telefone", eleitor.getTelefone()).commit();
        sp.edit().putString("Municipio", eleitor.getMunicipio()).commit();
        sp.edit().putString("UF", eleitor.getUf()).commit();
        sp.edit().putString("LocalidadeId", eleitor.getLocalidadeId()).commit();
        sp.edit().putString("UfId", eleitor.getUfId()).commit();
        sp.edit().putString("Hash", eleitor.getHash()).commit();
    }

    public void deleteEleitor() {
        SecurePreferences sp = new SecurePreferences(ctx);
        sp.edit().remove("Id").commit();
        sp.edit().remove("Titulo").commit();
        sp.edit().remove("DataHora").commit();
        sp.edit().remove("Nome").commit();
        sp.edit().remove("NomeMae").commit();
        sp.edit().remove("NaoMae").commit();
        sp.edit().remove("Nascto").commit();
        sp.edit().remove("Email").commit();
        sp.edit().remove("Telefone").commit();
        sp.edit().remove("Municipio").commit();
        sp.edit().remove("UF").commit();
        sp.edit().remove("LocalidadeId").commit();
        sp.edit().remove("UfId").commit();
        sp.edit().remove("Hash").commit();
        sp.edit().remove("isAtivado").commit();
        sp.edit().remove("UltimaAtualiza").commit();
        // Limpa tabela Proposta
        open();
        db1.beginTransaction();
        db1.execSQL("delete from PROPOSTA");
        db1.setTransactionSuccessful();
        db1.endTransaction();
        close();
        Parametro param = getParametros();
        param.setDataUltima(null);
        setParametros(param);
    }

    public Eleitor getEleitor() {
        if (temEleitor()) {
            Eleitor eleitor = new Eleitor();
            SecurePreferences sp = new SecurePreferences(ctx);
            eleitor.setId(sp.getLong("Id", 0L));
            eleitor.setTitulo(sp.getString("Titulo", eleitor.getTitulo()));
            eleitor.setNome(sp.getString("Nome", eleitor.getNome()));
            eleitor.setNomeMae(sp.getString("NomeMae", eleitor.getNomeMae()));
            eleitor.setNaoConstaMae(sp.getBoolean("NaoMae", eleitor.isNaoConstaMae()));
            eleitor.setDataNascto(sp.getString("Nascto", eleitor.getDataNascto()));
            eleitor.setEmail(sp.getString("Email", eleitor.getEmail()));
            eleitor.setTelefone(sp.getString("Telefone", eleitor.getTelefone()));
            eleitor.setMunicipio(sp.getString("Municipio", eleitor.getMunicipio()));
            eleitor.setUf(sp.getString("UF", eleitor.getUf()));
            eleitor.setLocalidadeId(sp.getString("LocalidadeId", eleitor.getUf()));
            eleitor.setUfId(sp.getString("UfId", eleitor.getUf()));
            return eleitor;
        } else
            return null;
    }

    public void setParametros(Parametro param) {
        SecurePreferences sp = new SecurePreferences(ctx);
        sp.edit().putBoolean("propNac", param.isPropNac()).commit();
        sp.edit().putBoolean("propEst", param.isPropEst()).commit();
        sp.edit().putBoolean("propMun", param.isPropMun()).commit();
        sp.edit().putBoolean("downAut", param.isDownAut()).commit();
        sp.edit().putBoolean("downWifi", param.isDownWifi()).commit();
        sp.edit().putString("dataUltima", param.getDataUltima()).commit();
    }

    public Parametro getParametros() {
        Parametro param = new Parametro();
        SecurePreferences sp = new SecurePreferences(ctx);
        param.setPropMun(sp.getBoolean("propMun", true));
        param.setPropEst(sp.getBoolean("propEst", true));
        param.setPropNac(sp.getBoolean("propNac", true));
        param.setPropNac(false);
        param.setDownAut(sp.getBoolean("downAut", true));
        param.setDownWifi(sp.getBoolean("downWifi", true));
        param.setDataUltima(sp.getString("dataUltima", "01/01/2018 00:00:01"));
        return param;
    }

}

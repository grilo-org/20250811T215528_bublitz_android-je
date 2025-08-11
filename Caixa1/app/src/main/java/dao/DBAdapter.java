package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.Utilitario;

/**
 * Created by jorgebublitz on 15/08/16.
 */
public class DBAdapter {
    private File path1, path2;
    private final Context ctx;
    private DatabaseHelper dbh;
    private SQLiteDatabase db1 = null, db2 = null;

    public DBAdapter(Context ctx) {
        this.ctx = ctx;
        path1 = ctx.getDatabasePath("trecaixa1.db3");
        //path1 = ctx.getExternalFilesDir("trecaixa1.db3");
        path2 = ctx.getDatabasePath("btvcaixa1.db3");
        //path2 = ctx.getExternalFilesDir("btvcaixa1.db3");
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
    public DBAdapter open() throws SQLException {
        String p = path1.getAbsolutePath().substring(0, path1.getAbsolutePath().lastIndexOf("/"));
        File direct = new File(p);
        if (!direct.exists()) {
            if (direct.mkdir())
                ; // se não existir o diretorio é criado
        }
        File file = new File(path1.getPath());
        if (!file.exists()) {
            try {
                CopyDB(this.ctx.getAssets().open(path1.getName()),
                        new FileOutputStream(path1.getPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file = new File(path2.getPath());
        if (!file.exists()) {
            try {
                CopyDB(this.ctx.getAssets().open(path2.getName()),
                        new FileOutputStream(path2.getPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (db1 == null) {
            db1 = SQLiteDatabase.openDatabase(path1.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        } else if (!db1.isOpen()) {
            db1 = SQLiteDatabase.openDatabase(path1.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }

        if (db2 == null) {
            db2 = SQLiteDatabase.openDatabase(path2.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        } else if (!db2.isOpen()) {
            db2 = SQLiteDatabase.openDatabase(path2.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return this;
    }

    // ---fecha o banco---
    public void close() {
        db1.close();
        db2.close();
        dbh.close();
    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public List<Municipio> getMunicipios() {
        List<Municipio> lista = new ArrayList<Municipio>();
        Cursor c = this.db1.rawQuery("select Id, Nome, Eleitorado, Lim1Prefeito, " +
                "Lim2Prefeito, QtCaboPrefeito, Lim1Vereador, QtCaboVereador from Municipio " +
                "order by Nome", null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                lista.add(new Municipio(c.getLong(0), c.getString(1), c.getInt(2),
                        c.getDouble(3), c.getDouble(4), c.getInt(5),
                        c.getLong(6), c.getInt(7)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<UnidadeEleitoral> getUnidadeEleitoral(String uf) {

        List<UnidadeEleitoral> lista = new ArrayList<UnidadeEleitoral>();
        Cursor c = this.db1.rawQuery("select Id, UE_Superior, Tipo, Nome, " +
                "StCapital from UE " +
                "where UE_Superior = '" + uf + "'", null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            do {

                lista.add(new UnidadeEleitoral(c.getString(0), c.getString(1), c.getInt(2),
                        c.getString(3), c.getString(4)));

            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }


    public UnidadeEleitoral getUnidadeEleitoral(Long id) {

        UnidadeEleitoral ue = null;

        Cursor c = this.db1.rawQuery("select Id, UE_Superior, Tipo, Nome, " +
                "StCapital from UE " +
                "where ID = '" + id + "'", null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            ue = new UnidadeEleitoral(c.getString(0), c.getString(1), c.getInt(2),
                    c.getString(3), c.getString(4));

        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return ue;
    }

    public List<Limite> getLimite(String uf, int idEleicao) {

        List<Limite> lista = new ArrayList<Limite>();
        Cursor c = this.db1.rawQuery("select ID, IdEleicao, IdCargo, IdUE, " +
                "ValorMaximo, CaboMaximo from Limite " +
                "where IdUE = '" + uf +"' and IdEleicao = "+ idEleicao + "  order by IdCargo asc ", null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            do {
                lista.add(new Limite(c.getLong(0), c.getLong(1), c.getLong(2),
                        c.getString(3), c.getDouble(4), c.getInt(5)));
            } while (c.moveToNext());

        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<CandPart> getCandidatos(Long id_munic) {
        List<CandPart> lista = new ArrayList<CandPart>();
        Cursor c = this.db1.rawQuery("select c.Id, c.Id_municipio, m.Nome NomeMunic, c.Numero, " +
                        "c.Tipo, c.Nome, c.NomeUrna " +
                        "from CandPart c, Municipio m " +
                        "where c.Id_municipio = m.Id and c.Id_municipio = ?",
                new String[]{Long.toString(id_munic)});
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                lista.add(new CandPart(c.getLong(0), new Municipio(c.getLong(1), c.getString(2)),
                        c.getInt(3), c.getInt(4), c.getString(5), c.getString(6), ""));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<Candidato> getCandidatos(String uf) {

        List<Candidato> lista = new ArrayList<>();


        String sql = "select c.ID, c.IdEleicao, c.SqCandidato, c.IdUE, c.IdCargo, c.Numero, " +
                           "c.NomeCompleto, c.NomeUrna " +
                       "from Candidato c " +
                      "where (c.IdUE = '"+ uf + "' or c.IdUE = 'BR')";

        Log.i("SQL", sql);

        Cursor c = this.db1.rawQuery(sql, null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            do {

                lista.add(new Candidato(c.getLong(0), c.getLong(1), c.getLong(2),
                        c.getString(3), c.getInt(4), c.getInt(5), c.getString(6),
                        c.getString(7)));

            } while (c.moveToNext());

        }

        if (c != null && !c.isClosed()) {
            c.close();
        }

        return lista;
    }

    public ChaveDeAtualizacao getChaveAtualizacao() {

        ChaveDeAtualizacao lista = null;

        Cursor c = this.db1.rawQuery("select c.id, c.chave " +
                "from ChaveDeAtualizacao c order by c.id desc limit 1", null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            lista = new ChaveDeAtualizacao(c.getInt(0), c.getString(1));
        }

        if (c != null && !c.isClosed()) {
            c.close();
        }

        return lista;
    }

    public String getUltimaDataSincronizada(int id) {

        String ultimaData = "";

        Cursor c = this.db1.rawQuery("select c.id, c.flag, c.dataUltima " +
                "from Sincronizacao c where c.id =" + id, null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            if(!Utilitario.isNullOrEmpty(c.getString(2))) {
                ultimaData = c.getString(2);
            }
        }

        if (c != null && !c.isClosed()) {
            c.close();
        }

        return ultimaData;
    }

    public Date obterUltimaSincronizacao(int id) {

        Cursor c = this.db1.rawQuery("select c.id, c.flag, c.dataUltima " +
                "from Sincronizacao c where c.id = " + id, null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            String dataUltima = c.getString(2);

            c.close();
        }


        return null;
    }

    public int atualizarChave(String chave, Date dataUltimaAtualizacao){

        ContentValues args = new ContentValues();
        args.put("chave", chave);
        args.put("data_ultima_atualizacao", dataUltimaAtualizacao.toString());

        this.db1.beginTransaction();
        int i = this.db1.update("ChaveDeAtualizacao", args, "id=1", null);
        this.db1.setTransactionSuccessful();
        this.db1.endTransaction();

        return i;
    }


    public List<Estado> getEstados() {

        List<Estado> lista = new ArrayList<Estado>();
        Cursor c = this.db1.rawQuery("select c.Id, c.descricao, c.uf " +
                        "from Estado c ", null);

        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                lista.add(new Estado(c.getLong(0), c.getString(1), c.getString(2)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }


    public String getUrlCandidato(Long id) {
        String ret = Long.toString(id);
        /*Cursor c = this.db1.rawQuery("select Id_municipio, Numero " +
                        "from CandPart " +
                        "where Id = ?",
                new String[]{Long.toString(id)});
        if ((c.getCount() == 1) && (c.moveToFirst())) {
            ret = c.getString(0) + "/" + c.getString(1);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }*/
        return ret;
    }

    public List<String> getNomeCand(Long numCand, Long numMunic) {
        List<String> lista = new ArrayList<String>();
        Cursor c = this.db1.rawQuery("select c.NomeUrna, m.Nome NomeMunic, m.Id " +
                        "from CandPart c, Municipio m " +
                        "where c.Id_municipio = m.Id and c.Numero = ? and m.Id = ?",
                new String[]{Long.toString(numCand), Long.toString(numMunic)});
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            lista.add(c.getString(0));
            lista.add(c.getString(1));
            lista.add(c.getString(2));
        } else {
            lista.add("");
            lista.add("");
            lista.add("0");
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<String> getNomeCand(Long idCand) {
        List<String> lista = new ArrayList<String>();
        Cursor c = this.db1.rawQuery("select c.NomeUrna, m.Nome NomeMunic, c.Numero " +
                        "from CandPart c, Municipio m " +
                        "where c.Id_municipio = m.Id and c.Id = ?",
                new String[]{Long.toString(idCand)});
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            lista.add(c.getString(0));
            lista.add(c.getString(1));
            lista.add(c.getString(2));
        } else {
            lista.add("");
            lista.add("");
            lista.add("0");
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public String pegarNomeCandidato(Long idCand) {


        String nomeCandidato = "";

        Cursor c = this.db1.rawQuery("select c.NomeUrna " +
                        "from Candidato c " +
                        "where c.id = ?",
                new String[]{Long.toString(idCand)});

        if ((c.getCount() > 0) && (c.moveToFirst())) {

            if(!Utilitario.isNullOrEmpty(c.getString(0))) {
                nomeCandidato = c.getString(0);
            }

        }

        if (c != null && !c.isClosed()) {
            c.close();
        }

        return nomeCandidato;
    }

    public Gasto novoGasto() {
        Gasto gasto = new Gasto();
        Cursor c = this.db2.rawQuery("select Id, Protocolo from informacao order by 1 desc",
                null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            if (c.getString(1) != null ) {
                gasto.setId(c.getLong(0));
            } else {
                this.db2.execSQL("insert into Informacao (tipo_cand) values (1)");
                c = this.db2.rawQuery("select last_insert_rowid()", null);
                if ((c.getCount() > 0) && (c.moveToFirst())) {
                    gasto.setId(c.getLong(0));
                }
            }
        } else {
            this.db2.execSQL("insert into Informacao (tipo_cand) values (1)");
            c = this.db2.rawQuery("select last_insert_rowid()", null);
            if ((c.getCount() > 0) && (c.moveToFirst())) {
                gasto.setId(c.getLong(0));
            }
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return gasto;
    }

    public boolean insertCandidatos(JSONArray jsonArray) {


        try {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {


                    JSONObject jo = new JSONObject(jsonArray.getString(i));

                    long id = jo.getLong("id");
                    String idUe = jo.getString("unidadeEleitoral");
                    int idCargo = jo.getInt("cargo");
                    int numero = jo.getInt("numero");
                    String nome = jo.getString("nome");
                    String nomeDeUrna = jo.getString("nomeDeUrna");

                    this.db1.execSQL("insert into Candidato (ID, IdUE, IdCargo, Numero, NomeCompleto, NomeUrna ) " +
                            "values (" + id + ",'" + idUe + "'," + idCargo + "," + numero + ",'" + nome + "','" + nomeDeUrna + "') ");

                } catch (SQLiteConstraintException e) {

                }
            }


        }catch (Exception e) {

            String str2 = e.getLocalizedMessage().toString();
            return false;

        }


        return true;
    }

    public int atualizarUltimaSincronizacao(String data, int id){

        if(Utilitario.isNullOrEmpty(data)) {
            return 0;
        }

        if (id == 0){
            return 0;
        }

        ContentValues args = new ContentValues();
        args.put("dataUltima", data);

        this.db1.beginTransaction();
        int i = this.db1.update("Sincronizacao", args, "id="+id, null);
        this.db1.setTransactionSuccessful();
        this.db1.endTransaction();

        return i;
    }

    public Gasto newGasto() {
        Gasto gasto = new Gasto();
        this.db2.execSQL("insert into Informacao (tipo_cand) values (1)");
        Cursor c = this.db2.rawQuery("select last_insert_rowid()", null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            gasto.setId(c.getLong(0));
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return gasto;
    }

    public void atualizaStatus(Long idMunic, String txt){
        ContentValues args = new ContentValues();
        args.put("idMunic", idMunic);
        args.put("TxtConsulta", txt);
        this.db1.beginTransaction();
        this.db1.update("Status", args, null, null);
        this.db1.setTransactionSuccessful();
        this.db1.endTransaction();
    }

    public boolean saveGasto(Gasto gasto, Long numCand) {

        ContentValues args = new ContentValues();
        args.put("Numero", numCand);
        args.put("Descricao", gasto.getDescricao());
        args.put("Cod_Tipo", gasto.getTipoDeGasto());
        args.put("numero", numCand);

        if (!Utilitario.isNullOrEmpty(gasto.getUrl().trim())) {

            args.put("url", gasto.getUrl());
        }else
        {
            args.put("url", "");
        }

        if (gasto.getIdUe() != 0) {
            args.put("id_ue", gasto.getIdUe());
        }

        if (gasto.getInformante() != null) {
            args.put("Cpf", gasto.getInformante().getCpf());
        }
        this.db2.beginTransaction();
        int i = this.db2.update("Informacao", args, "Id=" + Long.toString(gasto.getId()), null);
        this.db2.setTransactionSuccessful();
        this.db2.endTransaction();
        return (i == 1);
    }

    public boolean saveGasto(Gasto gasto, Long numCand, RespGasto respGasto) {

        ContentValues args = new ContentValues();

        args.put("Numero", numCand);
        args.put("Descricao", gasto.getDescricao());
        args.put("Cod_Tipo", gasto.getTipoDeGasto());
        args.put("numero", numCand);

        if (!Utilitario.isNullOrEmpty(gasto.getUrl().trim())) {
            args.put("url", gasto.getUrl().trim());
        }

        if (gasto.getIdUe() != 0) {
            args.put("id_ue", gasto.getIdUe());
        }


        if (gasto.getInformante() != null) {
            args.put("Cpf", gasto.getInformante().getCpf());
        }

        if (respGasto != null) {
            args.put("protocolo", respGasto.getProtocolo());
            args.put("token", respGasto.getChaveParaEnvioDeMidia());
            args.put("dh_envio", respGasto.getRecebidoEm().getTime());
            args.put("processo", respGasto.getProcesso());
        }
        this.db2.beginTransaction();
        int i = this.db2.update("Informacao", args, "Id=" + Long.toString(gasto.getId()), null);
        this.db2.setTransactionSuccessful();
        this.db2.endTransaction();
        return (i == 1);
    }


    public Long numCand(Gasto gasto) {
        Long num = 0L;
        Cursor c = this.db2.rawQuery("select Numero " +
                        "from Informacao " +
                        "where Id = ?",
                new String[]{Long.toString(gasto.getId())});
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            num = c.getLong(0);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return num;
    }

    public List<GastoComp> getGastos(boolean comMidias, boolean enviada) {

        List<GastoComp> lista = new ArrayList<GastoComp>();
        String sql = "select i.id, i.tipo_cand, i.numero, i.cod_tipo, i.descricao, i.cpf," +
                " i.dh_criacao, i.protocolo, i.dh_envio, count(m.id) midias, i.id_ue, i.url, i.processo " +
                " from informacao i, midia m" +
                " where i.numero is not null and i.id=m.id_info";
        if (enviada) sql += " and protocolo is not null"; else sql += " and protocolo is null";
        sql +=  " group by i.id, i.tipo_cand, i.numero, i.cod_tipo, i.descricao, i.cpf," +
                " i.dh_criacao, i.protocolo, i.dh_envio, i.id_ue, i.url, i.processo ";
        if (comMidias) sql += " having count(m.id) > 0";
        sql += " order by 9 desc";
        Cursor c = this.db2.rawQuery(sql, null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {
                List<String> dados = getNomeCand(c.getLong(2));
                lista.add(new GastoComp(c.getLong(0), c.getInt(1), c.getLong(2),
                        c.getInt(3), c.getString(4), c.getString(5),
                        new Timestamp(c.getLong(6)), c.getString(7),
                        new Timestamp(c.getLong(8)), c.getInt(9),
                        dados.get(1), dados.get(0), c.getLong(10), c.getString(11), c.getString(12)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<GastoComp> getGastosEleicaoNacional(boolean comMidias, boolean enviada) {

        List<GastoComp> lista = new ArrayList<GastoComp>();
        String sql = "select i.id, i.tipo_cand, i.numero, i.cod_tipo, i.descricao, i.cpf," +
                " i.dh_criacao, i.protocolo, i.dh_envio, count(m.id) midias, i.id_ue, i.url, i.processo " +
                " from informacao i, midia m" +
                " where i.numero is not null and i.id=m.id_info";
        if (enviada) sql += " and protocolo is not null"; else sql += " and protocolo is null";
        sql +=  " group by i.id, i.tipo_cand, i.numero, i.cod_tipo, i.descricao, i.cpf," +
                " i.dh_criacao, i.protocolo, i.dh_envio, i.id_ue, i.url, i.processo ";
        if (comMidias) sql += " having count(m.id) > 0";
        sql += " order by 9 desc";
        Cursor c = this.db2.rawQuery(sql, null);
        if ((c.getCount() > 0) && (c.moveToFirst())) {
            do {

                lista.add(new GastoComp(c.getLong(0), c.getInt(1), c.getLong(2),
                        c.getInt(3), c.getString(4), c.getString(5),
                        new Timestamp(c.getLong(6)), c.getString(7),
                        new Timestamp(c.getLong(8)), c.getInt(9),
                        "", pegarNomeCandidato(c.getLong(2)), c.getLong(10), c.getString(11), c.getString(12)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<Midia> getMidias(Long id) {
        List<Midia> lista = new ArrayList<Midia>();
        Cursor c = this.db2.rawQuery("select Id, tipo, filename, dh_envio, token, " +
                        "data_registro_midia, latitude, longitude " +
                        "from Midia where Id_info = ?",
                new String[]{Long.toString(id)});
        if ((c.getCount() > 0) && (c.moveToFirst())) {

            do {
                lista.add(new Midia(c.getLong(0), null,
                        c.getInt(1), c.getString(2), new Date(c.getInt(3)), c.getString(4),
                        c.getString(5) , c.getDouble(6), c.getDouble(7)));
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return lista;
    }

    public List<TipoDeGasto> getTipoDeGasto() {

        List<TipoDeGasto> lista = new ArrayList<>();

        lista.add(new TipoDeGasto(0,null,"Selecione um tipo de gasto.",null,null));

        try {

            Cursor c = this.db1.rawQuery("select id, cd_drd, descricao, preco, precodescricao " +
                    "from TipoDeGasto", null);

            if ((c.getCount() > 0) && (c.moveToFirst())) {

                do {

                    lista.add(new TipoDeGasto(c.getInt(0), c.getLong(1), c.getString(2),
                            c.getDouble(3), c.getString(4)));

                } while (c.moveToNext());

            }

            if (c != null && !c.isClosed()) {
                c.close();
            }

        }catch (Exception e) {
            Log.i("error", e.getLocalizedMessage().toString());
        }

        return lista;
    }


    public void salvaMidias(Gasto gasto, List<Midia> midias) {

        if (midias != null) {
            this.db2.beginTransaction();
            this.db2.delete("Midia", "Id_info=" + gasto.getId(), null);
            ContentValues args;
            for (int i = 0; i < midias.size(); i++) {
                args = new ContentValues();
                args.put("Id_info", gasto.getId());
                args.put("filename", midias.get(i).getFilename());
                if (midias.get(i).getToken() != null) {
                    args.put("token", midias.get(i).getToken());
                    args.put("data_registro_midia", midias.get(i).getDataRegistroMidia());
                    args.put("latitude", midias.get(i).getLatitude());
                    args.put("longitude", midias.get(i).getLongitude());
                    //args.put("Dh_envio", Long.toString(System.currentTimeMillis()));
                }
                this.db2.insert("Midia", null, args);
            }
            this.db2.setTransactionSuccessful();
            this.db2.endTransaction();
        }
    }

    public void salvaMidiasSemToken(Gasto gasto, List<Midia> midias) {

        if (midias != null) {
            this.db2.beginTransaction();
            this.db2.delete("Midia", "Id_info=" + gasto.getId(), null);
            ContentValues args;
            for (int i = 0; i < midias.size(); i++) {
                args = new ContentValues();
                args.put("Id_info", gasto.getId());
                args.put("filename", midias.get(i).getFilename());
                args.put("data_registro_midia", midias.get(i).getDataRegistroMidia());
                args.put("latitude", midias.get(i).getLatitude());
                args.put("longitude", midias.get(i).getLongitude());
                //args.put("Dh_envio", Long.toString(System.currentTimeMillis()));

                this.db2.insert("Midia", null, args);
            }
            this.db2.setTransactionSuccessful();
            this.db2.endTransaction();
        }
    }

    public void salvaMidia(Long id, String nome) {

        ContentValues args = new ContentValues();
        args = new ContentValues();
        args.put("Id_info", id);
        args.put("filename", nome);
        args.put("data_registro_midia", "");
        args.put("latitude", 0);
        args.put("longitude", 0);
        this.db2.beginTransaction();
        this.db2.insert("Midia", null, args);
        this.db2.setTransactionSuccessful();
        this.db2.endTransaction();
    }

    public void salvaMidia(Long id, String nome, String dataRegistroMidia, Location local) {

        ContentValues args = new ContentValues();
        args = new ContentValues();
        args.put("Id_info", id);
        args.put("filename", nome);

        if (local != null) {
            args.put("latitude", local.getLatitude());
            args.put("longitude", local.getLongitude());
        }

        args.put("data_registro_midia", dataRegistroMidia);

        this.db2.beginTransaction();
        this.db2.insert("Midia", null, args);
        this.db2.setTransactionSuccessful();
        this.db2.endTransaction();
    }

    public void atualiza() {
        db2.execSQL("delete from informacao where protocolo is null");
    }
}

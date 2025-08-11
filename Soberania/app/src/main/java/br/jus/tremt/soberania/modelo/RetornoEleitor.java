package br.jus.tremt.soberania.modelo;

/**
 * Created by jorgebublitz on 15/02/2018.
 */

public class RetornoEleitor {
    private long idEleitor;
    private String cidade;
    private String uf;
    private String localidadeId;
    private String ufId;

    public RetornoEleitor() {
    }

    public RetornoEleitor(long idEleitor, String cidade, String uf, String localidadeId, String ufId) {
        this.idEleitor = idEleitor;
        this.cidade = cidade;
        this.uf = uf;
        this.localidadeId = localidadeId;
        this.ufId = ufId;
    }

    public long getIdEleitor() {
        return idEleitor;
    }

    public void setIdEleitor(long idEleitor) {
        this.idEleitor = idEleitor;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLocalidadeId() {
        return localidadeId;
    }

    public void setLocalidadeId(String localidadeId) {
        this.localidadeId = localidadeId;
    }

    public String getUfId() {
        return ufId;
    }

    public void setUfId(String ufId) {
        this.ufId = ufId;
    }
}

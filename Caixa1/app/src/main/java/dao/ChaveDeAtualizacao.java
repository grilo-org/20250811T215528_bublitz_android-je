package dao;

import java.util.Date;

public class ChaveDeAtualizacao {

    private Integer id;
    private String chave;
    private Date dataUltimaAtualizacao;


    public ChaveDeAtualizacao() {
    }

    public ChaveDeAtualizacao(Integer id, String chave) {

        this.id = id;
        this.chave = chave;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Date getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

}

package dao;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class Gasto {

    private Long id;
    private String descricao;
    private Timestamp criadoEm;
    private int tipoDeGasto;
    private Informante informante;
    private Long idUe;
    private String url; //url site de campanha


    public Gasto() {
    }

    public Gasto(Long id, String descricao, Timestamp criadoEm, int tipoDeGasto, Informante informante) {
        this.id = id;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
        this.tipoDeGasto = tipoDeGasto;
        this.informante = informante;
    }

    public Gasto(String descricao, Timestamp criadoEm, int tipoDeGasto, Informante informante) {
        this.descricao = descricao;
        this.criadoEm = criadoEm;
        this.tipoDeGasto = tipoDeGasto;
        this.informante = informante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

    public int getTipoDeGasto() {
        return tipoDeGasto;
    }

    public void setTipoDeGasto(int tipoDeGasto) {
        this.tipoDeGasto = tipoDeGasto;
    }

    public Informante getInformante() {
        return informante;
    }

    public void setInformante(Informante informante) {
        this.informante = informante;
    }

    public Long getIdUe() {
        return idUe;
    }

    public void setIdUe(Long idUe) {
        this.idUe = idUe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Gasto{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", criadoEm=" + criadoEm +
                ", tipoDeGasto=" + tipoDeGasto +
                '}';
    }
}

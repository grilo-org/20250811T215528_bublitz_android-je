package dao;

import java.sql.Timestamp;

/**
 * Created by jorgebublitz on 27/08/16.
 */
public class GastoComp {

    private Long id;
    private int tipo_cand;
    private Long numero;
    private int cod_tipo;
    private String descricao;
    private String cpf;
    private Timestamp dh_criacao;
    private String protocolo;
    private Timestamp dh_envio;
    private int midias;
    private String municipio;
    private String candidato;
    private Long idUe;
    private String url;
    private String processo;

    public GastoComp() {
    }

    public GastoComp(Long id, int tipo_cand, Long numero, int cod_tipo, String descricao,
                     String cpf, Timestamp dh_criacao, String protocolo, Timestamp dh_envio,
                     int midias, String municipio, String candidato, Long idUe, String url, String processo) {
        this.id = id;
        this.tipo_cand = tipo_cand;
        this.numero = numero;
        this.cod_tipo = cod_tipo;
        this.descricao = descricao;
        this.cpf = cpf;
        this.dh_criacao = dh_criacao;
        this.protocolo = protocolo;
        this.dh_envio = dh_envio;
        this.midias = midias;
        this.municipio = municipio;
        this.candidato = candidato;
        this.idUe = idUe;
        this.url = url;
        this.processo = processo;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTipo_cand() {
        return tipo_cand;
    }

    public void setTipo_cand(int tipo_cand) {
        this.tipo_cand = tipo_cand;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public int getCod_tipo() {
        return cod_tipo;
    }

    public void setCod_tipo(int cod_tipo) {
        this.cod_tipo = cod_tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Timestamp getDh_criacao() {
        return dh_criacao;
    }

    public void setDh_criacao(Timestamp dh_criacao) {
        this.dh_criacao = dh_criacao;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Timestamp getDh_envio() {
        return dh_envio;
    }

    public void setDh_envio(Timestamp dh_envio) {
        this.dh_envio = dh_envio;
    }

    public int getMidias() {
        return midias;
    }

    public void setMidias(int midias) {
        this.midias = midias;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCandidato() {
        return candidato;
    }

    public void setCandidato(String candidato) {
        this.candidato = candidato;
    }

    @Override
    public String toString() {
        return "GastoComp{" +
                "id=" + id +
                ", tipo_cand=" + tipo_cand +
                ", numero=" + numero +
                ", cod_tipo=" + cod_tipo +
                ", descricao='" + descricao + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dh_criacao=" + dh_criacao +
                ", protocolo='" + protocolo + '\'' +
                ", dh_envio=" + dh_envio +
                ", midias=" + midias +
                ", municipio='" + municipio + '\'' +
                ", candidato='" + candidato + '\'' +
                '}';
    }

    public Long getIdUe() {
        return idUe;
    }

    public String getUrl() {
        return url;
    }

    public String getProcesso() {
        return processo;
    }
}

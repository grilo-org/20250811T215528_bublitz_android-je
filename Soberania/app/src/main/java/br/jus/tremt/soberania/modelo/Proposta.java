package br.jus.tremt.soberania.modelo;

/**
 * Created by jorgebublitz on 22/12/2017.
 */

public class Proposta {
    private long id;
    private int abrangencia;
    private String nome;
    private String autor;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    private String dataVoto;
    private String voto;
    private Long totalAprovado;
    private Long totalRejeitado;
    private String link;
    private String lido;
    private String protocolo;
    private String dataProtocolo;
    private String linkAcompanharProjeto;

    public Proposta() {
    }

    public Proposta(long id, int abrangencia, String nome, String autor, String descricao,
                    String dataInicio, String dataFim, String dataVoto, String voto, Long totalAprovado,
                    Long rejeitado, String link, String lido, String protocolo, String dataProtocolo,
                    String linkAcompanharProjeto) {
        this.id = id;
        this.abrangencia = abrangencia;
        this.nome = nome;
        this.autor = autor;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataVoto = dataVoto;
        this.voto = voto;
        this.totalAprovado = totalAprovado;
        this.totalRejeitado = rejeitado;
        this.link = link;
        this.lido = lido;
        this.protocolo = protocolo;
        this.dataProtocolo = dataProtocolo;
        this.linkAcompanharProjeto = linkAcompanharProjeto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAbrangencia() {
        return abrangencia;
    }

    public void setAbrangencia(int abrangencia) {
        this.abrangencia = abrangencia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(String dataVoto) {
        this.dataVoto = dataVoto;
    }

    public String getVoto() {
        return voto;
    }

    public void setVoto(String voto) {
        this.voto = voto;
    }

    public Long getTotalAprovado() {
        return totalAprovado;
    }

    public void setTotalAprovado(Long totalAprovado) {
        this.totalAprovado = totalAprovado;
    }

    public Long getTotalRejeitado() {
        return totalRejeitado;
    }

    public void setTotalRejeitado(Long totalRejeitado) {
        this.totalRejeitado = totalRejeitado;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLido() {
        return lido;
    }

    public void setLido(String lido) {
        this.lido = lido;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(String dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public String getLinkAcompanharProjeto() {
        return linkAcompanharProjeto;
    }

    public void setLinkAcompanharProjeto(String linkAcompanharProjeto) {
        this.linkAcompanharProjeto = linkAcompanharProjeto;
    }

    @Override
    public String toString() {
        return "Proposta " + nome + "\n" +
                "Baixe o inteiro teor: https://apps3.tre-mt.jus.br/soberano/api/pdf/" + id + "/";
    }
}

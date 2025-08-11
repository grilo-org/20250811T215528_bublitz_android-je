package br.jus.tremt.soberania.modelo;

/**
 * Created by jorgebublitz on 20/02/2018.
 */

public class RetornoQtde {
    private int totalPropostas;
    private String dataAtualizacao;

    public RetornoQtde() {
    }

    public RetornoQtde(int totalPropostas, String dataAtualizacao) {
        this.totalPropostas = totalPropostas;
        this.dataAtualizacao = dataAtualizacao;
    }

    public int getTotalPropostas() {
        return totalPropostas;
    }

    public void setTotalPropostas(int totalPropostas) {
        this.totalPropostas = totalPropostas;
    }

    public String getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(String dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}

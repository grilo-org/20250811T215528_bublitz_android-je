package br.jus.tremt.soberania.modelo;

/**
 * Created by jorgebublitz on 08/02/2018.
 */

public class RetornoErro {
    private String status;
    private String motivo;
    private String detalhe;

    public RetornoErro() {
    }

    public RetornoErro(String status, String motivo, String detalhe) {
        this.status = status;
        this.motivo = motivo;
        this.detalhe = detalhe;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
}

package dao;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class RespGasto implements Serializable {
    private Long protocolo;
    private Date recebidoEm;
    private String chaveParaEnvioDeMidia;
    private String processo;

    public RespGasto() {
    }

    public RespGasto(Long protocolo, Date recebidoEm, String chaveParaEnvioDeMidia, String processo) {
        this.protocolo = protocolo;
        this.recebidoEm = recebidoEm;
        this.chaveParaEnvioDeMidia = chaveParaEnvioDeMidia;
        this.processo = processo;
    }

    public RespGasto(Long protocolo, Date recebidoEm, String chaveParaEnvioDeMidia) {
        this.protocolo = protocolo;
        this.recebidoEm = recebidoEm;
        this.chaveParaEnvioDeMidia = chaveParaEnvioDeMidia;
    }

    public Long getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Long protocolo) {
        this.protocolo = protocolo;
    }

    public Date getRecebidoEm() {
        return recebidoEm;
    }

    public void setRecebidoEm(Date recebidoEm) {
        this.recebidoEm = recebidoEm;
    }

    public String getChaveParaEnvioDeMidia() {
        return chaveParaEnvioDeMidia;
    }

    public void setChaveParaEnvioDeMidia(String chaveParaEnvioDeMidia) {
        this.chaveParaEnvioDeMidia = chaveParaEnvioDeMidia;
    }

    public String getProcesso() {
        return processo;
    }

}

package br.jus.tremt.soberania.modelo;

/**
 * Created by jorgebublitz on 20/02/2018.
 */

public class Parametro {
    private boolean propNac;
    private boolean propEst;
    private boolean propMun;
    private boolean downAut;
    private boolean downWifi;
    private String dataUltima;

    public Parametro() {
    }

    public Parametro(boolean propNac, boolean propEst, boolean propMun, boolean downAut, boolean downWifi, String dataUltima) {
        this.propNac = propNac;
        this.propEst = propEst;
        this.propMun = propMun;
        this.downAut = downAut;
        this.downWifi = downWifi;
        this.dataUltima = dataUltima;
    }

    public boolean isPropNac() {
        return propNac;
    }

    public void setPropNac(boolean propNac) {
        this.propNac = propNac;
    }

    public boolean isPropEst() {
        return propEst;
    }

    public void setPropEst(boolean propEst) {
        this.propEst = propEst;
    }

    public boolean isPropMun() {
        return propMun;
    }

    public void setPropMun(boolean propMun) {
        this.propMun = propMun;
    }

    public boolean isDownAut() {
        return downAut;
    }

    public void setDownAut(boolean downAut) {
        this.downAut = downAut;
    }

    public boolean isDownWifi() {
        return downWifi;
    }

    public void setDownWifi(boolean downWifi) {
        this.downWifi = downWifi;
    }

    public String getDataUltima() {
        return dataUltima;
    }

    public void setDataUltima(String dataUltima) {
        this.dataUltima = dataUltima;
    }
}

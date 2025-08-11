package dao;

import java.nio.DoubleBuffer;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by jorgebublitz on 17/08/16.
 */
public class Midia {
    private Long id;
    private Gasto gasto;
    private int tipo;
    private String filename;
    private Date dh_envio;
    private String token;
    private String dataRegistroMidia;
    private Double latitude;
    private Double longitude;

    public Midia() {
    }

    public Midia(Gasto gasto, String filename, int tipo) {
        this.gasto = gasto;
        this.filename = filename;
        this.tipo = tipo;
    }

    public Midia(Long id, Gasto gasto, int tipo, String filename, Date dh_envio, String token,
                 String dataRegistroMidia, Double latitude, Double longitude) {

        this.id = id;
        this.gasto = gasto;
        this.tipo = tipo;
        this.filename = filename;
        this.dh_envio = dh_envio;
        this.token = token;
        this.dataRegistroMidia = dataRegistroMidia;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gasto getGasto() {
        return gasto;
    }

    public void setGasto(Gasto gasto) {
        this.gasto = gasto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getDh_envio() {
        return dh_envio;
    }

    public void setDh_envio(Date dh_envio) {
        this.dh_envio = dh_envio;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return filename;
    }

    public String getDataRegistroMidia() {
        return dataRegistroMidia;
    }

    public void setDataRegistroMidia(String dataRegistroMidia) {
        this.dataRegistroMidia = dataRegistroMidia;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}

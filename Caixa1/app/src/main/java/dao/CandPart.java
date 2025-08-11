package dao;

/**
 * Created by jorgebublitz on 16/08/16.
 */
public class CandPart {
    private Long id;
    private Municipio municipio;
    private int numero;
    private int tipo;
    private String nome;
    private String nomeUrna;
    private String link;

    public CandPart() {
    }

    public CandPart(Long id, Municipio municipio, int numero, int tipo, String nome, String nomeUrna, String link) {
        this.id = id;
        this.municipio = municipio;
        this.numero = numero;
        this.tipo = tipo;
        this.nome = nome;
        this.nomeUrna = nomeUrna;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeUrna() {
        return nomeUrna;
    }

    public void setNomeUrna(String nomeUrna) {
        this.nomeUrna = nomeUrna;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

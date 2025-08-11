package dao;

public class Candidato {

    private Long id;
    private Long idEleicao;
    private Long sqCandidato;
    private String idUe;
    private int idCargo;
    private int numero;
    private String nomeCompleto;
    private String nomeUrna;

    public Candidato(Long id, Long idEleicao, Long sqCandidato, String idUe, int idCargo,
                     int numero, String nomeCompleto, String nomeUrna) {

        this.id = id;
        this.idEleicao = idEleicao;
        this.sqCandidato = sqCandidato;
        this.idUe = idUe;
        this.idCargo = idCargo;
        this.numero = numero;
        this.nomeCompleto = nomeCompleto;
        this.nomeUrna = nomeUrna;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEleicao() {
        return idEleicao;
    }

    public void setIdEleicao(Long idEleicao) {
        this.idEleicao = idEleicao;
    }

    public Long getSqCandidato() {
        return sqCandidato;
    }

    public void setSqCandidato(Long sqCandidato) {
        this.sqCandidato = sqCandidato;
    }

    public String getIdUe() {
        return idUe;
    }

    public void setIdUe(String idUe) {
        this.idUe = idUe;
    }

    public int getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeUrna() {
        return nomeUrna;
    }

    public void setNomeUrna(String nomeUrna) {
        this.nomeUrna = nomeUrna;
    }
}

package dao;

public class Limite {

    private Long id;
    private Long idEleicao; //Primeiro ou segundo turno
    private Long idCargo; //codigo do cargo: Presidente, governador...
    private String idUE;// Unidade Eleitoral
    private Double valorMaximo; //valor maximo do gasto
    private Double valorMaximoSegTurno; //valor maximo do gasto
    private Integer caboMaximo; //total maximo de cabos eleitorais permitidos



    public Limite(Long id, Long idEleicao, Long idCargo, String idUE, Double valorMaximo, Integer caboMaximo, Double valorMaximoSegTurno) {

        this.id = id;
        this.idEleicao = idEleicao;
        this.idCargo = idCargo;
        this.idUE = idUE;
        this.valorMaximo = valorMaximo;
        this.caboMaximo = caboMaximo;
        this.setValorMaximoSegTurno(valorMaximoSegTurno);

    }

    public Limite(Long id, Long idEleicao, Long idCargo, String idUE, Double valorMaximo, Integer caboMaximo) {

        this.id = id;
        this.idEleicao = idEleicao;
        this.idCargo = idCargo;
        this.idUE = idUE;
        this.valorMaximo = valorMaximo;
        this.caboMaximo = caboMaximo;

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

    public Long getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(Long idCargo) {
        this.idCargo = idCargo;
    }

    public String getIdUE() {
        return idUE;
    }

    public void setIdUE(String idUE) {
        this.idUE = idUE;
    }

    public Double getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Double valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Integer getCaboMaximo() {
        return caboMaximo;
    }

    public void setCaboMaximo(Integer caboMaximo) {
        this.caboMaximo = caboMaximo;
    }

    public Double getValorMaximoSegTurno() {
        return valorMaximoSegTurno;
    }

    public void setValorMaximoSegTurno(Double valorMaximoSegTurno) {
        this.valorMaximoSegTurno = valorMaximoSegTurno;
    }
}

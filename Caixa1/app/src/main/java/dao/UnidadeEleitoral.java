package dao;

public class UnidadeEleitoral {

    private String id;
    private String ueSuperior;
    private int tipo;
    private String nome;
    private String stCapital;



    public UnidadeEleitoral(String id, String ueSuperior, int tipo, String nome, String stCapital) {

        this.id = id;
        this.ueSuperior = ueSuperior;
        this.tipo = tipo;
        this.nome = nome;
        this.stCapital = stCapital;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUeSuperior() {
        return ueSuperior;
    }

    public void setUeSuperior(String ueSuperior) {
        this.ueSuperior = ueSuperior;
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

    public String getStCapital() {
        return stCapital;
    }

    public void setStCapital(String stCapital) {
        this.stCapital = stCapital;
    }
}

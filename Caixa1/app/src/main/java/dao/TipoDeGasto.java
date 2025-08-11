package dao;

public class TipoDeGasto {

    private Integer id;
    private Long cdDrd; //Codigo SPCE
    private String descricao;
    private Double preco;
    private String precoDescricao;


    public TipoDeGasto(Integer id, Long cdDrd, String descricao, Double preco, String precoDescricao) {

        this.id = id;
        this.cdDrd = cdDrd;
        this.descricao = descricao;
        this.preco = preco;
        this.precoDescricao = precoDescricao;
    }

    public Integer getId() {
        return id;
    }

    public Long getCdDrd() {
        return cdDrd;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public String getPrecoDescricao() {
        return precoDescricao;
    }
}

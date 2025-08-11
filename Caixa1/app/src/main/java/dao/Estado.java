package dao;

public class Estado {

    private Long id;
    private String descricao;
    private String uf;

    public Estado(Long id, String descricao, String uf) {
        this.id = id;
        this.descricao = descricao;
        this.uf = uf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }



}

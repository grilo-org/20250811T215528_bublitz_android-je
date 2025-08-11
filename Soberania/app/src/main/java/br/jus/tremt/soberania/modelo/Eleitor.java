package br.jus.tremt.soberania.modelo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by jorgebublitz on 18/12/2017.
 */

public class Eleitor {
    private Long id;
    private String titulo;
    private String nome;
    private String nomePai;
    private boolean naoConstaPai;
    private String nomeMae;
    private boolean naoConstaMae;
    private String dataNascto;
    private String email;
    private String telefone;
    private String municipio;
    private String uf;
    private String localidadeId;
    private String ufId;
    private String hash;

    public Eleitor() {
    }

    public Eleitor(Long id, String titulo, String nome, String nomePai, boolean naoConstaPai, String nomeMae, boolean naoConstaMae, String dataNascto, String email, String telefone, String municipio, String uf, String localidadeId, String ufId) {
        this.id = id;
        this.titulo = titulo;
        this.nome = nome;
        this.nomePai = nomePai;
        this.naoConstaPai = naoConstaPai;
        this.nomeMae = nomeMae;
        this.naoConstaMae = naoConstaMae;
        this.dataNascto = dataNascto;
        this.email = email;
        this.telefone = telefone;
        this.municipio = municipio;
        this.uf = uf;
        this.localidadeId = localidadeId;
        this.ufId = ufId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public boolean isNaoConstaPai() {
        return naoConstaPai;
    }

    public void setNaoConstaPai(boolean naoConstaPai) {
        this.naoConstaPai = naoConstaPai;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public boolean isNaoConstaMae() {
        return naoConstaMae;
    }

    public void setNaoConstaMae(boolean naoConstaMae) {
        this.naoConstaMae = naoConstaMae;
    }

    public String getDataNascto() {
        return dataNascto;
    }

    public int getDataInt() {
        String num = dataNascto.substring(6) + dataNascto.substring(3, 5) + dataNascto.substring(0, 2);
        return Integer.parseInt(num);
    }

    public void setDataNascto(String dataNascto) {
        this.dataNascto = dataNascto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLocalidadeId() {
        return localidadeId;
    }

    public void setLocalidadeId(String localidadeId) {
        this.localidadeId = localidadeId;
    }

    public String getUfId() {
        return ufId;
    }

    public void setUfId(String ufId) {
        this.ufId = ufId;
    }

    public String getHash() {
        try {
            // Create SHA-256 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String s = titulo + nome + nomeMae + email + telefone + String.valueOf(getDataInt());
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //public void setHash(String hash) {
    //    this.hash = hash;
    //}

    @Override
    public String toString() {
        return "Eleitor{" +
                "nome='" + nome + '\'' +
                '}';
    }
}

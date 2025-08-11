package dao;

/**
 * Created by jorgebublitz on 16/08/16.
 */

import java.util.Comparator;

public class Municipio implements Comparator<Municipio> {
    private Long id;
    private String nome;
    private int eleitorado;
    private double lim1Prefeito;
    private double lim2Prefeito;
    private int qtCaboPrefeito;
    private double lim1Vereador;
    private int qtCaboVereador;

    public Municipio() {
    }

    public Municipio(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Municipio(Long id, String nome, int eleitorado, double lim1Prefeito, double lim2Prefeito, int qtCaboPrefeito, double lim1Vereador, int qtCaboVereador) {
        this.id = id;
        this.nome = nome;
        this.eleitorado = eleitorado;
        this.lim1Prefeito = lim1Prefeito;
        this.lim2Prefeito = lim2Prefeito;
        this.qtCaboPrefeito = qtCaboPrefeito;
        this.lim1Vereador = lim1Vereador;
        this.qtCaboVereador = qtCaboVereador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getEleitorado() {
        return eleitorado;
    }

    public void setEleitorado(int eleitorado) {
        this.eleitorado = eleitorado;
    }

    public double getLim1Prefeito() {
        return lim1Prefeito;
    }

    public void setLim1Prefeito(double lim1Prefeito) {
        this.lim1Prefeito = lim1Prefeito;
    }

    public double getLim2Prefeito() {
        return lim2Prefeito;
    }

    public void setLim2Prefeito(double lim2Prefeito) {
        this.lim2Prefeito = lim2Prefeito;
    }

    public int getQtCaboPrefeito() {
        return qtCaboPrefeito;
    }

    public void setQtCaboPrefeito(int qtCaboPrefeito) {
        this.qtCaboPrefeito = qtCaboPrefeito;
    }

    public double getLim1Vereador() {
        return lim1Vereador;
    }

    public void setLim1Vereador(double lim1Vereador) {
        this.lim1Vereador = lim1Vereador;
    }

    public int getQtCaboVereador() {
        return qtCaboVereador;
    }

    public void setQtCaboVereador(int qtCaboVereador) {
        this.qtCaboVereador = qtCaboVereador;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public int compare(Municipio lhs, Municipio rhs) {
        return lhs.getNome().compareTo(rhs.getNome());
    }
}

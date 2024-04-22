package com.example.telapi;

import java.io.Serializable;
import java.util.Date;

public class Despesa implements Serializable {

    Long id;
    String descricao, vencimento;
    double valor;


    public Despesa(Long id, String descricao, double valor, String vencimento) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
    }

    public Despesa(){

    }
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    @Override
    public String toString(){
          return id + " - " + descricao + "\n" +
            "Valor: "+  valor + "\n" +
            "Vencimento: "+ vencimento;
}
}

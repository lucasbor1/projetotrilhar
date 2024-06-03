package com.example.telapi;

import android.icu.text.SimpleDateFormat;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Locale;

public class Despesa implements Serializable {
    private String descricao;
    private double valor;
    private Timestamp vencimento;
    private String categoria;
    private String id;

    public Despesa() {

    }

    public Despesa(String id, String categoria, String descricao, double valor, Timestamp vencimento) {
        this.id = id;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
    }


    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Timestamp getVencimento() {
        return vencimento;
    }

    public void setVencimento(Timestamp vencimento) {
        this.vencimento = vencimento;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataFormatada = vencimento != null ? sdf.format(vencimento.toDate()) : "Sem Data";
        return  descricao +  String.format("%.2f", valor)  + dataFormatada;
    }

}

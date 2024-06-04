package com.example.telapi;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Despesa implements Serializable {
    private String id;
    private String descricao;
    private double valor;
    private String vencimento;
    private String categoria;

    public Despesa() {
        // Construtor vazio necessário para Firestore
    }


    public Despesa(String categoria, String descricao, double valor, String vencimento) {
        this.id = UUID.randomUUID().toString();
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
    }

    public Despesa(String id, String categoria, String descricao, double valor, String vencimento) {
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

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
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
        return descricao + '\n' +
               + valor +
                "\n'" + vencimento;
    }
}

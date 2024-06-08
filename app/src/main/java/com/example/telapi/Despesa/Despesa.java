package com.example.telapi.Despesa;

import android.net.ParseException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Despesa implements Serializable {
    private String id;
    private String descricao;
    private double valor;
    private String vencimento;
    private String categoria;
    private  boolean pago;

    public Despesa() {

    }

    public boolean isAtrasada() {
        // Obtenha a data atual
        Calendar hoje = Calendar.getInstance();
        // Converta a data de vencimento da despesa para o tipo Calendar
        Calendar dataVencimento = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dataVencimento.setTime(sdf.parse(this.getVencimento()));
        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
            // Se houver um erro ao analisar a data, considere a despesa como não atrasada
            return false;
        }
        // Verifique se a data atual é posterior à data de vencimento
        return hoje.after(dataVencimento);
    }



    public Despesa(String categoria, String descricao, double valor, String vencimento) {
        this.id = UUID.randomUUID().toString();
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
    }

    public Despesa(String id, String categoria, String descricao, double valor, String vencimento, boolean pago) {
        this.id = id;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
        this.pago = pago;
    }

    // Getters e Setters

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

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

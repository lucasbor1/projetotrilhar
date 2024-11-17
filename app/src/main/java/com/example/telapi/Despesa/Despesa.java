package com.example.telapi.Despesa;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Despesa implements Serializable {
    private int id;
    private String categoria;
    private String descricao;
    private double valor;
    private String vencimento;
    private boolean pago;

    public Despesa(String categoria, String descricao, double valor, String vencimento, boolean pago) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
        this.pago = pago;
    }

    public Despesa(int id, String categoria, String descricao, double valor, String vencimento, boolean pago) {
        this.id = id;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
        this.pago = pago;
    }

    public boolean isAtrasada() {
        Calendar hoje = Calendar.getInstance();
        Calendar dataVencimento = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date data = sdf.parse(this.getVencimento());
            if (data != null) {
                dataVencimento.setTime(data);

                return !isPago() && hoje.after(dataVencimento);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getVencimento() { return vencimento; }
    public void setVencimento(String vencimento) { this.vencimento = vencimento; }

    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }

    @Override
    public String toString() {
        return descricao + '\n' +
                "Valor: R$" + String.format(Locale.getDefault(), "%.2f", valor) + '\n' +
                "Vencimento: " + vencimento + '\n' +
                (isAtrasada() ? "Status: Atrasada" : "Status: Em dia");
    }
}

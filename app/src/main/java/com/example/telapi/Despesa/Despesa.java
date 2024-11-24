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
    private String vencimento; // formato "dd/MM/yyyy"
    private int ano;
    private boolean pago;
    private boolean permanente; // Nova funcionalidade
    private boolean parcelada;  // Nova funcionalidade
    private int numeroParcelas; // Total de parcelas
    private int parcelaAtual;   // Parcela atual (para controle)

    // Construtores
    public Despesa(String categoria, String descricao, double valor, String vencimento, int ano,
                   boolean pago, boolean permanente, boolean parcelada, int numeroParcelas, int parcelaAtual) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
        this.ano = ano;
        this.pago = pago;
        this.permanente = permanente;
        this.parcelada = parcelada;
        this.numeroParcelas = numeroParcelas;
        this.parcelaAtual = parcelaAtual;
    }

    public Despesa(int id, String categoria, String descricao, double valor, String vencimento, int ano,
                   boolean pago, boolean permanente, boolean parcelada, int numeroParcelas, int parcelaAtual) {
        this.id = id;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.vencimento = vencimento;
        this.ano = ano;
        this.pago = pago;
        this.permanente = permanente;
        this.parcelada = parcelada;
        this.numeroParcelas = numeroParcelas;
        this.parcelaAtual = parcelaAtual;
    }

    // Método para verificar se a despesa está atrasada
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

    public int getDiaVencimento() {
        String[] partesData = vencimento.split("/");
        return Integer.parseInt(partesData[0]);
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

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }

    public boolean isPermanente() { return permanente; }
    public void setPermanente(boolean permanente) { this.permanente = permanente; }

    public boolean isParcelada() { return parcelada; }
    public void setParcelada(boolean parcelada) { this.parcelada = parcelada; }

    public int getNumeroParcelas() { return numeroParcelas; }
    public void setNumeroParcelas(int numeroParcelas) { this.numeroParcelas = numeroParcelas; }

    public int getParcelaAtual() { return parcelaAtual; }
    public void setParcelaAtual(int parcelaAtual) { this.parcelaAtual = parcelaAtual; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(descricao).append('\n')
                .append("Valor: R$").append(String.format(Locale.getDefault(), "%.2f", valor)).append('\n')
                .append("Vencimento: ").append(vencimento).append('\n')
                .append("Ano: ").append(ano).append('\n')
                .append(isAtrasada() ? "Status: Atrasada" : "Status: Em dia").append('\n');

        if (permanente) {
            sb.append("Despesa Permanente\n");
        }

        if (parcelada) {
            sb.append("Parcela: ").append(parcelaAtual).append(" de ").append(numeroParcelas).append('\n');
        }

        return sb.toString();
    }
}

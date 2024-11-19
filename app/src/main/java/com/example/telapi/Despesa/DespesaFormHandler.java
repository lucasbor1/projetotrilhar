package com.example.telapi.Despesa;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.telapi.Despesa.Despesa;
import com.example.telapi.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class DespesaFormHandler {
    private final AutoCompleteTextView autoCompleteCategoria;
    private final EditText edtDescricao, edtValor, edtVencimento;
    private final SwitchMaterial switchDespesaPaga;
    private boolean isFormatting = false;

    public DespesaFormHandler(AutoCompleteTextView autoCompleteCategoria,
                              EditText edtDescricao, EditText edtValor,
                              EditText edtVencimento, SwitchMaterial switchDespesaPaga) {
        this.autoCompleteCategoria = autoCompleteCategoria;
        this.edtDescricao = edtDescricao;
        this.edtValor = edtValor;
        this.edtVencimento = edtVencimento;
        this.switchDespesaPaga = switchDespesaPaga;

        edtValor.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                formatarValor();
            }
        });
    }

    public Despesa obterDespesa(int id) {
        String categoria = autoCompleteCategoria.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();
        String vencimento = edtVencimento.getText().toString().trim();
        boolean pago = switchDespesaPaga.isChecked();
        int ano = obterAnoDoVencimento(vencimento);

        String valorStr = edtValor.getText().toString()
                .replace("R$", "")
                .replace("\u00A0", "")
                .trim();

        double valor = 0.0;

        try {
            NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
            Number number = format.parse(valorStr);
            if (number != null) {
                valor = number.doubleValue();
            }
        } catch (Exception e) {
            Log.e("DespesaFormHandler", "Erro ao converter valor: " + e.getMessage());
        }

        return new Despesa(id, categoria, descricao, valor, vencimento, ano, pago);
    }

    private int obterAnoDoVencimento(String vencimento) {
        try {
            String[] partesData = vencimento.split("/");
            if (partesData.length == 3) {
                return Integer.parseInt(partesData[2]);
            }
        } catch (Exception e) {
            Log.e("DespesaFormHandler", "Erro ao obter o ano do vencimento: " + e.getMessage());
        }
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public void setCategoria(String categoria) {
        autoCompleteCategoria.setText(categoria);
    }

    public void setDescricao(String descricao) {
        edtDescricao.setText(descricao);
    }

    public void setValor(double valor) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        edtValor.setText(currencyFormat.format(valor));
    }

    public void setVencimento(String vencimento) {
        edtVencimento.setText(vencimento);
    }

    public void setPago(boolean pago) {
        switchDespesaPaga.setChecked(pago);
    }

    public void abrirCalendario(Context context) {
        Calendar calendario = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                R.style.CustomDatePicker,
                (view, year, month, dayOfMonth) -> {
                    String dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    edtVencimento.setText(dataSelecionada);
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public void formatarValor() {
        if (isFormatting) return;
        isFormatting = true;

        try {
            String valorStr = edtValor.getText().toString()
                    .replace("R$", "")
                    .replace("\u00A0", "")
                    .replaceAll("[^\\d]", "");

            if (valorStr.isEmpty()) {
                edtValor.setText("R$ 0,00");
                edtValor.setSelection(5);
                isFormatting = false;
                return;
            }

            double valor = Double.parseDouble(valorStr) / 100.0;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            String valorFormatado = currencyFormat.format(valor);

            edtValor.setText(valorFormatado);
            edtValor.setSelection(valorFormatado.length());

        } catch (NumberFormatException e) {
            Log.e("DespesaFormHandler", "Erro ao formatar valor: " + e.getMessage());
            edtValor.setText("R$ 0,00");
            edtValor.setSelection(5);
        } finally {
            isFormatting = false;
        }
    }
}

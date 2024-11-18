package com.example.telapi.Despesa;

import android.app.DatePickerDialog;
import android.content.Context;
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
    }

    public Despesa obterDespesa(int id) {
        String categoria = autoCompleteCategoria.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();
        String vencimento = edtVencimento.getText().toString().trim();
        boolean pago = switchDespesaPaga.isChecked();

        String valorStr = edtValor.getText().toString()
                .replace("R$", "")
                .replace("\u00A0", "")
                .replaceAll("[^\\d.,]", "")
                .replace(",", ".");
        double valor = valorStr.isEmpty() ? 0.0 : Double.parseDouble(valorStr);

        return new Despesa(id, categoria, descricao, valor, vencimento, pago);
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

    void abrirCalendario(Context context) {
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

        String valorStr = edtValor.getText().toString()
                .replace("R$", "")
                .replace("\u00A0", "")
                .replaceAll("[^\\d.,]", "")
                .replace(",", ".");
        if (valorStr.isEmpty()) {
            edtValor.setText("R$0,00");
            edtValor.setSelection(4);
            isFormatting = false;
            return;
        }

        double valor = Double.parseDouble(valorStr);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = currencyFormat.format(valor);

        edtValor.setText(valorFormatado);
        edtValor.setSelection(valorFormatado.length());
        isFormatting = false;
    }
}

package com.example.telapi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class calendario extends DatePickerDialog {

    public calendario(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
        getDatePicker().setCalendarViewShown(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int darkBlue = ContextCompat.getColor(getContext(), R.color.azul_escuro);
        ((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(0).setBackgroundColor(darkBlue);
    }
    public static String obterDataFormatada(Calendar calendario) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendario.getTime());
    }
}

package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telapi.Despesa.atv_despesa;
import com.example.telapi.grafico.AtvGraph;

public class atv_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void controlefinanceiro(View view) {
        Intent intent = new Intent(this, atv_despesa.class);
        startActivity(intent);
    }

    public void videos(View view) {
        Intent intent = new Intent(this, atv_modulos.class);
        startActivity(intent);
    }
    public void metas(View view) {
        Intent intent = new Intent(this, atv_metas.class);
        startActivity(intent);
    }

    public void graph(View view) {
        Intent intent = new Intent(this, AtvGraph.class);
        startActivity(intent);
    }
}

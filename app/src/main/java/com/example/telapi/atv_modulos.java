package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telapi.Videos.TelaVideo;
import com.example.telapi.Videos.TelaVideo2;
import com.example.telapi.Videos.TelaVideo3;
import com.example.telapi.Videos.TelaVideo4;
import com.example.telapi.Videos.TelaVideo5;

public class atv_modulos extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_modulos);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Remove o título

        // Habilita o botão de voltar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        if (getSupportActionBar() != null) {
            // Usa um ícone de tamanho maior do drawable
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_botao_back_small ); // Substitua por seu ícone personalizado
        }
    }
    public void video1(View view) {
        Intent intent = new Intent(this, TelaVideo.class);
        startActivity(intent);
        
    }

    public void video2(View view) {
        Intent intent = new Intent(this, TelaVideo2.class);
        startActivity(intent);
    }

    public void video3(View view) {
        Intent intent = new Intent(this, TelaVideo3.class);
        startActivity(intent);
    }

    public void video4(View view) {
        Intent intent = new Intent(this, TelaVideo4.class);
        startActivity(intent);
    }

    public void video5(View view) {
        Intent intent = new Intent(this, TelaVideo5.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finaliza a atividade atual e retorna à anterior
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}






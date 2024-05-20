package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class atv_menu  extends AppCompatActivity {

    Button btnfin, btnvid, btnrec, btnconf;

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

        btnfin = findViewById(R.id.btnfin);
        btnvid = findViewById(R.id.btnvid);
        btnrec = findViewById(R.id.btnrec);
        btnconf = findViewById(R.id.btnconf);
    }

    public void controlefinanceiro(View view) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    public void telavideo(View view) {
        Intent intent = new Intent(this, TelaVideo.class);
        startActivity(intent);
    }
}

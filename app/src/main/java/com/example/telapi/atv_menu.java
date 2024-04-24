package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class atv_menu  extends AppCompatActivity {

    Button btnfin, btnvid, btnrec, btnconf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_menu);

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

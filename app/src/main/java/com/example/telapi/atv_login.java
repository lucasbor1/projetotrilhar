package com.example.telapi;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class atv_login extends AppCompatActivity {

    EditText user, senha;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_login);

        user = findViewById(R.id.user);
        senha = findViewById(R.id.senha);
        btn = findViewById(R.id.btn);
    }

    /*
      btn.setOnClickListener((View) -> {
         startActivity(new Intent(MainActivity.this, Loginsucesso.class));
      });
      */

    public void clicaBotao(View view) {
        if (user.getText().toString().equals("Admin") && senha.getText().toString().equals("123")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this,"Senha Incorreta",Toast.LENGTH_LONG).show();
            }
        }
    }

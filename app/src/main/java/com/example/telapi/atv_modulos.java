package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class atv_modulos extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.atv_modulos);

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


}






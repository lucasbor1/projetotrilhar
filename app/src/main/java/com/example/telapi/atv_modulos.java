package com.example.telapi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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






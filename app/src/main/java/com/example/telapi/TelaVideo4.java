package com.example.telapi;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telapi.R;

public class TelaVideo4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_video4);


        WebView webView4 = findViewById(R.id.webView4);
        String video4 = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/3uwjOzbEYjU?si=Za7SX6G--I285rpO\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView4.loadData(video4, "text/html", "utf-8");
        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.setWebChromeClient(new WebChromeClient());
    }
}

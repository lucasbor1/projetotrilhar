package com.example.telapi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaVideo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_video);



        // Configurando os WebViews
        WebView webView = findViewById(R.id.webView);
        String video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/bFLp81P4C-U?si=iyq03uCzWHyVpV5J\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
        webView.loadData(video, "text/html", "utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());


    }
}

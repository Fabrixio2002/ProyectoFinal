package hn.uth.proyectopm1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
         webView = findViewById(R.id.webView);

        // Configurar opciones del WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript en la página
        webView.setWebViewClient(new WebViewClient()); // Mantener la navegación en el WebView

        // Obtener la URL de la página web de los extras del intent
        String url = getIntent().getStringExtra("url");

        // Cargar la página web en el WebView
        webView.loadUrl(url);
    }
}
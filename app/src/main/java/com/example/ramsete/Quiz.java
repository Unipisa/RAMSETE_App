package com.example.ramsete;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Quiz extends AppCompatActivity {

    private WebView myWebView;
    private WebSettings myWebSettings;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);
        myWebView = (WebView) findViewById(R.id.quizzone);
        //questo per non farlo uscire dalla webview
        myWebView.setWebViewClient(new WebViewClient());

        //mettiamo le impostazioni corrette per vedere tutta la pagina
        myWebSettings = myWebView.getSettings();
        //per le cose javascript
        myWebSettings.setJavaScriptEnabled(true);
        //non vogliamo la pagina troppo zoommata
        myWebSettings.setLoadWithOverviewMode(true);
        myWebSettings.setUseWideViewPort(true);

        myWebView.loadUrl("https://gamificationmuseo.ml/quiz-2/");

        //voglio capire quale sia il plugin
        Document doc = null;
        try {
            doc = Jsoup.connect("https://gamificationmuseo.ml/quiz-2/").get();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect: "+e);
        }

        Elements postwpoints = doc.select("meta[property]");

        String tipo = postwpoints.attr("property");
        String plugin_name = postwpoints.attr("content");

        Toast.makeText(this, plugin_name+" "+tipo, Toast.LENGTH_LONG).show();
    }
}

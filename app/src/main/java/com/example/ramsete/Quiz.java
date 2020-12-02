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

        //l'url è salvata nei contenuti dell'intent che vengono dalla lettura del QR
        String gameUrl = getIntent().getStringExtra("CERTOSA_PAGE_ID");

        myWebView.loadUrl(gameUrl);

        //voglio capire quale sia il plugin
        //mi connetto alla pagina giusta
        Document doc = null;
        try {
            doc = Jsoup.connect(gameUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect: "+e);
        }

        //seleziono il meta giusto
        Elements postwpoints = doc.select("meta[property]");

        String tipo = postwpoints.attr("property");
        // se la proprietà non ha il valore giusto... dico di riprovare
        if(!tipo.equals("quiz_QSM")){
            Toast.makeText(this, tipo+" non è questo il tipo di pagina che cerco", Toast.LENGTH_LONG).show();
            finish();
        }else{
            String plugin_name = postwpoints.attr("content");

            Toast.makeText(this, plugin_name+"|||"+tipo, Toast.LENGTH_LONG).show();
        }
    }
}

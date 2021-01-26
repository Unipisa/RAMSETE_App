package com.example.ramsete;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz extends AppCompatActivity {

    //l'url a cui si dovrà connettere
    String url = null;
    //l'url usata per il testing
    //"https://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museohttps://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo-0&em=1&comments=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=";

    private WebView myWebView;
    private WebSettings myWebSettings;
    private JavaScriptInterface js;

    protected void onCreate(Bundle savedInstanceState) {
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
        //qui inizializzo l'interfaccia Javascript
        js = new JavaScriptInterface(this, myWebView, "JavaScriptInterface");
        //l'aggiungo alla webview
        myWebView.addJavascriptInterface(js, js.name);
        //qui osserva il punteggio
        myWebView.setWebViewClient(new WebViewClient() {
            @SuppressLint("NewApi")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                js.observeFinalScore("header-area css-api-card-header css-api-card-header--closing");
            }
        });
        //recupero l'url dai dati extra dell'intent
        url = getIntent().getStringExtra("CERTOSA_PAGE_ID");
        //carico infine l'url
        myWebView.loadUrl(url);

        //voglio capire quale sia il plugin
       /* Document doc = null;
        try {
            doc = Jsoup.connect("https://gamificationmuseo.ml/quiz-2/").get();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to connect: " + e);
        }

        Elements postwpoints = doc.select("meta[property]");

        String tipo = postwpoints.attr("property");
        String plugin_name = postwpoints.attr("content");

        Toast.makeText(this, plugin_name + " " + tipo, Toast.LENGTH_LONG).show(); */
    }
}

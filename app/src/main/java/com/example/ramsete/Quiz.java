package com.example.ramsete;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}

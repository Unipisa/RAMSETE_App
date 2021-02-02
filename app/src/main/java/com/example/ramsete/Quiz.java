package com.example.ramsete;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz extends AppCompatActivity {

    //url to connect to
    String url = null;
    //url for testing
    //"https://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museohttps://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo-0&em=1&comments=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=";

    //webview seen by user
    private WebView myWebView;
    //settings necessary to inject JS code
    private WebSettings myWebSettings;
    //class needed to inject JS interface into html page
    private JavaScriptInterface js;

    protected void onCreate(Bundle savedInstanceState) {
        //get caller Activity state
        super.onCreate(savedInstanceState);
        //set layout of this activity from quiz.xml file
        setContentView(R.layout.quiz);
        //finds view id from quiz.xml
        myWebView = (WebView) findViewById(R.id.quizzone);
        //declaring Client for this webview
        myWebView.setWebViewClient(new WebViewClient());

        //get settings to see whole page
        myWebSettings = myWebView.getSettings();
        //enables JS execution in the webview
        myWebSettings.setJavaScriptEnabled(true);
        //JS interface initialization
        js = new JavaScriptInterface(this, myWebView, "JavaScriptInterface");
        //adding JS to the webview
        myWebView.addJavascriptInterface(js, js.name);
        //add score observer when page has finished loading to the client
        myWebView.setWebViewClient(new WebViewClient() {
            //inject JS code when page finished loading
            //checks for a specific class (the one that contains the score)
            @SuppressLint("NewApi")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                js.observeFinalScore("header-area css-api-card-header css-api-card-header--closing");
            }
        });
        //get url from intent's extra data
        url = getIntent().getStringExtra("CERTOSA_PAGE_ID");
        //load url
        myWebView.loadUrl(url);

        //obsolete code useful to check plugin type on wordpress page (if specified with MetaTagManager)
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

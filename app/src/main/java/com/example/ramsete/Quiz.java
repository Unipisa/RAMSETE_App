package com.example.ramsete;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Integer.parseInt;

public class Quiz extends AppCompatActivity {

    //url to connect to
    String url = null;
    //url for testing
    //"https://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museohttps://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo-0&em=1&comments=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=";

    //username for ops with server
    String usrName= null;

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

        //get url from intent's extra data
        url = getIntent().getStringExtra("CERTOSA_PAGE_ID");
        //get username from intent's extra data
        usrName = getIntent().getStringExtra("USER_NAME");

        //take QRID (2nd part of string)
        String[] segmnUrl = url.split("/");
        //check if QRID already present
        URL urlQRIDCheck = null;
        try {
            urlQRIDCheck = new URL("https://gamificationmuseo.ml/nebettaui.php?op=addQR&name="+usrName+"&QR="+segmnUrl[3]);


            URLConnection urlCon = urlQRIDCheck.openConnection();
            urlCon.connect();

            //buffers to memorize all lines from the site
            BufferedReader hPassBUf = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            StringBuffer strBuf = new StringBuffer();
            String line;
            //read all lines
            while ((line = hPassBUf.readLine()) != null) {
                strBuf.append(line);
            }

            //first convert the string into a JSONObject
            JSONObject usrData = (JSONObject) new JSONParser().parse(strBuf.toString());

            //and get result needed (state of QR ID added)
            Object result = ((JSONObject) usrData).get("result");

            //check if result==null (operation went wrong)
            if (result == null) {
                Toast.makeText(this, "Qualcosa è andato storto, prova a inquadrare di nuovo il QR.", Toast.LENGTH_LONG).show();
                //terminate activity if op went wrong
                finish();
            }

            //get the confirmation
            //see if it's different from 0
            Integer intQRCheck = parseInt(result.toString().trim());
            Intent resultIntent = new Intent();
            if (intQRCheck == 1) { //this is the code for first qr found
                //if confirmation ok return result and terminate activity
                Toast.makeText(this, "Fantastico, hai trovato il tuo primo QR!", Toast.LENGTH_LONG).show();
            } else if(intQRCheck == 2){ // code for QR already found
                //TODO finire di aggiungere le opzioni degli errori del qr
                    // aggiungere nuova activity che chiede se si vuole rifare il QR o meno
                    //aggiungere un sistema che conta i QR IDs e ti dà punti extra
                //if confirmation not ok display message and let user retry
                Toast.makeText(getApplicationContext(), "QR già trovato", Toast.LENGTH_SHORT).show();
            }else if(intQRCheck < 0){//-1 is update gone wrong and -2 is QR null, no need to differentiate
                //still terminate activity
                Toast.makeText(getApplicationContext(), "errore nell'aggiunta del QR", Toast.LENGTH_SHORT).show();
                finish();
            }
            Toast.makeText(getApplicationContext(), "aggiunto "+segmnUrl[3]+" QR", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error parsing JSONObject while adding QR");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Error in the url while adding QR");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error receiving data while adding QR");
        }

        //enables JS execution in the webview
        myWebSettings.setJavaScriptEnabled(true);
        //JS interface initialization
        js = new JavaScriptInterface(this, myWebView, "JavaScriptInterface",usrName,segmnUrl[3]);
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


        //load url of QR
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

package com.example.ramsete;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import static java.lang.Integer.parseInt;

public class Quiz extends AppCompatActivity {

    //url to connect to
    String url = null;
    //url for testing
    //"https://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museohttps://www.opinionstage.com/nastilucia1/inizia-la-tua-avventura-nel-museo?wid=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo-0&em=1&comments=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=&referring_widget=%2Fnastilucia1%2Finizia-la-tua-avventura-nel-museo&autoswitch=1&of=nastilucia1&os_utm_source=";

    //bonus points based on #qr found
    int bonus = 0;

    //username for ops with server
    String usrName= null;

    //webview seen by user
    private WebView myWebView;
    //settings necessary to inject JS code
    private WebSettings myWebSettings;
    //class needed to inject JS interface into html page
    private JavaScriptInterface js;

    //callback object for the javascript termination
    //without this, the Quiz activity doesn't know when to finish
    public EventReceiver javaScriptInterfaceTermination;
    //callback object for main activity
    public ResultReceiver closeApp;

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

        //better reset bonus here too
        bonus = 0;

        //take QRID (3rd part of url)
        String[] segmnUrl = url.split("/");
        String QRName = segmnUrl[3];
        //check if QRID already present
        URL urlQRIDCheck = null;
        try {
            urlQRIDCheck = new URL("https://gamificationmuseo.ml/nebettaui.php?op=addQR&name="+usrName+"&QR="+QRName);


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

            //confirmation not null so usrData should be not null
            //counts #qr, if #qr % 2 == 0 then adds bonus points to current qr
            //jsonarray in the jsonobject returned
            JSONArray qrArr = (JSONArray) ((JSONObject)usrData).get("progress");
            if(qrArr == null){
                System.out.println("l'array c'è");
            }
            //iterator for qr array
            Iterator<JSONObject> iterator = qrArr.iterator();
            //number of qrs
            int countQR = 0;
            //counts all qrs
            while(iterator.hasNext()){
                System.out.println(iterator.next());
                countQR++;
            }
            //every 2 qrs gives bouns
            if((countQR % 2) == 0){
                Toast.makeText(this, "Yuhuu! Questo QR ti dà punti bonus!", Toast.LENGTH_LONG).show();
                bonus += 3;
            }

            //get the confirmation
            //see if it's different from 0
            Integer intQRCheck = parseInt(result.toString().trim());
            Intent resultIntent = new Intent();
            if (intQRCheck == 1) { //this is the code for first qr found
                //if confirmation ok return result and terminate activity
                Toast.makeText(this, "Fantastico, hai trovato il tuo primo QR!", Toast.LENGTH_LONG).show();
            } else if(intQRCheck == 2){ // code for QR already found
                //declare/initialize new intent and start popup activity
                Intent popIntent = new Intent(this,Pop.class);
                startActivityForResult(popIntent,3);

            }else if(intQRCheck < 0){//-1 is update gone wrong and -2 is QR null, no need to differentiate
                //still terminate activity
                Toast.makeText(getApplicationContext(), "errore nell'aggiunta del QR", Toast.LENGTH_SHORT).show();
                finish();
            }
            Toast.makeText(getApplicationContext(), "aggiunto "+QRName+" QR", Toast.LENGTH_SHORT).show();

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
        //need to get the callback object from main activity for message of exit app
        closeApp = getIntent().getParcelableExtra("CLOSE_APP");
        //before initializing the javascript interface we must setup the service receiver (for the callback)
        setupServiceReceiver(this);

        //JS interface initialization
        js = new JavaScriptInterface(this, myWebView, "JavaScriptInterface",usrName,QRName,bonus,javaScriptInterfaceTermination);
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
                js.observeFinalScore("question");
            }
        });

        //obsolete class to watch "header-area css-api-card-header css-api-card-header--closing"

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 3:
                //when the popup closes if result ok then close Quiz
                if(resultCode == Activity.RESULT_OK){
                    finish();
                }

                break;
        }
    }

    // Setup the callback for when data is received from the javascript interface
    //need the context because it will be passed to the activity after the quiz
    public void setupServiceReceiver(final Context forNewActivity) {
        javaScriptInterfaceTermination = new EventReceiver(new Handler());
        // This is where we specify what happens when data is received from the javascript interface
        javaScriptInterfaceTermination.setReceiver(new EventReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                //when the quiz has ended and the parameters are updated, the activity closes itself
                if (resultCode == RESULT_OK) {
                    //actually, before closing itself, creates an AfterQuizContinue activity
                    //and passes the callback object of the main activity
                    Intent i = new Intent(forNewActivity, AfterQuizContinue.class);
                    i.putExtra("CLOSE_APP",closeApp);
                    forNewActivity.startActivity(i);

                    //sending a number to let main activity know the quiz ended
                    //for now it's used to notify the main activity to change the image from bw to color
                    closeApp.send(42,null);
                    //then closes itself
                    finish();
                }
            }
        });
    }

}

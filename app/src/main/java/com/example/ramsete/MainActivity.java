package com.example.ramsete;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //QRcode scanner button
    //also takes you to the site once the scan it's finished
    Button scanBtn;
    //button that gives you points
    Button gimPoi;
    //button that takes you to the quiz page
    Button quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UI can now FREEZE but it's NOT GOOD AND HAS TO BE DELETED
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //shows the layout defined in activity_main.xml
        setContentView(R.layout.activity_main);

        //finds button with scanBtn id in activity_main.xml
        scanBtn = findViewById(R.id.scanBtn);
        //registers a listener on that button
        scanBtn.setOnClickListener(this);

        //quiz button linked with quiz id
        quiz = (Button) findViewById(R.id.quiz);
        //registered listener on button with "quiz" id
        //anonymously defined in its call
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this button starts a new instance of Quiz class as an activity
                Intent intent = new Intent(v.getContext(),Quiz.class);
                //context needed because there are variables passed from activity to activity
                v.getContext().startActivity(intent);
            }
        });

        //gimme points button (outdated)
        gimPoi = (Button) findViewById(R.id.gimPoi);
        //registered listener on button with "gimPoi" id
        //anonymously defined in its call
        gimPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(MainActivity.this, "connessione", Toast.LENGTH_SHORT).show();

                    //connects to specified page and gets html code
                    Document doc = Jsoup.connect("https://gamificationmuseo.ml/pagina-prova/").get();
                    //selecting class with id post-38
                    Elements postwpoints = doc.select("article#post-38");
                    //selecting value of et_pb_text_inner
                    String punti = String.valueOf(postwpoints.select(".et_pb_text_inner").text());

                    //shows value found
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(punti);
                    builder.setTitle("Ho trovato questo!");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (IOException e) {
                    //something wrong with connection
                    //returns error message
                    Toast.makeText(MainActivity.this, "errore", Toast.LENGTH_SHORT).show();
                    System.err.println("Pagina non trovata" + e.toString());
                    e.printStackTrace();
                }

            }
        });
    }

    //overrides onClick (of button with scanBtn id) that now uses scanCode function
    @Override
    public void onClick(View v) {
        scanCode();
    }

    /**Starts a scan of a QRcode using CaptureAct class,
     * to check the result it's necessary to wait for the activity to finish
     */
    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    //waits scanner result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //prendo il risultato dell'activity che dovrebbe essere un link
        //takes activivty's result (should be a link)
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){

                //once getContents != null they're added to the intent so Quiz class can get them
                Intent intent = new Intent(this,Quiz.class);
                intent.putExtra("CERTOSA_PAGE_ID",result.getContents());
                //starts new Quiz activity
                this.startActivity(intent);

            }
            else{
                //if contents==null, DON'T try again
                Toast.makeText(this, "Niente, non ho trovato nulla", Toast.LENGTH_LONG).show();
            }
        }
        else{
            //if result == null, try again
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
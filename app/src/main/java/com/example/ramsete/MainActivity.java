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

    Button scanBtn;
    Button gimPoi;
    Button quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //questo serve per lasciare freezare l'ui ma NON SI FA E VA TOLTO
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.activity_main);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        //bottone del quiz
        quiz = (Button) findViewById(R.id.quiz);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Quiz.class);
                v.getContext().startActivity(intent);
            }
        });

        //bottone del gimme points
        gimPoi = (Button) findViewById(R.id.gimPoi);
        gimPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(MainActivity.this, "connessione", Toast.LENGTH_SHORT).show();

                    Document doc = Jsoup.connect("https://gamificationmuseo.ml/pagina-prova/").get();

                    Elements postwpoints = doc.select("article#post-38");

                    String punti = String.valueOf(postwpoints.select(".et_pb_text_inner").text());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(punti);
                    builder.setTitle("Ho trovato questo!");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "errore", Toast.LENGTH_SHORT).show();
                    System.err.println("Pagina non trovata" + e.toString());
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Ho trovato questo!");
                builder.setPositiveButton("Nuova Scan", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        scanCode();
                    }
                }).setNegativeButton("Fatto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
            else{
                Toast.makeText(this, "Niente, non ho trovato nulla", Toast.LENGTH_LONG).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
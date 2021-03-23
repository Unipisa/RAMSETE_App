package com.example.ramsete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Pop extends AppCompatActivity implements View.OnClickListener {

    //choice buttons
    Button redoYes;
    Button redoNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //we want to display the popup window smaller
        //than the actual window of the phone
        setContentView(R.layout.pop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //make window appear 80% and 60% smaller
        getWindow().setLayout((int) (width*.8), (int) (height*.6));

        //buttons assignment
        redoYes = (Button) findViewById(R.id.redo_yes);
        redoNo = (Button) findViewById(R.id.redo_no);

        //buttons listeners
        redoYes.setOnClickListener(this);
        redoNo.setOnClickListener(this);

    }

    //onclick for every button in the popup activity
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.redo_no:
                //in this case the activity returns an Activity ok
                //to also close Quiz activity
                Toast.makeText(this, "Ottima scelta, troviamone un altro.", Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK,resultIntent);
                //then finishes
                finish();
                break;

            case R.id.redo_yes:
                //here the popup just finishes
                Toast.makeText(this, "Ma certo! Bisogna farlo perfetto!", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }
}

package com.example.ramsete;

import android.app.Activity;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AfterQuizContinue extends AppCompatActivity implements View.OnClickListener {


    //button declarations
    Button continueGame;
    Button endGame;

    ResultReceiver closeApp;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize interface
        setContentView(R.layout.afterquiz_continue);

        //button initialization
        continueGame = (Button) findViewById(R.id.continue_button);
        endGame = (Button) findViewById(R.id.exit_button);

        continueGame.setOnClickListener(this);
        endGame.setOnClickListener(this);

        //get the callback object for the main activity
        closeApp = getIntent().getParcelableExtra("CLOSE_APP");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //continue button pressed
            case R.id.continue_button:
                //this activity is on top of the main one
                //so if the user wishes to continue, it just ends itself
                finish();
                break;

            case R.id.exit_button:
                //here we want to close the app
                //so it's necessary to register and use a callback to the main activity
                closeApp.send(Activity.RESULT_OK,null);
                //then terminate the activity
                finish();
                break;
        }
    }
}

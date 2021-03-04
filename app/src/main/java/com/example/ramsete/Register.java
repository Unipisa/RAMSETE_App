package com.example.ramsete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import static com.example.ramsete.R.id.alreadyAccount_register;
import static com.example.ramsete.R.string.incorrectUsrNmOrPsw_login;
import static java.lang.Integer.parseInt;

public class Register extends AppCompatActivity implements View.OnClickListener{

    //register button
    Button registerButton;

    //text fields
    EditText usrName;
    EditText pass;
    EditText confPass;
    EditText eMail;

    //clickable text
    TextView alreadyReg;

    //takes state from login activity
    //and initializes all variables/buttons
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //text fields assignment
        usrName = findViewById(R.id.userName_field);
        pass = findViewById(R.id.pass_field);
        confPass = findViewById(R.id.confirmPass_field);
        eMail = findViewById(R.id.email_field);

        /* button assignment */
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        //clickable text assignment
        alreadyReg = findViewById(R.id.alreadyAccount_register);
        alreadyReg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //user clicks on register button
            case R.id.registerButton:
                //check if any field is missing
                if(usrName.getText().toString().length()<1 || pass.getText().toString().length()<1 || confPass.getText().toString().length()<1 || eMail.getText().toString().length()<1){
                    Toast.makeText(this, "Assicurati di aver completato tutti i campi... manca qualcosa...", Toast.LENGTH_LONG).show();
                    break;
                }

                //check if email valid?

                //check if password != confPass lexicographically
                if( pass.getText().toString().compareTo(confPass.getText().toString()) != 0){
                    Toast.makeText(this, "Le password inserite sono diverse, assicurati di scriverle uguali", Toast.LENGTH_LONG).show();
                    break;
                }

                //connects to the url
                URL url = null;
                try {
                    url = new URL("https://gamificationmuseo.ml/nebettaui.php?op=register&name="+usrName.getText().toString()+"&pass="+pass.getText().toString().hashCode()+"&email="+eMail.getText().toString());

                    URLConnection urlCon = url.openConnection();
                    urlCon.connect();
                    //buffers to memorize all lines from the site
                    BufferedReader hPassBUf = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                    StringBuffer strBuf = new StringBuffer();
                    String line;
                    //read all lines
                    while((line = hPassBUf.readLine())!=null){
                        strBuf.append(line);
                    }
                    //first convert the string into a JSONObject
                    JSONObject usrData = (JSONObject) new JSONParser().parse(strBuf.toString());

                    //and get result needed (registration confirmed)
                    Object result = ((JSONObject) usrData).get("result");

                    //check if strPassH==null (wrong username)
                    if(result==null){
                        Toast.makeText(this,"Username non corretto!", Toast.LENGTH_LONG).show();
                        break;
                    }

                    //get the confirmation
                    //see if it's 0
                    Integer intRegConf = parseInt(result.toString().trim());
                    Intent resultIntent = new Intent();
                    if(intRegConf == 0){
                        //if confirmation ok return result and terminate activity
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }else{
                        //if confirmation not ok display message and let user retry
                        Toast.makeText(getApplicationContext(),"Nome utente già presente!",Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    System.err.println("Error connecting to the specified url "+url);
                    e.printStackTrace();
                } catch (ParseException e) {
                    System.err.println("Error parsing the JSONObject from the specified URL "+url);
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Error retrieving data from the specified url "+url);
                    e.printStackTrace();
                }

                break;

            //user clicked text goes back to login and closes register
            case alreadyAccount_register:
                finish();
                break;

            default:
                break;
        }
    }
}

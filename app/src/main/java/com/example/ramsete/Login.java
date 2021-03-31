package com.example.ramsete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.example.ramsete.R.string.incorrectUsrNmOrPsw_login;
import static java.lang.Integer.parseInt;

public class Login extends AppCompatActivity implements View.OnClickListener {

    //login button
    Button loginButton;

    //text fields
    EditText userName;
    EditText pass;

    //clickable text
    TextView registerTxt;

    //takes state from main activity
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //text fields assignment
        userName = findViewById(R.id.userName);
        pass = findViewById(R.id.pass);

        /* button assignment */
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        //clickable text assignment
        registerTxt = findViewById(R.id.registerNow_login);
        registerTxt.setOnClickListener(this);
    }



    //onclick for every button/text in the activity
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Login button clicked
            case R.id.loginButton:
                
                //url that connects for a login request
                try {
                    //before connecting check if username or password are null
                    if(userName.getText().toString().length()<1 || pass.getText().toString().length()<1){
                        Toast.makeText(this, incorrectUsrNmOrPsw_login, Toast.LENGTH_LONG).show();
                        break;
                    }

                    //connects to the url
                    URL url = new URL("https://gamificationmuseo.ml/nebettaui.php?op=login&name="+userName.getText().toString());
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

                    //and get result needed
                    Object strPassH = ((JSONObject) usrData).get("result");

                    //check if strPassH==null (wrong username)
                    if(strPassH==null){
                        Toast.makeText(this,"Username non corretto!", Toast.LENGTH_LONG).show();
                        break;
                    }

                    //get the hashed password on string
                    //and compare it with the given one
                    Integer intPassH = parseInt(strPassH.toString().trim());
                    Intent resultIntent = new Intent();
                    //put string and password as strings for main activity
                    resultIntent.putExtra("USER_NAME",userName.getText().toString());
                    resultIntent.putExtra("USER_PASS", pass.getText().toString().hashCode());
                    if(pass.getText().toString().hashCode() == intPassH){
                        //if password correct return result and terminate activity
                        setResult(Activity.RESULT_OK, resultIntent);
                        //TODO add remember me and on file memorization for login
                        finish();
                    }else{
                        //if password not correct display message and let user retry
                        Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    System.err.println("Incorrect url!");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Error retrieving password from database");
                } catch (ParseException e) {
                    e.printStackTrace();
                    System.err.println("Error parsing JSON Object");
                }
                break;

            //Register now clicked
            case R.id.registerNow_login:

                //start registration activity and wait for result
                Intent registerIntent = new Intent(this,Register.class);
                startActivityForResult(registerIntent,2);

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //register activity returned something
            case 2:
                //if result not ok user is back into login activity
                //else display "success message" for user
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(this,"Registrazione effettuata con successo!", Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
    }
}



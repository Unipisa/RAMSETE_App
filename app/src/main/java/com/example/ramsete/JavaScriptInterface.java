package com.example.ramsete;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

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


class JavaScriptInterface {

    //variables for injected js interface
    private Context ctx;
    private WebView webView;
    String name;

    //variables for adding QR points on server
    //name of user
    String userName;
    String QR;
    //bonus points are added to the current animal points
    int bonus;

    /** Constructor of JavaScriptInterface class
     * with this you can inject JS code into the webview specified
     *
     * @param ctx   context from which I'm calling
     * @param webView Webview where I want to inject JS code
     * @param name name of the interface injected
     * @param userName name of user logged
     * @param QR id of QR found
     * @param bonus bonus points to add
     */
    JavaScriptInterface(Context ctx, WebView webView, String name, String userName, String QR, int bonus) {
        this.ctx = ctx;
        this.webView = webView;
        this.name = name;
        this.userName = userName;
        this.QR = QR;
        this.bonus = bonus;
    }
    //mi dirà quando cambia l'elemento che mi interessa

    /**When the element changes prints out the new value
     *
     * @param newValue value of the element changed
     */
    @JavascriptInterface
    public void onElementChanged(String newValue) {
        Toast.makeText(ctx, newValue, Toast.LENGTH_LONG).show();
        //newValue is actually a sentence
        //let's split it
        //newValSplit[0]->points
        String[] newValSplit = newValue.split("/");

        //adding bonus points to current animal
        int pointsTotal = Integer.parseInt(newValSplit[0]) + bonus;

        //animalSplit[1]->animal name
        String[] animalSplit = newValSplit[1].split(" ");

        //Reset QR
        URL urlQRIDCheck = null;
        try {
            urlQRIDCheck = new URL("https://gamificationmuseo.ml/nebettaui.php?op=resetQR&name="+userName+"&QR="+QR);

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
                Toast.makeText(ctx, "Qualcosa è andato storto, nel resettare il punteggio del QR.", Toast.LENGTH_LONG).show();
                //terminate function if op went wrong
                return;
            }

            //get the confirmation
            //see if it's different from 0
            Integer intQRCheck = parseInt(result.toString().trim());
            if (intQRCheck == -1) { //this is the code for no QRIDs found in user data
                Toast.makeText(ctx, "Non ci son QR da resettare!", Toast.LENGTH_LONG).show();
                return;
            } else if(intQRCheck == -2){ //code for specified QR not found
                Toast.makeText(ctx, "Questo QR non esiste!", Toast.LENGTH_SHORT).show();
                return;
            } else if(intQRCheck == -4){//code for username wrong
                Toast.makeText(ctx, "Username sbagliato nel reset del QR", Toast.LENGTH_SHORT).show();
                return;
            }
            //-3 case tells if nothing has been updated (aka QR was already empty) so it doesn't concern us

        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error parsing JSON Object while resetting QR in JavascriptInterface");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Error writing URL while resetting QR in JavascriptInterface");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error receiving data while resetting QR in JavascriptInterface");
        }

        //then add points to qr
        try {
            urlQRIDCheck = new URL("https://gamificationmuseo.ml/nebettaui.php?op=updatePoints&name="+userName+"&QR="+QR+"&animal="+animalSplit[1]+"&points="+pointsTotal);

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
                Toast.makeText(ctx, "Qualcosa è andato storto, nell'aggiunta dei punti", Toast.LENGTH_LONG).show();
                //terminate function if op went wrong
                return;
            }

            //get the confirmation
            //see if it's different from 0
            Integer intQRCheck = parseInt(result.toString().trim());
            if (intQRCheck == -1) { //this is the code for no QRIDs found in user data
                Toast.makeText(ctx, "Non ci son QR a cui aggiungere punti", Toast.LENGTH_LONG).show();
                return;
            } else if(intQRCheck == -2){ //code for specified QR not found
                Toast.makeText(ctx, "Questo QR non esiste", Toast.LENGTH_SHORT).show();
                return;
            } else if(intQRCheck == -3){//could not add points to db (fun has to modify db)
                Toast.makeText(ctx, "Impossibile aggiungere punti nel QR specificato", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("Error parsing JSON Object while adding points");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Error writing URL while adding points");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error receiving data while adding points");
        }

        Toast.makeText(ctx, "Reset QR & punti aggiunti con successo!", Toast.LENGTH_SHORT).show();

    }
    //version required at least KITKAT
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    /**
     * Observes changes that occur in the value of the class
     * also evaluates the JS code injected
     *
     * @param scoreJSClassName name of the class that contains the value
     */
    public void observeFinalScore(String scoreJSClassName) {
        webView.evaluateJavascript(observeElementByClassName(scoreJSClassName, this.name), null);
    }

    /**Observes when a specified class changes
     *
     * @param elementClassName the class to observe
     * @param jsIName the name of the JS interface injected
     * @return the javascript code injected
     */
    private String observeElementByClassName(String elementClassName, String jsIName) {
        return "// Select the node that will be observed for mutations\n" +
                "var targetNode = document.getElementsByClassName('" + elementClassName + "')[0];\n" +
                "// Options for the observer (which mutations to observe)\n" +
                "var config = { attributes: true, childList: true, subtree: true };\n" +
                "\n" +
                "// Callback function to execute when mutations are observed\n" +
                "var callback = function(mutationsList, observer) {\n" +
                "    console.log('Elemento modificato');\n" +
                "    " + jsIName + ".onElementChanged(targetNode.textContent);\n" +
                "};\n" +
                "// Create an observer instance linked to the callback function\n" +
                "var observer = new MutationObserver(callback);\n" +
                "\n" +
                "// Start observing the target node for configured mutations\n" +
                "observer.observe(targetNode, config);\n" +
                "\n";
    }
}

package com.example.ramsete;

import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

class JavaScriptInterface {

    private Context ctx;
    private WebView webView;
    String name;

    /** Constructor of JavaScriptInterface class
     * with this you can inject JS code into the webview specified
     *
     * @param ctx   context from which I'm calling
     * @param webView Webview where I want to inject JS code
     * @param name name of the interface injected
     */
    JavaScriptInterface(Context ctx, WebView webView, String name) {
        this.ctx = ctx;
        this.webView = webView;
        this.name = name;
    }
    //mi dirà quando cambia l'elemento che mi interessa

    /**When the element changes prints out the new value
     *
     * @param newValue value of the element changed
     */
    @JavascriptInterface
    public void onElementChanged(String newValue) {
        Toast.makeText(ctx, newValue, Toast.LENGTH_LONG).show();
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

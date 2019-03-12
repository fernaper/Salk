package goatclaw.salk;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Enrique on 07/03/2019.
 */

public class ConnectAPI extends AsyncTask<Bitmap, String, Boolean> {

    @Override
    protected void onPreExecute() {

    }
    @Override
    protected Boolean doInBackground(Bitmap... params) {

        // Create URL
/*
        URL RedNeuronal = null;
        try {
            RedNeuronal = new URL("https://api.github.com/"); //todo poner la API
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Create connection
        HttpsURLConnection myConnection = null;
        try {
            myConnection = (HttpsURLConnection) RedNeuronal.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //todo cambiar datos
        myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");

        myConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
        myConnection.setRequestProperty("Contact-Me", "hathibelagal@example.com");

        try {
            if (myConnection.getResponseCode() == 200) {
                // Success
                // Further processing here

                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                //todo procesar respuesta

            } else {
                // Error handling code goes here
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

*/


        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        CameraActivity.t = false;
    }
}

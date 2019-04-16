package goatclaw.salk;


import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.BoringLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Enrique on 07/03/2019.
 */

public class ConnectAPI extends AsyncTask<String, Boolean, Boolean> {

    final String URL = "http://88.0.109.140:5500/check_frame";
    private static final String URL_DATABASE = "http://92.176.178.247:5754/";
    public static HashMap<String, String> respuesta;


    //Crea el mensaje POST y mapea la respuesta de forma asincrona
    public void sendImage(byte[] image, final Context ctx, final char letra) {
        // Instantiate the RequestQueue.
        final String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Display the first 500 characters of the response string.

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        respuesta = mapper.readValue(response, new TypeReference<Map<String, String>>(){});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast toast1 = Toast.makeText(ctx, "Letra detectada: " + respuesta.get("prediction") + "\nConfianza: " + respuesta.get("confidence") , Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();

                    Log.i("PETITION",  response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("PETITION",  error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("frame", encodedImage);
                    params.put("letter", "" + letra);
                    return params;
                }
            };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void sendUserName(final String username, final String language, final Context ctx){
        RequestQueue queueDatabase = Volley.newRequestQueue(ctx);

        if (language != null && !language.equals("") && username != null && !username.equals("")) {
            StringRequest myReq = new StringRequest(Request.Method.POST, URL_DATABASE + "create_user", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        respuesta = mapper.readValue(response, new TypeReference<Map<String, String>>(){});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("PETITION_DB",  response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    respuesta.put("warning", "Error en la conexión");
                    Log.i("PETITION_DB",  error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user", username);
                    params.put("language", language);
                    return params;
                }
            };
            queueDatabase.add(myReq);
        }
    }

    public HashMap<String, String> getRespuesta(){ return respuesta; }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }
}

package goatclaw.salk;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.BufferedReader;
import android.util.JsonReader;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Enrique on 07/03/2019.
 */

public class ConnectAPI{

    final String URL = "https://88.0.109.140:5500/";
    Post respuesta;

    //Crea el mensaje POST y mapea la respuesta de forma asincrona
    public void sendImage(byte[] image) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Camera2Activity.ctx);
        String url ="http://88.0.109.140:5500/check_frame_test";

        HashMap<String, byte[]> params = new HashMap<String, byte[]>();
        params.put("frame", image);
        //todo: Hay que ver que cojones hacemos aqui para pasar los parametros con volley
        //JSONObject json = new JSONObject(params).toString().getBytes();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("PETITION",  response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("PETITION",  error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

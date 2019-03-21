package goatclaw.salk;


import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Enrique on 07/03/2019.
 */

public class ConnectAPI{

    final String URL = "http://88.0.109.140:5500/check_frame";

    public static String get64BaseImage (Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    //Crea el mensaje POST y mapea la respuesta de forma asincrona
    public void sendImage(final Bitmap image) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(Camera2Activity.ctx);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("frame", get64BaseImage(image));
                    return params;
                }
            };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}

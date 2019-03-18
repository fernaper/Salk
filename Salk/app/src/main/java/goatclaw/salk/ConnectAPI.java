package goatclaw.salk;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.BufferedReader;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Enrique on 07/03/2019.
 */

public class ConnectAPI{

    final String URL = "https://88.0.109.140:5500/";
    Post respuesta;

    //Crea el mensaje POST y mapea la respuesta de forma asincrona
    public void sendImage(Image image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostService postService = retrofit.create(PostService.class);
        //Aqui es donde realizamos la petición POST y recibimos la respuesta asincrona
        postService.savePost(image).enqueue(new Callback<Post>() {
            //Se supone que al recibir la respuesta esto queda mapeado en la instancia Post que guardamos en respuesta
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(response.isSuccessful()) {
                    respuesta = response.body();
                    Log.i("PETITION", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("PETITION", "unable post to API.");
            }
        });
    }
}

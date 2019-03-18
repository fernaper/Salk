package goatclaw.salk;

import android.graphics.Bitmap;
import android.media.Image;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/*
    Definimos la URI de la API y definimos las operaciones que queremos
    realizar sobre ella, en nuestro caso solo nos interesa la operacion POST
    que envía una imagen (aun no es seguro que sea un BufferedReader) identificado
    por la clave "frame".
 */
public interface PostService {

    String API_ROUTE = "/check_frame";

    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<Post> savePost(@Field("frame") Image key);
}
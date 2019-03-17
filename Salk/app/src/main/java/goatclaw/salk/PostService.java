package goatclaw.salk;

import java.io.BufferedReader;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface PostService {

    String API_ROUTE = "/check_frame";

    @GET(API_ROUTE)
    Call< Post > getPost();

    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<Post> savePost(@Field("frame") BufferedReader key);
}
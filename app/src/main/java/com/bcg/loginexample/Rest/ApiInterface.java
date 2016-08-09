package com.bcg.loginexample.Rest;

import com.bcg.loginexample.Responses.Avatar;
import com.bcg.loginexample.Responses.Session;
import com.bcg.loginexample.Responses.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
//      String ENDPOINT = "http://192.168.0.11:1337/";
    String ENDPOINT = "https://rest-service1.herokuapp.com/";

     @FormUrlEncoded
    @POST("sessions/new")
    Call<Session> newsession(@Field("email") String email, @Field("password")  String password);

    @GET("users/{userid}")
    Call<User> getuser(@Path("userid") String userid);

    @Multipart
    @POST ("users/{userid}/avatar/{avatar}")
    Call<Avatar> postAvatar (@Part("image\"; filename=\"pic.jpg\" ") RequestBody file , @Part("FirstName") RequestBody description);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
           .build();
}



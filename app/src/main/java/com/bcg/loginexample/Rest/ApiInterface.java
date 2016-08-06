package com.bcg.loginexample.Rest;

import com.bcg.loginexample.Responses.Avatar;
import com.bcg.loginexample.Responses.Session;
import com.bcg.loginexample.Responses.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    String ENDPOINT = "http://missing-url.com";

    @GET("sessions/new/{email}/{password}")
    Call<Session> newsession(@Path("email") String email, @Path("password") String password);

    @GET("users/{userid}")
    Call<User> getuser(@Path("userid") String userid);

    @POST("users/{userid}/avatar")
    Call<Avatar> postAvatar(@Path("userid") String userid);

}



package com.bcg.loginexample.Rest;

/**
 * Created by jalle on 6.8.2016.
 */

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class RestClient {

    private static ApiInterface mRestService = null;

    public static ApiInterface getClient() {
        if(mRestService == null) {

            final OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new FakeInterceptor())
                    .build();


            final Retrofit retrofit = new Retrofit.Builder()
                    // Using custom Jackson Converter to parse JSON
                    // Add dependencies:
                    // com.squareup.retrofit:converter-jackson:2.0.0-beta2
                    .addConverterFactory(JacksonConverterFactory.create())
                    // Endpoint
                    .baseUrl(ApiInterface.ENDPOINT)
                    .client(client)
                    .build();

            mRestService = retrofit.create(ApiInterface.class);
        }
        return mRestService;
    }
}

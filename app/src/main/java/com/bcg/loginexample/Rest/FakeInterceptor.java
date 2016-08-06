package com.bcg.loginexample.Rest;

import java.io.IOException;
import java.net.URI;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by jalle on 5.8.2016.
 */

public class FakeInterceptor implements Interceptor {
    // FAKE RESPONSES.
        private final static String USER_ID_1 = "{\"userid\":jalle,\"token\":123456}";
    private final static String USER_ID_1_AVATAR = "{\"email\":jaskobh@hotmail.com,\"avatar_url\":http:\\google.com\"}";
    private final static String POST_USER_1_AVATAR = "{\"avatar_url\":1\"}";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = null;
        if( true) {
            String responseString;

            String url = chain.request().url().toString();
            final URI uri = URI.create(url); // chain.request().uri();
            // Get Query String.
            final String query = uri.getQuery();
            // Parse the Query String.

            responseString = USER_ID_1_AVATAR;

            /*
            final String[] parsedQuery = query.split("=");
            if(parsedQuery[0].equalsIgnoreCase("jaskobh@hotmail.com") && parsedQuery[1].equalsIgnoreCase("123456")) {
                responseString = USER_ID_1_AVATAR;
            }
            else {
                responseString = "false";
            }*/

            response = new Response.Builder()
                    .code(200)
                    .message(responseString)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();
        }
        else {
            response = chain.proceed(chain.request());
        }

        return response;
    }
}

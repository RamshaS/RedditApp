package com.udacity.ramshasaeed.redditapp.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://www.reddit.com/";

    private static RetrofitClient client;

    public static APIs api;


    private RetrofitClient(APIs api) {
        this.api = api;
    }

    public static RetrofitClient getInstance() {
        if (client == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            client = new RetrofitClient(retrofit.create(APIs.class));
        }

        return client;
    }


}

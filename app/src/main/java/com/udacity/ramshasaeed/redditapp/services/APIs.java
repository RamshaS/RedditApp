package com.udacity.ramshasaeed.redditapp.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIs {



    @GET("search.json?")
    Call<ResponseBody> getSearchqueryResult(@Query("q") String searchkeyword);

    @GET("r/")
    Call<ResponseBody> getHome(String search);

    @GET("")
    Call<ResponseBody> getAll(String data);



}

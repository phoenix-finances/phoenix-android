package com.ornoma.phoenix.api;


import com.ornoma.phoenix.response.LogInResponse;
import com.ornoma.phoenix.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;

public interface Api {

    @POST("/api/users")
    Call<RegisterResponse> register(
            @Field("username") String username,
            @Field("email") String email,
            @Field("password") String password
    );


    @POST("/users/login")
    Call<LogInResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );
}


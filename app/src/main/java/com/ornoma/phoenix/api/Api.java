package com.ornoma.phoenix.api;


import com.ornoma.phoenix.api.response.LoginRequest;
import com.ornoma.phoenix.api.response.LoginResponse;
import com.ornoma.phoenix.api.response.RegistrationRequest;
import com.ornoma.phoenix.api.response.RegistrationResponse;
import com.ornoma.phoenix.api.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @POST("/users")
    Call<RegistrationResponse> register(
           /* @Query("username") String username,
            @Query("email") String email,
            @Query("password") String password*/
            @Body RegistrationRequest request
            );

    @POST("/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // /users/me?param1=value1&param2=value2

    @POST("/users/{userId}")
    Call<?> testReferences(
            @Query("param1") String p1, @Query("param2") String p2, @Body LoginRequest request,
            @Path("userId") String userId,
            @Header("Authorization") String token
    );

    @GET("/users/me")
    Call<UserResponse> getMyself(@Header("Authorization") String auth);
}


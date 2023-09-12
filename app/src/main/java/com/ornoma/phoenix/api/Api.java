package com.ornoma.phoenix.api;


import com.ornoma.phoenix.api.response.CreateLedgersRequest;
import com.ornoma.phoenix.api.response.CreateLedgersResponse;
import com.ornoma.phoenix.api.response.CreateTransactionGroupRequest;
import com.ornoma.phoenix.api.response.CreateTransactionGroupResponse;
import com.ornoma.phoenix.api.response.LoginRequest;
import com.ornoma.phoenix.api.response.LoginResponse;
import com.ornoma.phoenix.api.response.RegistrationRequest;
import com.ornoma.phoenix.api.response.RegistrationResponse;
import com.ornoma.phoenix.api.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    //Registration
    @POST("/users")
    Call<RegistrationResponse> register(
            @Body RegistrationRequest request
    );

    //Login
    @POST("/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // /users/me?param1=value1&param2=value2

   /* @POST("/users/{userId}")
    Call<?> testReferences(
            @Query("param1") String p1, @Query("param2") String p2, @Body LoginRequest request,
            @Path("userId") String userId,
            @Header("Authorization") String token
    );*/

    //Me
    @GET("/users/me")
    Call<UserResponse> getMyself(@Header("Authorization") String auth);

    //Create Ledgers
    @POST("/ledgers")
    Call<CreateLedgersResponse> createLedger(@Header("Authorization") String auth,@Body CreateLedgersRequest request);

    @GET("/ledgers")
    Call<List<CreateLedgersResponse>> getLedgers(@Header("Authorization") String auth);

    @POST("/transaction-groups")
    Call<CreateTransactionGroupResponse> createGroupTransaction(@Header("Authorization") String auth, @Body CreateTransactionGroupRequest groupTransactionRequest);

}


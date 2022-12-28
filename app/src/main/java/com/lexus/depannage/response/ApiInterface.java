package com.lexus.depannage.response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @Headers({"Accept: application/json", "Content-Type: text/html"})

    @GET("user_login_register.php")
    Call<Users> login_register(
      @Query("user_phone") String user_phone,
      @Query("user_auth_token") String user_auth_token,
      @Query("token") String token );
}

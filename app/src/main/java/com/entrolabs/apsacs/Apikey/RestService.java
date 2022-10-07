package com.entrolabs.apsacs.Apikey;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface RestService {


    @POST("newapi.php?")
    Call<String> getLogin(@HeaderMap Map<String,String> params1, @Body String params2
    );
    @POST("newapi.php?")
    Call<String> getLogin1(@HeaderMap Map<String,String> params1, @Body String params2
    );



}

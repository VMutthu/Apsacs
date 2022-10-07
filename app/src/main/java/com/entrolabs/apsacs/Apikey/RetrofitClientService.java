package com.entrolabs.apsacs.Apikey;
import com.entrolabs.apsacs.URL.UrlBase;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitClientService {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        OkHttpClient okHttpClient= Utils.getUnsafeOkHttpClient();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(UrlBase.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    //.client(okHttpClient)
                    .build();

        }
        return retrofit;
    }


}

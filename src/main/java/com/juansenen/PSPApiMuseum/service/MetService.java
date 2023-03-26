package com.juansenen.PSPApiMuseum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MetService {

    static final String BASE_URL = "https://collectionapi.metmuseum.org/public/collection/v1/";
    private MetAPI metAPI;

    public MetService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient clientlogger = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //Objeto Gson
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientlogger) //Logger
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        metAPI = retrofit.create(MetAPI.class);
    }

    public Observable<ObjectsMain> getTotalObjects(){
        return this.metAPI.loadObjects();
    }

}

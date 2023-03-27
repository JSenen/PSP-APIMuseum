package com.juansenen.PSPApiMuseum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.juansenen.PSPApiMuseum.domain.Deparments;
import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

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
    public Observable<List<Department>> getAllDeparments(){
        return this.metAPI.loadDeparments().map(deparments -> deparments.getDepartments());
    }
    public Observable<ObjectsMain> getALlObjectsIdDepartment(int id){
        return this.metAPI.loadObjectsByDepartment(id);
    }
    public Observable<ObjectsByID> getObjectById(int objectId){

        return this.metAPI.loadOneObject(objectId);
    }
    public Observable<ObjectsMain> getObjectByIdforDepart(int idItem, String cadena){
        return this.metAPI.loadObjectByIDdepart(idItem, cadena).map(objectsMain -> objectsMain);
    }


}

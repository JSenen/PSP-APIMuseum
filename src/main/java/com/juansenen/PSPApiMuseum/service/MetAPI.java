package com.juansenen.PSPApiMuseum.service;

import com.juansenen.PSPApiMuseum.domain.Deparments;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MetAPI {
    /** interfaz de Retrofit que defina los endpoints de la API */

    @GET("objects/{objectId}")
    Observable<ObjectsByID> loadOneObject(@Path("objectId") int objectId);

    @GET("objects")
    Observable<ObjectsMain> loadObjects();    /** Obtener objetos */
    @GET("departments")
    Observable<Deparments> loadDeparments(); /** Obtener los departamentos*/


}

package com.juansenen.PSPApiMuseum.service;

import com.juansenen.PSPApiMuseum.domain.Deparments;
import com.juansenen.PSPApiMuseum.domain.ObjectsList;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface MetAPI {
    /** interfaz de Retrofit que defina los endpoints de la API */

    @GET("objects/{objectId}")
    Observable<ObjectsByID> loadOneObject(@Path("objectId") int objectId);

    @GET("objects")
    Observable<ObjectsMain> loadObjects();    /** Obtener objetos */
    @GET("departments")
    Observable<Deparments> loadDeparments(); /** Obtener los departamentos*/

    @GET("search") /** Obtener los objetos totales de un Departamento**/
    Observable<ObjectsMain> loadObjectsByDepartment(@Query("departmentIds") int departmentIds, @Query("q") String cadena);





}

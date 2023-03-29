package com.juansenen.PSPApiMuseum.service;

import com.juansenen.PSPApiMuseum.domain.Deparments;
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
    Observable<ObjectsByID> loadOneObject(@Path("objectId") int objectId);  /** Busqueda de todos los datos de un objeto por su Id */

    @GET("objects")
    Observable<ObjectsMain> loadObjects();    /** Obtener listado objetos Total y Ids todos los objetos */
    @GET("departments")
    Observable<Deparments> loadDeparments(); /** Obtener los departamentos con Id y Nombre*/

    @GET("search") /** Obtener datos de los objetos con diferentes filtros de busqueda **/
    Observable<ObjectsMain> loadObjectsByDepartment(@Query("departmentId") int departmentIds, @Query("q") String cadena);





}

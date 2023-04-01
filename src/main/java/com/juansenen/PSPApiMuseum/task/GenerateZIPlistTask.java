package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;

import java.util.List;

public class GenerateZIPlistTask extends Task<List<String>> {

    private List<String> objetosToZip;
    private List<Integer> idObjects;
    private MetService service;
    public GenerateZIPlistTask(List<Integer> idObjects, List<String> objetosToZip){
        this.objetosToZip = objetosToZip;
        this.idObjects = idObjects;

    }
    @Override
    protected List<String> call() throws Exception {
        service = new MetService();
        for (Integer ids: idObjects){ //Recorremos el List de Ids que pertenecen al Departamento

            Consumer<ObjectsByID> obj = (info) -> {

                objetosToZip.add(info.getTitle()+" "+info.getArtistDisplayName()+" "+info.getCountry()+" "+"\n");

            };
            service.getObjectById(ids).subscribe(obj);

        }

        return null;
    }
}

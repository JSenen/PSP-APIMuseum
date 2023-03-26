package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;

import java.util.List;

public class ObjectsByDepartmentTask extends Task<ObjectsMain> {
    public MetService api;
    public int idDeparment;
    public boolean isOnView,hasImages,isHighlight;
    public Consumer<ObjectsMain> user;
    public List<ObjectsMain> objectsMainList;

    public ObjectsByDepartmentTask(int idDeparment, Consumer<ObjectsMain> user){
        this.idDeparment = idDeparment;
        this.user = user;
    }
    @Override
    protected ObjectsMain call() throws Exception {
        api = new MetService();
        api.getALlObjectsIdDepartment(idDeparment);
        return null;
    }
}

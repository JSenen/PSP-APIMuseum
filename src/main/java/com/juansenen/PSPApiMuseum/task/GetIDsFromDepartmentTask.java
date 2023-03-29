package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class GetIDsFromDepartmentTask extends Task<ObjectsMain> {
    private int idItemSelected;
    private String cadena;
    private Consumer<ObjectsMain> user;
    private MetService api;
    public GetIDsFromDepartmentTask(int idItemSelected, String cadena, Consumer<ObjectsMain> user) {
        this.idItemSelected = idItemSelected;
        this.cadena = cadena;
        this.user = user;
    }

    @Override
    protected ObjectsMain call() throws Exception {
        api = new MetService();
        api.getObjectByIdforDepart(idItemSelected,cadena).subscribe(user);
        Thread.sleep(10); //TODO Fix HandLeing exception

        return null;
    }
}

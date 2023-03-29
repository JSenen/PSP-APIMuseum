package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;

public class GetIDsFromDepartmentTask extends Task<ObjectsMain> {
    private int idItemSelected;
    private String cadena;
    private Consumer<ObjectsMain> user;
    private int partes;
    private MetService api;
    private ProgressIndicator progressIndicator;
    public GetIDsFromDepartmentTask(int idItemSelected, String cadena,
                                    Consumer<ObjectsMain> user, ProgressIndicator progressIndicator) {
        this.idItemSelected = idItemSelected;
        this.cadena = cadena;
        this.user = user;
        this.progressIndicator = progressIndicator;
    }

    @Override
    protected ObjectsMain call() throws Exception {

        api = new MetService();
        api.getObjectByIdforDepart(idItemSelected,cadena).subscribe(user);
        return null;
    }

    @Override
    protected void succeeded() {
        progressIndicator.setVisible(false);
    }
}

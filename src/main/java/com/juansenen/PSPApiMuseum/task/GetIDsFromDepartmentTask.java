package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.controller.AppController;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GetIDsFromDepartmentTask extends Task<List<Integer>> {
    private int idItemSelected;
    private String cadena;
    private MetService api;
    private ProgressIndicator progressIndicator;
    private Text txtTotalDepart;
    private TextField txtFieldSearch;
    public GetIDsFromDepartmentTask(int idItemSelected, String cadena,
                                    ProgressIndicator progressIndicator, TextField txtFieldSearch, Text txtTotalDepart) {
        this.idItemSelected = idItemSelected;
        this.cadena = cadena;
        this.txtTotalDepart = txtTotalDepart;
        this.txtFieldSearch = txtFieldSearch;
        this.progressIndicator = progressIndicator;

    }

    @Override
    protected List<Integer> call() throws Exception {
        List<Integer> idObjects = new ArrayList<>();
        Consumer<ObjectsMain> deparObjs = (info) -> {
            Platform.runLater(() -> {
                txtTotalDepart.setText(String.valueOf(info.getTotal()));
                txtTotalDepart.setFill(Color.GREEN);
            });
            //Añadimos a la lista los IDs de los objetos del departamento
            idObjects.addAll(info.getObjectIDs());
            txtFieldSearch.setText("");
        };

        api = new MetService();
        api.getObjectByIdforDepart(idItemSelected,cadena).subscribe(deparObjs);

        return idObjects;
    }

    @Override
    protected void succeeded() {
        progressIndicator.setVisible(false);
    }
}

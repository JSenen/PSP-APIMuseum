package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public Label labelTotal;
    public Label labelMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTotalNumberObjects();

    }

    public void setTotalNumberObjects(){
        MetService metService = new MetService();
        Consumer<ObjectsMain> totalObj = (info) -> {
            labelTotal.setText(String.valueOf(info.getTotal()));
        };

        metService.getTotalObjects().subscribe(totalObj);


    }
}

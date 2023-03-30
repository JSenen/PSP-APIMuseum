package com.juansenen.PSPApiMuseum.controller;


import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ObjectScreenController implements Initializable {
    @FXML
    private Text txtTitle;
    private ObjectsByID objectsByID;

    public ObjectScreenController(ObjectsByID objectsByID) {
        this.objectsByID = objectsByID;

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData(objectsByID);

    }
    public void initData(ObjectsByID selectedObject){
        txtTitle.setText(objectsByID.getTitle());
    }




}

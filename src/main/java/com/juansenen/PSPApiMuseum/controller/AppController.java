package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import com.juansenen.PSPApiMuseum.task.TotalObjectTask;
import io.reactivex.functions.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public Label labelTotal;
    public Button buttonLoad;
    public Text txtTotal;
    public int totalObjects;
    public TableView<Department> tableMain;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void prepareTableODepartment(){
        TableColumn<Department, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));

        tableMain.getColumns().add(idColumn);

    }

    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){
        setTotalNumberObjects();

    }

    public void setTotalNumberObjects(){
        txtTotal.setText("Cargando...");
        Consumer<ObjectsMain> totalObj = (info) -> {
            txtTotal.setText(String.valueOf(info.getTotal()));
        };

        TotalObjectTask totalObjectTask = new TotalObjectTask(totalObj);
        new Thread(totalObjectTask).start();

    }

}

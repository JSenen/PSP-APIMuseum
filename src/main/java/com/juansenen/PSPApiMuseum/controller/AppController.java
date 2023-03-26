package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Deparments;
import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import com.juansenen.PSPApiMuseum.task.TotalObjectTask;
import io.reactivex.functions.Consumer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public Label labelTotal;
    public Button buttonLoad;
    public Text txtTotal;
    public int totalObjects;
    public TableView<Department> tableMain;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment();

        MetService service = new MetService();
        Consumer<Deparments> dep = (info) -> {
            List<Department> departmentsList = info.getDepartments();
            tableMain.setItems(FXCollections.observableArrayList(departmentsList));
        };
        service.getAllDeparments().subscribe(dep);


    }

    public void prepareTableDepartment(){
        TableColumn<Department, Integer> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        TableColumn<Department, String> nameColumn = new TableColumn<>("Nombre");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(idColumn);
        tableMain.getColumns().add(nameColumn);

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

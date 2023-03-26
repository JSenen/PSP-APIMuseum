package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public Label labelTotal;
    public Label labelMessage;
    public TableView<ObjectsByID> tableObjects;


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

    public void prepareTableObjects(){
        TableColumn<ObjectsByID, String> idColumn = new TableColumn<>("Id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("objectID"));
        tableObjects.getColumns().add(idColumn);




    }
}

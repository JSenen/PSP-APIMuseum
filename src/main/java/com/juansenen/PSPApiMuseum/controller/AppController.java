package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    public Button buttonLoad;
    public Text txtTotal;
    public int totalObjects;
    public List<Integer> idObjects;
    public TableView<Department> tableMain;
    public TableView<Integer> tableObjects; //TODO CAMBIAR
    public int IDitemSelected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Preparar la Tabla
        setTotalDeparments();     //Cargar los datos de departamentos

    }

    public void prepareTableDepartment(){

        TableColumn<Department, Integer> idColumn = new TableColumn<>("Id");        //Crear columna
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));   //Asociar a campo clase
        TableColumn<Department, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(idColumn);   //Datos a la columna
        tableMain.getColumns().add(nameColumn);

    }
    public void prepareTableDObjects(){

        TableColumn<Integer, Integer> idColumn = new TableColumn<>("ID"); //TODO CAMBIAR
        idColumn.setCellValueFactory(new PropertyValueFactory<>("objectIDs"));

        tableObjects.getColumns().add(idColumn);   //Datos a la columna
    }

    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }
    @FXML
    public void tableMouseClickItem(MouseEvent event) {      //Pulsar 2 veces sobre un elemento de la tabla

        if (event.getClickCount() == 2) {
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();

            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id de la selección
            getTotalObjectsByDepartment(IDitemSelected);
            // TODO Only for test purpose
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have selected " + selectedDepartment.getDisplayName());
            alert.show();
        }
    }

    private void getTotalObjectsByDepartment(int iDitemSelected) {
        /** Se hara una busqueda de todos los objetos del departamento seleccionado, pero filtrando
         * tambien por los que se hayan expuestos, para asi acortar el listado
         */
        Consumer<ObjectsMain> numberObjIds = (info) -> {
            idObjects = info.getObjectIDs(); //Recuperamos las Id de los Objetos del departamento
            txtTotal.setText(String.valueOf(info.getTotal())); //Indicamos el numero de objetos en pantalla
            System.out.println(info.getObjectIDs()); //TODO Borrar tras prueba
            getObjectsById(iDitemSelected);  //TODO Terminar metodo (BUscar por ID con parametros para acortar)
        };
//        ObjectsByDepartmentTask objectsByDepartmentTask = new ObjectsByDepartmentTask(iDitemSelected,numberObjIds);
//        new Thread(objectsByDepartmentTask).start();
        MetService api = new MetService();
        api.getALlObjectsIdDepartment(iDitemSelected).subscribe(numberObjIds);
    }

    private void getObjectsById(int iDitemSelected) {
        MetService service = new MetService();
        prepareTableDObjects();     //Preparamos la tabla
        Consumer<ObjectsMain> object = (objectId) -> {
            System.out.println(objectId.getObjectIDs()); //Borrar tras prueba
            List<Integer> ids = objectId.getObjectIDs();
            tableObjects.setItems(FXCollections.observableArrayList(ids)); //TODO CAMBIAR

        };
        String cadena = "sunflowers"; //TODO realizar tablero para introducir cadena de busqueda
        service.getObjectByIdforDepart(iDitemSelected, cadena).subscribe(object);

    }

    public void setTotalNumberObjects(){
        txtTotal.setText("Cargando...");
        Consumer<ObjectsMain> totalObj = (info) -> {            //Creamos el consumidor
            txtTotal.setText(String.valueOf(info.getTotal()));
        };

        TotalObjectTask totalObjectTask = new TotalObjectTask(totalObj);  //Creamos un hilo
        new Thread(totalObjectTask).start();                             // Ejecutamos hilo

    }
    public void setTotalDeparments(){
        MetService service = new MetService();
        Consumer<List<Department>> dep = (info) -> {
            tableMain.setItems(FXCollections.observableArrayList(info));
        };
        service.getAllDeparments().subscribe(dep);
    }


}

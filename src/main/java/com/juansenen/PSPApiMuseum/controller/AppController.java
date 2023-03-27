package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import com.juansenen.PSPApiMuseum.task.TotalObjectTask;
import io.reactivex.functions.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public ObservableList<Integer> idsObjectsFromDepartment;
    public ComboBox<Integer> comboIDfromDepar;
    public TableView<Department> tableMain;
    public TableView<Integer> tableObjects; //TODO CAMBIAR
    public int IDitemSelected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Preparar la Tabla Departamentos
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

        TableColumn<Integer,Integer> idColumn = new TableColumn<>("Name"); //TODO CAMBIAR
        idColumn.setCellValueFactory(new PropertyValueFactory<>("objectId"));

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

            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            getTotalObjectsByDepartment(IDitemSelected);
            // TODO Only for test purpose
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have selected " + selectedDepartment.getDisplayName());
            alert.show();
        }
    }

    private void getTotalObjectsByDepartment(int iDitemSelected) {
        /** Buscamos todos las Ids Objetos que pertenecen a un Departamento. La API nos devuelve el ID de todos ellos */

        Consumer<ObjectsMain> numberObjIds = (info) -> {
            idObjects = info.getObjectIDs(); //Recuperamos las Id de los Objetos del departamento
            txtTotal.setText(String.valueOf(info.getTotal())); //Indicamos el numero de objetos en pantalla
            System.out.println(info.getObjectIDs()); //TODO Borrar tras prueba
            setObjectsByID(iDitemSelected);
        };
//        ObjectsByDepartmentTask objectsByDepartmentTask = new ObjectsByDepartmentTask(iDitemSelected,numberObjIds);
//        new Thread(objectsByDepartmentTask).start();
        MetService api = new MetService();
        api.getObjectByIdforDepart(iDitemSelected).subscribe(numberObjIds);
    }


    private void setObjectsByID(int idDepartment) {
        MetService service = new MetService();
        /** Para recoger los objetos hemos recogido las ID que pertenecen al departamento por que la API
         * no filtra objetos por departamento. Solo aporta las ids de los objetos del departamento
         */
        idsObjectsFromDepartment = FXCollections.observableArrayList(); //Array Observable
        for (Integer idsOb: idObjects){
            idsObjectsFromDepartment.add(idsOb); //Añadimos los datos
        }
        comboIDfromDepar.setItems(idsObjectsFromDepartment);


////        for (Integer id : idObjects){
////            Consumer<ObjectsByID> objectsById = (info) -> {
////
////                System.out.println(info.getObjectID());
////                idObject.add(info.getObjectID());
////                tableObjects.setItems(FXCollections.observableArrayList(info.getObjectID())); //TODO CAMBIAR y llevar a task
////                //TODO arreglar solo pinta ultimo objeto
////                //TODO List<ObjectsByID> ???? en Consumer
////                //tableObjects.setItems(FXCollections.observableArrayList(info));
////            };
////            service.getObjectById(id).subscribe(objectsById);
////            tableObjects.setItems(FXCollections.observableArrayList(idObjects)); //TODO CAMBIAR y llevar a task
//
//        }

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

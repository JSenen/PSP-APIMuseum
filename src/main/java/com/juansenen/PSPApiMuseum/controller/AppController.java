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
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    public Button buttonLoad;

    @FXML
    public Text txtTotal;
    public int totalObjects;
    public List<Integer> idObjects; //Lista para guardar Ids de Objetos en memoria
    public List<String> title;      //Lista para guardar titulos de Objetos en memoria
    public ObservableList<Integer> idsObjectsFromDepartment;
    public ObservableList<String> titleObjectsFromDepartment;
    @FXML
    public ComboBox<Integer> comboIDfromDepar;
    @FXML
    public TableView<Department> tableMain;
    @FXML
    public TableView<ObjectsMain> tableIds;
    public int IDitemSelected;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Inicializa la Tabla Departamentos
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
    public void prepareTableIDs(){

        TableColumn<ObjectsMain, Integer> idColumn = new TableColumn<>("IDs");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("objectIDs"));

        tableIds.getColumns().add(idColumn);
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

            System.out.println(IDitemSelected); //TODO Borrar tras pruebas
            setTableIds();
            // TODO Only for test purpose
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Ha seleccionado " + selectedDepartment.getDisplayName());
            alert.show();
        }
    }

    private void setTableIds() {    //TODO ARREGLAR!!!!!!
        MetService service = new MetService();
        Consumer<List<Department>> dep = (info) -> {
            tableMain.setItems(FXCollections.observableArrayList(info));
        };
        service.getAllDeparments().subscribe(dep);
        prepareTableIDs();    //Inicializa tabla objetos
        idsObjectsFromDepartment.addAll(idObjects); //TODO Pasar Ids a Table

    }

    private void getTotalObjectsByDepartment(int iDitemSelected) {
        /** Buscamos todos las Ids Objetos que pertenecen a un Departamento. La API nos devuelve el ID de todos ellos */

        Consumer<ObjectsMain> numberObjIds = (info) -> {
            idObjects = info.getObjectIDs(); //Recuperamos las Id de los Objetos del departamento y las guardamos en memoria
            txtTotal.setText(String.valueOf(info.getTotal())); //Indicamos el numero de objetos en pantalla //TODO Cambiar a etiqueta Departamentos visible
            //setComboObjectsID(iDitemSelected);


        };
        //TODO Llevar al task
        MetService api = new MetService();
        api.getObjectByIdforDepart(iDitemSelected).subscribe(numberObjIds);
    }


    private void setComboObjectsID(int idDepartment) {

        /** Para recoger los objetos hemos recogido las ID que pertenecen al departamento por que la API
         * mostramos las ID de los Objetos por Departamento en un ComboBox
         */
        idsObjectsFromDepartment = FXCollections.observableArrayList();
        titleObjectsFromDepartment = FXCollections.observableArrayList();

        //Añadimos los datos
        idsObjectsFromDepartment.addAll(idObjects); //Añadimos todos los ids a una ObservableArrayList
        comboIDfromDepar.setItems(idsObjectsFromDepartment); //Rellenamos ComboBox con las Ids

        comboIDfromDepar.setOnAction((evento) -> {          //Listener en el comboBox
            int selectedIndex = comboIDfromDepar.getSelectionModel().getSelectedIndex();
            Object selectedItem = comboIDfromDepar.getSelectionModel().getSelectedItem();

            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
            System.out.println("   ComboBox.getValue(): " + comboIDfromDepar.getValue());
            //getObjectstoTable(idObjects);
        });
        //getObjectById(comboIDfromDepar.getValue());






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

    public void getObjectstoTable(List<Integer> idObjects){ //TODO POR ARREGLAR
        MetService service = new MetService();
        for (Integer ids: idObjects){
            Consumer<ObjectsByID> obj = (info) -> {
                title.add(info.getTitle());
            };
            service.getObjectById(ids).subscribe(obj);
        }
        titleObjectsFromDepartment.addAll(title);
        //tableIds.setItems(titleObjectsFromDepartment);

    }


}

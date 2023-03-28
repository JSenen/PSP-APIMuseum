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

    @FXML
    public Button buttonLoad, butSelectObjects;

    @FXML
    public Text txtTotal;
    public int totalObjects;
    public List<Integer> idObjects = new ArrayList<>(); //Lista para guardar Ids de Objetos en memoria
    public List<String> title = new ArrayList<>();      //Lista para guardar titulos de Objetos en memoria
    public ObservableList<Integer> idsObjectsFromDepartment;
    @FXML
    public ObservableList<ObjectsByID> titleObjectsFromDepartment = FXCollections.observableArrayList();
    @FXML
    public TableView<Department> tableMain;
    @FXML
    public TableView<ObjectsByID> tableObjects;
    public int IDitemSelected;
    public String cadena = "sunflowers"; //TODO llevarse cadena a inputext


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Inicializa la Tabla Departamentos
        setTotalDeparments();     //Cargar los datos de departamentos


    }
    /** Tabla Departamentos **/
    public void prepareTableDepartment(){

        TableColumn<Department, Integer> idColumn = new TableColumn<>("Id");        //Crear columna
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));   //Asociar a campo clase
        TableColumn<Department, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(idColumn);   //Datos a la columna
        tableMain.getColumns().add(nameColumn);

    }
    /** Tabla Titulos de Obras **/
    public void prepareTableObjects(){

        TableColumn<ObjectsByID, String> titleColumn = new TableColumn<>("TITLE");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        tableObjects.getColumns().add(titleColumn); //Dato titulo

    }

    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }
    /**Acción seleccionar un Departamento en la tabla Departamentos **/
    @FXML
    public void tableMouseClickItem(MouseEvent event) {      //Pulsar 1 veces sobre un elemento de la tabla

        if (event.getClickCount() == 2) {
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();

            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            getTotalObjectsByDepartment();

            // TODO Only for test purpose
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("SE HA CARGADO SU SELECCION  " + selectedDepartment.getDisplayName());
            alert.show();
        }
    }
    /** Buscar Total de Objetos que corresponden al Departamento*/
    private void getTotalObjectsByDepartment() {

        MetService service = new MetService();
        Consumer<ObjectsMain> deparObjs = (info) ->{
            txtTotal.setText(String.valueOf(info.getTotal()));
            idObjects.addAll(info.getObjectIDs());
            getObjectstoTable(idObjects);

        };
        service.getObjectByIdforDepart(IDitemSelected,cadena).subscribe(deparObjs);

    }
    public void getObjectstoTable(List<Integer> idObjects){ //Recuperamos datos objetos segun ids de los Departamentos

        MetService service = new MetService();

        for (Integer ids: idObjects){       //Recorremos el List de Ids que pertenecen al Departamento
            Consumer<ObjectsByID> obj = (info) -> {

                titleObjectsFromDepartment.add(new ObjectsByID(info.getObjectID(), info.getAccessionNumber(),
                                info.getAccessionYear(),info.isPublicDomain(), info.getPrimaryImage(),
                                info.getTitle(),info.getCountry()));

            };
            service.getObjectById(ids).subscribe(obj);
        }
        prepareTableObjects();
        tableObjects.setItems(FXCollections.observableArrayList(titleObjectsFromDepartment));

    }
    @FXML
    public void setTotalObjects(ActionEvent actionEvent){   //Llebar los objetos a la tabla
        prepareTableObjects();
        tableObjects.setItems(FXCollections.observableArrayList(titleObjectsFromDepartment));

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

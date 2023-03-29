package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import com.juansenen.PSPApiMuseum.task.GetIDsFromDepartmentTask;
import com.juansenen.PSPApiMuseum.task.GetObjectsByIdsTask;
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
    public Button buttonLoad;

    @FXML
    public Text txtTotal, idDepartment;
    public List<Integer> idObjects = new ArrayList<>(); //Lista para guardar Ids de Objetos en memoria
    //Observable List para la table de Objetos por titulo
    @FXML
    public ObservableList<ObjectsByID> titleObjectsFromDepartment = FXCollections.observableArrayList();
    @FXML
    public TableView<Department> tableMain;
    @FXML
    public TableView<ObjectsByID> tableObjects;
    public int IDitemSelected;  //Id del Departamento seleccionado

    public String cadena = "cat"; /** Filtro de busqueda por defecto para acortar las busquedas */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Inicializa la Tabla Departamentos
        prepareTableObjects();    //Inicializar tabla Objetos
        setTotalDeparments();     //Cargar los nombres de departamentos a tabla inicial.
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
    /** Objetos totales en el Museo **/
    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }
    /**Acción seleccionar un Departamento en la tabla Departamentos **/
    @FXML
    public void tableMouseClickItem(MouseEvent event) {

        if (event.getClickCount() == 2) {//Pulsar 2 veces sobre un elemento de la tabla para elegir departamento
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();

            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            idDepartment.setText(String.valueOf(IDitemSelected));
            getTotalObjectsByDepartment();


        }
    }
    /** Buscar Total de Ids Objetos que corresponden al Departamento*/
    private void getTotalObjectsByDepartment() {

        idObjects.clear();
        txtTotal.setText("Cargando...");

        Consumer<ObjectsMain> deparObjs = (info) ->{
            txtTotal.setText(String.valueOf(info.getTotal()));
            idObjects.addAll(info.getObjectIDs()); //Añadimos a la lista los IDs de los objetos del departamento
            getObjectstoTable(idObjects);          //Buscamos todos los Objetos de las anteriores IDs


        };
        GetIDsFromDepartmentTask getIDsFromDepartmentTask = new GetIDsFromDepartmentTask(IDitemSelected,cadena,deparObjs);
        new Thread(getIDsFromDepartmentTask).start();

    }
    /** Recuperar los datos individuales de cada objeto por su Id **/
    public void getObjectstoTable(List<Integer> idObjects){ //TODO Hacer 2 hilos. 1 coge id y luego mandar a llenar tabla
        GetObjectsByIdsTask getObjectsByIdsTask = new GetObjectsByIdsTask(idObjects,titleObjectsFromDepartment,tableObjects);
        new Thread(getObjectsByIdsTask).start();
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
        service.getAllDeparments().subscribe(dep); //TODO llevar a Thread
    }






}

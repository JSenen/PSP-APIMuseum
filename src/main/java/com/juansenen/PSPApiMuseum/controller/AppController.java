package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import com.juansenen.PSPApiMuseum.task.ObjectsByDepartmentTask;
import com.juansenen.PSPApiMuseum.task.TotalObjectTask;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
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
    public ObservableList<String> titleObjectsFromDepartment;
    @FXML
    public ComboBox<Integer> comboIDfromDepar;
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

    public void prepareTableDepartment(){

        TableColumn<Department, Integer> idColumn = new TableColumn<>("Id");        //Crear columna
        idColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));   //Asociar a campo clase
        TableColumn<Department, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(idColumn);   //Datos a la columna
        tableMain.getColumns().add(nameColumn);

    }
    public void prepareTableObjects(){

        TableColumn<ObjectsByID, String> titleColumn = new TableColumn<>("TITLE");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        tableObjects.getColumns().add(titleColumn); //Dato titulo
    }

    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }

    @FXML
    public void tableMouseClickItem(MouseEvent event) {      //Pulsar 1 veces sobre un elemento de la tabla

        if (event.getClickCount() == 2) {
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();

            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            getTotalObjectsByDepartment();
            System.out.println(IDitemSelected); //TODO Borrar tras pruebas

            // TODO Only for test purpose
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Ha seleccionado " + selectedDepartment.getDisplayName());
            alert.show();
        }
    }
    private void getTotalObjectsByDepartment() {
        /** BUSCAMOS EL TOTAL DE OBJETOS DEL DEPARTAMENTO*/
        MetService service = new MetService();
        Consumer<ObjectsMain> deparObjs = (info) ->{
            txtTotal.setText(String.valueOf(info.getTotal()));
            idObjects.addAll(info.getObjectIDs());
            System.out.println(idObjects);
            getObjectstoTable(idObjects);

        };
        service.getObjectByIdforDepart(IDitemSelected,cadena).subscribe(deparObjs);

    }
    public void getObjectstoTable(List<Integer> idObjects){ //TODO Llenar tabla Objetos
        MetService service = new MetService();
        titleObjectsFromDepartment = FXCollections.observableArrayList();
        for (Integer ids: idObjects){
            Consumer<ObjectsByID> obj = (info) -> {
                if (info.getTitle().equals("")){     //La Api en ocasiones no asigna titulo a una obra
                    title.add("No Title");
                }else{
                    title.add(info.getTitle());
                }
                for(String ti:title){
                    System.out.println(ti);
                }
            };
            service.getObjectById(ids).subscribe(obj);
        }
        titleObjectsFromDepartment.addAll(title);



    }


    private void getObjectsDataByDepartment(ActionEvent event){
//        MetService service = new MetService();
//        ObservableList<Integer> ids = FXCollections.observableList(idObjects) ; // nuestro ObservableArray de ids
//        ids.forEach((getObj) -> {                               // recorremos el ObservableList y recueramos titulos de obras
//            Consumer<ObjectsByID> objectsTitle = (info) -> {
//                System.out.println(info.getTitle());
//            };
//            service.getObjectById(getObj).subscribe(objectsTitle);
//        });
        for(Integer ids: idObjects){
            System.out.println(ids);
        }
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




}

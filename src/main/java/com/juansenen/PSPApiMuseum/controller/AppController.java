package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.task.GetIDsFromDepartmentTask;
import com.juansenen.PSPApiMuseum.task.GetObjectsByIdsTask;
import com.juansenen.PSPApiMuseum.task.GetTotalDepartmentTask;
import com.juansenen.PSPApiMuseum.task.TotalObjectTask;
import io.reactivex.functions.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    public Button buttonLoad;
    @FXML
    public TextField textFieldSearch; //Se usa para especificar en las busquedas de objetos
    @FXML
    public ProgressIndicator progressIndicator;
    @FXML
    public Text txtTotal,txtTotalDepart,messageDownload;
    public int partes;
    public List<Integer> idObjects = new ArrayList<>(); //Lista para guardar Ids de Objetos en memoria
    //Observable List para la table de Objetos por titulo
    @FXML
    public ObservableList<ObjectsByID> titleObjectsFromDepartment;
    @FXML
    public TableView<Department> tableMain = new TableView<>();
    @FXML
    public TableView<ObjectsByID> tableObjects;

    public ObjectsByID objectsByID;

    public int IDitemSelected;
    public String cadena;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        prepareTableDepartment(); //Inicializa la Tabla Departamentos
        prepareTableObjects();    //Inicializar tabla Objetos
        setTotalDeparments();     //Cargar los nombres de departamentos a tabla inicial.
        titleObjectsFromDepartment = FXCollections.observableArrayList();
        progressIndicator.setVisible(true);
    }


    /** Tabla Departamentos **/
    public void prepareTableDepartment(){

        TableColumn<Department, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(nameColumn);

    }
    /** Tabla Titulos de Obras **/
    public void prepareTableObjects(){

        TableColumn<ObjectsByID, String> titleColumn = new TableColumn<>("Titulo");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));


        tableObjects.getColumns().addAll(titleColumn);

    }
    /** Boton Objetos totales en el Museo **/
    @FXML
    public void loadTotalObjects (ActionEvent actionEvent){   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }
    /**Acción seleccionar un Departamento en la tabla Departamentos **/
    @FXML
    public void tableDepartmentClickItem(MouseEvent event) {

        if (event.getClickCount() == 2) {//Pulsar 2 veces sobre un elemento de la tabla para elegir departamento
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();
            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            cadena = textFieldSearch.getText();
            if (cadena == null || textFieldSearch.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Información");
                alert.setHeaderText("Mensaje de información");
                alert.setContentText("Debe introducir una palabra de filtrado");
                alert.showAndWait();
            }
            titleObjectsFromDepartment.clear();
            getTotalObjectsByDepartment();
        }

    }
    @FXML
    public void tableObjectClickItem(MouseEvent event) throws IOException {
        objectsByID = new ObjectsByID();
        if (event.getClickCount() == 2) {//Pulsar 2 veces sobre un elemento de la tabla para elegir objeto
            objectsByID = tableObjects.getSelectionModel().getSelectedItem();

            launchScreen(objectsByID);
        }


    }
    /** Lanzamos ventana independiente para mostrar detalles del objeto **/
    private void launchScreen(ObjectsByID objectsByID) {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ObjectScreen.fxml"));

            ObjectScreenController objectScreenController = new ObjectScreenController(this.objectsByID);
            loader.setController(objectScreenController);
            AnchorPane anchorPane = loader.load();
            Scene scene = new Scene(anchorPane);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Museum");
            stage.show();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /** Buscar Total de Ids Objetos que corresponden al Departamento*/
    private void getTotalObjectsByDepartment() {

        idObjects.clear();

        Consumer<ObjectsMain> deparObjs = (info) ->{
            txtTotalDepart.setText(String.valueOf(info.getTotal()));
            txtTotalDepart.setFill(Color.GREEN);
            idObjects.addAll(info.getObjectIDs()); //Añadimos a la lista los IDs de los objetos del departamento
            getObjectstoTable(idObjects);
            textFieldSearch.setText("");

        };
        GetIDsFromDepartmentTask getIDsFromDepartmentTask = new GetIDsFromDepartmentTask(IDitemSelected,cadena,deparObjs,
                progressIndicator);
        new Thread(getIDsFromDepartmentTask).start();
    }
    /** Recuperar los datos individuales de cada objeto por su Id **/
    public void getObjectstoTable(List<Integer> idObjects){

        GetObjectsByIdsTask getObjectsByIdsTask = new GetObjectsByIdsTask(idObjects,titleObjectsFromDepartment,
                tableObjects,progressIndicator,messageDownload);
        new Thread(getObjectsByIdsTask).start();

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

        Consumer<List<Department>> dep = (info) -> {
            tableMain.setItems(FXCollections.observableArrayList(info));
            progressIndicator.setVisible(false);
        };
        GetTotalDepartmentTask getTotalDepartmentTask = new GetTotalDepartmentTask(dep,progressIndicator);
        new Thread(getTotalDepartmentTask).start();
    }
    private void openObjectScreen(int iDObjectSelected)  {


    }


}

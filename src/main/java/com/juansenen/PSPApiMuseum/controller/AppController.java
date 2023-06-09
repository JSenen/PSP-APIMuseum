package com.juansenen.PSPApiMuseum.controller;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.task.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppController implements Initializable {

    @FXML
    public Button buttonLoad;
    @FXML
    public Button btDelete;
    @FXML
    public Button butCSV;
    @FXML
    public Button butZIP;
    @FXML
    public TextField textFieldSearch; //Se usa para especificar en las busquedas de objetos
    @FXML
    public TextField txtFieldDelete; //Se usa para seleccionar un elemento del Text Area
    @FXML
    public ProgressIndicator progressIndicator; //Indica la carga en segundo plano
    @FXML
    public Text txtTotal, txtTotalDepart, messageDownload, txtZIPMade;
    public List<Integer> idObjects = new ArrayList<>(); //Lista para guardar Ids de Objetos en memoria
    //Observable List para la table de Objetos por titulo
    @FXML
    public ObservableList<ObjectsByID> titleObjectsFromDepartment;
    @FXML
    public TableView<Department> tableMain = new TableView<>();
    @FXML
    public TableView<ObjectsByID> tableObjects;
    @FXML
    public TextArea tAreaObejtosList;
    @FXML
    public ImageView imgLogo;

    public List<String> listObjectToTextArea;
    public List<String> objetosAlist;
    public String path = System.getProperty("user.dir"); //Variable ruta del usuario
    public ObjectsByID objectsByID;
    public int IDitemSelected;
    public String cadena;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        prepareTableDepartment(); //Inicializa la Tabla Departamentos
        prepareTableObjects();    //Inicializar tabla Objetos
        setTotalDeparments();     //Cargar los nombres de departamentos a tabla inicial.

        titleObjectsFromDepartment = FXCollections.observableArrayList();
        listObjectToTextArea = new ArrayList<>();
        progressIndicator.setVisible(true);
    }

    /**
     * Tabla Departamentos
     **/
    public void prepareTableDepartment() {

        TableColumn<Department, String> nameColumn = new TableColumn<>("Departamento");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(nameColumn);
    }

    /**
     * Tabla Titulos de Obras
     **/
    public void prepareTableObjects() {

        TableColumn<ObjectsByID, String> titleColumn = new TableColumn<>("Titulo");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        tableObjects.getColumns().addAll(titleColumn);

    }

    /**
     * Boton Objetos totales en el Museo
     **/
    @FXML
    public void loadTotalObjects(ActionEvent actionEvent) {   //Boton pulsar para conocer número total de objetos
        setTotalNumberObjects();

    }

    /**
     * Acción seleccionar un Departamento en la tabla Departamentos
     **/
    @FXML
    public void tableDepartmentClickItem(MouseEvent event) {

        if (event.getClickCount() == 2) {//Pulsar 2 veces sobre un elemento de la tabla para elegir departamento
            Department selectedDepartment = tableMain.getSelectionModel().getSelectedItem();
            IDitemSelected = selectedDepartment.getDepartmentId();  //Obtenemos Id del Departamento
            cadena = textFieldSearch.getText();
            if (cadena == null || textFieldSearch.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Información");
                alert.setHeaderText("Mensaje de información");
                alert.setContentText("Debe introducir una palabra de filtrado");
                alert.showAndWait();
            }
            titleObjectsFromDepartment.clear();
            textFieldSearch.setText("");
            getTotalObjectsByDepartment();
        }
    }

    /**
     * Accion seleccionar un objeto de la tabla de objetos
     * añade tambien el objeto al listado de consultados
     **/
    @FXML
    public void tableObjectClickItem(MouseEvent event) throws IOException {
        objectsByID = new ObjectsByID();

        if (event.getClickCount() == 2) {//Pulsar 2 veces sobre un elemento de la tabla para elegir objeto
            //Recuperamos elemento
            objectsByID = tableObjects.getSelectionModel().getSelectedItem();

            //Añadimos el elemento a la lista
            listObjectToTextArea.add("---> TITULO: " + objectsByID.getTitle() + " --ARTISTA: " + objectsByID.getArtistDisplayName() +
                    " --FECHA: " + objectsByID.getObjectDate() + " --PAIS: " + objectsByID.getCountry());
            //Hilo para confeccionar el listado del TextArea
            ListObjectsTask listObjectsTask = new ListObjectsTask(listObjectToTextArea, tAreaObejtosList);
            new Thread(listObjectsTask).start();

            //Abrimos segunda ventana
            launchScreen(objectsByID);
        }
    }

    /**
     * Lanzamos ventana independiente para mostrar detalles del objeto
     **/
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
            stage.setResizable(false);
            stage.show();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Buscar Total de Ids Objetos que corresponden al Departamento
     */
    private void getTotalObjectsByDepartment() {

        idObjects.clear();
        GetIDsFromDepartmentTask getIDsFromDepartmentTask = new GetIDsFromDepartmentTask(IDitemSelected, cadena,
                progressIndicator, textFieldSearch, txtTotalDepart);
        getIDsFromDepartmentTask.setOnSucceeded(event -> {
            idObjects = getIDsFromDepartmentTask.getValue();
            getObjectstoTable(idObjects);
        });
        new Thread(getIDsFromDepartmentTask).start();
    }

    /**
     * Recuperar los datos individuales de cada objeto por su Id
     **/
    public void getObjectstoTable(List<Integer> idObjects) {

        GetObjectsByIdsTask getObjectsByIdsTask = new GetObjectsByIdsTask(idObjects, titleObjectsFromDepartment,
                tableObjects, progressIndicator, messageDownload);
        new Thread(getObjectsByIdsTask).start();

    }

    public void setTotalNumberObjects() {
        txtTotal.setText("Cargando...");
        TotalObjectTask totalObjectTask = new TotalObjectTask(txtTotal);  //Creamos un hilo
        new Thread(totalObjectTask).start();                             // Ejecutamos hilo
    }

    public void setTotalDeparments() {

        GetTotalDepartmentTask getTotalDepartmentTask = new GetTotalDepartmentTask(tableMain, progressIndicator);
        new Thread(getTotalDepartmentTask).start();
    }

    /**
     * Borrar un elemento de la lista de objetos vistos
     **/
    @FXML
    public void delObject(ActionEvent event) {

        //Recuperamos numero introducido
        int indexObject = Integer.parseInt(txtFieldDelete.getText());
        if (indexObject <= listObjectToTextArea.size()) {
            //Eliminamos el objeto seleccionado
            listObjectToTextArea.remove(indexObject);
            ListObjectsTask listObjectsTask = new ListObjectsTask(listObjectToTextArea, tAreaObejtosList);
            new Thread(listObjectsTask).start();
        }
    }

    /**
     * Botón crear archivo CSV
     **/
    @FXML
    public void madeCSV(ActionEvent event) {
        crearCSV(listObjectToTextArea);
    }

    /**
     * Metodo crear CSV
     */
    private void crearCSV(List<String> listaobjetos) {
        CSVCreatorTask task = new CSVCreatorTask(listaobjetos, txtZIPMade);
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Este código crea un objeto ZipOutputStream que se usa para crear el archivo ZIP
     * y agregar el archivo CSV al archivo ZIP.
     * El proceso de compresión se ejecuta en segundo plano utilizando un CompletableFuture.
     * Cuando la compresión se completa con éxito, se muestra un mensaje.
     * Si ocurre alguna excepción, se maneja imprimiendo el rastro de la pila en la consola..
     */
    @FXML
    public void zipFileCSV(ActionEvent event) {
        generateListObjects(); //Generamos el listado

        /** CompletableFuture es clase que representa el resultado futuro
         * de una operación asíncrona ; ejecuta tarea en paralelo y esperar su resultado
         */
        CompletableFuture.runAsync(() -> {
            //Creamos el archivo csv
            crearCSV(listObjectToTextArea);
            //buscamos en el directorio que le indica el archivo terminado en datoscsv.csv
            File directorio = new File(path);
            File[] archivos = directorio.listFiles((dir, nombre) -> nombre.endsWith("datoscsv.csv"));
            //Comprobamos si el archivo csv ya se creo y si no lo crearemos
            File csvFile = new File("datoscsv.csv");
            if (!csvFile.exists()) {
                try {
                    csvFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String nombreArchivoZip = "objectsList.zip";
            try {
                // Crear un archivo de salida ZIP
                FileOutputStream fos = new FileOutputStream(nombreArchivoZip);
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                // Agregar un archivo CSV al archivo ZIP

                File fileToZip = new File("datoscsv.csv");
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                // Cerrar los streams
                zipOut.closeEntry();
                fis.close();
                zipOut.close();

                // Mostrar mensaje de éxito
                txtZIPMade.setText("ZIP Creado ..");
            } catch (IOException e) {
                // Manejar la excepción
                e.printStackTrace();
            }
        });
    }

    public void generateListObjects() {

        //Pasamos los datos de la ObservableList a una List de String
        objetosAlist = new ArrayList<>();
        for (ObjectsByID objects : tableObjects.getItems()) {
            objetosAlist.add(objects.toString());
        }
        //Creamos CSV con los datos recogidos de la tabla objetos
        crearCSV(objetosAlist);
    }
}

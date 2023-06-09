### PSP API Museum
***
Actividad de aprendizaje 2ª Evaluación Programación Servicios y Procesos
***
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFx-ED8B00?style=for-the-badge&logo=javaFXlogoColor=white)
![RxJava](https://img.shields.io/badge/RXJava-a032a8?style=for-the-badge&logo=RxJavalogoColor=white)
![Retrofit2](https://img.shields.io/badge/Retrofit2-7dcf55?style=for-the-badge&logo=Retrofit2Color=white)
***

Aplicación que obtiene datos de la Api que ofrece el Museo Metropolitano de New York (https://metmuseum.github.io).
Por medio de la que se obtienen datos para mostrar en detalle los objetos de los que dispone, filtrados por
departamentos.


Requisitos

✅● La aplicación deberá utilizar técnicas de programación reactiva utilizando la librería RxJava en algún momento
Para todas las operaciones de la aplicación, se ha seguido un modelo Observable de eventos utilizando la libreria RxJava

```
        <dependency>
            <groupId>io.reactivex.rxjava2</groupId>
            <artifactId>rxjava</artifactId>
            <version>2.2.21</version>
        </dependency>`
```


✅● Se mostrará un listado de datos utilizando dos operaciones diferentes de la API.
En la pantalla inicial se muestran 2 tablas, en una de ellas el listado de Departamentos que nos ofrece la api y que
se carga desde el inicio de la aplicación. 
```
   @GET("departments")
    Observable<Deparments> loadDeparments(); /** Obtener los departamentos con Id y Nombre*/
```
Tras seleccionar uno de los departamentos, la api solo nos ofrece un listado de Ids de objetos, por lo que tendremos
que recoger primero esas Ids,
```
@GET("objects")
    Observable<ObjectsMain> loadObjects();    /** Obtener listado objetos Total y Ids todos los objetos */
```
Y despues pasar esos Ids a otra consulta en la Api para obtener los objetos
```
@GET("objects/{objectId}")
    Observable<ObjectsByID> loadOneObject(@Path("objectId") int objectId);  
    /** Busqueda de todos los datos de un objeto por su Id */
```
Se ha usado otra petición a la API, para poder buscar filtrando los resultados por un tema, palabra, etc

```
@GET("search")
    Observable<ObjectsMain> loadObjectsByDepartment(@Query("departmentId") int departmentIds, @Query("q") String cadena)
```

✅● Se mostrará información detallada de los items de los dos listados anteriores.
Del listado de departaemntos ofrecemos los datos de nombre y total de objetos de los que dispone
```
    /**
     * Tabla Departamentos
     **/
    public void prepareTableDepartment() {

        TableColumn<Department, String> nameColumn = new TableColumn<>("Departamento");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        tableMain.getColumns().add(nameColumn);

    }
```
Del listado de objetos, podemos seleccionar cualquiera de la tabla con doble click y se nos abrira una ventana en detalle de ese objeto
```
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
```


✅● Todas las operaciones de carga de datos se harán en segundo plano y se mostrará una barra de progreso o similar 
al usuario
Para todas las operaciones que se efectuan en segundo plano, el usuario puede observar un texto que le indica la carga 
de datos así como un indicador visual de la libreria JavaFX.
```
progressIndicator.setVisible(true);
```

En el caso de la carga de los objetos de un departamento, si que se puede observar el porcentaje de lo que se lleva descargado

```
 updateProgress(counterprogress,idObjects.size()); //Actualizamos el estado del progreso
                double progress = counterprogress / idObjects.size(); // calcular el progreso como un porcentaje
                progressIndicator.setProgress(progress); // actualizar el valor del progreso en el ProgressIndicator
                counterprogress++;

```

✅● Incorporar alguna operación de búsqueda o filtrado sobre los datos cargados de la API 
(búsqueda o filtrado que se hará desde la aplicación JavaFX, diferentes a las opciones de filtrado que permita la API)
Los objetos seleccionados que vamos seleccionando para ver los detalles, se van agrupando en memoria por medio de una Lista que nos muestra 
los campos básicos en un text-area. El usuario puede ver ese listado o borrar el que no le interese. Para lo que no usa la
api en ningún momento.
Recogemos en la lista código:
```
 //Recuperamos elemento
            objectsByID = tableObjects.getSelectionModel().getSelectedItem();

            //Añadimos el elemento a la lista
            listObjectToTextArea.add("---> TITULO: " + objectsByID.getTitle() + " --ARTISTA: " + objectsByID.getArtistDisplayName() +
                    " --FECHA: " + objectsByID.getObjectDate() + " --PAIS: " + objectsByID.getCountry());
```
Borramos de la lista codigo:
```
 @FXML
    public void delObject(ActionEvent event) {

        //Recuperamos numero introducido
        int indexObject = Integer.parseInt(txtFieldDelete.getText());
        if (indexObject <= listObjectToTextArea.size()){
            //Eliminamos el objeto seleccionado
            listObjectToTextArea.remove(indexObject);
            ListObjectsTask listObjectsTask = new ListObjectsTask(listObjectToTextArea, tAreaObejtosList);
            new Thread(listObjectsTask).start();
        }

    }
```



Otras funcionalidades


✅● Cargar algún tipo de contenido gráfico a partir de información dada por la API (una foto, por ejemplo)
En la pantalla que se ha creado al efecto para mostrar los detalles del objeto seleccionado, se ha añadido un campo
ImageView de la libreria JAvaFX, para poder mostrar la imagen (de tenerla) que nos ofrece la API por medio de un
enlace a una página url.
```
 //Descargamos la imagen de la Url proporcionada por la Api
        try {
            URL url = new URL(objectsByID.getPrimaryImageSmall());
            if (url.getPath().isEmpty() || url.getPath().isBlank() || url.getPath().equals("")) {
                txtProgressionImg.setText("SIN IMAGEN");
                progressionImg.setVisible(false);
            } else {
                txtProgressionImg.setText("Cargando imagen..");
                txtProgressionImg.setFill(Color.BLUE);
                progressionImg.setVisible(true);
            }
            //Creamos un Thread para su carga
            GetImagenTask getImagenTask = new GetImagenTask(url, progressionImg, txtProgressionImg);
            new Thread(getImagenTask).start();

            showImageFromUrl(url.toString());

        } catch (Exception e) {
            System.out.println("Error al descargar la URL: " + e.getMessage());
        }
```
✅● Permitir la exportación del contenido a un fichero CSV
El usuario puede exportar el contenido que se ha ido almacenando en la List de memporía a un fichero csv que se crea
en el directorio del mismo
Para ello se ha usado la libreria opencsv, añadiendo la dependencia al pom
```
  <!-- Creacion de CSV -->
        <dependency>
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>5.7.1</version>
        </dependency>
```
```
private void crearCSV(){

        //Damos nombre al archivo con la fecha a actual delante
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String fecha = dateFormat.format(new Date());
        String nombreArchivo = fecha + "datoscsv.csv";

        File file = new File(nombreArchivo);
        try (FileWriter writer = new FileWriter(file);
             CSVWriter csvWriter = new CSVWriter(writer, ',', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // Escribir los elementos de la lista en el archivo CSV
            for (String elemento : listObjectToTextArea) {
                csvWriter.writeNext(new String[] { elemento });
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("CSV");
            alert.setContentText("Archivo CSV Creado");
            alert.initStyle(StageStyle.UTILITY); //Sin botones de cierre
            //Damos 3 segundos para que el usuario vea el mensaje mergente
            GenericTask genericTask = new GenericTask(alert);
            new Thread(genericTask).start();
            alert.showAndWait();

        } catch (IOException e) {
            txtZIPMade.setText("Error  CSV: " + e.getMessage());
        }

    }
```


✅● Implementar una funcionalidad que permite exportar algún listado (devuelto por
alguna operación de la API) a un CSV y se comprima en zip (La idea es implementarlo usando CompletableFuture). 
Se ha creado un boton, en el que al pulsarlo. El programa solicita a la api un listado de los titulos y otros datos 
perteneciente a los objetos que tenemos en el listado del departamento. Y nos comprime un fichero zip con un csv del 
listado solicitado

```
 @FXML
    public void zipFileCSV(ActionEvent event){
        generateListObjects(); //Generamos el listado

        CompletableFuture.runAsync(() -> {


            //buscamos en el directorio que le indica el archivo terminado en datoscsv.csv
            File directorio = new File(path);
            File[] archivos = directorio.listFiles((dir, nombre) -> nombre.endsWith("datoscsv.csv"));

            String nombreArchivoZip = "objectsList.zip";
            try {
                // Crear un archivo de salida ZIP
                FileOutputStream fos = new FileOutputStream(nombreArchivoZip);
                ZipOutputStream zipOut = new ZipOutputStream(fos);

                // Agregar un archivo CSV al archivo ZIP

                File fileToZip = new File("datosCSV.csv");
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

```


● Crea, utilizando WebFlux, un pequeño servicio web relacionado con la API seleccionada y consúmelo desde alguna zona de la aplicación JavaFX utilizando WebClient


✅●Utiliza correctamente la clase ObservableList de JavaFX para la visualización de los contenidos en los diferentes controles de JavaFX que decidas utilizar (ComboBox, TableView, ListView, . . .)
Para la viualización de las tablas usadas en JAvaFX, se ha usado ObservableList


✅● Realizar el seguimiento del proyecto utilizando la plataforma GitHub 

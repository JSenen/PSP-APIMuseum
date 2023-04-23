package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GetObjectsByIdsTask extends Task<ObjectsByID> {
    private MetService service;
    private List<Integer> idObjects;
    private Consumer<ObjectsByID> user;
    private ObservableList<ObjectsByID> titleObjectsFromDepartment;
    private TableView<ObjectsByID> tableObjects;;
    private ProgressIndicator progressIndicator;
    private Text messageDownload;
    private double counterprogress;

    private int ids;
    public GetObjectsByIdsTask(List<Integer> idObjects,
                               ObservableList <ObjectsByID> titleObjectsFromDepartment,
                               TableView<ObjectsByID> tableObjects,
                               ProgressIndicator progressIndicator,
                               Text messageDownload) {

        this.idObjects = idObjects;
        this.titleObjectsFromDepartment = titleObjectsFromDepartment;
        this.tableObjects = tableObjects;
        this.progressIndicator = progressIndicator;
        this.messageDownload = messageDownload;
        this.counterprogress = 0;

    }

    @Override
    protected ObjectsByID call() throws Exception {
        service = new MetService();
        progressIndicator.setVisible(true);
        progressIndicator.setPrefSize(150, 150); // Tamaño en píxeles
        progressIndicator.setProgress(0.0); //Inicializamos el Progress Indicator
        messageDownload.setText("Cargando ...");

        // Lista temporal para ir agregando los objetos según se vayan recibiendo
        List<ObjectsByID> tempObjectsList = new ArrayList<>();

        for (Integer ids: idObjects){       //Recorremos el List de Ids que pertenecen al Departamento

            Consumer<ObjectsByID> obj = (info) -> {

                // Agregar el objeto a la lista temporal
                tempObjectsList.add(new ObjectsByID(info.getObjectID(), info.getAccessionNumber(),
                        info.getAccessionYear(), info.isPublicDomain(), info.getPrimaryImage(), info.getPrimaryImageSmall(),
                        info.getTitle(), info.getCountry(), info.getCulture(), info.getPeriod(), info.getArtistDisplayName(),
                        info.getArtistDisplayBio(), info.getArtistNationality(), info.getObjectDate(), info.getMedium(),
                        info.getDimensions()));

                // Actualizar la tabla después de agregar cada objeto a la lista temporal
                Platform.runLater(() -> {
                    tableObjects.setItems(FXCollections.observableArrayList(tempObjectsList));
                });

                updateProgress(counterprogress,idObjects.size()); //Actualizamos el estado del progreso
                double progress = counterprogress / idObjects.size(); // calcular el progreso como un porcentaje
                progressIndicator.setProgress(progress); // actualizar el valor del progreso en el ProgressIndicator
                counterprogress++;


            };
            service.getObjectById(ids).subscribe(obj);

            // Agregar los objetos descargados a la lista final
            titleObjectsFromDepartment.addAll(tempObjectsList);


        }
        //tableObjects.setItems(FXCollections.observableArrayList(titleObjectsFromDepartment));
        progressIndicator.setVisible(false);
        messageDownload.setText("");

        return null;
    }
}

package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

import java.util.List;

public class GetObjectsByIdsTask extends Task<ObjectsByID> {
    private MetService service;
    private List<Integer> idObjects;
    private Consumer<ObjectsByID> user;
    private ObservableList<ObjectsByID> titleObjectsFromDepartment;
    private TableView<ObjectsByID> tableObjects;;
    private ProgressIndicator progressIndicator;

    private int ids;
    public GetObjectsByIdsTask(List<Integer> idObjects,
                               ObservableList <ObjectsByID> titleObjectsFromDepartment,
                               TableView<ObjectsByID> tableObjects,
                               ProgressIndicator progressIndicator) {

        this.idObjects = idObjects;
        this.titleObjectsFromDepartment = titleObjectsFromDepartment;
        this.tableObjects = tableObjects;
        this.progressIndicator = progressIndicator;

    }

    @Override
    protected ObjectsByID call() throws Exception {
        service = new MetService();
        progressIndicator.setVisible(true);
        for (Integer ids: idObjects){       //Recorremos el List de Ids que pertenecen al Departamento

            Consumer<ObjectsByID> obj = (info) -> {

                titleObjectsFromDepartment.add(new ObjectsByID(info.getObjectID(), info.getAccessionNumber(),
                        info.getAccessionYear(),info.isPublicDomain(), info.getPrimaryImage(),
                        info.getTitle(),info.getCountry()));


            };
            service.getObjectById(ids).subscribe(obj);

        }
        tableObjects.setItems(FXCollections.observableArrayList(titleObjectsFromDepartment));
        progressIndicator.setVisible(false);

        return null;
    }
}

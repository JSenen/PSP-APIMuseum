package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

import java.util.List;

public class GetTotalDepartmentTask extends Task<List<Department>> {

    private MetService service;
    private ProgressIndicator progressIndicator;
    private TableView tableMain;

    public GetTotalDepartmentTask(TableView tableMain, ProgressIndicator progressIndicator) {
        this.tableMain = tableMain;
        this.progressIndicator = progressIndicator;
    }

    @Override
    protected List<Department> call() throws Exception {

        Consumer<List<Department>> dep = (info) -> {

            progressIndicator.setVisible(false);
            Platform.runLater(() -> tableMain.setItems(FXCollections.observableArrayList(info)));

        };
        service = new MetService();
        service.getAllDeparments().subscribe(dep);
        return null;
    }



}

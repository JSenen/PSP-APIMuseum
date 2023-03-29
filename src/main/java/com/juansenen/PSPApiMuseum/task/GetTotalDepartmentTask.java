package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.Department;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

public class GetTotalDepartmentTask extends Task<List<Department>> {

    private Consumer<List<Department>> user;
    private MetService service;
    private ProgressIndicator progressIndicator;

    public GetTotalDepartmentTask(Consumer<List<Department>> user, ProgressIndicator progressIndicator) {
        this.user = user;
        this.progressIndicator = progressIndicator;
    }

    @Override
    protected List<Department> call() throws Exception {
        service = new MetService();
        service.getAllDeparments().subscribe(user);
        return null;
    }



}

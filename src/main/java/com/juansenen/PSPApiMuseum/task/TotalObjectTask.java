package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;

public class TotalObjectTask extends Task<ObjectsMain> {
    public Consumer<ObjectsMain> totalObj;

    public TotalObjectTask(Consumer<ObjectsMain> totalObj){
        this.totalObj = totalObj;
    }
    @Override
    protected ObjectsMain call() throws Exception {
        MetService metService = new MetService();
        metService.getTotalObjects().subscribe(totalObj);
        return null;
    }
}

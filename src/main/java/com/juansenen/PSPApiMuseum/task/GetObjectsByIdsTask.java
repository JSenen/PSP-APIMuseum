package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.concurrent.Task;

public class GetObjectsByIdsTask extends Task<ObjectsByID> {
    private MetService service;
    private Consumer<ObjectsByID> user;
    private int ids;
    public GetObjectsByIdsTask(int ids, Consumer<ObjectsByID> user) {
        this.ids = ids;
        this.user = user;
    }

    @Override
    protected ObjectsByID call() throws Exception {
        service = new MetService();
        service.getObjectById(ids).subscribe(user);
        return null;
    }
}

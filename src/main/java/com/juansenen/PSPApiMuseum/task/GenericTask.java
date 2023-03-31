package com.juansenen.PSPApiMuseum.task;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class GenericTask extends Task<Alert> {
    public Alert alert;
    public GenericTask(Alert alert){
        this.alert = alert;

    }
    @Override
    protected Alert call() throws Exception {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void succeeded() {
        alert.hide();
    }
}

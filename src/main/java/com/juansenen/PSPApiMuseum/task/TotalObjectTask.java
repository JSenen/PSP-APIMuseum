package com.juansenen.PSPApiMuseum.task;

import com.juansenen.PSPApiMuseum.domain.ObjectsMain;
import com.juansenen.PSPApiMuseum.service.MetService;
import io.reactivex.functions.Consumer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.Text;


public class TotalObjectTask extends Task<ObjectsMain> {
    public Consumer<ObjectsMain> totalObj;
    public MetService api;

    public Text txtTotal;


    public TotalObjectTask(Text txtTotal){
        this.txtTotal = txtTotal;
    }
    @Override
    protected ObjectsMain call() throws Exception {
        Consumer<ObjectsMain> totalObj = (info) -> {

            /** En este código, estamos utilizando Platform.runLater() para actualizar el texto
             * de txtTotal en el hilo de la interfaz de usuario, lo que evitará cualquier error relacionado con la actualización
             *  de la interfaz de usuario desde un hilo
             */

            Platform.runLater(()-> {
                txtTotal.setText(String.valueOf(info.getTotal()));
            });
        };

        api = new MetService();
        api.getTotalObjects().subscribe(totalObj); //Suscripcion del observador
        return null;
    }
}

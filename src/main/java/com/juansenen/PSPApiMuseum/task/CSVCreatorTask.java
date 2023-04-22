package com.juansenen.PSPApiMuseum.task;

import com.opencsv.CSVWriter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CSVCreatorTask extends Task<Void> {

    private List<String> listaobjetos;
    private Text txtZIPMade;

    public CSVCreatorTask(List<String> listaobjetos, Text txtZIPMade) {
        this.listaobjetos = listaobjetos;
        this.txtZIPMade = txtZIPMade;
    }

    @Override
    protected Void call() throws Exception {
        //Damos nombre al archivo con la fecha a actual delante
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String fecha = dateFormat.format(new Date());
        String nombreArchivo = "datoscsv.csv";

        File file = new File(nombreArchivo);
        try (FileWriter writer = new FileWriter(file);
             CSVWriter csvWriter = new CSVWriter(writer, ',', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

            // Escribir los elementos de la lista en el archivo CSV
            /** CompletableFuture es clase que representa el resultado futuro
             * de una operación asíncrona ; ejecuta tarea en paralelo y esperar su resultado
             */
            CompletableFuture.runAsync(() -> {
                for (String elemento : listaobjetos) {
                    csvWriter.writeNext(new String[]{elemento});
                }
            }).join();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("LISTADO");
                alert.setContentText("Creado archivo en "+System.getProperty("user.home")); //Directorio el usuario
                alert.initStyle(StageStyle.UTILITY); //Sin botones de cierre
                alert.showAndWait();
            });

        } catch (IOException e) {
            txtZIPMade.setText("Error  CSV: " + e.getMessage());
        }

        return null;
    }
}

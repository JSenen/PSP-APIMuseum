package com.juansenen.PSPApiMuseum.task;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetImagenTask extends Task<String> {
    private URL url;
    private ProgressIndicator progressIndicator;
    private Text txtProgressionImg;
    public GetImagenTask(URL url, ProgressIndicator progressIndicator, Text txtImgCharge){
        this.url = url;
        this.progressIndicator = progressIndicator;
        this.txtProgressionImg = txtImgCharge;

    }

    @Override
    protected String call() throws Exception {

        //Descargamos la imagen de la Url proporcionada por la Api
        try {
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            in.close();
        } catch (Exception e) {
            System.out.println("Error al descargar la URL: " + e.getMessage());
        }
        txtProgressionImg.setText("");
        progressIndicator.setVisible(false);
        return null;
    }
}

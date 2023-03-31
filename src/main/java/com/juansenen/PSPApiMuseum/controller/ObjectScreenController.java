package com.juansenen.PSPApiMuseum.controller;


import com.juansenen.PSPApiMuseum.domain.ObjectsByID;
import com.juansenen.PSPApiMuseum.task.GetImagenTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

public class ObjectScreenController implements Initializable {
    @FXML
    private Text txtTitle, txtAuthorName, txtBiography, txtMedium, txtDimensions, txtPeriod, txtCountry, txtNacionality,
            txtDate, txtAccesionYear, txtCulture, txtIdentificationNumber, txtProgressionImg;
    @FXML
    private CheckBox chkPublicDomain;
    @FXML
    private Pane paneImage;
    @FXML
    private ProgressIndicator progressionImg;


    private ObjectsByID objectsByID;

    public ObjectScreenController(ObjectsByID objectsByID) {
        this.objectsByID = objectsByID;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData(objectsByID);


    }

    public void initData(ObjectsByID selectedObject) {
        //Rellenamos campos de texto
        txtTitle.setText(objectsByID.getTitle());
        txtAuthorName.setText(objectsByID.getArtistDisplayName());
        txtBiography.setText(objectsByID.getArtistDisplayBio());
        txtMedium.setText(objectsByID.getMedium());
        txtDimensions.setText(objectsByID.getDimensions());
        txtPeriod.setText(objectsByID.getPeriod());
        txtCountry.setText(objectsByID.getCountry());
        txtNacionality.setText(objectsByID.getArtistNationality());
        txtDate.setText(objectsByID.getObjectDate());
        txtAccesionYear.setText(objectsByID.getAccessionYear());
        txtCulture.setText(objectsByID.getCulture());
        txtIdentificationNumber.setText(objectsByID.getAccessionNumber());
        chkPublicDomain.setSelected(objectsByID.isPublicDomain());
        chkPublicDomain.setDisable(true);

        //Descargamos la imagen de la Url proporcionada por la Api
        try {
            URL url = new URL(objectsByID.getPrimaryImageSmall());
            if (url.getPath().toString().isEmpty() || url.getPath().isBlank() || url.getPath().equals("")) {
                txtProgressionImg.setText("SIN IMAGEN");
                progressionImg.setVisible(false);
            } else {
                txtProgressionImg.setText("Cargando imagen..");
                txtProgressionImg.setFill(Color.BLUE);
                progressionImg.setVisible(true);
            }
            //Creamos un Thread para su carga
            GetImagenTask getImagenTask = new GetImagenTask(url, progressionImg, txtProgressionImg);
            new Thread(getImagenTask).start();

            showImageFromUrl(url.toString());

        } catch (Exception e) {
            System.out.println("Error al descargar la URL: " + e.getMessage());
        }

    }

    public void showImageFromUrl(String urlString) {

        // Creamos una instancia de ImageView y la agregamos al contenedor
        ImageView imageView = new ImageView();

        //Ajustamos imagen al Panel
        imageView.setFitWidth(311);
        imageView.setFitHeight(275);
        imageView.setPreserveRatio(true);

        paneImage.getChildren().add(imageView);

        // Cargar la imagen desde la URL y la establecemos en la instancia de ImageView
        Image image = new Image(urlString);
        imageView.setImage(image);
    }


}

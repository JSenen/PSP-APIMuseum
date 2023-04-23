package com.juansenen.PSPApiMuseum.task;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;


import java.util.List;

public class ListObjectsTask extends Task<String> {

    public List<String> listObjectToTextArea;
    public TextArea tAreaObejtosList;
    public ListObjectsTask(List<String> listObjectToTextArea,TextArea tAreaObejtosList){
        this.listObjectToTextArea = listObjectToTextArea;
        this.tAreaObejtosList = tAreaObejtosList;

    }
    @Override
    protected String call() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listObjectToTextArea.size(); i++) {
            String line = String.format("%d. %s", i, listObjectToTextArea.get(i));
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        tAreaObejtosList.setText(sb.toString());
        return null;
    }
}

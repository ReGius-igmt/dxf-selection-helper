package ru.regiuss.dxf.selection.helper.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ru.regiuss.dxf.selection.helper.model.TaskResult;

public class SuccessController {

    @FXML
    private Text copied;

    @FXML
    private TextArea files;

    @FXML
    private Text found;

    @FXML
    private Text noCopied;

    @FXML
    void onClick(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    public void init(TaskResult result) {
        found.setText(Integer.toString(result.getFound()));
        copied.setText(Integer.toString(result.getCopied()));
        noCopied.setText(Integer.toString(result.getFound() - result.getCopied()));
        files.setText(String.join("\n", result.getFiles()));
    }

}

package ru.regiuss.dxf.selection.helper.node;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.model.TaskResult;

@Log4j2
public class SuccessScreen {
    @FXML
    private Text copied;

    @FXML
    private TextArea files;

    @FXML
    private Text found;

    @FXML
    private Text noCopied;

    private Stage stage;

    @FXML
    void onClick(ActionEvent event) {
        stage.close();
    }

    public void open(Stage owner, TaskResult result) {
        if(stage != null) stage.close();
        stage = new Stage();
        stage.setTitle("Success");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        Parent p = open(result);
        if (p == null) return;
        stage.setScene(new Scene(p));
        stage.showAndWait();
    }

    public Parent open(TaskResult result) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/success.fxml"));
        loader.setController(this);
        try {
            Parent p = loader.load();
            init(result);
            return p;
        } catch (Exception e) {
            log.error("FXMLLoader success.fxml", e);
        }
        return null;
    }

    public void init(TaskResult result) {
        found.setText(Integer.toString(result.getFound()));
        copied.setText(Integer.toString(result.getCopied()));
        noCopied.setText(Integer.toString(result.getFound() - result.getCopied()));
        files.setText(String.join("\n", result.getFiles()));
    }
}

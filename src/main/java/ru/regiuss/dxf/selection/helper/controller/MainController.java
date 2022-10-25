package ru.regiuss.dxf.selection.helper.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Setter;
import ru.regiuss.dxf.selection.helper.App;
import ru.regiuss.dxf.selection.helper.SpecificationStorage;
import ru.regiuss.dxf.selection.helper.task.StartTask;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private CheckBox clearResultFolderCheckBox;

    @FXML
    private HBox listViewsBox;

    @FXML
    private ListView<String> opListView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField resultFolderField;

    @FXML
    private ListView<String> sizeListView;

    @FXML
    private TextField sourceFolderField;

    @FXML
    private TextField specificationFileField;

    @FXML
    private Text statusText;

    @FXML
    private ListView<String> templateListView;
    @Setter
    private App app;
    private StartTask startTask;

    @FXML
    void onBrowseResultFolder(ActionEvent event) {
        browseFolder(resultFolderField);
    }

    @FXML
    void onBrowseSourceFolder(ActionEvent event) {
        browseFolder(sourceFolderField);
    }

    @FXML
    void onBrowseSpecificationFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        if(!specificationFileField.getText().isEmpty()) {
            File initialDirectory = new File(specificationFileField.getText());
            if(initialDirectory.exists())
                chooser.setInitialDirectory(initialDirectory.isFile() ? initialDirectory.getParentFile() : initialDirectory);
        }
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXCEL", "*.xls", "*.xlsx"));
        File specificationFile = chooser.showOpenDialog(app.getStage());
        if(specificationFile != null) specificationFileField.setText(specificationFile.getAbsolutePath());
    }

    @FXML
    void onStart(ActionEvent event) {
        Button target = (Button) event.getTarget();
        if(startTask != null) {
            target.setText("Старт");
            target.getStyleClass().remove("danger");
            startTask.cancel(true);
            startTask = null;
            statusClear();
            return;
        }
        startTask = new StartTask(
                new HashSet<>(opListView.getSelectionModel().getSelectedItems()),
                new HashSet<>(templateListView.getSelectionModel().getSelectedItems()),
                new HashSet<>(sizeListView.getSelectionModel().getSelectedItems()),
                new File(specificationFileField.getText()),
                Paths.get(sourceFolderField.getText()),
                Paths.get(resultFolderField.getText()),
                clearResultFolderCheckBox.isSelected()
        );
        startTask.setOnSucceeded(workerStateEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Программа успешно завершила работу");
            alert.setTitle("Оповещение");
            alert.setHeaderText("Успех");
            alert.show();
            statusClear();
        });
        startTask.setOnFailed(workerStateEvent -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, startTask.getException().getMessage());
            alert.setTitle("Ошибка");
            alert.setHeaderText("Программа завершила работу с ошибкой");
            alert.show();
            statusClear();
        });
        progressBar.progressProperty().bind(startTask.progressProperty());
        statusText.textProperty().bind(startTask.messageProperty());
        app.getEs().execute(startTask);
    }

    private void statusClear() {
        progressBar.progressProperty().unbind();
        statusText.textProperty().unbind();
        progressBar.setProgress(0);
        statusText.setText("Статус");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        opListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        templateListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        sizeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        specificationFileField.textProperty().addListener(onSpecificationChange());
    }

    private ChangeListener<? super String> onSpecificationChange() {
        return (observableValue, s, t1) -> {
            if(t1 == null || t1.isEmpty()) return;
            if(t1.indexOf('.', t1.length() - 5) < 0) return;
            File f = new File(t1);
            if(!f.exists() && f.isDirectory()) return;
            listViewsBox.setDisable(true);
            SpecificationStorage storage = new SpecificationStorage(f);
            Task<Void> readTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    storage.read();
                    return null;
                }
            };
            readTask.setOnSucceeded(event -> {
                opListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getOp())));
                templateListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getTemplate())));
                sizeListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getSize())));
                listViewsBox.setDisable(false);
            });
            app.getEs().execute(readTask);
        };
    }

    private void browseFolder(TextField field) {
        DirectoryChooser chooser = new DirectoryChooser();
        if(!field.getText().isEmpty()) {
            File initialDirectory = new File(field.getText());
            if(initialDirectory.exists() && initialDirectory.isDirectory())
                chooser.setInitialDirectory(initialDirectory);
        }
        File resultFolder = chooser.showDialog(app.getStage());
        if(resultFolder != null) field.setText(resultFolder.getAbsolutePath());
    }
}

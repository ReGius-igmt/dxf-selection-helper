package ru.regiuss.dxf.selection.helper.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private CheckBox clearResultFolderCheckBox;

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
    private Stage stage;

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

    }

    @FXML
    void onStart(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialize");
    }

    private void browseFolder(TextField field) {
        DirectoryChooser chooser = new DirectoryChooser();
        if(!field.getText().isEmpty()) {
            File initialDirectory = new File(field.getText());
            if(initialDirectory.exists() && initialDirectory.isDirectory())
                chooser.setInitialDirectory(initialDirectory);
        }
        File resultFolder = chooser.showDialog(stage);
        if(resultFolder != null) field.setText(resultFolder.getAbsolutePath());
    }
}

package ru.regiuss.dxf.selection.helper.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.App;
import ru.regiuss.dxf.selection.helper.SpecificationStorage;
import ru.regiuss.dxf.selection.helper.model.Settings;
import ru.regiuss.dxf.selection.helper.model.TaskResult;
import ru.regiuss.dxf.selection.helper.node.SelectSpecification;
import ru.regiuss.dxf.selection.helper.task.StartTask;
import ru.regiuss.dxf.selection.helper.util.Utils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

@Log4j2
public class MainController implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private CheckBox clearResultFolderCheckBox;

    @FXML
    private CheckBox checkCountCheckBox;

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
    private App app;
    private StartTask startTask;
    private int[] indexes;

    @FXML
    void onBrowseResultFolder(ActionEvent event) {
        Utils.browseFolder(resultFolderField, ((Node)event.getTarget()).getScene().getWindow());
    }

    @FXML
    void onBrowseSourceFolder(ActionEvent event) {
        Utils.browseFolder(sourceFolderField, ((Node)event.getTarget()).getScene().getWindow());
    }

    @FXML
    void onBrowseSpecificationFile(ActionEvent event) {
        Utils.browseFile(specificationFileField, ((Node)event.getTarget()).getScene().getWindow());
    }

    @FXML
    void onStart(ActionEvent event) {
        Button target = (Button) event.getTarget();
        if(startTask != null) {
            startTask.cancel(true);
            statusClear(target);
            return;
        }
        try {
            for(Field field : getClass().getDeclaredFields()) {
                if(field.isAnnotationPresent(FXML.class) && field.getType().isAssignableFrom(TextField.class)) {
                    if(((TextField)field.get(this)).getText().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Заполните все поля");
                        alert.show();
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}
        target.setText("Стоп");
        target.getStyleClass().add("danger");

        Settings settings = getSettings();
        saveSettings(settings);

        startTask = new StartTask(settings);
        startTask.setOnSucceeded(workerStateEvent -> {
            TaskResult result = startTask.getValue();
            Stage stage = new Stage();
            stage.setTitle("Success");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/success.fxml"));
            try {
                Parent p = loader.load();
                SuccessController controller = loader.getController();
                controller.init(result);
                stage.setScene(new Scene(p));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(app.getStage());
                stage.showAndWait();
            } catch (Exception e) {
                log.error("load success fxml", e);
            }
            statusClear(target);
        });
        startTask.setOnFailed(workerStateEvent -> {
            log.error("start task error", startTask.getException());
            Alert alert = new Alert(Alert.AlertType.ERROR, startTask.getException().getMessage());
            alert.setTitle("Ошибка");
            alert.setHeaderText("Программа завершила работу с ошибкой");
            alert.show();
            statusClear(target);
        });
        progressBar.progressProperty().bind(startTask.progressProperty());
        statusText.textProperty().bind(startTask.messageProperty());
        app.getEs().execute(startTask);
    }

    private Settings getSettings() {
        Settings settings = new Settings();
        settings.setOp(new HashSet<>(opListView.getSelectionModel().getSelectedItems()));
        settings.setTemplate(new HashSet<>(templateListView.getSelectionModel().getSelectedItems()));
        settings.setSize(new HashSet<>(sizeListView.getSelectionModel().getSelectedItems()));
        settings.setSpecification(specificationFileField.getText());
        settings.setSource(sourceFolderField.getText());
        settings.setResult(resultFolderField.getText());
        settings.setClearResultFolder(clearResultFolderCheckBox.isSelected());
        settings.setCheckCount(checkCountCheckBox.isSelected());
        settings.setIndexes(indexes);
        return settings;
    }

    private void statusClear(Button button) {
        button.setText("Старт");
        button.getStyleClass().remove("danger");
        startTask = null;
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
        pane.setOnDragOver(event -> {
            if (event.getGestureSource() != pane
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            }
            event.consume();
        });
        pane.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            dragEvent.setDropCompleted(false);
            if(db.hasFiles()) {
                File file = db.getFiles().get(0);
                if(file.isFile()) specificationFileField.setText(file.getAbsolutePath());
                else {
                    if(dragEvent.getY() > pane.getHeight()/2) resultFolderField.setText(file.getAbsolutePath());
                    else sourceFolderField.setText(file.getAbsolutePath());
                }
                dragEvent.setDropCompleted(true);
            }
            dragEvent.consume();
        });
    }

    public void init(App app) {
        this.app = app;
        loadSettings();
    }

    private void loadListViews(File f, int[] indexes, Runnable onSuccess) {
        if(!f.exists() && f.isDirectory()) return;
        listViewsBox.setDisable(true);
        SpecificationStorage storage = new SpecificationStorage(f);
        Task<Void> readTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                storage.read(indexes);
                return null;
            }
        };
        readTask.setOnSucceeded(event -> {
            opListView.getSelectionModel().clearSelection();
            opListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getOp())));
            templateListView.getSelectionModel().clearSelection();
            templateListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getTemplate())));
            sizeListView.getSelectionModel().clearSelection();
            sizeListView.setItems(FXCollections.observableList(new ArrayList<>(storage.getSize())));
            listViewsBox.setDisable(false);
            if(onSuccess != null) onSuccess.run();
        });
        readTask.setOnFailed(workerStateEvent -> {
            log.error("specification read task error", readTask.getException());
            Alert alert = new Alert(Alert.AlertType.ERROR, readTask.getException().getMessage());
            alert.setTitle("Ошибка");
            alert.setHeaderText("Сбой при чтении файла спецификации");
            alert.show();
            listViewsBox.setDisable(false);
        });
        app.getEs().execute(readTask);
    }

    @FXML
    void onSpecificationSettings(ActionEvent event) {
        int[] data = new SelectSpecification().open(app.getStage(), indexes, specificationFileField.getText());
        if(data != null) {
            indexes = data;
            loadListViews(new File(specificationFileField.getText()), data,null);
        }
    }

    private void saveSettings(Settings settings) {
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("cache"))) {
            os.writeObject(settings);
        } catch (Exception e) {
            log.error("save settings exception", e);
        }
    }

    private void loadSettings() {
        File cacheFile = new File("cache");
        if(!cacheFile.exists() || !cacheFile.isFile()) return;
        try(ObjectInputStream is = new ObjectInputStream(new FileInputStream("cache"))) {
            Settings settings = (Settings) is.readObject();
            specificationFileField.setText(settings.getSpecification());
            sourceFolderField.setText(settings.getSource());
            resultFolderField.setText(settings.getResult());
            indexes = settings.getIndexes();
            clearResultFolderCheckBox.setSelected(settings.isClearResultFolder());
            checkCountCheckBox.setSelected(settings.isCheckCount());
            loadListViews(new File(settings.getSpecification()), indexes, () -> {
                listViewSelect(opListView, settings.getOp());
                listViewSelect(templateListView, settings.getTemplate());
                listViewSelect(sizeListView, settings.getSize());
            });
        } catch (Exception e) {
            log.error("load settings exception", e);
        }
    }

    private ChangeListener<? super String> onSpecificationChange() {
        return (observableValue, s, t1) -> {
            if(t1 == null || t1.isEmpty()) return;
            if(t1.indexOf('.', t1.length() - 5) < 0) return;
            loadListViews(new File(t1), indexes, null);
        };
    }

    private <T> void listViewSelect(ListView<T> list, Set<T> values) {
        MultipleSelectionModel<T> sm = list.getSelectionModel();
        ObservableList<T> items = list.getItems();
        for (int i = 0; i < items.size(); i++) {
            if(values.contains(items.get(i))) sm.select(i);
        }
    }
}

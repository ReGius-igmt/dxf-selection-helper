package ru.regiuss.dxf.selection.helper.node;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.model.SpecificationData;
import ru.regiuss.dxf.selection.helper.reader.Reader;
import ru.regiuss.dxf.selection.helper.reader.ReaderFactory;
import ru.regiuss.dxf.selection.helper.reader.Row;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class SelectSpecification {

    @FXML
    private VBox choiceBoxes;

    @FXML
    private TextField specificationFileField;
    private ChoiceBox<String>[] boxes;
    private Stage stage;
    private final List<String> columns = Arrays.asList("Обозначение", "Заготовка", "Типоразмер", "Оп1", "К-во");
    private int[] indexes = new int[columns.size()];
    private SpecificationData response;

    @FXML
    void onBrowseSpecificationFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        if(specificationFileField.getText() != null && !specificationFileField.getText().isEmpty()) {
            File initialDirectory = new File(specificationFileField.getText());
            if(initialDirectory.exists())
                chooser.setInitialDirectory(initialDirectory.isFile() ? initialDirectory.getParentFile() : initialDirectory);
        }
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("EXCEL", "*.xls"));
        File specificationFile = chooser.showOpenDialog(stage);
        log.debug("select specification file - {}", specificationFile);
        if(specificationFile != null) specificationFileField.setText(specificationFile.getAbsolutePath());
    }

    @FXML
    void onCancelClick(ActionEvent event) {
        stage.close();
    }

    @FXML
    void onConfirmClick(ActionEvent event) {
        response = new SpecificationData(
                specificationFileField.getText(),
                Arrays.stream(boxes).mapToInt(b->b.getSelectionModel().getSelectedIndex()).toArray()
        );
        stage.close();
    }

    public SpecificationData open(Stage owner, int[] indexes, String path) {
        if(stage != null) stage.close();
        stage = new Stage();
        stage.setTitle("Файл спецификации");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        Parent p = open(indexes, path);
        if (p == null) return null;
        stage.setScene(new Scene(p));
        stage.showAndWait();
        return response;
    }

    public Parent open(int[] indexes, String path) {
        Parent p;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/selectSpecification.fxml"));
        loader.setController(this);
        try {
            p = loader.load();
            init(indexes, path);
        } catch (Exception e) {
            log.error("FXMLLoader selectSpecification.fxml", e);
            return null;
        }
        return p;
    }

    private void init(int[] indexes, String path) {
        log.info(Arrays.toString(indexes));
        if(indexes != null) this.indexes = indexes;
        this.specificationFileField.setText(path);
        generateBoxes(columns);
        File f = new File(path);
        if(f.exists()) fillBoxes(f, false);
        specificationFileField.textProperty().addListener(onSpecificationChange());

    }

    private void generateBoxes(List<String> columns) {
        boxes = new ChoiceBox[columns.size()];
        for (int i = 0; i < boxes.length; i++) {
            ChoiceBox<String> cb = new ChoiceBox<>();
            cb.setPrefWidth(150);
            Label label = new Label(columns.get(i));
            label.setPrefWidth(120);
            label.setFont(Font.font(16));
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(label, cb);
            boxes[i] = cb;
            choiceBoxes.getChildren().add(hBox);
        }
    }

    private ChangeListener<? super String> onSpecificationChange() {
        return (observableValue, s, t1) -> {
            if(t1 == null || t1.isEmpty()) return;
            if(t1.indexOf('.', t1.length() - 5) < 0) return;
            File f = new File(t1);
            if(!f.exists()) {
                new Alert(Alert.AlertType.ERROR, "Файл не существует - " + f.getAbsolutePath());
                return;
            }
            fillBoxes(f, true);
        };
    }

    private void fillBoxes(File f, boolean auto) {
        List<String> headers = readHeaders(f);
        if(auto) {
            Arrays.fill(indexes, 0);
            List<String> headerLowerCase = headers.stream().map(String::toLowerCase).collect(Collectors.toList());
            for (int i = 0; i < indexes.length; i++) {
                int index = headerLowerCase.indexOf(columns.get(i).toLowerCase(Locale.ROOT));
                if(index != -1) indexes[i] = index;
            }
        }
        ObservableList<String> value = FXCollections.observableList(headers);
        for (int i = 0; i < columns.size(); i++) {
            boxes[i].setItems(value);
            boxes[i].getSelectionModel().select(indexes[i]);
        }
    }

    private List<String> readHeaders(File f) {
        try(Reader reader = ReaderFactory.create(f)) {
            if(!reader.hasNext()) return Collections.emptyList();
            Row row = reader.next();
            List<String> headers = new ArrayList<>(row.size());
            for (int i = 0; i < row.size(); i++) {
                headers.add(row.get(i));
            }
            return headers;
        } catch (Exception e) {
            throw new RuntimeException(String.format("create reader for %s %s", f.getName(), e.getMessage()));
        }
    }
}

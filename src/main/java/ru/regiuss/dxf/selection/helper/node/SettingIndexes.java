package ru.regiuss.dxf.selection.helper.node;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.util.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SettingIndexes {

    @FXML
    private VBox choiceBoxes;

    @FXML
    private TableView<String[]> preview;

    private ChoiceBox<String>[] boxes;
    private Stage stage;
    private int[] indexes;

    @FXML
    void onCancelClick(ActionEvent event) {
        stage.close();
    }

    @FXML
    void onConfirmClick(ActionEvent event) {
        indexes = Arrays.stream(boxes).mapToInt(b->b.getSelectionModel().getSelectedIndex()).toArray();
        stage.close();
    }

    public int[] open(Stage owner, int[] indexes, List<String[]> previewData) {
        if(stage != null) stage.close();
        stage = new Stage();
        stage.setTitle("Файл спецификации");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        Parent p = open(indexes, previewData);
        if (p == null) return null;
        stage.setScene(new Scene(p));
        stage.showAndWait();
        return this.indexes;
    }

    public Parent open(int[] indexes, List<String[]> previewData) {
        Parent p;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settingIndexes.fxml"));
        loader.setController(this);
        try {
            p = loader.load();
            init(indexes, previewData);
        } catch (Exception e) {
            log.error("FXMLLoader settingIndexes.fxml", e);
            return null;
        }
        return p;
    }

    private void init(int[] indexes, List<String[]> previewData) {
        List<String> columns = Utils.getColumns();
        generateBoxes(columns);
        fillBoxes(Arrays.asList(previewData.get(0)), indexes == null ? new int[columns.size()] : indexes);
        for (int i = 0; i < previewData.get(0).length; i++) {
            TableColumn<String[], String> column = new TableColumn<>(previewData.get(0)[i]);
            final int colIndex = i ;
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[colIndex]));
            column.setResizable(true);
            preview.getColumns().add(column);
        }
        preview.setItems(FXCollections.observableList(previewData.stream().skip(1).collect(Collectors.toList())));
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

    private void fillBoxes(List<String> headers, int[] indexes) {
        ObservableList<String> value = FXCollections.observableList(headers);
        for (int i = 0; i < indexes.length; i++) {
            boxes[i].setItems(value);
            boxes[i].getSelectionModel().select(indexes[i]);
        }
    }
}

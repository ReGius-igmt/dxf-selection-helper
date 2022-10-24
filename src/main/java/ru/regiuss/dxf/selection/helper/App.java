package ru.regiuss.dxf.selection.helper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("DXFSelectionHelper");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setWidth(800);
        stage.setHeight(600);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent parent = loader.load();
        stage.setScene(new Scene(parent));
        stage.show();
    }
}

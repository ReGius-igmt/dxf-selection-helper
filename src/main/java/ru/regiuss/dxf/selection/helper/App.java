package ru.regiuss.dxf.selection.helper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.regiuss.dxf.selection.helper.controller.MainController;

import java.util.prefs.Preferences;

public class App extends Application {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        stage.setTitle("DXFSelectionHelper");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        double x = userPrefs.getDouble("stage.position.x", -1);
        double y = userPrefs.getDouble("stage.position.y", -1);
        double w = userPrefs.getDouble("stage.position.width", 800);
        double h = userPrefs.getDouble("stage.position.height", 600);
        stage.setWidth(w);
        stage.setHeight(h);
        if(x > 0 && y > 0) {
            stage.setX(x);
            stage.setY(y);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent parent = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(parent));
        stage.show();
    }

    @Override
    public void stop() {
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        userPrefs.putDouble("stage.position.x", stage.getX());
        userPrefs.putDouble("stage.position.y", stage.getY());
        userPrefs.putDouble("stage.position.width", stage.getWidth());
        userPrefs.putDouble("stage.position.height", stage.getHeight());
    }

}

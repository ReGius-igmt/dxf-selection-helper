package ru.regiuss.dxf.selection.helper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ru.regiuss.dxf.selection.helper.controller.MainController;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;

@Getter
@Log4j2
public class App extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Stage stage;
    private boolean checkUpdate;
    private String version;
    private ExecutorService es;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.es = Executors.newFixedThreadPool(2, r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            t.setName("DXFSelectionHelperThread");
            return t;
        });

        Properties pomProperties = new Properties();
        pomProperties.load(getClass().getResourceAsStream("/pom.properties"));
        version = pomProperties.getProperty("app.version");
        log.info("running version: {}", version);
        checkUpdate = !getParameters().getRaw().contains("-update");
        log.info("check updates: {}", checkUpdate);

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("DXFSelectionHelper v" + version);
        stage.setMinWidth(WIDTH);
        stage.setMinHeight(HEIGHT);

        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        double x = userPrefs.getDouble("stage.position.x", -1);
        double y = userPrefs.getDouble("stage.position.y", -1);
        double w = userPrefs.getDouble("stage.position.width", WIDTH);
        double h = userPrefs.getDouble("stage.position.height", HEIGHT);
        stage.setWidth(w);
        stage.setHeight(h);
        if(x > 0 && y > 0) {
            stage.setX(x);
            stage.setY(y);
        }
        stage.setMaximized(userPrefs.getBoolean("stage.position.isMaximized", false));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent parent = loader.load();
        MainController controller = loader.getController();
        controller.init(this);
        stage.setScene(new Scene(parent));
        stage.show();
    }

    @Override
    public void stop() {
        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        boolean isMaximized = stage.isMaximized();
        userPrefs.putDouble("stage.position.x", stage.getX());
        userPrefs.putDouble("stage.position.y", stage.getY());
        if(isMaximized) {
            userPrefs.putDouble("stage.position.width", WIDTH);
            userPrefs.putDouble("stage.position.height", HEIGHT);
        } else {
            userPrefs.putDouble("stage.position.width", stage.getWidth());
            userPrefs.putDouble("stage.position.height", stage.getHeight());
        }
        userPrefs.putBoolean("stage.position.isMaximized", isMaximized);
        es.shutdown();
    }

}

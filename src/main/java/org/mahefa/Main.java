package org.mahefa;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        boolean is3DSupported = Platform.isSupported(ConditionalFeature.SCENE3D);

        if(!is3DSupported) {
            throw new Exception("Sorry, 3D is not supported in JavaFX on this platform.");
        }

        Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);

        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add(getClass().getResource("/compiled-css/style.css").toExternalForm());
        scene.setFill(Color.WHITE);

        stage.setTitle("Pathfinding Visualizer");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(870);
        stage.setMaximized(false);

        stage.show();
    }

    public void init() throws Exception {
        springContext = SpringApplication.run(Main.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        rootNode = fxmlLoader.load();
    }

    public void stop() {
        springContext.close();
    }
}

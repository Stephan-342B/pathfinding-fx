package org.mahefa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PathFindingFx extends Application {

    private ConfigurableApplicationContext springContext;
    private Parent rootNode;

    public static Stage stage;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);

        Scene scene = new Scene(rootNode);
        scene.getStylesheets().add(getClass().getResource("/compiled-css/style.css").toExternalForm());
        scene.setFill(Color.WHITE);

        scaleWindows(scene);

        stage.setTitle("PathfindingFx");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(870);
        stage.setMaximized(false);

        this.stage = stage;

        stage.show();
    }

    public void init() throws Exception {
        springContext = SpringApplication.run(PathFindingFx.class);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);

        rootNode = fxmlLoader.load();
    }

    public void stop() {
        springContext.close();
    }

    private void scaleWindows(Scene scene) {
        Scale scale = new Scale(1, 1);
        scale.setPivotX(0);
        scale.setPivotY(0);
        scene.getRoot().getTransforms().setAll(scale);
    }
}

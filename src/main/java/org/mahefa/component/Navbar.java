package org.mahefa.component;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.mahefa.events.MenuEventHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Navbar extends FlowPane implements Initializable {

    @FXML private HBox title;
    @FXML private Text label;
    @FXML private MenuBar menuBar;

    private MenuEventHandler menuEventHandler = new MenuEventHandler();
    private ObjectProperty<Menu> currentActiveMenu = new SimpleObjectProperty<>();

    public Navbar() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/Navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // set FXMLLoader's classloader!
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuBar.prefWidthProperty().bind(
                Bindings.when(prefWidthProperty().subtract(title.widthProperty()).subtract(42).greaterThanOrEqualTo(menuBar.widthProperty()))
                .then(prefWidthProperty().subtract(title.widthProperty()).subtract(42))
                .otherwise(prefWidthProperty().subtract(42))
        );

        // Init menu event handler
        menuBar.getMenus().forEach(currentMenu -> currentMenu.setOnMouseClicked(menuEventHandler::handle));
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public void setTitle(String value) {
        titleProperty().set(value);
    }

    public StringProperty titleProperty() {
        return label.textProperty();
    }

    public Menu getCurrentActiveMenu() {
        return currentActiveMenu.get();
    }

    public ObjectProperty<Menu> currentActiveMenuProperty() {
        return currentActiveMenu;
    }

    public void setCurrentActiveMenu(Menu currentActiveMenu) {
        this.currentActiveMenu.set(currentActiveMenu);
    }
}

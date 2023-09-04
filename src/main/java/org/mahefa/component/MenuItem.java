package org.mahefa.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuItem extends StackPane implements Initializable {

    @FXML
    private Text label;

    private Menu menu;

    private final BooleanProperty weighted = new SimpleBooleanProperty(false);
    private final BooleanProperty guaranteeShortestPath = new SimpleBooleanProperty(false);

    public MenuItem() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/MenuItem.fxml"));
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
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public Text getLabel() {
        return label;
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public boolean isWeighted() {
        return weighted.get();
    }

    public void setWeighted(boolean weighted) {
        this.weighted.set(weighted);
    }

    public boolean isGuaranteeShortestPath() {
        return guaranteeShortestPath.get();
    }

    public void setGuaranteeShortestPath(boolean guaranteeShortestPath) {
        this.guaranteeShortestPath.set(guaranteeShortestPath);
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}

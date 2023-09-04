package org.mahefa.component;

import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@DefaultProperty("items")
public class Menu extends HBox implements Initializable {

    @FXML private Text label;
    @FXML private FontIcon fontIcon;

    private final ObjectProperty<ObservableList<MenuItem>> items = new SimpleObjectProperty<>(this, "items", FXCollections.observableArrayList());

    public Menu() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/Menu.fxml"));
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
        getItems().addListener((InvalidationListener) change -> {
            if (getItems().size() > 0) {
                fontIcon.setIconLiteral("ci-caret-down");
                fontIcon.setIconSize(25);
            }
        });
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public Text getLabel() {
        return label;
    }

    public FontIcon getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(FontIcon fontIcon) {
        this.fontIcon = fontIcon;
    }

    public ObservableList<MenuItem> getItems() {
        return items.get();
    }

    public ObjectProperty<ObservableList<MenuItem>> itemsProperty() {
        return items;
    }

    public boolean hasItems() {
        return getItems().size() > 0;
    }
}

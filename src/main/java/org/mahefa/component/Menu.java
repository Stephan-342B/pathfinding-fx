package org.mahefa.component;

import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.mahefa.common.StateStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.mahefa.common.StateStyle.BLOCKED_PSEUDO_CLASS;
import static org.mahefa.common.StateStyle.DISABLED_PSEUDO_CLASS;

@DefaultProperty("items")
public class Menu extends HBox implements Initializable {

    @FXML private Text label;
    @FXML private FontIcon fontIcon;

    private BooleanProperty lockable = new SimpleBooleanProperty(true);
    private ObjectProperty<MenuItem> selectedItem = new SimpleObjectProperty<>();
    private final ObjectProperty<ObservableList<MenuItem>> items = new SimpleObjectProperty<>(this, "items", FXCollections.observableArrayList());
    private ObjectProperty<StateStyle.State> currentState = new SimpleObjectProperty<>() {
        @Override
        public void invalidated() {
            pseudoClassStateChanged(BLOCKED_PSEUDO_CLASS, false);
            pseudoClassStateChanged(DISABLED_PSEUDO_CLASS, false);

            switch (get()) {
                case BLOCKED:
                    pseudoClassStateChanged(BLOCKED_PSEUDO_CLASS, true);
                    break;
                case DISABLED:
                    pseudoClassStateChanged(DISABLED_PSEUDO_CLASS, true);
                    break;
                default:
                    break;
            }
        }
    };

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
        setCurrentState(StateStyle.State.READY);

        getItems().addListener((InvalidationListener) change -> {
            if (getItems().size() > 0) {
                fontIcon.setIconLiteral("ci-caret-down");
                fontIcon.setIconSize(25);
            }
        });

        currentStateProperty().addListener((observableValue, state, t1) -> {
            if (!hasItems()) {
                if (t1.equals(StateStyle.State.BLOCKED)){
                    getStyleClass().add("blocked");
                } else{
                    getStyleClass().remove("blocked");
                }
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

    public boolean isLockable() {
        return lockable.get();
    }

    public BooleanProperty lockableProperty() {
        return lockable;
    }

    public void setLockable(boolean lockable) {
        this.lockable.set(lockable);
    }

    public MenuItem getSelectedItem() {
        return selectedItem.get();
    }

    public ObjectProperty<MenuItem> selectedItemProperty() {
        return selectedItem;
    }

    public void setSelectedItem(MenuItem selectedItem) {
        this.selectedItem.set(selectedItem);
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

    public StateStyle.State getCurrentState() {
        return currentState.get();
    }

    public ObjectProperty<StateStyle.State> currentStateProperty() {
        return currentState;
    }

    public void setCurrentState(StateStyle.State currentState) {
        this.currentState.set(currentState);
    }
}

package org.mahefa.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.mahefa.common.enumerator.PathFindingAlgorithm;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.mahefa.common.StateStyle.*;

public class Button extends HBox implements Initializable {

    @FXML private Text label;

    private ObjectProperty<PathFindingAlgorithm> selectedAlgorithm = new SimpleObjectProperty<>();
    private ObjectProperty<State> currentState = new SimpleObjectProperty<>() {
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

    public Button() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/Button.fxml"));
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
        setCurrentState(State.READY);
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

    public PathFindingAlgorithm getSelectedAlgorithm() {
        return selectedAlgorithm.get();
    }

    public ObjectProperty<PathFindingAlgorithm> selectedAlgorithmProperty() {
        return selectedAlgorithm;
    }

    public void setSelectedAlgorithm(PathFindingAlgorithm selectedAlgorithm) {
        this.selectedAlgorithm.set(selectedAlgorithm);
    }

    public State getCurrentState() {
        return currentState.get();
    }

    public ObjectProperty<State> currentStateProperty() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState.set(currentState);
    }
}

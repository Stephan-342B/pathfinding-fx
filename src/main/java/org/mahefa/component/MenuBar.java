package org.mahefa.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuBar extends FlowPane implements Initializable {

    @FXML
    private Button btnPlay;

    private List<Menu> menus = new LinkedList<>();

    public MenuBar() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/MenuBar.fxml"));
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
        getChildren()
                .stream()
                .filter(node -> node instanceof Menu)
                .map(node -> (Menu) node)
                .forEach(menu -> menus.add(menu));
    }

    public Button getBtnPlay() {
        return btnPlay;
    }

    public List<Menu> getMenus() {
        return menus;
    }
}

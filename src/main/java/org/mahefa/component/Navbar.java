package org.mahefa.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.mahefa.common.MenuStyle;
import org.springframework.util.CollectionUtils;

import java.io.IOException;

public class Navbar extends FlowPane {

    @FXML
    private Text label;

//    @FXML
//    private HBox navbarMenu;

    private StackPane area;
    private ObjectProperty<Menu> currentActiveMenu = new SimpleObjectProperty<>(null);

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

            if (!CollectionUtils.isEmpty(getChildren())) {
                getChildren()
                        .stream()
                        .filter(node -> node.getId() != null && node.getId().startsWith("menu_"))
                        .map(node -> (Menu) node)
                        .forEach(menu -> {
                            menu.onMouseClickedProperty().set(event -> menu.getMenuEventHandler().handle(currentActiveMenu, event));
                        });
            }

            currentActiveMenu.addListener((observableValue, old, current) -> {
                if(old != null) {
                    old.setState(MenuStyle.State.NONE);

                    if (old.hasSubmenu()) {
                        area.getChildren().remove(1);
                    }
                }

                if (current != null && current.hasSubmenu()) {
                    area.getChildren().add(1, current.getSubmenu());
                    area.setAlignment(current.getSubmenu(), Pos.TOP_LEFT);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StackPane getArea() {
        return this.area;
    }

    public void setArea(StackPane area) {
        this.area = area;
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

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }
}

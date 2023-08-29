package org.mahefa.events;

import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.mahefa.component.Menu;

import static org.mahefa.common.MenuStyle.State.ACTIVE;
import static org.mahefa.common.MenuStyle.State.ACTIVE_WITH_DROPDOWN;

public class MenuEventHandler implements EventHandler<Event> {

    ObjectProperty<Menu> currentActiveMenu;

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClickedProperty((MouseEvent) event);
        }
    }

    public void handle(ObjectProperty<Menu> currentActiveMenu, Event event) {
        this.currentActiveMenu = currentActiveMenu;
        handle(event);
    }

    private void setOnMouseClickedProperty(MouseEvent event) {
        Menu currentMenu = (Menu) event.getSource();
        currentMenu.setState((currentMenu.hasSubmenu()) ? ACTIVE_WITH_DROPDOWN : ACTIVE);

        currentActiveMenu.setValue(
                (currentActiveMenu.get() == null || !currentActiveMenu.get().equals(currentMenu)) ? currentMenu : null
        );

        event.consume();
    }
}

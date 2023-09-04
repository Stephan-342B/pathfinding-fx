package org.mahefa.events;

import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.mahefa.component.Menu;

public class MenuEventHandler implements EventHandler<Event> {

    private ObjectProperty<Menu> currentActiveMenu;

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClickedProperty((MouseEvent) event);
        }
    }

    private void setOnMouseClickedProperty(MouseEvent event) {
        Menu menu = (Menu) event.getSource();


        event.consume();
    }

    public void attachObservableProperty(ObjectProperty<Menu> currentActiveMenu) {
        this.currentActiveMenu = currentActiveMenu;
    }
}

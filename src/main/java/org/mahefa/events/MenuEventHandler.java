package org.mahefa.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.mahefa.component.Menu;
import org.mahefa.component.MenuBar;
import org.mahefa.component.Navbar;

public class MenuEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClickedProperty((MouseEvent) event);
        }
    }

    private void setOnMouseClickedProperty(MouseEvent event) {
        Menu currentMenu = (Menu) event.getSource();
        MenuBar menuBar = (MenuBar) currentMenu.getParent();
        Navbar navbar = (Navbar) menuBar.getParent();

        navbar.setCurrentActiveMenu(currentMenu);

        event.consume();
    }
}

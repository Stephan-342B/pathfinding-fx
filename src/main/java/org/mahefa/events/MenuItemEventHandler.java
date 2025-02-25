package org.mahefa.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.mahefa.component.Menu;
import org.mahefa.component.MenuBar;
import org.mahefa.component.MenuItem;
import org.mahefa.component.Navbar;

import static org.mahefa.common.StateStyle.State;

public class MenuItemEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClickedProperty((MouseEvent) event);
        }
    }

    private void setOnMouseClickedProperty(MouseEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Menu parent = menuItem.getMenu();
        State currentState = parent.getCurrentState();

        if (currentState.equals(State.READY) || !parent.isLockable()) {
            parent.selectedItemProperty().setValue(menuItem);

            // Release the current drop-down menu
            MenuBar menuBar = (MenuBar) parent.getParent();
            Navbar navbar = (Navbar) menuBar.getParent();
            navbar.setCurrentActiveMenu(null);
        }

        event.consume();
    }
}

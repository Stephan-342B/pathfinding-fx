package com.mahefa.pathfindingfx.ui.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import com.mahefa.pathfindingfx.ui.component.Menu;
import com.mahefa.pathfindingfx.ui.component.MenuBar;
import com.mahefa.pathfindingfx.ui.component.MenuItem;
import com.mahefa.pathfindingfx.ui.component.Navbar;

import static com.mahefa.pathfindingfx.ui.style.StateStyle.State;

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

package sample.interfaces;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import sample.DateElement;

public class CustomCheckBoxFactory {
    private CustomEvents customEvents;

    public CustomCheckBoxFactory(CustomEvents customEvents) {
        this.customEvents = customEvents;
    }

    public TableCell<DateElement, Boolean> checkBoxFactory(TableColumn<DateElement, Boolean> column) {
        return new TableCell<DateElement, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    CheckBox checkBox = new CheckBox();
                    checkBox.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            customEvents.onCheckBoxChange(getIndex(), event);
                        }
                    });
                    if (Boolean.TRUE.equals(item)) {
                        checkBox.setSelected(true);
                    } else {
                        checkBox.setSelected(false);
                    }
                    setGraphic(checkBox);
                }

            }
        };
    }
}

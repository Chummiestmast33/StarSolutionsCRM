package com.starsolutions.starsolutionscrm.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Function;

public class SearchableComboBoxUtil {

    public static <T> void setupSearchableComboBox(ComboBox<T> comboBox, List<T> items, Function<T, String> displayTextFunction) {
        ObservableList<T> observableItems = FXCollections.observableArrayList(items);
        comboBox.setItems(observableItems);
        comboBox.setEditable(true);

        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                if (object == null) return "";
                return displayTextFunction.apply(object);
            }

            @Override
            public T fromString(String string) {
                return comboBox.getItems().stream()
                        .filter(item -> displayTextFunction.apply(item).equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }

            Platform.runLater(() -> {
                if (comboBox.getSelectionModel().getSelectedItem() != null &&
                        displayTextFunction.apply(comboBox.getSelectionModel().getSelectedItem()).equals(newValue)) {
                    return;
                }

                if (newValue == null || newValue.isEmpty()) {
                    comboBox.setItems(observableItems);
                } else {
                    ObservableList<T> filteredList = FXCollections.observableArrayList();
                    for (T item : observableItems) {
                        String display = displayTextFunction.apply(item);
                        if (display != null && display.toLowerCase().contains(newValue.toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                    comboBox.setItems(filteredList);
                }
            });
        });

        // Hide popup when focus is lost
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                comboBox.hide();
            }
        });
    }
}

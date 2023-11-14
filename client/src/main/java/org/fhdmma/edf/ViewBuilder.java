package org.fhdmma.edf;

import java.util.Objects;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class ViewBuilder implements Builder<Region> {

    private final Model model;

    public ViewBuilder(Model model) {
        this.model = model;
    }

    @Override
    public Region build() {
        BorderPane mainPane = new BorderPane();
        mainPane.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
        mainPane.setTop(headingLabel("EDF visualizer"));
        mainPane.setLeft(createLeft());
        mainPane.setRight(createRight());
        mainPane.setCenter(createCenter());
        return mainPane;
    }

    private Node createCenter() {
        Label tempLabel = new Label("TODO: centre panel");
        return tempLabel;
    }

    private Node createRight() {
        VBox vbox = new VBox();
        return vbox;
    }

    private Node createLeft() {
        return null;
    }

    private Node headingLabel(String string) {
        return null;
    }
}

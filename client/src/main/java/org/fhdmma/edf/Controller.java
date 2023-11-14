package org.fhdmma.edf;

import javafx.scene.layout.Region;
import javafx.util.Builder;

public class Controller {

    private Builder<Region> viewBuilder;
    private Interactor interactor;

    public Controller() {
        Model model = new Model();
        this.viewBuilder = new ViewBuilder(model);
        this.interactor = new Interactor(model);
    }

    public Region getView() {
        return viewBuilder.build();
    }
}


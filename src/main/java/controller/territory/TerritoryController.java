package controller.territory;

import controller.FXMLController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseButton;
import javafx.util.Pair;
import model.territory.PlaceOnTileSelection;
import model.territory.Territory;
import util.Position;

import java.io.IOException;

public class TerritoryController {

    private final PlaceOnTileSelection selection;

    private final FXMLController controller;
    private final Territory territory;

    public TerritoryController(Territory territory, FXMLController controller) {
        this.territory = territory;
        this.controller = controller;

        selection = new PlaceOnTileSelection();

        controller.territoryPanel.setTerritory(territory);

        setUpEventHandlers();
    }

    /** Add EventHandlers to change the territory with the buttons. */
    private void setUpEventHandlers() {
        controller.placeCustomerButton.selectedProperty().bindBidirectional(controller.placeCustomerMenuItem.selectedProperty());
        controller.placeShelfButton.selectedProperty().bindBidirectional(controller.placeShelfMenuItem.selectedProperty());
        controller.placeCartButton.selectedProperty().bindBidirectional(controller.placeCartMenuItem.selectedProperty());
        controller.placePresentButton.selectedProperty().bindBidirectional(controller.placePresentMenuItem.selectedProperty());
        controller.clearTileButton.selectedProperty().bindBidirectional(controller.clearTileMenuItem.selectedProperty());

        // event handlers to synchronize the selection of the toggle groups
        controller.placeItemToggleToolbar.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == controller.placeCustomerButton) {
                selection.setSelected(PlaceOnTileSelection.ACTOR);
            } else if (newToggle == controller.placeShelfButton) {
                selection.setSelected(PlaceOnTileSelection.SHELF);
            } else if (newToggle == controller.placeCartButton) {
                selection.setSelected(PlaceOnTileSelection.CART);
            } else if (newToggle == controller.placePresentButton) {
                selection.setSelected(PlaceOnTileSelection.PRESENT);
            } else if (newToggle == controller.clearTileButton) {
                selection.setSelected(PlaceOnTileSelection.REMOVE);
            }
        });

        // create a dialog window which takes the territory's dimensions as input and resizes the market based on those values
        EventHandler<ActionEvent> resizerHandler = event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResizeDialogView.fxml"));
                DialogPane dialogPane = loader.load();

                ResizeDialogController resizeController = loader.getController();
                resizeController.setColumnInputText(String.valueOf(territory.getWidth()));
                resizeController.setRowInputText(String.valueOf(territory.getHeight()));

                Dialog<Pair<String, String>> dialog = new Dialog<>();
                dialog.setDialogPane(dialogPane);
                dialog.setTitle("Territorium-Größe");

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.OK) {
                        return new Pair<>(resizeController.getRowInputText(), resizeController.getColumnInputText());
                    }
                    return null;
                });

                dialog.showAndWait().ifPresent(pair ->
                        territory.resizeTerritory(Integer.parseInt(pair.getKey()), Integer.parseInt(pair.getValue()))
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        controller.resizeMarketButton.addEventHandler(ActionEvent.ACTION, resizerHandler);
        controller.resizeMarketMenuItem.addEventHandler(ActionEvent.ACTION, resizerHandler);

        // event handlers to place objects
        controller.territoryPanel.setOnMousePressed(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position pos = controller.territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
                placeItemAtPosition(pos);
            }
        });
        controller.territoryPanel.setOnMouseDragged(me -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                Position pos = controller.territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
                if (pos != null) {
                    placeItemAtPosition(pos);
                }
            }
        });

        // context menu from where the user can call every method the actor implements
        controller.territoryPanel.setOnContextMenuRequested(me -> {
            Position pos = controller.territoryPanel.getTilePositionAtCoordinate(me.getX(), me.getY());
            if (pos.getX() == territory.getActorPosition().getX() && pos.getY() == territory.getActorPosition().getY()) {
                controller.createActionsMenu(me.getScreenX() + 10, me.getScreenY() - 10);
            }
        });
    }

    /**
     * Place an "item" at a specific location. Placing the actor will try to place the actor at that position, but if
     * the tile is blocked, the actor will not be moved.
     *
     * @param pos the position the "item" should bee placed at
     */
    public void placeItemAtPosition(Position pos) {
        switch (selection.getSelected()) {
            case PlaceOnTileSelection.ACTOR:
                territory.tryPlaceActor(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.SHELF:
                territory.placeShelf(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.CART:
                territory.placeCart(pos.getX(), pos.getY());
                break;
            case PlaceOnTileSelection.PRESENT:
                territory.placePresent(pos.getX(), pos.getY());
                break;
            default:
                territory.clearTile(pos.getX(), pos.getY());
        }
    }
}

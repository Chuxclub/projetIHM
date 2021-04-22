package controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import model.Characters.Player;
import model.Game.SIS;
import model.Utils.Scalar2D;
import view.*;
import java.io.IOException;


/* -----------------------------------------------------------------------------
 * Contrôleur du jeu:
 *
 * Rôle: Lance le jeu par le biais de plusieurs modèles MVC chacun spécialisé
 * sur une partie du jeu (Exemple: pour les pièces du jeu => le RoomController,
 * pour les acteurs => le ActorController, etc.) Il initialise les gestionnaires
 * d'événements globaux du jeu (menu d'aide, load()/save(), etc.) et fournit les
 * méthodes de réactions aux événements principaux du jeu comme la fin de partie
 * ----------------------------------------------------------------------------- */

public class GameController {
    public final static Scalar2D DEFAULT_ROOMS_SIZE = new Scalar2D(11, 11);

    //Quelques éléments du modèle et leurs vues associées:
    private final SIS gameModel;
    private final GameView gameView;
    private final Player playerModel;
    private final ActorView playerView = new ActorView("player");

    //Les sous-contrôleurs spécialisés:
    private final ActorController actorController;
    private final RoomController roomController;
    private final InventoryController inventoryController;

    //Gestion du manuel d'aide:
    private String previousDialog;
    private Boolean isHelpManualOn = false;

    //=============== CONSTRUCTEURS/INITIALISEURS ===============
    public GameController() throws IOException {
        //On charge la vue:
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/GameView.fxml"));
        loader.load();
        gameView = loader.getController();

        //On charge le modèle:
        gameModel = new SIS(gameView);
        playerModel = gameModel.getShip().getPlayer();

        //On initialise les sous-contrôleurs spécialisés:
        actorController = new ActorController(this);
        inventoryController = new InventoryController(this);
        roomController = new RoomController(this);

        //On charge les gestionnaires d'événement globaux du jeu:
        initHandlers();
    }

    public void initHandlers()
    {
        // ========== SAVE & LOAD BUTTON
        gameView.getSaveButton().setOnMouseClicked(e-> {
            playerModel.save();
            gameView.update("You successfully saved the game!");
        });

        gameView.getLoadButton().setOnMouseClicked(e-> {
            playerModel.load();
            gameView.update("You successfully loaded the game!");
        });

        //Pour que la pièce passe derrière la fenêtre si débordement:
        final Rectangle clipPane = new Rectangle();
        gameView.getMapPane().setClip(clipPane);
        gameView.getMapPane().layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clipPane.setWidth(newValue.getWidth());
            clipPane.setHeight(newValue.getHeight());
        });

        //Les sliders réinitialisent la pièce au centre du panneau à chaque redimensionnement de la fenêtre:
        gameView.getMapHorizontalSlider().maxProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                gameView.getMapHorizontalSlider().setValue(gameView.getMapHorizontalSlider().getMax()/2);
            }
        });
        gameView.getMapVerticalSlider().maxProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                gameView.getMapVerticalSlider().setValue(gameView.getMapVerticalSlider().getMax()/2);
            }
        });

        //Le manuel d'aide:
        previousDialog = gameView.getDialogTextArea().getText();
        gameView.getHelpButton().setOnAction(e -> {
            if(isHelpManualOn) {
                isHelpManualOn = false;
                gameView.getHelpButton().setText("?");
                gameView.getDialogTextArea().setText(previousDialog);
            }

            else {
                isHelpManualOn = true;
                previousDialog = gameView.getDialogTextArea().getText();
                gameView.getDialogTextArea().setText("");
                gameView.getHelpButton().setText("Back to the Game");
                gameModel.printHelp();
            }
        });
    }

    //====================== PREDICATS ==========================
    public void isGameOver(){
        if(gameModel.isEndGame()){
            Alert popup = new Alert(Alert.AlertType.INFORMATION);
            popup.setTitle("Félicitations!");
            popup.setContentText("Merci d'avoir joué à Silent In Space! Et tout particulièrement merci à notre bêta-testeuse Ophélie De Sousa Oliveira :) !");
            popup.showAndWait();
            Platform.exit();
        }
    }

    //====================== GETTERS ==========================
    public ActorController getActorController() { return actorController; }
    public InventoryController getInventoryController() { return inventoryController; }
    public SIS getGameModel(){ return gameModel; }
    public GameView getGameView() { return gameView; }
    public Player getPlayerModel() { return playerModel; }
    public ActorView getPlayerView() { return playerView; }
    public RoomController getRoomController() { return roomController; }
    public HBox getScene() { return gameView.getSceneHBox(); }
}

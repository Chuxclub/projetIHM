package controller;

import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import model.Characters.Player;
import model.Containers.Inventory;
import model.Items.Item;
import view.GameView;

import java.util.LinkedHashMap;

public class InventoryController {
    //====================== ATTRIBUTS ==========================
    private final GameController gameController;
    private final GameView gameView;
    private final Inventory playerInvModel;
    private final VBox playerInvView;
    private final ToggleGroup invTG;
    private RoomController roomController;
    private EventHandler<MouseEvent>[] fireHandlers;

    //=============== CONSTRUCTEURS/INITIALISEURS ===============
    public InventoryController(GameController c) {
        gameController = c;
        gameView = c.getGameView();
        Player playerModel = c.getPlayerModel();
        playerInvModel = playerModel.getInventory();
        playerInvView = gameView.getInventoryVBox();
        invTG = new ToggleGroup();
        initHandlers();
    }

    public void initHandlers(){
        //On initialise le handler du bouton drop():
        gameView.getDropButton().setOnAction(e -> {
            String itemTag = ((ToggleButton) invTG.getSelectedToggle()).getText();
            drop(itemTag);
        });

        gameView.getGiveButton().setOnAction(e -> { give(); });
    }

    public void initInventory(){
        for(Item item : playerInvModel.getItems()){
            ToggleButton tgBtn = new ToggleButton(item.getTag());
            setTgBtnHandler(tgBtn);
            invTG.getToggles().add(tgBtn);
            playerInvView.getChildren().add(tgBtn);
        }
    }

    //====================== UPDATERS =========================
    public void addInInventory(Item item){
        //On met à jour le modèle:
        playerInvModel.addItem(item);
        roomController.getCurrentRoomModel().getInventory().removeItem(item.getTag());

        //On met à jour la vue:
        ToggleButton tgBtn = new ToggleButton(item.getTag());
        setTgBtnHandler(tgBtn);
        invTG.getToggles().add(tgBtn);
        playerInvView.getChildren().add(tgBtn);
        roomController.getCurrentRoomView().removeFromRoom(item.getTag());
    }

    public void clearEventHandlers(){
        LinkedHashMap<String, Shape> roomViews = roomController.getCurrentRoomView().getGameElementViews();
        int count = 0;
        for (String viewTag : roomViews.keySet()) {
            if(fireHandlers[count] != null) {
                roomController.getCurrentRoomView().getFromRoom(viewTag).removeEventHandler(MouseEvent.MOUSE_PRESSED, fireHandlers[count]);
                count++;
            }
        }
    }

    public void drop(String itemTag){
        //On met à jour la vue:
        playerInvView.getChildren().remove((ToggleButton) invTG.getSelectedToggle());
        invTG.getToggles().remove(invTG.getSelectedToggle());
        roomController.addItemInRoom(playerInvModel.getItem(itemTag));

        //On met à jour le modèle:
        playerInvModel.moveItem(itemTag, roomController.getCurrentRoomModel().getInventory());

        //On élimine les handlers() dus à la sélection du bouton:
        clearEventHandlers();
    }

    public void give(){
        ToggleButton itemBtn = (ToggleButton) invTG.getSelectedToggle();

        if(itemBtn != null){
            String actorTag = gameView.getActorLabel().getText();
            String itemTag = itemBtn.getText();

            //On met à jour le modèle:
            gameController.getPlayerModel().give(itemTag, roomController.getCurrentRoomModel().getActor(actorTag));

            //On met à jour la vue:
            updateInventory();
        }
    }

    public void setTgBtnHandler(ToggleButton btn){
        btn.setOnAction(e -> {
            //On fait le ménage dans le tableau des handlers (si un objet de l'inventaire avait été sélectionné avant par exemple...) :
            clearEventHandlers();

            //On récupère tous les éléments visuels de la pièce associés à leurs étiquettes:
            LinkedHashMap<String, Shape> roomViews = roomController.getCurrentRoomView().getGameElementViews();

            //On récupère l'élément du modèle qui devra appeler la fonction du modèle si cet élément n'est pas
            //dans l'inventaire de la pièce c'est qu'il est dans l'inventaire du joueur:
            Item itemUsed;
            if(roomController.getCurrentRoomModel().getInventory().getItem(btn.getText()) != null)
                itemUsed = roomController.getCurrentRoomModel().getInventory().getItem(btn.getText());
            else
                itemUsed = playerInvModel.getItem(btn.getText());

            //On parcourt chacun de ces éléments pour leur associer un gestionnaire d'événement:
            int count = 0;
            for(String viewTag : roomViews.keySet()) {
                EventHandler<MouseEvent> useOnHandler = ev -> {
                    if (ev.isPrimaryButtonDown()) {
                        //On applique la fonction d'utilisation de l'objet définie dans le modèle:
                        itemUsed.isUsedOn(roomController.getCurrentRoomModel().getUsableBy(viewTag));
                        clearEventHandlers();
                        btn.setSelected(false);
                    }
                };
                fireHandlers[count++] = useOnHandler;
                roomController.getCurrentRoomView().getFromRoom(viewTag).addEventHandler(MouseEvent.MOUSE_PRESSED, useOnHandler);
            }
        });
    }

    public void updateInventory(){
        int nbToggleBtns = invTG.getToggles().size();

        for(int i = 0; i < nbToggleBtns; i++){
            //À chaque suppression les Toggles se réarrangent. On supprime donc le premier toggle "n" fois:
            ToggleButton itemBtn = (ToggleButton) invTG.getToggles().get(0);
            playerInvView.getChildren().remove(itemBtn);
            invTG.getToggles().remove(itemBtn);
        }

        clearEventHandlers();
        initInventory();
    }

    public void updateRoom(RoomController roomController){
        this.roomController = roomController;
        LinkedHashMap<String, Shape> roomViews = roomController.getCurrentRoomView().getGameElementViews();

        //On va stocker tous les gestionnaires d'événements que la sélection d'un bouton aura créé dans un tableau:
        fireHandlers = new EventHandler[roomViews.size()];
    }
}

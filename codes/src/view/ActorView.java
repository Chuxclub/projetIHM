package view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ActorView extends Circle
{
    public ActorView(String catActor) {
        int actorViewRadius = 10;
        setRadius(actorViewRadius);
        setActorViewColor(catActor);
    }

    public static ActorView getPlayerView(){ return new ActorView("player"); }

    // Gestion de la couleur du personnage sur la carte, suivant sa catégorie
    public void setActorViewColor(String catActor) {
        switch (catActor) {
            case "player" -> this.setFill(Color.BLUE);
            case "ally" -> this.setFill(Color.LIME);
            case "neutral" -> this.setFill(Color.DARKGOLDENROD);
            case "hostile" -> this.setFill(Color.RED);
            default -> this.setFill(Color.LIGHTGRAY);
        }
    }
}

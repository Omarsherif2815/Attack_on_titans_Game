package game.gui.GameControl;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import game.engine.Battle;
import game.gui.GameView.view;

public class Game  {


    public void displayScene(Stage primaryStage,Battle battle,int h) throws Exception {
        view gameView = new view(primaryStage, battle,h);
        gameView.initialize();
        
        primaryStage.setTitle("Aot");
        
        
        
    }
}
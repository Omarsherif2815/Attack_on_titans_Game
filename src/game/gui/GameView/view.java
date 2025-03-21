package game.gui.GameView;
import game.engine.Battle;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.engine.lanes.Lane;
import game.engine.titans.AbnormalTitan;
import game.engine.titans.ArmoredTitan;
import game.engine.titans.ColossalTitan;
import game.engine.titans.PureTitan;
import game.engine.titans.Titan;
import game.engine.weapons.WeaponRegistry;
import game.engine.weapons.factory.WeaponFactory;
import game.gui.GameControl.GameOver;
import game.gui.GameControl.MoveSuggestion;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class view {
    private Stage primaryStage;
    private Battle battle;
    private Label scoreLabel;
    private Label turnLabel;
    private Label phaseLabel;
    private Label resourcesLabel;
    private Button bestMoveButton;
    private VBox lanesBox;
    private VBox[] lanes;
    private ArrayList<Image> images = new ArrayList<Image>();
    private Label bestMoveLabel;
    private int[] piercingCount; 
    private int[] SniperCount;
    private int[] VolleyCount; 
    private int[] WallTrapCount;
    private int HighScore;
    private Map<Titan, Double> titanPositions = new HashMap<>();

    public view(Stage primaryStage, Battle battle,int h) {
        this.primaryStage = primaryStage;
        this.battle = battle;
        this.initializeImages();
        piercingCount = new int[battle.getOriginalLanes().size()];
        SniperCount = new int[battle.getOriginalLanes().size()];
        VolleyCount = new int[battle.getOriginalLanes().size()];
        WallTrapCount = new int[battle.getOriginalLanes().size()];
        HighScore = h;
    }

    public void initialize() {
        // Load background image
        Image backgroundImage = new Image("file:Sunset.jpeg");

        // Create a background image view with high-quality interpolation
        ImageView backgroundImageView = new ImageView();
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setImage(getHighResolutionImage(backgroundImage, 1920, 1080));
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        // Initialize the lanesBox
        lanesBox = new VBox();
        lanesBox.setSpacing(20);
        lanesBox.setAlignment(Pos.CENTER);
        lanesBox.setTranslateY(60);

        // Get the original lanes from the battle
        ArrayList<Lane> originalLanes = battle.getOriginalLanes();
        int numLanes = originalLanes.size();
        lanes = new VBox[numLanes];
        
        // Load wall images
        Image wallImageLowHealth = getHighResolutionImage(new Image("file:wall_low_health.jpeg"), 120, 100);
        Image wallImageMediumHealth = getHighResolutionImage(new Image("file:wall_medium_health.jpeg"), 120, 100);
        Image wallImageHighHealth = getHighResolutionImage(new Image("file:wall_high_health.jpeg"), 120, 100);

        // Iterate through the original lanes and create VBox for each lane
        for (int i = 0; i < numLanes; i++) {
            VBox laneBox = new VBox();
            laneBox.setAlignment(Pos.CENTER);
            laneBox.setSpacing(10);

            // Create an HBox to hold the lane and the wall
            HBox laneWithWall = new HBox();
            laneWithWall.setAlignment(Pos.CENTER_LEFT);

            // Create an ImageView for the wall and set image based on wall health
            ImageView wallImageView = new ImageView();
            wallImageView.setSmooth(true);
            wallImageView.setPreserveRatio(true);
            int wallHealth = originalLanes.get(i).getLaneWall().getCurrentHealth();
            if (wallHealth >= 2000) {
                wallImageView.setImage(wallImageHighHealth);
            } else if (wallHealth >= 400) {
                wallImageView.setImage(wallImageMediumHealth);
            } else {
                wallImageView.setImage(wallImageLowHealth);
            }
            
            wallImageView.setFitWidth(120);
            wallImageView.setFitHeight(100);

            // Add the wall image to the laneWithWall
            laneWithWall.getChildren().add(wallImageView);

            // Create a label to display the wall's health
            Label wallHealthLabel = new Label("Wall Health: " + wallHealth);
            if (wallHealth == 0)
                wallHealthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #FF0000;");
            else
                wallHealthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #000000;");

            // Create a label to display the danger level
            Label dangerLevelLabel = new Label("Danger Level: " + originalLanes.get(i).getDangerLevel());
            dangerLevelLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

            // Add the wall health label above the wall
            VBox wallInfoBox = new VBox();
            wallInfoBox.getChildren().addAll(dangerLevelLabel, wallImageView, wallHealthLabel);
            wallInfoBox.setAlignment(Pos.CENTER);

            // Add the wallInfoBox to the laneWithWall
            laneWithWall.getChildren().add(wallInfoBox);

            // Create weapon images with high resolution
            Image piercingCannonImage = getHighResolutionImage(new Image("file:PiercingCannon.jpeg"), 80, 80);
            Image sniperCannonImage = getHighResolutionImage(new Image("file:SniperCannon.jpeg"), 80, 80);
            Image volleySpreadCannonImage = getHighResolutionImage(new Image("file:VolleySpread.jpeg"), 80, 80);
            Image wallTrapImage = getHighResolutionImage(new Image("file:WallTrap.jpeg"), 80, 80);

            // Create ImageView for each weapon image
            ImageView weaponImageView1 = new ImageView(piercingCannonImage);
            weaponImageView1.setSmooth(true);
            weaponImageView1.setPreserveRatio(true);
            weaponImageView1.setFitWidth(80);
            weaponImageView1.setFitHeight(80);

            ImageView weaponImageView2 = new ImageView(sniperCannonImage);
            weaponImageView2.setSmooth(true);
            weaponImageView2.setPreserveRatio(true);
            weaponImageView2.setFitWidth(80);
            weaponImageView2.setFitHeight(80);

            ImageView weaponImageView3 = new ImageView(volleySpreadCannonImage);
            weaponImageView3.setSmooth(true);
            weaponImageView3.setPreserveRatio(true);
            weaponImageView3.setFitWidth(80);
            weaponImageView3.setFitHeight(80);

            ImageView weaponImageView4 = new ImageView(wallTrapImage);
            weaponImageView4.setSmooth(true);
            weaponImageView4.setPreserveRatio(true);
            weaponImageView4.setFitWidth(80);
            weaponImageView4.setFitHeight(80);

            // Create labels for each image
            Label label1 = new Label("Count: " + 0);
            Label label2 = new Label("Count: " + 0);
            Label label3 = new Label("Count: " + 0);
            Label label4 = new Label("Count: " + 0);

            // Set label styles
            label1.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
            label2.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
            label3.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
            label4.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

            // Create VBox for each weapon image and its label
            VBox vbox1 = new VBox(5);
            vbox1.getChildren().addAll(weaponImageView1, label1);
            vbox1.setAlignment(Pos.CENTER);

            VBox vbox2 = new VBox(5);
            vbox2.getChildren().addAll(weaponImageView2, label2);
            vbox2.setAlignment(Pos.CENTER);

            VBox vbox3 = new VBox(5);
            vbox3.getChildren().addAll(weaponImageView3, label3);
            vbox3.setAlignment(Pos.CENTER);

            VBox vbox4 = new VBox(5);
            vbox4.getChildren().addAll(weaponImageView4, label4);
            vbox4.setAlignment(Pos.CENTER);

            // Create HBox for all weapon images and labels
            HBox weaponBox = new HBox(0);
            weaponBox.getChildren().addAll(vbox1, vbox2, vbox3, vbox4);
            weaponBox.setAlignment(Pos.CENTER);

            // Add the laneWithWall and the weaponBox side by side within the laneBox
            HBox laneContent = new HBox();
            laneContent.getChildren().addAll(weaponBox, laneWithWall); // Switched order of adding
            laneContent.setAlignment(Pos.CENTER_LEFT);
            laneContent.setSpacing(20); // Adjust spacing between weaponBox and laneWithWall
            weaponBox.setTranslateX(-5);
            laneWithWall.setTranslateX(-5);
            
            laneBox.getChildren().addAll(laneContent);
            laneBox.setAlignment(Pos.CENTER_LEFT); // Align the laneBox content to the left

            // Add the laneBox to the lanesBox
            lanesBox.getChildren().add(laneBox);
            lanes[i] = laneBox;
        }

        // Create the top section
        HBox topBox = new HBox(100);
        topBox.setAlignment(Pos.BOTTOM_CENTER);

        // Create and add labels for current score, turn, phase, and resources
        scoreLabel = new Label("Current Score: " + battle.getScore());
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-text-fill: #FFFFFF;"); // White text color

        turnLabel = new Label("Current Turn: " + battle.getNumberOfTurns());
        turnLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-text-fill: #FFFFFF;"); // White text color

        phaseLabel = new Label("Current Phase: " + battle.getBattlePhase());
        phaseLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-text-fill: #FFFFFF;"); // White text color

        resourcesLabel = new Label("Current Resources: " + battle.getResourcesGathered());
        resourcesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px; -fx-text-fill: #FFFFFF;"); // White text color

        topBox.getChildren().addAll(scoreLabel, turnLabel, phaseLabel, resourcesLabel);

        // Create the bottom section
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.TOP_CENTER);

        // Create buttons
        WeaponFactory wp = battle.getWeaponFactory();
        Button PiercingCannon = createWeaponButton(wp.getWeaponShop().get(1), "file:PiercingCannon.jpeg", battle, primaryStage);
        Button SniperCannon = createWeaponButton(wp.getWeaponShop().get(2), "file:SniperCannon.jpeg", battle, primaryStage);
        Button Volley = createWeaponButton(wp.getWeaponShop().get(3), "file:VolleySpread.jpeg", battle, primaryStage);
        Button WallTrap = createWeaponButton(wp.getWeaponShop().get(4), "file:WallTrap.jpeg", battle, primaryStage);
        PiercingCannon.setPrefWidth(300);
        PiercingCannon.setPrefHeight(100);
        SniperCannon.setPrefWidth(300);
        SniperCannon.setPrefHeight(100);
        Volley.setPrefWidth(300);
        Volley.setPrefHeight(100);
        WallTrap.setPrefWidth(300);
        WallTrap.setPrefHeight(100);

        bottomBox.getChildren().addAll(PiercingCannon, SniperCannon, Volley, WallTrap);

        // Create the skip button
        Button skipButton = new Button("Skip");
        skipButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateView();
                battle.passTurn();
                updateView();
            }
        });

        // Create an HBox for the skip button
        HBox skipBox = new HBox(PiercingCannon, SniperCannon, Volley, WallTrap, skipButton);
        skipBox.setSpacing(20);
        skipBox.setAlignment(Pos.TOP_CENTER);
        skipBox.setTranslateY(20);
        skipBox.setTranslateX(-70);
        skipButton.setPrefHeight(40);
        skipButton.setPrefWidth(90);
        skipButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
        skipButton.setOnMouseEntered(e -> skipButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        skipButton.setOnMouseExited(e -> skipButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));

        // Create the label to display the best move
        bestMoveLabel = new Label("Best Move: ");
        bestMoveLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #FFFFFF;");

        bestMoveButton = new Button("Best Move");
        bestMoveButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
        bestMoveButton.setOnMouseEntered(e -> bestMoveButton.setStyle("-fx-background-color: #00FF00; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        bestMoveButton.setOnMouseExited(e -> bestMoveButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));

        bestMoveButton.setLayoutX(1600);
        bestMoveButton.setLayoutY(100);
        bestMoveLabel.setLayoutX(1600);
        bestMoveLabel.setLayoutY(200);
        
        Button bestSim = new Button("AI Simulate");
        bestSim.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
        bestSim.setOnMouseEntered(e -> bestSim.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        bestSim.setOnMouseExited(e -> bestSim.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        bestSim.setLayoutX(1600);
        bestSim.setLayoutY(100);
        
        // Create an HBox for the best move button and label
        bestMoveButton.setTranslateX(580);
        bestMoveButton.setTranslateY(-430);
        bestSim.setTranslateX(780);
        bestSim.setTranslateY(-465);
        
        bestSim.setOnAction(event -> {
            bestMoveButton.setDisable(true);
            skipButton.setDisable(true);
            WallTrap.setDisable(true);
            Volley.setDisable(true);
            SniperCannon.setDisable(true);
            PiercingCannon.setDisable(true);
            bestSim.setDisable(true);
            executeGameLoop();
        });
        
        bestMoveButton.setOnAction(event -> {
            MoveSuggestion mv = new MoveSuggestion();
            Lane lastElement = null; 
            for (Lane element : battle.getLanes()) {
                lastElement = element;
            }
            String bestMove = mv.suggestBestMove(lastElement, battle.getWeaponFactory(), battle.getResourcesGathered());
            int i = lanenumber(battle.getOriginalLanes(), lastElement);
            switch(bestMove) {
                case "Skip":
                    battle.passTurn();
                    break;
                case "Anti-Titan Shell":
                    try {
                        battle.purchaseWeapon(1, lastElement);
                        this.piercingCount[i]++;
                    } catch (InsufficientResourcesException | InvalidLaneException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Long Range Spear":
                    try {
                        battle.purchaseWeapon(2, lastElement);
                        this.SniperCount[i]++;
                    } catch (InsufficientResourcesException | InvalidLaneException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Wall Spread Cannon":
                    try {
                        battle.purchaseWeapon(3, lastElement);
                        this.VolleyCount[i]++;
                    } catch (InsufficientResourcesException | InvalidLaneException e) {
                        e.printStackTrace();
                    }
                    break;
                case "Proximity Trap":
                    try {
                        battle.purchaseWeapon(4, lastElement);
                        this.WallTrapCount[i]++;
                    } catch (InsufficientResourcesException | InvalidLaneException e) {
                        e.printStackTrace();
                    }
                    break; 
            }
            updateView();
        });

        // Add lanesBox, topBox, bottomBox, skipBox, and bestMoveBox to the rootPane
        StackPane rootPane = new StackPane();
        rootPane.getChildren().addAll(backgroundImageView, lanesBox, topBox, bottomBox, skipBox, bestMoveButton, bestSim);

        // Create the scene
        Scene scene = new Scene(rootPane);

        // Set the scene and stage properties
        primaryStage.setScene(scene);
        primaryStage.setHeight(1900);
        primaryStage.setWidth(1900);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
    
    private Image getHighResolutionImage(Image image, int width, int height) {
	    // Check if the input image is null
	    if (image == null) {
	        return null;
	    }

	    // Create a new writable image with the desired width and height
	    WritableImage highResImage = new WritableImage(width, height);

	    // Get the pixel reader from the input image
	    PixelReader pixelReader = image.getPixelReader();

	    // Get the pixel writer from the writable image
	    PixelWriter pixelWriter = highResImage.getPixelWriter();

	    // Scale the image using bilinear interpolation for high quality
	    for (int y = 0; y < height; y++) {
	        for (int x = 0; x < width; x++) {
	            // Calculate the corresponding coordinates in the original image
	            double originalX = x * (image.getWidth() / width);
	            double originalY = y * (image.getHeight() / height);

	            // Get the color from the original image and set it to the writable image
	            Color color = pixelReader.getColor((int) originalX, (int) originalY);
	            pixelWriter.setColor(x, y, color);
	        }
	    }

	    return highResImage;
	}

	private void executeGameLoop() {
        if (battle.isGameOver()) {
            GameOver gv = new GameOver();
            gv.displayScene(primaryStage, battle,HighScore);
            return;
        }
        
        MoveSuggestion mv = new MoveSuggestion();
        Lane lastElement = null; 
        for (Lane element : battle.getLanes()) {
            lastElement = element;
        }
        
        String bestMove = mv.suggestBestMove(lastElement, battle.getWeaponFactory(), battle.getResourcesGathered());
        int i = lanenumber(battle.getOriginalLanes(), lastElement);

        try {
            switch(bestMove) {
                case "Skip":
                    battle.passTurn();
                    break;
                    
                case "Anti-Titan Shell":
                    battle.purchaseWeapon(1, lastElement);
                    this.piercingCount[i]++;
                    break;
                    
                case "Long Range Spear":
                    battle.purchaseWeapon(2, lastElement);
                    this.SniperCount[i]++;
                    break;
                    
                case "Wall Spread Cannon":
                    battle.purchaseWeapon(3, lastElement);
                    this.VolleyCount[i]++;
                    break;
                    
                case "Proximity Trap":
                    battle.purchaseWeapon(4, lastElement);
                    this.WallTrapCount[i]++;
                    break;
            }
        } catch (InsufficientResourcesException | InvalidLaneException e) {
            e.printStackTrace();
        }

        this.updateView();

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> executeGameLoop());
        pause.play();
    }


    private int lanenumber(ArrayList<Lane> originalLanes, Lane targetLane) {
        for (int i = 0; i < originalLanes.size(); i++) {
            if (originalLanes.get(i) == targetLane) { // Compare the lane references directly
                return i ; // Lane numbers are 1-based
            }
        }
        return 0; // Return 0 if the lane is not found, though this should not happen
    }



    private TranslateTransition createTranslateTransition(Node node, double fromX, double toX, double durationInSeconds) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), node);
        transition.setFromX(fromX);
        transition.setToX(toX);
        transition.setCycleCount(1);
        transition.setAutoReverse(false);
        return transition;
    }

    
    private void addTitansToLane(VBox laneBox, Lane lane, int i) {
        // Create a StackPane to hold the titan images and their health labels
        StackPane titanStackPane = new StackPane();
        
     // Create a rectangle for the wall
        HBox laneWithWall = new HBox();
        laneWithWall.setAlignment(Pos.CENTER_LEFT); // Align the wall to the left side of the lane

     // Load wall images
        Image wallImageLowHealth = new Image("file:wall_low_health.jpeg");
        Image wallImageMediumHealth = new Image("file:wall_medium_health.jpeg");
        Image wallImageHighHealth = new Image("file:wall_high_health.jpeg");

        // Create ImageView for the wall and set image based on wall health
        ImageView wallImageView = new ImageView();
        wallImageView.setSmooth(true);
        wallImageView.setPreserveRatio(true);
        int wallHealth = lane.getLaneWall().getCurrentHealth();
        if (wallHealth >= 5000) {
            wallImageView.setImage(wallImageHighHealth);
        } else if (wallHealth >= 1500) {
            wallImageView.setImage(wallImageMediumHealth);
        } else {
            wallImageView.setImage(wallImageLowHealth);
        }
        
        wallImageView.setFitWidth(120);
        wallImageView.setFitHeight(100);

        // Add the wall image to the laneWithWall
        laneWithWall.getChildren().add(wallImageView);

     // Create a label to display the wall's health
        Label wallHealthLabel = new Label("Wall Health: " + wallHealth);
        if (wallHealth == 0)
        	wallHealthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #FF0000;");
        else
        	wallHealthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #000000;");
        
        // Create a label to display the danger level
        Label dangerLevelLabel = new Label("Danger Level: " + lane.getDangerLevel());
        dangerLevelLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #FFFFFF;");

        // Add the wall health label above the wall
        VBox wallInfoBox = new VBox();
        wallInfoBox.getChildren().addAll(dangerLevelLabel, wallImageView, wallHealthLabel);
        wallInfoBox.setAlignment(Pos.CENTER);

        HBox weaponBox = new HBox();
        weaponBox.setAlignment(Pos.CENTER_RIGHT); // Align weapons to the right side of the lane
        weaponBox.setSpacing(5);
        weaponBox.setTranslateX(1000);
        
        // Image paths
        Image PirecingCannonImage = new Image("file:PiercingCannon.jpeg");
        Image SniperCannonImage = new Image("file:SniperCannon.jpeg");
        Image VolleySpreadCannonImage = new Image("file:VolleySpread.jpeg");
        Image WallTrapImage = new Image("file:WallTrap.jpeg");

        // Create ImageView for each weapon image
        ImageView weaponImageView1 = new ImageView(PirecingCannonImage);
        weaponImageView1.setFitWidth(80);
        weaponImageView1.setFitHeight(80);

        ImageView weaponImageView2 = new ImageView(SniperCannonImage);
        weaponImageView2.setFitWidth(80);
        weaponImageView2.setFitHeight(80);

        ImageView weaponImageView3 = new ImageView(VolleySpreadCannonImage);
        weaponImageView3.setFitWidth(80);
        weaponImageView3.setFitHeight(80);

        ImageView weaponImageView4 = new ImageView(WallTrapImage);
        weaponImageView4.setFitWidth(80);
        weaponImageView4.setFitHeight(80);

        // Create labels for each image
        Label label1 = new Label("Count: " + this.piercingCount[i]);
        Label label2 = new Label("Count: " + this.SniperCount[i]);
        Label label3 = new Label("Count: " + this.VolleyCount[i]);
        Label label4 = new Label("Count: " + this.WallTrapCount[i]);

        // Set label styles
        label1.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
        label2.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
        label3.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
        label4.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");

        // Create VBox for each weapon image and its label
        VBox vbox1 = new VBox(5);
        vbox1.getChildren().addAll(weaponImageView1, label1);
        vbox1.setAlignment(Pos.CENTER);

        VBox vbox2 = new VBox(5);
        vbox2.getChildren().addAll(weaponImageView2, label2);
        vbox2.setAlignment(Pos.CENTER);

        VBox vbox3 = new VBox(5);
        vbox3.getChildren().addAll(weaponImageView3, label3);
        vbox3.setAlignment(Pos.CENTER);

        VBox vbox4 = new VBox(5);
        vbox4.getChildren().addAll(weaponImageView4, label4);
        vbox4.setAlignment(Pos.CENTER);

        // Create HBox for all weapon images and labels
        weaponBox = new HBox(0);
        weaponBox.getChildren().addAll(vbox1, vbox2, vbox3, vbox4);
        weaponBox.setAlignment(Pos.CENTER);

        // Add the laneWithWall and the weaponBox side by side within the laneBox
        HBox laneContent = new HBox();
        laneContent.getChildren().addAll(weaponBox, laneWithWall); // Switched order of adding
        laneContent.setAlignment(Pos.CENTER_LEFT);
        laneContent.setSpacing(20); // Adjust spacing between weaponBox and laneWithWall
        weaponBox.setTranslateX(-5);
        laneWithWall.setTranslateX(-5);
        
        laneBox.getChildren().addAll(laneContent);
        laneBox.setAlignment(Pos.CENTER_LEFT); // Align the laneBox content to the left
        
        // Iterate through titans in the lane and add images to the titanStackPane
        for (Titan titan : lane.getTitans()) {
            Image titanImage = null;
            
            if (titan instanceof AbnormalTitan) {
                titanImage = this.images.get(4);
            } else if (titan instanceof ArmoredTitan) {
                titanImage = this.images.get(5);
            } else if (titan instanceof ColossalTitan) {
                titanImage = this.images.get(6);
            } else if (titan instanceof PureTitan) {
                titanImage = this.images.get(7);
            }

            // Create ImageView for the titan image
            ImageView titanImageView = new ImageView(titanImage);
            titanImageView.setFitWidth(60); // Adjust width of the titan image
            titanImageView.setFitHeight(50 + titan.getHeightInMeters()); // Adjust height of the titan image

            // Create a label to display the titan's speed
            Label speedLabel = new Label("Speed: " + titan.getSpeed());
            speedLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #000000;"); // Set text color to black

            // Create a progress bar to display the titan's health
            ProgressBar healthProgressBar = new ProgressBar();
            healthProgressBar.setProgress(titan.getCurrentHealth() / (double) titan.getBaseHealth());
            healthProgressBar.setPrefWidth(80);
            if (titan instanceof ColossalTitan) {
                if (titan.getCurrentHealth() <= 200)
                    healthProgressBar.setStyle("-fx-accent: red;");
                else if (titan.getCurrentHealth() > 200 && titan.getCurrentHealth() <= 500)
                    healthProgressBar.setStyle("-fx-accent: orange;");
                else
                    healthProgressBar.setStyle("-fx-accent: green;");
            } else if (titan instanceof ArmoredTitan) {    
                if (titan.getCurrentHealth() <= 40)
                    healthProgressBar.setStyle("-fx-accent: red;");
                else if (titan.getCurrentHealth() > 40 && titan.getCurrentHealth() <= 90)
                    healthProgressBar.setStyle("-fx-accent: orange;");
                else
                    healthProgressBar.setStyle("-fx-accent: green;");
            } else {
                if (titan.getCurrentHealth() <= 30)
                    healthProgressBar.setStyle("-fx-accent: red;");
                else if (titan.getCurrentHealth() > 30 && titan.getCurrentHealth() <= 50)
                    healthProgressBar.setStyle("-fx-accent: orange;");
                else
                    healthProgressBar.setStyle("-fx-accent: green;");
            }
            healthProgressBar.setPrefSize(80, 4);
            
            // Create a VBox to hold the speed label, titan image, and health label
            VBox titanInfoBox = new VBox();
            titanInfoBox.setAlignment(Pos.CENTER);
            

            // Calculate the new translateX value based on titan distance
            double newTranslateX = titan.getDistance(); // Assuming spawnDistance is relative to the left edge of the lane

         // Check if the titan was already added before
         if (titanPositions.containsKey(titan)) {
        	 titanInfoBox.setTranslateX(titanPositions.get(titan));
             double previousTranslateX = titanPositions.get(titan);

             // Create and apply the animation
             TranslateTransition transition = createTranslateTransition(titanInfoBox, previousTranslateX, newTranslateX, 1.0); // 1 second duration for animation
             transition.play();
         } else {
             // Set the initial position directly
             titanInfoBox.setTranslateX(newTranslateX);
         }

         // Update the previous position in the map
         titanPositions.put(titan, newTranslateX);


            // Add the titan info box to the titanStackPane
            titanStackPane.getChildren().add(titanInfoBox);
            titanInfoBox.getChildren().addAll(healthProgressBar, titanImageView, speedLabel);
        }

        // Add the titanStackPane to the laneWithWall
        laneWithWall.getChildren().addAll(weaponBox, wallInfoBox, titanStackPane);
        laneBox.getChildren().add(laneWithWall);
    }
    
    private Button createWeaponButton(WeaponRegistry weapon, String path, Battle b, Stage primaryStage) {
        Button button = new Button();
        button.setPrefSize(100, 100);

        // Set weapon image
        ImageView imageView = new ImageView(new Image(path));
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        button.setGraphic(imageView);

        // Set weapon information as tooltip
        String s = "";
        switch (weapon.getCode()) {
            case 1:
                s = "Piercing Cannon";
                break;
            case 2:
                s = "Sniper Cannon";
                break;
            case 3:
                s = "Volley Spread Cannon";
                break;
            case 4:
                s = "Walltrap";
                break;
            default:
                s = "Unknown Weapon";
                break;
        }
        String tooltipText = String.format("Name: %s\n Type: %s\n Price: %d\nDamage: %d", weapon.getName(), s, weapon.getPrice(), weapon.getDamage());
        button.setTooltip(new javafx.scene.control.Tooltip(tooltipText));

        // Set button text
        button.setText(String.format("%s\nPrice: %d", weapon.getName(), weapon.getPrice()));

        // Set button action
        button.setOnAction(event -> {
            int i = showButtons(primaryStage, b, weapon.getCode());
            try {
                b.purchaseWeapon(weapon.getCode(), b.getOriginalLanes().get(i));
                switch (weapon.getCode()) {
                    case 1:
                        piercingCount[i]++;
                        break;
                    case 2:
                        SniperCount[i]++;
                        break;
                    case 3:
                        VolleyCount[i]++;
                        break;
                    case 4:
                        WallTrapCount[i]++;
                        break;
                }
                primaryStage.setFullScreen(true);
                updateView();
            } catch (InsufficientResourcesException e) {
                showErrorDialog("Error", "Insufficient resources. Cannot purchase weapon.", primaryStage, b);
            } catch (InvalidLaneException e) {
                showErrorDialog2("Error", "Lane is destroyed. Cannot deploy weapon.", primaryStage, b);
            }
        });

        return button;
    }

    private int showButtons(Stage primaryStage, Battle b, int weaponCode) {
        // Create a modal stage
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("Select Lane");
        WeaponFactory wp = null;
        try {
            wp = new WeaponFactory();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Label title = new Label("Select Lane to deploy " + wp.getWeaponShop().get(weaponCode).getName());
        title.setTextFill(Color.BLACK);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));

        // Define an array to hold the selected index
        final int[] selectedIndex = {-1};

        // Create a VBox to layout buttons
        VBox vBox = new VBox(10);  // Adding some spacing between elements
        vBox.setAlignment(Pos.CENTER);

        // Add the title to the VBox
        vBox.getChildren().add(title);

        // Set background image
        Image backgroundImage = new Image("file:Sunset.jpeg"); // Update the path as necessary
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        vBox.setBackground(new Background(bgImage));

        // Create buttons for each lane
        ArrayList<Lane> lanes = b.getOriginalLanes();
        for (int i = 0; i < lanes.size(); i++) {
            Button laneButton = new Button("Lane " + (i + 1));
            laneButton.setPrefSize(300, 100);
            laneButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
            laneButton.setOnMouseEntered(e -> laneButton.setStyle("-fx-background-color: #00FF00; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
            laneButton.setOnMouseExited(e -> laneButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));

            int index = i; // To capture the correct index in lambda expression
            laneButton.setOnAction(e -> {
                // When button clicked, set the index as the user's choice and close the modal stage
                selectedIndex[0] = index;
                modalStage.close();
            });
            vBox.getChildren().add(laneButton);
        }

        // Set scene with VBox
        Scene scene = new Scene(vBox);
        modalStage.setScene(scene);
        modalStage.setFullScreen(true);

        // Show the modal stage and wait for user input
        modalStage.showAndWait();

        // Return the index corresponding to the user's choice
        return selectedIndex[0];
    }



    
    private void showErrorDialog(String title, String message,Stage primaryStage,Battle b) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage); // Assuming primaryStage is accessible
        Button bu = new Button("OK");
        VBox vbox = new VBox(new Label(message), bu);
        bu.setOnAction(event -> {
        	//Weaponshop g = new Weaponshop();
			 try {
				//g.displayScene(primaryStage,b);
				dialogStage.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
                
            });
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle(title);
        dialogStage.show();
    }
    
    private void showErrorDialog2(String title, String message,Stage primaryStage,Battle b) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage); // Assuming primaryStage is accessible
        Button bu = new Button("OK");
        VBox vbox = new VBox(new Label(message), bu);
        bu.setOnAction(event -> {
				dialogStage.close();
                
            });
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 200);
        dialogStage.setScene(dialogScene);
        dialogStage.setTitle(title);
        dialogStage.show();
    }

    public void updateView(){
        // Clear the lanesBox before updating
        lanesBox.getChildren().clear();
        
        // Update top box
        updateTopBox();
        
        // Add all lanes to lanesBox
        for (int i = 0; i < lanes.length; i++) {
            lanesBox.getChildren().add(lanes[i]);
        }
        
        // Move titans within lanes
        moveTitans();
        
        // Check if the game is over
        if(battle.isGameOver()){
        	GameOver gv = new GameOver();
        	gv.displayScene(primaryStage,battle,HighScore);
        }
    }


 
	private void updateTopBox() {
        // Update labels for current score, turn, phase, and resources
        scoreLabel.setText("Current Score: " + battle.getScore());
        turnLabel.setText("Current Turn: " + battle.getNumberOfTurns());
        phaseLabel.setText("Current Phase: " + battle.getBattlePhase());
        resourcesLabel.setText("Current Resources: " + battle.getResourcesGathered());
     // Create the best move button
        

     

      // Create the label to display the best move
      
      // Set action for the best move button
      
      	
    }

    private void moveTitans() {
    	
    	for (int i = 0; i < this.lanes.length; i++) {
    		lanes[i].getChildren().clear();
	        // Add walls and titans to the laneBox
	        addTitansToLane(this.lanes[i], battle.getOriginalLanes().get(i),i);
	    }
    }
    private void initializeImages() {
        images.add(new Image("file:PiercingCannon.jpeg"));
        images.add(new Image("file:SniperCannon.jpeg"));
        images.add(new Image("file:VolleySpread.jpeg"));
        images.add(new Image("file:WallTrap.jpeg"));
        images.add(new Image("file:AbnormalTitan.png"));
        images.add(new Image("file:ArmoredTitan.png"));
        images.add(new Image("file:ColossalTitan.png"));
        images.add(new Image("file:PureTitan.png"));
    }
   



}

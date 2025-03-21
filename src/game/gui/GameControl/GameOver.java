package game.gui.GameControl;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import game.engine.Battle;

public class GameOver {

    public void displayScene(Stage primaryStage, Battle b, int h) {
        // Create the root node
        StackPane root = new StackPane();
        
        // Load background image
        Image backgroundImage = new Image("file:GameOver.jpeg"); // Adjust the path as per your file structure
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setImage(getHighResolutionImage(backgroundImage, 1920, 1080));
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        // Create VBox to hold content
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setTranslateY(250); // Adjust vertical position as needed

        final int[] highScore = {h};
        if (b.getScore() > highScore[0]) {
            highScore[0] = b.getScore();
        }

        Text highScoreText = new Text("High Score: " + highScore[0]);
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 50)); // Increased font size
        highScoreText.setFill(Color.DARKGOLDENROD); // Gold color
        highScoreText.setEffect(new javafx.scene.effect.DropShadow(10, Color.BLACK)); // Text shadow

        // Display the score
        Text scoreText = new Text("Score: " + b.getScore());
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 50)); // Increased font size
        scoreText.setFill(Color.DARKGOLDENROD); // Gold color
        scoreText.setEffect(new javafx.scene.effect.DropShadow(10, Color.BLACK)); // Text shadow
        
        Text turnsText = new Text("Number of Turns: " + b.getNumberOfTurns());
        turnsText.setFont(Font.font("Arial", FontWeight.BOLD, 50)); // Increased font size
        turnsText.setFill(Color.DARKGOLDENROD); // Gold color
        turnsText.setEffect(new javafx.scene.effect.DropShadow(10, Color.BLACK)); // Text shadow
        
        // Create button to switch scene
        Button switchButton = new Button("Play Again");
        switchButton.setPrefWidth(200);
        switchButton.setPrefHeight(50);
        switchButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.75));

        switchButton.setEffect(dropShadow);
        switchButton.setOnAction(e -> {
            Modes m = new Modes();
            m.displayscene(primaryStage, highScore[0]); // Use array to pass high score
        });
        
        // Add content to VBox
        content.getChildren().addAll(highScoreText, scoreText, turnsText, switchButton);
        
        // Add background and content to root node
        root.getChildren().addAll(backgroundImageView, content);
        
        // Create and set the scene
        Scene scene = new Scene(root, 1920, 1080); // Match scene size with stage size
        scene.getStylesheets().add("file:buttonStyles.css"); // Link the CSS file
        primaryStage.setScene(scene);
        
        // Set fullscreen mode
        primaryStage.setFullScreen(true);
        
        // Show the stage
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
}

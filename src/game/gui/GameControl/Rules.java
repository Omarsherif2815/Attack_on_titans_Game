package game.gui.GameControl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Rules  {
    
    public void displayScene(Stage primaryStage) {
        // Load background image
        Image backgroundImage = new Image("file:download (2).jpeg");

     // Create a background image view with high-quality interpolation
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setImage(getHighResolutionImage(backgroundImage, 1920, 1080));
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        
        // Game rules text
        Text title = new Text("Game Rules:");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        title.setEffect(new DropShadow(10, Color.BLACK));
        
       
        Text gameRulesText = new Text(". This is a tower defence game, your main goal is to protect your lane walls from the approaching titans by buying weapons \n into the lanes to attack the titans present in it. \n\n. The game ends when all lane walls are destroyed.");
        gameRulesText.setFont(Font.font("Arial", FontWeight.NORMAL, 30));
        gameRulesText.setFill(Color.WHITE);
        gameRulesText.setEffect(new DropShadow(5, Color.BLACK));
        
        // Button to switch to another scene
        Button switchSceneButton = new Button("Continue");
        switchSceneButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;");
        switchSceneButton.setEffect(new DropShadow(10, Color.BLACK));
        switchSceneButton.setOnMouseEntered(e -> switchSceneButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        switchSceneButton.setOnMouseExited(e -> switchSceneButton.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 2px;"));
        switchSceneButton.setOnAction(event -> {
            // Switch to another scene when the button is clicked
            Modes x = new Modes();
            x.displayscene(primaryStage,0);
        });

        // Layout for game rules, button, and background image
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundImageView, createContent(title, gameRulesText, switchSceneButton));
       
        // Create the scene
        Scene scene = new Scene(root, 800, 600);

        // Set the scene to the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game Rules");
       
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    // Method to create content layout (game rules text and button)
    private AnchorPane createContent(Text title, Text gameRulesText, Button switchSceneButton) {
        AnchorPane content = new AnchorPane(); // Vertical spacing between nodes
        content.setPadding(new Insets(50)); // Padding around the layout
        title.setLayoutY(200);
        title.setLayoutX(750);
        content.getChildren().add(title);
        gameRulesText.setLayoutY(680);
        gameRulesText.setLayoutX(110);
        content.getChildren().add(gameRulesText);
        switchSceneButton.setLayoutY(850);
        switchSceneButton.setLayoutX(900);
        switchSceneButton.setPrefSize(150, 50);
        content.getChildren().add(switchSceneButton);
        return content;
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
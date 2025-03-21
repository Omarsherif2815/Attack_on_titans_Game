package game.gui.GameControl;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

public class GameStart extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the first scene with a background image
        StackPane root1 = new StackPane();
        // Load background image
        Image backgroundImage = new Image("file:Title.jpeg");

        // Create a background image view with high-quality interpolation
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setImage(getHighResolutionImage(backgroundImage, 1920, 1080));
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

        
        // Create and style the button
        Button button = new Button("Start");
        button.setTranslateY(250);
        button.setPrefWidth(110);
        button.setPrefHeight(50);
        button.getStyleClass().add("cool-button");

        // Add shadow effect
        DropShadow shadow = new DropShadow();
        button.setEffect(shadow);

        // Add scale transition for hover effect
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        button.setOnMouseEntered(e -> {
            st.setToX(1.2);
            st.setToY(1.2);
            st.playFromStart();
        });
        button.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });

        root1.getChildren().addAll(backgroundImageView, button);

        Scene scene1 = new Scene(root1);
        scene1.getStylesheets().add("file:buttonStyles.css"); // Load the CSS file

        // Create the second scene
        StackPane root2 = new StackPane();
        root2.getChildren().add(new Button("This is the second scene"));

        // Set button action to switch scenes
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Rules x = new Rules();
                x.displayScene(primaryStage);
            }
        });

        // Set up the stage
        primaryStage.setTitle("Attack on Titans");
        primaryStage.getIcons().add(new Image("file:icon.jpg"));
        primaryStage.setScene(scene1);
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

	public static void main(String[] args) {
        launch(args);
    }
}

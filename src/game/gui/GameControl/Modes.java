package game.gui.GameControl;

import game.engine.Battle;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Modes {

   
	public void displayscene(Stage primaryStage,int h) {
	    // Load background image
	    Image backgroundImage = new Image("file:background.jpeg");

	 // Create a background image view with high-quality interpolation
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setImage(getHighResolutionImage(backgroundImage, 1920, 1080));
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());

	    // Create buttons
	    Button button1 = new Button("Easy");
	    button1.setStyle("-fx-background-color: #00ff00; -fx-font-size: 18px;"); // Set background color to green
	    Button button2 = new Button("Hard");
	    button2.setStyle("-fx-background-color: #ff0000; -fx-font-size: 18px;"); // Set background color to green
	    
	    // Set button sizes
	    button1.setPrefSize(100, 50); // Set preferred width and height for button 1
	    button2.setPrefSize(100, 50); // Set preferred width and height for button 2
	    
	 // Add scale transition for hover effect
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button1);
        button1.setOnMouseEntered(e -> {
            st.setToX(1.3);
            st.setToY(1.3);
            st.playFromStart();
        });
        button1.setOnMouseExited(e -> {
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
        
     // Add scale transition for hover effect
        ScaleTransition st1 = new ScaleTransition(Duration.millis(200), button2);
        button2.setOnMouseEntered(e -> {
            st1.setToX(1.2);
            st1.setToY(1.2);
            st1.playFromStart();
        });
        button2.setOnMouseExited(e -> {
            st1.setToX(1.0);
            st1.setToY(1.0);
            st1.playFromStart();
        });
        
	    // Set button actions
	    button1.setOnAction(event -> {
	        // Switch to another scene when the button is clicked
	        try {
	            Battle b = new Battle(1, 0, 800, 3, 250);
	            Game g = new Game();
	            g.displayScene(primaryStage, b,h);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    button2.setOnAction(event -> {
	        // Switch to another scene when the button is clicked
	        try {
	            Battle b = new Battle(1, 0, 800, 5, 125);
	            Game g = new Game();
	            g.displayScene(primaryStage, b,h);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });

	    // Create title
	    Text title = new Text("Select Mode: ");
	    title.setFill(Color.WHITE);
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 90));

	    // Create a layout for buttons and title
	    VBox layout = new VBox(20); // Vertical spacing between elements
	    layout.setAlignment(Pos.CENTER); // Center align the content
	    layout.setPadding(new Insets(20)); // Padding around the layout
	    layout.setSpacing(50);
	    layout.getChildren().addAll(title, button1, button2);

	    // Stack the background image and layout
	    StackPane root = new StackPane();
	    root.getChildren().addAll(backgroundImageView, layout);

	    // Create the scene
	    Scene scene = new Scene(root);

	    // Set the scene to the stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Mode");
	    
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

}


   

package Default;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InventoryUI.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Load style2.css
        URL style2 = getClass().getResource("style2.css");
        if (style2 != null) {
            scene.getStylesheets().add(style2.toExternalForm());
            System.out.println("✅ Loaded style2.css: " + style2);
        } else {
            System.out.println("❌ style1.css NOT FOUND!");
        }
        
        // Load style1.css
        URL style1 = getClass().getResource("style1.css");
        if (style1 != null) {
            scene.getStylesheets().add(style1.toExternalForm());
            System.out.println("✅ Loaded style1.css: " + style1);
        } else {
            System.out.println("❌ style1.css NOT FOUND!");
        }

        // Load style.css
        URL style = getClass().getResource("style.css");
        if (style != null) {
            scene.getStylesheets().add(style.toExternalForm());
            System.out.println("✅ Loaded style.css: " + style);
        } else {
            System.out.println("❌ style.css NOT FOUND!");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("InvenPharm - Inventory System");
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

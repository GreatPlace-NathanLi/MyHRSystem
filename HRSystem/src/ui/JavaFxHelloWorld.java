package ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.stage.Stage;
public class JavaFxHelloWorld extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final Button button = new Button("Click me");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        Parent root = BorderPaneBuilder.create().center(button).build();
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("德盛人力项目管理");
        primaryStage.show();
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}  

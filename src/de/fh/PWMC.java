package de.fh;

import de.fh.GUI.GameScene;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class PWMC extends Application {

	@Override
	public final void start(Stage primaryStage) {
		BorderPane bp = new BorderPane();
		
		primaryStage.setScene(new GameScene(bp, primaryStage));
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}

package de.fh.GUI.util;

import de.fh.util.Vector2;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public final class NewLevelView extends ModalStage {

	private Vector2 vector = new Vector2(0, 0);
	private TextField columns;
	private TextField rows;
	private Stage primaryStage;
	
	private NewLevelView(Stage primaryStage) {
		super(primaryStage);
		this.primaryStage = primaryStage;
	}

	/**
	 * Request a new NewLevelView instance.
	 * 
	 * @param primaryStage
	 * @return 
	 */
	public static NewLevelView create(Stage primaryStage) {
		return new NewLevelView(primaryStage);
	}

	/**
	 * Shows the window. The method blocks until the user closes the dialogue.
	 * If the dialogue is closed by pressing the Button "cancel", then
	 * a <code>Vector2</code> containing (0, 0) is returned.
	 * 
	 * @return a <code>Vector2</code> containing the entered number of columns and 
	 * number of rows for the new level
	 */
	public Vector2 showView() {
		this.setResizable(false);
		this.setTitle("Neues Level erstellen");
		
		BorderPane bp = new BorderPane();
		bp.setPrefWidth(280.0);
		
		
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPadding(new Insets(10.0));
		gp.setHgap(5.0);
		gp.setVgap(10.0);
		
		Label label1 = new Label("Anzahl Spalten:");
		this.columns = new TextField("15");
		gp.addRow(0, label1, this.columns);
		
		Label label2 = new Label("Anzahl Zeilen:");
		this.rows = new TextField("15");
		gp.addRow(1, label2, this.rows);
		
		
		FlowPane fp = new FlowPane();
		fp.setAlignment(Pos.CENTER);
		fp.setPadding(new Insets(20.0));
		fp.setHgap(20.0);
		
		Button ok = new Button("OK");
		ok.setOnAction(ae->this.applyChanges());
		Button cancel = new Button("Abbrechen");
		cancel.setOnAction(ae->this.cancel());
		
		fp.getChildren().addAll(ok, cancel);
		
		
		bp.setCenter(gp);
		bp.setBottom(fp);
		
		Scene scene = new Scene(bp);
		this.setScene(scene);
		
		this.showAndWait();
		
		return this.vector;
	}

	private void applyChanges() {
		try {
			int columns = Integer.valueOf(this.columns.getText());
			int rows = Integer.valueOf(this.rows.getText());
			
			if (columns < 0 || rows < 0)
				throw new NumberFormatException();
			
			this.vector = new Vector2(columns, rows);
			
			this.close();
		} catch (NumberFormatException e) {
			MessageView.create(primaryStage, "Error", "Please enter only positive integer values.").showView();
		}
	}

	private void cancel() {
		this.columns.setText("0");
		this.rows.setText("0");
		
		this.applyChanges();
	}

}

package de.fh.GUI.util;

import de.fh.mapGenerator.MapGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ConfigView extends ModalStage {

	private Stage primaryStage;
	private TextField stenchDis;
	private CheckBox orderFacts;
	
	public ConfigView(Stage primaryStage) {
		super(primaryStage);
		
		this.primaryStage = primaryStage;
	}

	/**
	 * Shows the window. This method blocks until the user closes the dialogue.
	 * If the dialogue is closed by pressing the button "cancel", everything will remain unchanged.
	 */
	public void showView() {
		setResizable(false);
		setTitle("Konfiguration");
		
		BorderPane bp = new BorderPane();
		
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		
		tabPane.getTabs().add(setupTab());
		
		FlowPane fp = new FlowPane();
		fp.setAlignment(Pos.CENTER);
		fp.setPadding(new Insets(20.0));
		fp.setHgap(20.0);
		
		Button ok = new Button("OK");
		ok.setOnAction(ae->applyChanges());
		Button cancel = new Button("Abbrechen");
		cancel.setOnAction(ae->cancel());
		Button defaultSettings = new Button("Default");
		defaultSettings.setOnAction(ae->restoreDefaultConfig());
		
		fp.getChildren().addAll(ok, cancel, defaultSettings);
		
		
		bp.setCenter(tabPane);
		bp.setBottom(fp);
		
		Scene scene = new Scene(bp);
		setScene(scene);
		
		showAndWait();
	}

	private Tab setupTab() {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPadding(new Insets(10.0));
		gp.setHgap(5.0);
		gp.setVgap(10.0);
		
		Label labelStenchDis = new Label("Gestank-Distanz (Felder):");
		stenchDis = new TextField(""+MapGenerator.MAX_STENCH_DISTANCE);
		gp.addRow(0, labelStenchDis, stenchDis);
		
		Label labelOrderFacts = new Label("Fakten sortieren:");
		orderFacts = new CheckBox();
		orderFacts.setSelected(!MapGenerator.ORDER_FACTS_BASED_ON_COORDINATES);
		gp.addRow(1, labelOrderFacts, orderFacts);
		
		Tab tab = new Tab("Editor", gp);
		return tab;
	}

	private void applyChanges() {
		try {
			int stenchDis = Integer.valueOf(this.stenchDis.getText());
			if (stenchDis < 0)
				throw new NumberFormatException();
			
			if (stenchDis > 15)
				stenchDis = 15;
			MapGenerator.MAX_STENCH_DISTANCE = stenchDis;
			
			MapGenerator.ORDER_FACTS_BASED_ON_COORDINATES = !orderFacts.isSelected();
			
			close();
		} catch (NumberFormatException e) {
			MessageView.create(primaryStage, "Error", "Please enter only positive integer values.").showView();
		}
	}

	private void restoreDefaultConfig() {
		stenchDis.setText(""+3);
		orderFacts.setSelected(true);
	}

	private void cancel() {
		close();
	}

}

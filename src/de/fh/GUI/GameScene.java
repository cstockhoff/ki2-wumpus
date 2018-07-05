package de.fh.GUI;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jpl7.JPL;

import de.fh.GUI.util.ConfigView;
import de.fh.GUI.util.NewLevelView;
import de.fh.dataManagement.DataHandler;
import de.fh.dataManagement.SerializingLevelDAO;
import de.fh.level.ITileType;
import de.fh.level.Level;
import de.fh.level.Level.WumpusTileType;
import de.fh.level.Tile;
import de.fh.level.TileTool;
import de.fh.mapGenerator.MapGenerator;
import de.fh.util.Util;
import de.fh.util.Vector2;

public class GameScene extends Scene {

	/** The default directory to save and load levels */
	private static final String LEVEL_PATH = "data/level/";
	/** The maximum amount of columns or rows a newly created level can have */
	public static final int MAX_LEVEL_SIZE = 100;
	/** The spacing between individual tiles */
	protected static final double TILE_SPACING = 1.0;
	
	/** The primary stage for this application */
	protected Stage primaryStage;

	/** The filename of the level backup, is used to reset the level */
	protected static final String BACKUP_LEVEL_NAME = "level.bak";
	
	protected Level level;
	/** The currently selected Tool */
	protected TileTool selectedTool;
	/** Canvas to draw the current level onto */
	private Canvas canvas;
	/** GraphicsContext used to draw onto the Canvas */
	protected GraphicsContext gc;
	
	private Thread renderThread;
	/** The current x coordinate of the mouse */
	private SimpleIntegerProperty mouseX = new SimpleIntegerProperty();
	/** The current y coordinate of the mouse */
	private SimpleIntegerProperty mouseY = new SimpleIntegerProperty();
	
	private Label levelName;
	
	private boolean dragNDrop;

	public GameScene(BorderPane bp, Stage primaryStage) {
		super(bp);
		
		this.primaryStage = primaryStage;
		
		JPL.init();
		
		level = new Level(15, 15);
		save(BACKUP_LEVEL_NAME);
		
		primaryStage.setTitle("Prolog Wumpus Map Generator");
		
		bp.setTop(createMenuBar());
		bp.setCenter(createEditor());
		bp.setBottom(createStatusBar());
		
		renderThread = new Thread(()->{
			while (true) {
				try {
					//If the time the game sleeps each circle is way higher
					//then the interval in which everything is rendered, then
					//there will be huge frame gaps
					//Thus the time is adjusted here
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//If a drag and drop gesture is taking place custom rendering is used
				//and the default rendering thread is idle
				if (!dragNDrop)
					Platform.runLater(()->redrawCanvas());
			}
		});
		renderThread.setDaemon(true);
		renderThread.start();
		
		this.primaryStage.setOnCloseRequest(we->quit());
	}

	/**
	 * Creates the GUI's menuBar.
	 * @return A new menubar.
	 */
	private MenuBar createMenuBar() {
		MenuItem newLevel = new MenuItem("Neues Level erstellen");
		newLevel.setOnAction(ae->newLevel());
		newLevel.setAccelerator(KeyCombination.valueOf("Ctrl+N"));
		//
		MenuItem loadLevel = new MenuItem("Level laden");
		loadLevel.setOnAction(ae->loadLevel());
		loadLevel.setAccelerator(KeyCombination.valueOf("Ctrl+L"));
		//
		MenuItem saveLevel = new MenuItem("Level speichern");
		saveLevel.setOnAction(ae->saveLevel());
		saveLevel.setAccelerator(KeyCombination.valueOf("Ctrl+S"));
		//
		MenuItem saveLevelAs = new MenuItem("Level speichern unter...");
		saveLevelAs.setOnAction(ae->saveLevelAs());
		//
		MenuItem saveLevelAsProlog = new MenuItem("Level als .pl speichern");
		saveLevelAsProlog.setOnAction(ae->saveLevelAsProlog());
		//
		MenuItem config = new MenuItem("Konfiguration");
		config.setOnAction(ae->showConfigView());
		//
		MenuItem quit = new MenuItem("Beenden");
		quit.setOnAction(ae->quit());
		quit.setAccelerator(KeyCombination.valueOf("Ctrl+Q"));
		
		Menu dataMenu = new Menu("Datei");
		dataMenu.getItems().addAll(newLevel, new SeparatorMenuItem(), loadLevel, saveLevel, 
				saveLevelAs, new SeparatorMenuItem(),
				saveLevelAsProlog,
				new SeparatorMenuItem(), config, new SeparatorMenuItem(), quit);
		
		MenuBar mb = new MenuBar();
		mb.getMenus().addAll(dataMenu);
		
		return mb;
	}

	/**
	 * Shows the user a <code>NewLevelView</code> dialogue.<br>
	 * Waits for input and creates a new level based on the entered number of columns and<br>
	 * number of rows.
	 */
	private void newLevel() {
		Vector2 v = NewLevelView.create(primaryStage).showView();
		
		//If the returned width and height values are valid
		if (v.getX() > 0 && v.getY() > 0) {
			//Adjust width and height if too small / big
			int width = v.getX() < 4 ? 4 : v.getX() > MAX_LEVEL_SIZE ? MAX_LEVEL_SIZE : v.getX();
			int height = v.getY() < 4 ? 4 : v.getY() > MAX_LEVEL_SIZE ? MAX_LEVEL_SIZE : v.getY();
			
			level = new Level(width, height);
			save(BACKUP_LEVEL_NAME);
			refreshCanvas();
			levelName.setText(level.getName());
		}
	}

	private void loadLevel() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(LEVEL_PATH));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("map", "*.*"));
		
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		
		if (selectedFile != null) {
			try {
				level = (Level) new SerializingLevelDAO().load(selectedFile.getPath());
				save(BACKUP_LEVEL_NAME);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			refreshCanvas();
			levelName.setText(level.getName());
		}
	}

	private void save(String filename) {
		try {
			new SerializingLevelDAO().save(filename, level);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveLevel() {
		String filename = level.getName();
		
		if (filename == null)
			saveLevelAs();
		else {
			save(LEVEL_PATH+filename);
			save(BACKUP_LEVEL_NAME);
		}
	}

	private void saveLevelAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(LEVEL_PATH));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("map", "*.*"));
		
		File selectedFile = fileChooser.showSaveDialog(primaryStage);
		
		if (selectedFile != null) {
			level.setName(selectedFile.getName());
			save(selectedFile.getPath());
			save(BACKUP_LEVEL_NAME);
			levelName.setText(level.getName());
		}
	}

	private void saveLevelAsProlog() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(LEVEL_PATH));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("pl", "*.*"));
		
		File selectedFile = fileChooser.showSaveDialog(primaryStage);
		
		if (selectedFile != null) {
			MapGenerator.saveLevelAsProlog(level.getTiles(), LEVEL_PATH+selectedFile.getName());
		}
	}

	/**
	 * Stops the game if running and terminates the application.
	 */
	private void quit() {
		Platform.exit();
	}

	private void showConfigView() {
		new ConfigView(primaryStage).showView();
	}

	private Node createEditor() {
		ToolBar controls = new ToolBar();
		
		Button writeMap = new Button("Write Prolog Map");
		writeMap.setOnAction(ae->writeMap());
		Button loadMap = new Button("Load Prolog Map");
		loadMap.setOnAction(ae->loadMap());
		
		Button resetMap = new Button("Reset Map");
		resetMap.setOnAction(ae->resetLevel());
		
		Button visitEveryField = new Button("Visit every empty field");
		visitEveryField.setOnAction(ae->visitEveryField());
		
		
		controls.getItems().addAll(writeMap, loadMap, resetMap, visitEveryField);
		
		ToolBar tools = new ToolBar();
		tools.getItems().addAll(createToolButtons());
		
		canvas = new Canvas();
		
		setupMouseActions();
		
		refreshCanvas();
		
		ScrollPane sp = new ScrollPane();
		sp.setContent(canvas);
		
		VBox vb = new VBox();
		vb.getChildren().addAll(new Separator(), controls, new Separator(), 
				tools, new Separator(), sp);
		
		return vb;
	}

	private void visitEveryField() {
		level.visitEveryField();
	}

	private void writeMap() {
		MapGenerator.generate(level.getTiles(), "weltkarte.pl");
	}

	private void loadMap() {
		level.setTiles(MapGenerator.loadMap(level.getTiles()));
		refreshCanvas();
	}

	private void setupMouseActions() {
		canvas.setOnMouseMoved(me->{
			//Remember the current position of the mouse
			mouseX.set(localXToGameX(me.getX()));
			mouseY.set(localYToGameY(me.getY()));
		});
		
		canvas.setOnDragDetected(me->{
			Dragboard db = startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			int x = (int) me.getX();
			int y = (int) me.getY();
			content.putString(""+x+","+y);
			
			//Remember which mouse button was pressed
			DataFormat df = DataFormat.lookupMimeType("MouseButton") == null ? new DataFormat("MouseButton")
					: DataFormat.lookupMimeType("MouseButton");
			content.put(df, me.getButton());
			
			db.setContent(content);
			dragNDrop = true;
			me.consume();
		});
		
		canvas.setOnDragOver(de->{
			//Remember the current position of the mouse
			mouseX.set(localXToGameX(de.getX()));
			mouseY.set(localYToGameY(de.getY()));
			
	        Dragboard db = de.getDragboard();
	        if (db.hasString()) {
	        	double endX = de.getX();
	        	double endY = de.getY();
				String[] s = db.getString().split(",");
				double startX = (int) (Integer.valueOf(s[0]));
				double startY = (int) (Integer.valueOf(s[1]));
				
		        redrawCanvas();
				strokeDragNDropFrame(startX, startY, endX, endY);
				
	            de.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	        }
	        de.consume();
		});
		
		canvas.setOnDragDropped(de->{
			dragNDrop = false;
			Dragboard db = de.getDragboard();
			boolean success = false;
			
			if (db.hasString() && db.hasContent(DataFormat.lookupMimeType("MouseButton"))) {
				int endX = (int) (de.getX() / (DataHandler.IMAGE_WIDTH + TILE_SPACING));
				int endY = (int) (de.getY() / (DataHandler.IMAGE_HEIGHT + TILE_SPACING));
				String[] s = db.getString().split(",");
				int startX = (int) (Integer.valueOf(s[0]) / (DataHandler.IMAGE_WIDTH + TILE_SPACING));
				int startY = (int) (Integer.valueOf(s[1]) / (DataHandler.IMAGE_HEIGHT + TILE_SPACING));
				
				MouseButton mb = (MouseButton) db.getContent(DataFormat.lookupMimeType("MouseButton"));
				if (mb == MouseButton.PRIMARY) {
					level.useTool(selectedTool, startX, startY, endX, endY);
				} else if (mb == MouseButton.SECONDARY) {
					level.useTool(new TileTool(WumpusTileType.EMPTY), startX, startY, endX, endY);
				}
				
				success = true;
			}
			de.setDropCompleted(success);
			de.consume();
		});
		
		canvas.setOnMouseReleased(me->{
			if (me.getButton() == MouseButton.PRIMARY) {
				if (selectedTool != null) {
					//Adjust mouseX/Y to account for the grid's spacing
					int x = (int) (me.getX() / (DataHandler.IMAGE_WIDTH+TILE_SPACING));
					int y = (int) (me.getY() / (DataHandler.IMAGE_HEIGHT+TILE_SPACING));
					level.useTool(selectedTool, x, y);
				}
			} else if (me.getButton() == MouseButton.SECONDARY) {
				//Adjust mouseX/Y to account for the grid's spacing
				int x = (int) (me.getX() / (DataHandler.IMAGE_WIDTH+TILE_SPACING));
				int y = (int) (me.getY() / (DataHandler.IMAGE_HEIGHT+TILE_SPACING));
				level.useTool(new TileTool(WumpusTileType.EMPTY), x, y);
			}
		});
	}

	private void strokeDragNDropFrame(double startX, double startY, double endX, double endY) {
		gc.strokeLine(startX, startY, endX, startY);
		gc.strokeLine(startX, startY, startX, endY);
		gc.strokeLine(endX, startY, endX, endY);
		gc.strokeLine(startX, endY, endX, endY);
	}

	/**
	 * Creates an array of ToggleButton based on getTileTypes() to select a TileTool.
	 * @return An array of ToggleButtons.
	 */
	private ArrayList<ToggleButton> createToolButtons() {
		ArrayList<ToggleButton> result = new ArrayList<>(getTileTypes().length);
		ToggleGroup toggleGroup = new ToggleGroup();
		
		for (int i = 0; i < getTileTypes().length; i++) {
			ITileType t = getTileTypes()[i];
			
			//Don't create a tool for the wall_or_trap type
			if (t == WumpusTileType.WALL_OR_TRAP || t == WumpusTileType.POSSIBLE_PIT ||
					t == WumpusTileType.POSSIBLE_WALL)
				continue;
			
			ToggleButton button = new ToggleButton("");
			button.setFocusTraversable(false);
			button.setGraphic(new ImageView(DataHandler.loadImage(t.getIconImageName())));
			
			button.setOnAction(ae->selectedTool = button.isSelected() ? new TileTool(t) : null);
			button.setTooltip(new Tooltip(Util.capitalize(t.getTooltip())));
			
			result.add(button);
			button.setToggleGroup(toggleGroup);
		}
		
		return result;
	}

	/**
	 * Defines which type of tiles will be used.
	 * @return An enum implementing TileType
	 */
	private ITileType[] getTileTypes() {
		return WumpusTileType.values();
	}

	/**
	 * Adjusts the canvas's width and height to fit the current level.
	 */
	private void refreshCanvas() {
		int width = level.getWidth();
		int height = level.getHeight();
		canvas.setWidth(width * DataHandler.IMAGE_WIDTH + TILE_SPACING * width);
		canvas.setHeight(height * DataHandler.IMAGE_HEIGHT + TILE_SPACING * height);
		gc = canvas.getGraphicsContext2D();
	}

	public void redrawCanvas() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		Tile[][] tiles = level.getTiles();
		
		gc.setFill(Color.WHITE);
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				gc.fillRect(x*DataHandler.IMAGE_WIDTH+TILE_SPACING*x, 
						y*DataHandler.IMAGE_HEIGHT+TILE_SPACING*y,
						DataHandler.IMAGE_WIDTH,
						DataHandler.IMAGE_HEIGHT);
				gc.drawImage(tiles[x][y].getImageProperty().get(),
						x*DataHandler.IMAGE_WIDTH+TILE_SPACING*x, 
						y*DataHandler.IMAGE_HEIGHT+TILE_SPACING*y);
			}
		}
		gc.setFill(Color.BLACK);
	}

	private void resetLevel() {
		try {
			level = (Level) new SerializingLevelDAO().load(BACKUP_LEVEL_NAME);
			save(BACKUP_LEVEL_NAME);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		refreshCanvas();
	}

	/**
	 * Transforms a x coordinate from the coordinate space of this scene into 
	 * the coordinate space of the game. 
	 * @param x
	 * @return
	 */
	protected int localXToGameX(double x) {
		return (int) (x / (DataHandler.IMAGE_WIDTH+TILE_SPACING));
	}

	/**
	 * Transforms a y coordinate from the coordinate space of this scene into 
	 * the coordinate space of the game. 
	 * @param y
	 * @return
	 */
	protected int localYToGameY(double y) {
		return (int) (y / (DataHandler.IMAGE_HEIGHT+TILE_SPACING));
	}

	/**
	 * Creates a node holding information about the current position of the mouse.
	 * @return
	 */
	private Node createStatusBar() {
		Label mouseX = new Label();
		Label mouseY = new Label();
		mouseX.textProperty().bind(this.mouseX.asString());
		mouseY.textProperty().bind(this.mouseY.asString());
		
		levelName = new Label();
		levelName.setText(level.getName());
		
		HBox hb = new HBox();
		hb.setSpacing(5.0);
		hb.getChildren().addAll(levelName, new Label("   "), mouseX, new Label(" | "), mouseY);
		
		return hb;
	}

}

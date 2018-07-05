package de.fh.level;

import java.io.Serializable;

public class Level implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum WumpusTileType implements ITileType {
		EMPTY, WALL, GOLD, PIT, HUNTER, WUMPUS, VISITED, WALL_OR_TRAP, POSSIBLE_PIT, POSSIBLE_WALL;
		
		@Override
		public String getTooltip() {
			switch (this) {
			case EMPTY: return "eraser";
			default: return this.toString().toLowerCase();
			}
		}

		@Override
		public String getIconImageName() {
			switch (this) {
			case EMPTY: return "eraser";
			default: return this.toString().toLowerCase();
			}
		}

		@Override
		public String getTileImageName() {
			switch (this) {
			default: return this.toString().toLowerCase();
			}
		}
	}

	private String name;
	
	/** The two-dimensinal arry of tiles representing the level */
	protected Tile[][] tiles;
	
	/**
	 * Creates a new level with the given width and height.
	 * @param width
	 * @param height
	 */
	public Level(int width, int height) {
		this.name = "unnamed";
		
		tiles = new Tile[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				tiles[x][y] = new Tile(WumpusTileType.EMPTY);
		
		//Create outer walls
		for (int x = 0; x < width; x++) {
			tiles[x][0].setType(WumpusTileType.WALL);
			tiles[x][height-1].setType(WumpusTileType.WALL);
		}
		for (int y = 0; y < height; y++) {
			tiles[0][y].setType(WumpusTileType.WALL);
			tiles[width-1][y].setType(WumpusTileType.WALL);
		}
	}

	public void useTool(TileTool selectedTool, int x, int y) {
		//Do nothing if clicked on outer wall
//		if (x <= 0 || y <= 0 || x >= width - 1 || y >= height - 1)
//			return;
		
		WumpusTileType type = selectedTool.getTileType();
		
		Tile tile = tiles[x][y];
		
		tile.setType(type);
	}

	/**
	 * Uses the given tool on a selected range of tiles
	 * @param selectedTool
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 */
	public void useTool(TileTool selectedTool, int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			for (int i = startX; i <= endX; i++) {
				if (startY < endY) {
					for (int j = startY; j <= endY; j++)
						useTool(selectedTool, i, j);
				} else {
					for (int j = startY; j >= endY; j--)
						useTool(selectedTool, i, j);
				}
			}
		} else {
			for (int i = startX; i >= endX; i--) {
				if (startY < endY) {
					for (int j = startY; j <= endY; j++)
						useTool(selectedTool, i, j);
				} else {
					for (int j = startY; j >= endY; j--)
						useTool(selectedTool, i, j);
				}
			}
		}
	}

	public Tile getTileAt(int x, int y) {
		return tiles[x][y];
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}

	public int getWidth() {
		return tiles.length;
	}

	public int getHeight() {
		return tiles[0].length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Changes all empty tiles to visited tiles
	 */
	public void visitEveryField() {
		for (Tile[] ts : tiles) {
			for (Tile t : ts) {
				if (t.getType() == WumpusTileType.EMPTY)
					t.setType(WumpusTileType.VISITED);
			}
		}
	}

}

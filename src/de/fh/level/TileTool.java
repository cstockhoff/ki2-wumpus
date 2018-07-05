package de.fh.level;

public final class TileTool {

	/* The TileType stored by this tool */
	private ITileType tileType;
	
	public TileTool(ITileType tileType) {
		this.tileType = tileType;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ITileType> T getTileType() {
		return (T) tileType;
	}
}

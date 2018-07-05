package de.fh.level;

import java.io.Serializable;

import de.fh.dataManagement.DataHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

public class Tile implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/** The tile's type */
	private ITileType type;
	
	/** The property storing the tile's image.<br>
	 *  Cannot be serialized and will be restored after deserialization per
	 *  getImageProperty(). */
	private transient SimpleObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	
	/**
	 * Creates a new Tile with the given TileType
	 * @param type
	 */
	public Tile(ITileType type) {
		setType(type);
	}

	public final void refreshImage() {
		imageProperty.set(DataHandler.loadImage(type.getTileImageName()));
	}

	public final ITileType getType() {
		return type;
	}

	public final void setType(ITileType type) {
		this.type = type;
		
		refreshImage();
	}

	/**
	 * Returns the tile's imageProperty, also restores it after deserialization.
	 * @return
	 */
	public final SimpleObjectProperty<Image> getImageProperty() {
		//Restore image after deserialization
		if (imageProperty == null) {
			imageProperty = new SimpleObjectProperty<>();
			refreshImage();
		}
		
		return imageProperty;
	}

}

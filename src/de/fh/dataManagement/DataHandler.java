package de.fh.dataManagement;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import java.nio.IntBuffer;

public class DataHandler {

	/** The default directory for images */
	public static final String IMAGE_PATH = "data/images/";
	/** The width all images loaded by <code>loadImage()</code> will be adjusted to */
	public static final int IMAGE_WIDTH = 32;
	/** The height all images loaded by <code>loadImage()</code> will be adjusted to */
	public static final int IMAGE_HEIGHT = 32;
	
	/**
	 * Loads the corresponding .png image from the default image path specified by IMAGE_PATH.
	 * @param filename The name of the file to load.
	 * @return
	 */
	public static Image loadImage(String filename) {
		return new Image("file:"+IMAGE_PATH+filename+".png", IMAGE_WIDTH, IMAGE_HEIGHT, false, false);
	}
	
	/**
	 * Dyes all fully transparent pixels of a given image in the given color.
	 * @param image
	 * @param color
	 * @return
	 */
	public static Image colorTransparency(Image image, Color color) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		PixelReader reader = image.getPixelReader();
		
		WritableImage result = new WritableImage(width, height);
		PixelWriter writer = result.getPixelWriter();
		
		IntBuffer buffer = IntBuffer.allocate(width*height);
		WritablePixelFormat<IntBuffer> wpf = PixelFormat.getIntArgbInstance();
		
		reader.getPixels(0, 0, width, height, wpf, buffer, width);
		writer.setPixels(0, 0, width, height, wpf, buffer, width);
		
		for (int i = 0; i < width*height; i++) {
			int pixel = buffer.array()[i];
			
			int alpha = ((pixel >> 24) & 0xff);
			
			if (alpha == 0) {
				writer.setColor(i%width, i/width, color);
			}
		}
		
		return result;
	}


}

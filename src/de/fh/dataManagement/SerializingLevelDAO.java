package de.fh.dataManagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.fh.level.Level;

public class SerializingLevelDAO implements ILevelDAO {

	@Override
	public void save(String filename, Level level) throws IOException {
		File file = new File(filename);
		
		try (FileOutputStream fos = new FileOutputStream(file); 
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(level);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Level load(String filename) throws IOException, FileNotFoundException {
		Level result = null;
		File file = new File(filename);
		
		try (FileInputStream fis = new FileInputStream(file); 
				ObjectInputStream ois = new ObjectInputStream(fis)) {
			result = (Level) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}

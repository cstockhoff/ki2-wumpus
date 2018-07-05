package de.fh.dataManagement;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.fh.level.Level;

public interface ILevelDAO {

	public void save(String filename, Level level) throws IOException, FileNotFoundException;
	
	public Level load(String filename) throws IOException, FileNotFoundException;
	
}

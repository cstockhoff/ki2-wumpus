package de.fh.mapGenerator;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;

import org.jpl7.PrologException;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;

import de.fh.dataManagement.DataHandler;
import de.fh.level.Level.WumpusTileType;
import de.fh.level.Tile;
import de.fh.util.Vector2;

public class MapGenerator {

	private static Color[] wumpusColors = {
			Color.RED, Color.GREEN, 
			Color.BLUE, Color.YELLOW,
			Color.ORANGE, Color.PINK};
	
	/**
	 * The maximum Manhattan-distance a wumpus can be smelled from.
	 */
	public static int MAX_STENCH_DISTANCE = 3;
	/**
	 * Determines whether all facts will be ordered based on
	 * the coordinates of their originating tiles or
	 * if all facts will be grouped by their predicate
	 */
	public static boolean ORDER_FACTS_BASED_ON_COORDINATES = true;
	
	/**
	 * Fill the file with the given filename with prolog facts based on the given Tile array.
	 * @param tiles The Tile array from which to generate facts.
	 * @param filename The name of the file to write to.
	 */
	public static void generate(Tile[][] tiles, String filename) {
		try (PrintWriter output = new PrintWriter(new FileOutputStream(filename, false))) {
			
			//Lists to store temporary data,
			//used to order all facts in the knowledge base based on their predicate
			LinkedList<String> visited = new LinkedList<>();
			LinkedList<String> breeze = new LinkedList<>();
			LinkedList<String> bump = new LinkedList<>();
			LinkedList<String> stench = new LinkedList<>();
			LinkedList<String> glitter = new LinkedList<>();
			LinkedList<String> hunter = new LinkedList<>();
			
			//List of wumpi coordinates,
			//used to generate an ID for each wumpi
			LinkedList<Vector2> wumpus = new LinkedList<>();

			output.println(
					"% Deactivate warnings for facts that are discontigous\n"
					+":- discontiguous visited/2.\n"
					+":- discontiguous bump/2.\n"
					+":- discontiguous breeze/2.\n"
					+":- discontiguous glitter/2.\n"
					+":- discontiguous hunter/3.\n"
					+":- discontiguous stench/4.\n\n"
					+ "max_stench_distance(" + MAX_STENCH_DISTANCE + ").\n\n"
					+ "% mapsize(Width,Height).\n"
					+ "mapsize(" + tiles.length + "," + tiles[0].length + ").\n\n"
			);


			if (ORDER_FACTS_BASED_ON_COORDINATES) {
				output.println(
						"% Facts:\n"
								+ "% visited(\"X,Y\")\n"
								+ "% glitter(\"X,Y\")\n"
								+ "% breeze(\"X,Y\")\n"
								+ "% bump(\"X,Y\").\n"
								+ "% hunter(\"X,Y\").\n"
								+ "% stench(\"X,Y,Intensity,WumpusID\").\n\n"
				);
			}


			
			int width = tiles.length;
			int height = tiles[0].length;
			
			//Iterate over all tiles and generate facts
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Tile t = tiles[x][y];
					WumpusTileType type = (WumpusTileType) t.getType();
					
					switch (type) {
					case VISITED:
						if (ORDER_FACTS_BASED_ON_COORDINATES)
							output.println("visited(" + x + "," + y + ").");
						else
							visited.add("visited(" + x + "," + y + ").");
						break;
					case GOLD:
						if (ORDER_FACTS_BASED_ON_COORDINATES) {
							output.println("glitter(" + x + "," + y + ").");
							output.println("visited(" + x + "," + y + ").");
						} else {
							glitter.add("glitter(" + x + "," + y + ").");
							visited.add("visited(" + x + "," + y + ").");
						}
						break;
					case PIT:
						LinkedList<String> breezes = new LinkedList<>();
						
						if (x-1 > 0 && tiles[x-1][y].getType() != WumpusTileType.PIT&&
							tiles[x-1][y].getType() != WumpusTileType.WALL&&
								tiles[x-1][y].getType() == WumpusTileType.VISITED)
							breezes.add("breeze(" + (x-1) + "," + y + ").");
						if (y-1 > 0 && tiles[x][y-1].getType() != WumpusTileType.PIT&&
								tiles[x][y-1].getType() != WumpusTileType.WALL&&
								tiles[x][y-1].getType() == WumpusTileType.VISITED)
							breezes.add("breeze(" + x + "," + (y-1) + ").");
						if (x+1 < width && tiles[x+1][y].getType() != WumpusTileType.PIT&&
								tiles[x+1][y].getType() != WumpusTileType.WALL&&
								tiles[x+1][y].getType() == WumpusTileType.VISITED)
							breezes.add("breeze(" + (x+1) + "," + y + ").");
						if (y+1 < height && tiles[x][y+1].getType() != WumpusTileType.PIT&&
								tiles[x][y+1].getType() != WumpusTileType.WALL&&
								tiles[x][y+1].getType() == WumpusTileType.VISITED)
							breezes.add("breeze(" + x + "," + (y+1) + ").");
						
						if (ORDER_FACTS_BASED_ON_COORDINATES) {
							for (String s : breezes)
								output.println(s);
						} else {
							for (String s : breezes)
								breeze.add(s);
						}
						break;
					case WALL:
						LinkedList<String> bumps = new LinkedList<>();
						
						if (x-1 > 0 && tiles[x-1][y].getType() != WumpusTileType.WALL&&
							tiles[x-1][y].getType() != WumpusTileType.PIT&&
								tiles[x-1][y].getType() == WumpusTileType.VISITED)
							bumps.add("bump(" + (x-1) + "," + y + ").");
						if (y-1 > 0 && tiles[x][y-1].getType() != WumpusTileType.WALL&&
								tiles[x][y-1].getType() != WumpusTileType.PIT&&
								tiles[x][y-1].getType() == WumpusTileType.VISITED)
							bumps.add("bump(" + x + "," + (y-1) + ").");
						if (x+1 < width && tiles[x+1][y].getType() != WumpusTileType.WALL&&
								tiles[x+1][y].getType() != WumpusTileType.PIT&&
								tiles[x+1][y].getType() == WumpusTileType.VISITED)
							bumps.add("bump(" + (x+1) + "," + y + ").");
						if (y+1 < height && tiles[x][y+1].getType() != WumpusTileType.WALL&&
								tiles[x][y+1].getType() != WumpusTileType.PIT&&
								tiles[x][y+1].getType() == WumpusTileType.VISITED)
							bumps.add("bump(" + x + "," + (y+1) + ").");
						
						if (ORDER_FACTS_BASED_ON_COORDINATES) {
							for (String s : bumps)
								output.println(s);
						} else {
							for (String s : bumps)
								bump.add(s);
						}
						break;
					case HUNTER:
						if (ORDER_FACTS_BASED_ON_COORDINATES)
							output.println("hunter(" + x + "," + y + ").");
						else
							hunter.add("hunter(" + x + "," + y + ").");
						break;
					case WUMPUS:
						//Uncomment the following line to fill the knowledge base with
						//the concrete position of the wumpus
						//output.println("wumpus(" + x + "," + y +","+wumpus.size()+").");
						int dis = MAX_STENCH_DISTANCE;
						for (int x2 = x-dis; x2 <= x+dis; x2++)
							for (int y2 = y-dis; y2 <= y+dis; y2++)
								//If the destination is inside the map
								if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height)
									for (int d = 1; d <= dis; d++)
										if (Math.abs(x2-x)+Math.abs(y2-y) == d) {
											if (tiles[x2][y2].getType() == WumpusTileType.VISITED) {
												if (ORDER_FACTS_BASED_ON_COORDINATES)
													output.println("stench("+x2+","+y2+","+d+","+wumpus.size()+").");
												else
													stench.add("stench("+x2+","+y2+","+d+","+wumpus.size()+").");
											}
										}
						//Remember the position of this wumpi
						wumpus.add(new Vector2(x,y));
						break;
					default:
						break;
					}
				}
			}
			if (!ORDER_FACTS_BASED_ON_COORDINATES) {
				//Print all facts ordered by their predicate
				output.println("% visited(\"X,Y\").");
				for (String s : visited) output.println(s);
				output.println("\n% glitter(\"X,Y\").");
				for (String s : glitter) output.println(s);
				output.println("\n% breeze(\"X,Y\").");
				for (String s : breeze) output.println(s);
				output.println("\n% bump(\"X,Y\").");
				for (String s : bump) output.println(s);
				output.println("\n% hunter(\"X,Y\").");
				for (String s : hunter) output.println(s);
				output.println("\n% stench(\"X,Y,Intensity,WumpusID\").");
				for (String s : stench) output.println(s);
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the tiles of the given Tile array with information based on
	 * the consulted prolog files.
	 * @param tiles
	 * @return
	 */
	public static Tile[][] loadMap(Tile[][] tiles) {
		if (!new File("weltkarte.pl").exists()) {
			return tiles;
		}
		if (!new File("rules.pl").exists()) {
			return tiles;
		}
		//Clear the database
		Query clearDatabase = new Query("mod1:unload_file('weltkarte.pl')");
		if (!clearDatabase.hasSolution()) {
			System.err.println("Database clearing failed");
			System.exit(1);
		}
		//Consult all necessary files to setup the knowledge base
		//to be able to solve requests
		Query consult_query = new Query("mod1:consult('rules.pl')");
		
		if (!consult_query.hasSolution()) {
			System.err.println("Consult failed");
			System.exit(1);
		}
		
		//Read the size of the map from the database
		Query mapsizeQuery = new Query("mapsize", new Term[]{new Variable("Width"),new Variable("Height")});
		mapsizeQuery.open();
		Map<String, Term> m = mapsizeQuery.getSolution();
		int width = Integer.parseInt(m.get("Width").toString());
		int height = Integer.parseInt(m.get("Height").toString());
		//And adjust the tile array if necessary
		if (tiles.length != width || tiles[0].length != height) {
			tiles = new Tile[width][height];
			for (int x = 0; x < tiles.length; x++)
				for (int y = 0; y < tiles[0].length; y++)
					tiles[x][y] = new Tile(WumpusTileType.EMPTY);
		}
		
		//Evaluate the id's of all wumpi
		Query query = new Query("stench(_,_,_,ID)");
		LinkedList<Integer> wumpusIDs = new LinkedList<>();
		if (query.hasSolution()) {
			for (Map<String, Term> sol : query.allSolutions()) {
				int id = sol.get("ID").intValue();
				
				if (!wumpusIDs.contains(id))
					wumpusIDs.add(id);
			}
		}
		
		//Fill the map with information based on 
		//the created rules and the given knowledge base
		//fields that can't be determined will be marked as empty
		for (int x = 0; x < tiles.length; x++)
			for (int y = 0; y < tiles[0].length; y++) {
				WumpusTileType type = WumpusTileType.EMPTY;
				
				if (consultingCoords("gold", x, y)) {
					type = WumpusTileType.GOLD;
				} else if (consultingCoords("hunter", x, y)) {
					type = WumpusTileType.HUNTER;
				} else {
					if (consultingCoords("trap", x, y)) {
						type = WumpusTileType.PIT;
					} else if (consultingCoords("wall", x, y)) {
						type = WumpusTileType.WALL;
					} else if (consultingCoords("possible_trap", x, y)) {
						if (consultingCoords("possible_wall", x, y)) {
							type = WumpusTileType.WALL_OR_TRAP;
						} else
							type = WumpusTileType.POSSIBLE_PIT;
					} else if (consultingCoords("possible_wall", x, y)) {
						type = WumpusTileType.POSSIBLE_WALL;
					}
				}
				
				tiles[x][y].setType(type);
				
				//Evaluate all possible wumpi positions and color their image based on their ID
				try {
					for (Integer id : wumpusIDs) {
							if (new Query("wumpus("+x+","+y+","+id+")").hasSolution()) {
								type = WumpusTileType.WUMPUS;
								tiles[x][y].setType(WumpusTileType.WUMPUS);
								tiles[x][y].getImageProperty().set(
										DataHandler.colorTransparency(tiles[x][y].getImageProperty().get(), 
												wumpusColors[id]));
							}
					}
				} catch (PrologException e) {
					//Query cannot be resolved: not enough knowledge
				}
			}
		return tiles;
	}

	/**
	 * Ask prolog about a /2 predicate with 2 integer arguments.
	 * @param funktor
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean consultingCoords(String funktor, int x, int y) {
		org.jpl7.Integer firstval = new org.jpl7.Integer(x);
		org.jpl7.Integer secondval = new org.jpl7.Integer(y);
		
		Term args[] = { firstval, secondval };
		
		return consulting(funktor, args);
	}
	
	/**
	 * Ask prolog about a given predicate with the given terms.
	 * @param funktor
	 * @param terms
	 * @return
	 */
	private static boolean consulting(String funktor, Term[] terms) {
		try {
			return new Query(funktor, terms).hasSolution();
		} catch (PrologException e) {
			//Query cannot be resolved: not enough knowledge
		}
		return false;
	}

	public static void saveLevelAsProlog(Tile[][] tiles, String filename) {
		try (PrintWriter output = new PrintWriter(new FileOutputStream(filename, false))) {

			output.println(
					"% Deactivate warnings for facts that are discontigous\n"
					+":- discontiguous wall/2.\n"
					+":- discontiguous trap/2.\n"
					+":- discontiguous gold/2.\n"
					+":- discontiguous wumpus/2.\n"
					+":- discontiguous hunter/2.\n"
					+ "% mapsize(Width,Height).\n"
					+ "mapsize(" + tiles.length + "," + tiles[0].length + ").\n\n"
			);

			int width = tiles.length;
			int height = tiles[0].length;
			
			//Iterate over all tiles and generate facts
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Tile t = tiles[x][y];
					WumpusTileType type = (WumpusTileType) t.getType();
					
					switch (type) {
					case GOLD:
						output.println("gold(" + x + "," + y + ").");
					case PIT:
						output.println("trap(" + x + "," + y + ").");
						break;
					case WALL:
						output.println("wall(" + x + "," + y + ").");
						break;
					case HUNTER:
						output.println("hunter(" + x + "," + y + ").");
						break;
					case WUMPUS:
						output.println("wumpus(" + x + "," + y + ").");
						break;
					default:
						break;
					}
				}
			}
			output.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

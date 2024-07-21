/*
	Boards contains spaces and their vertices (connections)
	
	Vertices are in the format [territory, connection1, connection2, ...]
		Territory is connected to connection 1, connection2, ...
		Territory has those territories added and they have territory added

	Board file format:
	#_of_territories #_of_connection_lines
	Name1 Value1 Crown(1or0) Name2(if needed) StartSpace(1or0 if needed)
	...
	CONNECTIONS
	Name1 Name2 Name3
	...

	For connections, name1 is connected to name2 and name3, they are conected back to name1.
*/
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class Board {
	private Territory[] spaces;
	private String[][] vertices;

	/*
		Constructors
	*/
	public Board() {
		spaces = null;
		vertices = null;
	}

	public Board(String path) {
		loadFromFile(path);
	}

	public Board(Territory[] spaces, String[][] vertices) {
		this.spaces = spaces;
		this.vertices = vertices;

		initializeConnections();
	}

	/*
		Retrieve territory object reference by name or crown name
	*/
	public Territory getTerritory(String name) {
		for (int i = 0; i < spaces.length; i++) {
			if (spaces[i].getName().equals(name) || (spaces[i].hasCrown() && spaces[i].getCrownName().equals(name))) {
				return spaces[i];
			}
		}

		return null;
	}

	public Territory[] getTerritories() {
		return spaces;
	}

	/*
		Creates territories and stores vertices from file
		Format described in header
	*/
	public void loadFromFile(String path) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			boolean readingConnections = false;

			// Create vars and extract the counts from the first line
			String line = reader.readLine();
			String[] tokens = parseLine(line, 2);
			int territoryCount = Integer.parseInt(tokens[0]);
			int connectionCount = Integer.parseInt(tokens[1]);

			// Initializes object variables using specified counts
			if (territoryCount > 0 && connectionCount > 0) {
				spaces = new Territory[territoryCount];
				vertices = new String[connectionCount][];
			} else {
				// Exit program if there is an invalid count
				return;
			}

			// While there is a line present...
			int count = 0;
			while ((line = reader.readLine()) != null) {

				// Checks to see if we are now reading connections
				if (!readingConnections && line.equals("CONNECTIONS"))  {
					// Switch bool, reset count for new array, and skip parsing this text
					readingConnections = true;
					count = 0;
					continue;
				}

				if (!readingConnections) {
					// Reading a territory
					tokens = parseLine(line, 5);

					// Converts the strings and creates a new territory object with them
					Territory temp = new Territory(tokens[0], Integer.parseInt(tokens[1]));

					if (tokens[2].equals("1")) {
						temp.setCrown(true);
						temp.setCrownName(tokens[3]);
						temp.setCanStart(tokens[4].equals("1") ? true : false);
					}

					spaces[count++] = temp;
				} else {
					// Reading a connection
					tokens = parseLine(line, 10);
					vertices[count++] = tokens;
				}
			}

			reader.close();
			initializeConnections();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
		Places tokens of text into an array of fixed size, trims array size if needed
	*/
	private String[] parseLine(String line, int maxSize) {
		String[] res = new String[maxSize];
		int ind = 0, prev = 0;

		for (int i = 0; i < line.length() && ind < maxSize; i++) {
			if (line.charAt(i) == ' ') {
				// Selects token and saves it
				res[ind++] = line.substring(prev, i);

				// Skips extra whitespace
				while (i < line.length() && line.charAt(i) == ' ') i++;
				prev = i;
			}
		}

		// This ensures that any token followed by '\0' instead of ' ' is added
		if (ind < maxSize) {
			res[ind] = line.substring(prev, line.length());
		}

		// Copies the array into a perfectly sized one if needed
		if (ind < maxSize-1) {
			String[] nRes = new String[ind+1];

			for (int i = 0; i < nRes.length; i++) {
				nRes[i] = res[i];
			}

			return nRes;
		}

		return res;
	}

	/*
		Requires both spaces and vertex be properly initialized
	*/
	private void initializeConnections() {
		// For each vertex
		for (int i = 0; i < vertices.length; i++) {
			Territory base = getTerritory(vertices[i][0]);

			// For each territory in vertex
			for (int j = 1; j < vertices[i].length; j++) {
				base.addConnection(getTerritory(vertices[i][j]));
			}
		}
	}

	/*
		To string
	*/
	public String toString() {
		String out = "Territories:\n";

		for (int i = 0; i < spaces.length; i++) {
			out += spaces[i].toString() + "\n";
		}

		return out;
	}
}
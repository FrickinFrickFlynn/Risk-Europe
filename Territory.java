/*
	Territories represent the tiles where the gameplay occurs
		- A game board consists of these tiles

	Each tile has a unique identifying name, tax value, and crown.
	Connections are shown through an array that houses adjacent territory references.
	Territories can house an army and hold attackers until battles get resolved.
	Castles can be placed on territories.
*/

public class Territory {
	private String name;
	private int value;
	private Territory[] connections;
	private Army attackers;
	private Army unit;
	private boolean crown;
	private String crownName;
	private boolean castle;

	/*
		Constructors for all fields, intensive qualities, and bare minimum
	*/
	public Territory(String name, int value, Territory[] connections, Army unit, Army attackers, boolean crown, String crownName, boolean castle) {
		this.name = name;
		this.value = value;
		this.connections = connections;
		this.unit = unit;
		this.attackers = attackers;
		this.crown = crown;
		this.crownName = crownName;
		this.castle = castle;
	}

	public Territory(String name, int value, boolean crown, String crownName) {
		this.name = name;
		this.value = value;
		this.crown = crown;
		this.crownName = crownName;
		connections = new Territory[0];
		unit = null;
		attackers = null;
		castle = false;
	}

	public Territory() {
		name = "";
		value = 1;
		connections = new Territory[0];
		unit = null;
		attackers = null;
		crown = false;
		castle = false;
	}

	/*
		Special getter method checks if the territory is disputed
	*/
	public boolean isDisputed() {
		return attackers != null;
	}

	/*
		Checks to see if other is connected
	*/
	public boolean isAdjacent(Territory other) {
		for (int i = 0; i < connections.length; i++) {
			if (connections[i].equals(other)) {
				return true;
			}
		}

		return false;
	}

	/*
		Connects two territories by appending their connection arrays
	*/
	public void addConnection(Territory newTerr) {
		// Adds connection to calling territory
		Territory[] newConnections = new Territory[connections.length+1];

		int i;
		for (i = 0; i < connections.length; i++) {
			newConnections[i] = connections[i];
		}

		newConnections[i] = newTerr;
		connections = newConnections;

		// Adds connection to other territory
		newConnections = new Territory[newTerr.getConnections().length+1];

		for (i = 0; i < newTerr.getConnections().length; i++) {
			newConnections[i] = newTerr.getConnections()[i];
		}

		newConnections[i] = this;
		newTerr.setConnections(newConnections);
	}

	/*
		Setters
	*/
	public void setUnit(Army unit) {
		this.unit = unit;
	}

	public void setAttackers(Army attackers) {
		this.attackers = attackers;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setConnections(Territory[] connections) {
		this.connections = connections;
	}

	public void setCrown(boolean crown) {
		this.crown = crown;
	}

	public void setCastle(boolean castle) {
		this.castle = castle;
	}

	public void setCrownName(String crownName) {
		this.crownName = crownName;
	}

	/*
		Getters
	*/
	public Army getAttackers() {
		return attackers;
	}

	public boolean hasCastle() {
		return castle;
	}

	public boolean hasCrown() {
		return crown;
	}

	public String getName() {
		return name;
	}

	public Army getUnit() {
		return unit;
	}

	public int getValue() {
		return value;
	}

	public Territory[] getConnections() {
		return connections;
	}

	public String getCrownName() {
		return crownName;
	}

	/*
		To string method
	*/
	public String toString() {
		String str = "[" + name + ", " + value + "]\nCrown: " + crown + "\nCrown Name: " + ((crownName == null) ? "None" : crownName) + "\nCastle: " + castle + "\nUnit: " + unit + "\nAttackers: " + attackers + "\nConnections:\n";
		
		for (int i = 0; i < connections.length; i++) {
			str += connections[i].getName() + "\n";
		}

		return str;
	}
}
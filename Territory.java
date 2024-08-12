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
	private Army atk;
	private Army def;
	private boolean crown;
	private String crownName;
	private boolean canStart;
	private boolean castle;

	/*
		Constructors for all fields, intensive qualities, and bare minimum
	*/
	public Territory(String name, int value, Territory[] connections, Army def, Army atk, boolean crown, String crownName, boolean canStart, boolean castle) {
		this.name = name;
		this.value = value;
		this.connections = connections;
		this.def = def;
		this.atk = atk;
		this.crown = crown;
		this.crownName = crownName;
		this.canStart = canStart;
		this.castle = castle;
	}

	public Territory(String name, int value, boolean crown, String crownName, boolean canStart) {
		this(name, value);
		this.crown = crown;
		this.crownName = crownName;
		this.canStart = canStart;
	}

	public Territory(String name, int value) {
		this.name = name;
		this.value = value;
		crownName = "";
		connections = new Territory[0];
	}

	public Territory() {
		this("", 1);
	}

	/*
		Special getter method checks if the territory is disputed
	*/
	public boolean isDisputed() {
		return atk != null;
	}

	/*
		Special getter returns the player who defends this territory
	*/
	public Player getDefPly() {
		if (def != null) {
			return def.getOwner();
		}

		return null;
	}

	/*
		Sepcial getter returns the player who is attacking
	*/
	public Player getAtkPly() {
		if (atk != null) {
			return atk.getOwner();
		}

		return null;
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
		Removes defending unit and replaces it with the attacker if there is one
		Otherwise makes the territory unclaimed
	*/
	public void removeDef() {
		def.getOwner().removeTerr(this);

		if (atk != null) {
			def = atk;
			atk = null;
		} else {
			def = null;
		}
	}

	/*
		Removes attacker
	*/
	public void removeAtk() {
		atk.getOwner().removeTerr(this);
		atk = null;
	}

	/*
		TerritoryName(crown preferred) - Def:(f, a, c, s) - Atk:(f, a, c, s) - Castle: Yes
	*/
	public void display() {
		String out;

		if (this.crown) {
			out = this.crownName;
		} else {
			out = this.name;
		}

		if (this.def != null) {
			out += " - Def:(" + def.getFoot() + ", " + def.getArcher() + ", " + def.getCavalry() + ", " + def.getSiege() + ")";
		}

		if (this.atk != null) {
			out += " - Atk:(" + atk.getFoot() + ", " + atk.getArcher() + ", " + atk.getCavalry() + ", " + atk.getSiege() + ")";
		}

		if (this.castle) {
			out += " - Castle";
		}

		System.out.println(out);
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
	public void setDef(Army def) {
		this.def = def;
	}

	public void setAtk(Army atk) {
		this.atk = atk;
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

	public void setCanStart(boolean canStart) {
		this.canStart = canStart;
	}

	/*
		Getters
	*/
	public Army getAtk() {
		return atk;
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

	public Army getDef() {
		return def;
	}

	public int getValue() {
		return value;
	}

	public boolean canStart() {
		return canStart;
	}

	public Territory[] getConnections() {
		Territory[] temp = new Territory[connections.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = connections[i];

		return temp;
	}

	public String getCrownName() {
		return crownName;
	}

	public String getPrefName() {
		return (crown ? crownName : name);
	}

	/*
		To string method
	*/
	public String toString() {
		String str = "[" + name + ", " + value + "]\nCrown: " + crown + "\nCrown Name: " + ((crownName == null) ? "None" : crownName) 
		+ "\nCanStart: " + canStart + "\nCastle: " + castle + "\nDefenders: " + def + "\nAttackers: " + atk + "\nConnections:\n";
		
		for (int i = 0; i < connections.length; i++) {
			str += connections[i].getName() + "\n";
		}

		return str;
	}
}
public class Army {

	//Stores unit types in the following order: foot, archer, cavalry, siege.
	private int[] units = new int[4];

	//The total amount of units regardless of type and value.
	private int unitTotal;

	//Player this army belongs to
	private Player owner;

	public Army(int foot, int archer, int cavalry, int siege, Player owner) {
		this(foot, archer, cavalry, siege);
		this.owner = owner;
	}

	public Army(int foot, int archer, int cavalry, int siege) {
		units[0] = foot;
		units[1] = archer;
		units[2] = cavalry;
		units[3] = siege;
		unitTotal = foot + archer + cavalry + siege;
	}

	//Return total amount of units
	public int getTotal() {
		return unitTotal;
	}

	//Return total amount of footsoldiers
	public int getFoot() {
		return units[0];
	}

	//Return total amount of archers
	public int getArcher() {
		return units[1];
	}

	//Return total amount of cavalry
	public int getCavalry() {
		return units[2];
	}

	//Return total amount of siege weapons
	public int getSiege() {
		return units[3];
	}

	//Return owner
	public Player getOwner() {
		return owner;
	}

	/* 
	    Returns the total game value for the units in the army.
	    Key: Foot:1 Archer:2 Cavalry:3 Siege:10
	*/
	public int totalValue() {
		return units[0] + (units[1] * 2) + (units[2] * 3) + (units[3] * 10);
	}

	/*
		Takes the number of units lost and removes them based on value.
		Foot units are removed first followed by archers and so on.
		If more units are destroyed than the total set all values to 0.
	*/
	public void destroyUnits(int hits) {
		if (hits >= unitTotal) {
			//Modify total unit count first
			owner.addTotalUnits(-units[0], -units[1], -units[2], -units[3]);

			//Sets units to 0
			for (int i = 0; i < 4; i++) {
				units[i] = 0;
			}

			unitTotal = 0;
		} else {
			unitTotal -= hits;

			//For each hit remove a unit in the order above
			while (hits > 0) {
				//Remove X unit if there are any
				if (units[0] != 0) {
					units[0]--;
					owner.addTotalUnits(-1,0,0,0);
				} else if (units[1] != 0) {
					units[1]--;
					owner.addTotalUnits(0,-1,0,0);
				} else if (units[2] != 0) {
					units[2]--;
					owner.addTotalUnits(0,0,-1,0);
				} else {
					units[3]--;
					owner.addTotalUnits(0,0,0,-1);
				}
				
				hits--;
			}
		}
	}

	/*
		Joins with another Army
		Other Army object is unchagned
	*/
	public void combine(Army other) {
		units[0] += other.getFoot();
		units[1] += other.getArcher();
		units[2] += other.getCavalry();
		units[3] += other.getSiege();

		unitTotal += other.getTotal();
	}

	/*
		Splits units from this Army into another Army
		Expects legal numbers
	*/
	public Army split(int f, int a, int c, int s) {
		Army other = new Army(f,a,c,s,owner);

		units[0] -= f;
		units[1] -= a;
		units[2] -= c;
		units[3] -= s;

		unitTotal -= other.getTotal();
		return other;
	}

	// Format: Footmen - #, Archer - #, Cavalry - #, Siege - #
	public String toString() {
		return "Footmen: " + units[0] + " | Archers: " + units[1] + 
		" | Cavalry: " + units[2] + " | Siege: " + units[3];
	}
}
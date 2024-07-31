/*
	Represents a player
	Holds important player stats (money, faction, crowns, totalActiveUnits, cards)
	Active units keeps track of what units the player has on the board
		- Used to keep track of total and maintain unit limits

	Cards are represented through unique card ids
		- 0 is special as it represents there being no card present
		- Ids communicate the function of the card, not unique identity

	Draw pile represents cards that are picked from at the start of each round,
		discard pile represents cards that have been played, active cards are the
		cards that will be played each turn
*/
import java.util.ArrayList;

public class Player {
	private String name;
	private String faction;
	private int money;
	private int crowns;

	private int[] totalActiveUnits; 				// [foot, arch, cav, siege]
	private ArrayList<Territory> occupiedTerritories; 

	private int[] drawPile;
	private int[] discardPile;
	private int[] activeCards;
	private int discardIndex;

	/*
		Constructors
	*/
	public Player(String name, String faction, int money, int crowns) {
		this(name, faction);
		this.money = money;
		this.crowns = crowns;
	}

	public Player(String name, String faction) {
		this.name = name;
		this.faction = faction;
		totalActiveUnits = new int[4];
		occupiedTerritories = new ArrayList<Territory>();
	}

	public Player() {
		this("John Doe", "No Team");
	}

	/*
		Give players their starting hand of any size
		Should be an even number
	*/
	public void giveCards(int[] cardIds) {
		drawPile = new int[cardIds.length];

		// Copy arrays to prevent sharing references
		for (int i = 0; i < cardIds.length; i++) {
			drawPile[i] = cardIds[i];
		}

		discardPile = new int[drawPile.length];
		activeCards = new int[2];
		discardIndex = 0;
	}

	/*
		Add a new active card, removes it from drawPile
		Card ids must be contained within drawPile
		Will not write unless there is a null cardId present
		If this is the case it does not remove from draw
	*/
	public int pickCard(int id) {
		// Find the index of the card
		int i;
		for (i = 0; i < drawPile.length && drawPile[i] != id; i++);

		// Check if no card was found
		if (i == drawPile.length) return 1;

		if (activeCards[0] != 0) {
			if (activeCards[1] != 0) {
				// There is no space present
				return 2;
			} else {
				// Place in second space
				activeCards[1] = id;
			}
		} else {
			// Place in first space
			activeCards[0] = id;
		}

		// Remove card from pile
		drawPile[i] = 0;
		return 0;
	}

	/*
		Returns the first card in activeCards
		Returns the null card if 

	*/
	public int useCard() {
		int card = activeCards[0];

		// Return -1 if no card
		if (card == 0) return 0;

		// If there is a second card
		if (activeCards[1] != 0) {
			// Move it, and null the empty space
			activeCards[0] = activeCards[1];
			activeCards[1] = 0;
		} else {
			// Null the empty space
			activeCards[0] = 0;
		}

		// Add used card to discard
		discardPile[discardIndex++] = card;

		// Check if the dicard is full
		if (discardIndex == discardPile.length) {
			for (int i = 0; i < drawPile.length; i++) {
				drawPile[i] = discardPile[i];
				discardPile[i] = 0;
			}

			discardIndex = 0;
		}

		// Finally return card
		return card;
	}

	/*
		Special getter, returns nonzero card ids from draw pile
	*/
	public int[] getCards() {
		int[] cards;
		int count = 0;

		for (int i = 0; i < drawPile.length; i++)
			if (drawPile[i] != 0) count++;
		
		cards = new int[count];

		for (int i = 0, j = 0; i < drawPile.length; i++) {
			if (drawPile[i] != 0) {
				cards[j++] = drawPile[i];
			}
		}

		return cards;
	}

	/*
		Easy adjustment of money count
	*/
	public void addMoney(int money) {
		this.money += money;
	}

	/*
		Easy adjustment of crown count
	*/
	public void addCrowns(int crowns) {
		this.crowns += crowns;
	}

	/*
		Allows for easy adjustment of unit counts
	*/
	public void addTotalUnits(int foot, int arch, int cav, int siege) {
		totalActiveUnits[0] += foot;
		totalActiveUnits[1] += arch;
		totalActiveUnits[2] += cav;
		totalActiveUnits[3] += siege;
	}

	// Checks if a player is on a territory
	public boolean isOn(Territory t) {
		return occupiedTerritories.contains(t);
	}

	// Checks if a player is defending a space, must be on the space
	public boolean isDefending(Territory t) {
		return this.equals(t.getDefPly());
	}

	// Adds a new territory to the player
	public void addTerr(Territory terr) {
		occupiedTerritories.add(terr);
	}

	// Returns an array of the supply chain starting from a territory
	public Territory[] getSupplyChain(Territory start) {
		Territory[] occupied = getTerritories();
		ArrayList<Territory> chain = new ArrayList<Territory>();

		// Check if the starting territory is owned
		boolean isOwn = isOn(start);

		// Also check if it is disputed
		if (!isOwn || start.isDisputed()) return null;

		chainRecursion(start, chain, occupied);

		return chain.toArray(new Territory[chain.size()]);
	}

	// Used by getSupplyChain to generate a list of territories connected to the chain
	// Expects that terr is owned and not disputed
	private void chainRecursion(Territory terr, ArrayList<Territory> chain, Territory[] owned) {
		// Add to chain list
		chain.add(terr);

		// Repeat this with owned adjacent territories that are not disputed
		for (int i = 0; i < owned.length; i++) {
			if (terr.isAdjacent(owned[i]) && !owned[i].isDisputed() && !chain.contains(owned[i])) {
				chainRecursion(owned[i], chain, owned);
			}
		}
	}

	/*
		Getters
	*/
	public Territory[] getTerritories() {
		// Returns a copy of the array that is ok to change
		return occupiedTerritories.toArray(new Territory[1]);
	}

	public String getName() {
		return name;
	}

	public String getFaction() {
		return faction;
	}

	public int getMoney() {
		return money;
	}

	public int getCrown() {
		return crowns;
	}

	public int getTotalFoot() {
		return totalActiveUnits[0];
	} 

	public int getTotalArch() {
		return totalActiveUnits[1];
	}

	public int getTotalCav() {
		return totalActiveUnits[2];
	}

	public int getTotalSiege() {
		return totalActiveUnits[3];
	}

	/*
		Setters
	*/
	public void setName(String name) {
		this.name = name;
	}

	public void setFaction(String faction) {
		this.faction = faction;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void setCrowns(int crowns) {
		this.crowns = crowns;
	}

	public void setFoot(int foot) {
		totalActiveUnits[0] = foot;
	}

	public void setArch(int arch) {
		totalActiveUnits[1] = arch;
	}

	public void setCav(int cav) {
		totalActiveUnits[2] = cav;
	}

	public void setSiege(int siege) {
		totalActiveUnits[3] = siege;
	}

	/*
		To-String
	*/
	public String toString() {
		String out = "Player: " + name + "\n";
		out += faction + " $" + money + " Cr: " + crowns + "\n";
		out += "Units: \n" + "Foot - " + totalActiveUnits[0] + "\nArch - " + totalActiveUnits[1]; 
		out += "\nCav - " + totalActiveUnits[2] + "\nSiege - " + totalActiveUnits[3] + "\n";

		if (drawPile != null) {
			out += "\nDraw Pile: ";

			for (int i = 0; i < drawPile.length; i++) {
				out += drawPile[i] + " ";
			}

			out += "\nDiscard Pile: ";

			for (int i = 0; i < discardPile.length; i++) {
				out += discardPile[i] + " ";
			}

			out += "\nActive Cards: " + activeCards[0] + " " + activeCards[1] + "\n";
		} else {
			out += "Cards not initialized";
		}

		out += "\n";

		return out;
	}
}
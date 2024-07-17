/*
	Represents a player
	Holds important player stats (money, faction, crowns, activeUnits, cards)
	Active units keeps track of what units the player has on the board
		- Used to keep track of total and maintain unit limits

	Cards are represented through unique card ids
		- 0 is special as it represents there being no card present
		- Ids communicate the function of the card, not unique identity

	Draw pile represents cards that are picked from at the start of each round,
		discard pile represents cards that have been played, active cards are the
		cards that will be played each turn
*/

public class Player {
	private String name;
	private String faction;
	private int money;
	private int crowns;
	private int[] activeUnits; // [foot, arch, cav, siege]

	private int[] drawPile;
	private int[] discardPile;
	private int[] activeCards;
	private int discardIndex;

	/*
		Constructors
	*/
	public Player(String name, String faction, int money, int crowns) {
		this.name = name;
		this.faction = faction;
		this.money = money;
		this.crowns = crowns;
		activeUnits = new int[4];
	}

	public Player(String name, String faction) {
		this.name = name;
		this.faction = faction;
		money = 0;
		crowns = 0;
		activeUnits = new int[4];
	}

	public Player() {
		name = "John Doe";
		faction = "No Team";
		money = 0;
		crowns = 0;
		activeUnits = new int[4];
	}

	/*
		Give players their starting hand of any size
		Should be an even number
	*/
	public void giveCards(int[] cardIds) {
		drawPile = cardIds;
		discardPile = new int[drawPile.length];
		activeCards = new int[2];
		discardIndex = 0;
	}

	/*
		Add two new active cards, removes them from drawPile
		Card ids must be contained within drawPile
		Any current active cards will be overwritten
	*/
	public void pickCards(int id1, int id2) {
		boolean found1 = false, found2 = false;

		for (int i = 0; i < drawPile.length; i++) {
			if (!found1 && drawPile[i] == id1) {
				activeCards[0] = id1;
				drawPile[i] = 0;
				found1 = true;
			} else if (!found2 && drawPile[i] == id2) {
				activeCards[1] = id2;
				drawPile[i] = 0;
				found2 = true;
			}
		}
	}

	/*
		Returns the first card in activeCards
	*/
	public int useCard() {
		int card = activeCards[0];

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
	public void addUnits(int foot, int arch, int cav, int siege) {
		activeUnits[0] += foot;
		activeUnits[1] += arch;
		activeUnits[2] += cav;
		activeUnits[3] += siege;
	}

	/*
		Getters
	*/
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

	public int getFoot() {
		return activeUnits[0];
	} 

	public int getArch() {
		return activeUnits[1];
	}

	public int getCav() {
		return activeUnits[2];
	}

	public int getSiege() {
		return activeUnits[3];
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
		activeUnits[0] = foot;
	}

	public void setArch(int arch) {
		activeUnits[1] = arch;
	}

	public void setCav(int cav) {
		activeUnits[2] = cav;
	}

	public void setSiege(int siege) {
		activeUnits[3] = siege;
	}

	/*
		To-String
	*/
	public String toString() {
		String out = "Player: " + name + "\n";
		out += faction + " $" + money + " Cr: " + crowns + "\n";
		out += "Units: \n" + "Foot - " + activeUnits[0] + "\nArch - " + activeUnits[1]; 
		out += "\nCav - " + activeUnits[2] + "\nSiege - " + activeUnits[3] + "\n";

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
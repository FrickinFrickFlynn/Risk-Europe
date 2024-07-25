/*
	Game represents a game instance
	Can only play one game
	
	Follows game procedure which is sumarized below.
	Game setup is done in another class (naming players, choosing a board, rules?)

	Before Start:
		- Setup players
		- Choose board
		- Choose rules? Implement later
		- Give starting supplies
		- Bet for first
		- Pick starting location

	Start of Round:
		- Assign player order, shift until fpt possessor
		- Pick cards, keep hidden from others
		- Turn1 & Turn2
		- Resolve territory disputes
		- Check for win, repeat otherwise

	public static void main(String[] args) {
		setup();

		while (winner == null) {
			setTurnOrder();
			chooseCards();
			takeTurns();
			resolveDisputes();
			checkWin();
		}
	}
*/
import java.util.Scanner;
import java.util.Random;
import java.util.InputMismatchException;

public class Game {
	private Board brd;						// Stores game board
	private Player[] players;				// Stores players
	private int[] cardPool;					// Stores what cards will be used

	private Player firstPlayer;				// Stores player ref who is first
	private boolean ready;					// Can the game be started?
	private boolean gameOver;				// Game over flag, set in start()

	private Scanner sc;						// Scanner for quick access
	private Random rng;						// RNG for quick access

	private int startAmt;					// Starting amount of money
	private int[] maxUnits;					// Max units a player is allowed
	private int turnsPerRound;				// Number of turns per round
	private int[] stArmyCnt;				// Starting unit count

	public Game(String[] playerNames, String boardPath, int[] cards) {
		sc = new Scanner(System.in);
		rng = new Random();
		brd = new Board(boardPath);
		cardPool = cards;
		
		/* 
			Game vars. Magic numbers are everywhere here. Ignore them as I intend
			on implementing a way to handle custom game var values. 
		*/
		startAmt = 5;
		turnsPerRound = 2;
		maxUnits = new int[4];
		maxUnits[0] = 25;
		maxUnits[1] = 12;
		maxUnits[2] = 12;
		maxUnits[3] = 4;

		stArmyCnt = new int[4];
		stArmyCnt[0] = 10; 		// The rest are zero

		// Create new player objects from their name, give them starting equipment
		players = new Player[playerNames.length];
		for (int i = 0; i < playerNames.length; i++) {
			players[i] = new Player(playerNames[i], "ffa", startAmt, 0);
			players[i].giveCards(cardPool);
		}

		ready = true;
	}

	/*
		Starts the game
	*/
	public void start() {
		// Start only if ready
		if (!ready) return;
		gameOver = false;

		// Bidding sequence
		bid();
		assignOrder();
		chooseStartingLocation();

		// Main game loop
		while (!gameOver) {
			// Assign player order
			assignOrder();

			// Pick cards
			pickingSequence();

			// Turn1 & 2
			for (int i = 0; i < turnsPerRound; i++) {
				for (int j = 0; j < players.length; j++) {
					takeTurn(players[j]);
				}
			}

			// Resolve disputes

			// Check for win
			gameOver = true;
		}
	}

	/*
		Runs a bidding sequence to set the initial FP
	*/
	private void bid() {
		int[] bids = new int[players.length];

		clearScreen();
		System.out.println("Bidding Time: ");

		// Gets each player's bid
		int b;

		for (int i = 0; i < players.length; i++) {
			System.out.print(players[i].getName() + "'s Bid: ");
			b = getIntInput(0, players[i].getMoney());
			bids[i] = b;
			clearScreen();
		}

		// Finds the largets bid's index
		int largest = 0;

		for (int i = 0; i < bids.length; i++) {
			if (bids[i] > bids[largest]) {
				largest = i;
			}
		}

		/*
			Rolls two dice for any duplicate (including the largest itself)
			The index aligns with bids and players. 
			The index of largest still corresponds to the first duplicate bid

			Then finds the largest roll. Any ties are given to the highest
			index player (why I chose two dice instead of one).
		*/
		int[] ties = new int[bids.length];

		for (int i = 0; i < bids.length; i++) {
			if (bids[largest] == bids[i]) {
				ties[i] = (rng.nextInt(6) + 1) + (rng.nextInt(6) + 1);
			}
		}

		for (int i = 0; i < ties.length; i++) {
			if (ties[i] >= ties[largest]) {
				largest = i;
			}
		}

		// The winner has been determined
		players[largest].addMoney(-bids[largest]);
		firstPlayer = players[largest];

		System.out.println(firstPlayer.getName() + " is the winner of the bid!");
		getConfirmation();
	}

	/*
		Rotates the players array based on who is first
	*/
	private void assignOrder() {
		// Find where the new FP is
		int ind;
		for (ind = 0; ind < players.length; ind++) {
			if (players[ind].equals(firstPlayer)) {
				break;
			}
		}

		// Rotate leftwards if not already in first 
		while (ind > 0) {
			// Save first element
			Player temp = players[0];

			// Shift all others left
			for (int i = 1; i < players.length; i++) {
				players[i-1] = players[i];
			}

			// Replace unshifted last element
			players[players.length-1] = temp;
			ind--;
		}
	}

	/*
		Runs the card picking sequence for each player
	*/
	private void pickingSequence() {
		for (int i = 0; i < players.length; i++) {
			Player curr = players[i];
			int[] currCards = curr.getCards();

			clearScreen();
			System.out.println(curr.getName() + "'s turn to pick");
			getConfirmation();
			clearScreen();
			System.out.println(curr.getName() + "'s available cards: ");

			// Print Cards
			for (int j = 0; j < currCards.length; j++)
				System.out.println(currCards[j] + " - " + getCardName(currCards[j]));

			System.out.print("\nPlease pick the card you will use first (card id): ");
			int card = getIntInput(1,255);

			// While the card is invalid ask again
			while (curr.pickCard(card) != 0) {
				System.out.print("Invalid, try again: ");
				card = getIntInput(1,255);
			}

			System.out.print("\nPlease pick the second card: ");
			card = getIntInput(1,255);

			// While the card is invalid ask again
			while (curr.pickCard(card) != 0) {
				System.out.print("Invalid, try again: ");
				card = getIntInput(1,255);
			}
		}

		clearScreen();
		System.out.println("Everybody has picked cards!");
	}

	/*
		Takes a turn for the given player.
		Turns involve choosing an action from the activecard and then playing it
	*/
	
	private void takeTurn(Player p) {
		int card = p.useCard();

		clearScreen();
		System.out.println(p.getName() + "'s Turn");
		getConfirmation();
		clearScreen();

		System.out.println(getCardName(card));
		System.out.println("Choose an action (1 or 2): ");

		int action = getIntInput(1,2);
		interpretCard(card, action, p);
	}

	// Sets up player starting positions for each player
	private void chooseStartingLocation() {
		for (int i = 0; i < players.length; i++) {
			Player p = players[i];
			Territory[] terrs = brd.getTerritories(); 

			clearScreen();
			System.out.println(p.getName() + "'s turn to choose a starting location");
			getConfirmation();
			clearScreen();

			// Display possible starting locations, cities
			System.out.println("Starting Locations: ");
			for (int j = 0; j < terrs.length; j++) {
				if (terrs[j].canStart() && terrs[j].getUnit() == null) {
					System.out.println(terrs[j].getCrownName());
				}
			}

			// Get input
			System.out.print("\nPlease type the name of your starting location: ");
			String choice = getStringInput();
			Territory chosenTerritory = brd.getTerritory(choice);

			// Check for bad input, either a nonexistent territory or a non-start territory
			while (chosenTerritory == null || !chosenTerritory.canStart() || chosenTerritory.getUnit() != null) {
				System.out.print("Invalid name, try again: ");
				choice = getStringInput();
				chosenTerritory = brd.getTerritory(choice);
			}

			clearScreen();

			// Create a unit for the capital and assign it to the player
			Army capUnit = new Army(stArmyCnt[0], stArmyCnt[1], stArmyCnt[2], stArmyCnt[3]);
			chosenTerritory.setUnit(capUnit);
			chosenTerritory.setCastle(true);
			p.addUnit(capUnit, chosenTerritory);
			p.addCrowns(1);
			p.addMoney(chosenTerritory.getValue());

			System.out.println("Your Kingdom's foundation has been established!");
			System.out.println("You gain a castle, starting army, and " + chosenTerritory.getValue() + " coins!");
			System.out.println("Now it is time to expand into other lands!");
			getConfirmation();

			expand(p);
		}
	}

	// Returns the name of the card
	private String getCardName(int id) {
		String c;

		switch (id) {
			case 0:
				c = "null";
				break;
			case 1:
				c = "Tax Spend";
				break;
			case 2:
				c = "Tax Spend (King Me)";
				break;
			case 3:
				c = "Expand Maneuver (Fortify)";
				break;
			case 4:
				c = "Expand Maneuver (Siege Assault)";
				break;
			case 5:
				c = "SplitExpand Maneuver";
				break;
			default:
				c = "invalid";
				break;
		}

		return c;
	}

	// Interprets the card id to perform an action
	private void interpretCard(int id, int action, Player p) {
		switch (id) {
			case 0:					// Null card id case
				System.out.println("Something really went wrong");
				break;
			case 1:					// Tax Spend
				if (action == 1) tax(p); 
				else spend(p);
				break;
			case 2:					// Tax Spend (King Me)
				if (action == 1) tax(p); 
				else spend(p);
				firstPlayer = p;
				break;
			case 3:					// Expand Maneuver (Fortify)
				if (action == 1) expand(p); 
				else maneuver(p);
				fortify(p);
				break;
			case 4:					// Expand Maneuver (Siege Assault)
				if (action == 1) expand(p); 
				else maneuver(p);
				siegeAssault(p);
				break;
			case 5:					// SplitExpand Maneuver
				if (action == 1) splitExpand(p); 
				else maneuver(p);
				break;
			default:				// Default case
				System.out.println("Invalid card id");
				break;
		}
	}

	// Finds highest tax value supply chain owned by p, adds it to their money
	private void tax(Player p) {
		clearScreen();

		// Get territories that have p's units stationed
		Territory[] terrs = p.getTerritories();

		// For each city compare its supply chain value to the largest
		int largest = 0;

		for (int i = 0; i < terrs.length; i++) {
			if (terrs[i].hasCrown()) {
				Territory[] supplyChain = p.getSupplyChain(terrs[i]);
				int totalValue = 0;

				for (int j = 0; j < supplyChain.length; j++) {
					totalValue += supplyChain[j].getValue();
				}

				if (totalValue > largest) {
					largest = totalValue;
				}
			}
		}
		
		// Add the largest value to the player's money
		p.addMoney(largest);
		System.out.println("Tax:\n");
		System.out.println("Taxing the largest supply chain yields " + largest + " coins");
		System.out.println("May your empire prosper");
		getConfirmation();
	}

	// Move units into undisputed territory (unowned)
	// Must keep one, or the same as the attackers if moving from disputed
	private void expand(Player p) {
		Territory[] owned = p.getTerritories();
		Territory[] fromAdj;
		Territory from = null, to = null;
		Army fromUnit;
		String input;
		boolean isFinal = false;
		int ft, ar, cv, sg;
		
		// Repeat until the user finalizes their decision
		while (!isFinal) {
			clearScreen();
			System.out.println("Expand:");

			// Display owned territories
			System.out.println("\nYour Territories: ");
			for (int i = 0; i < owned.length; i++) {
				if (owned[i].hasCrown()) {
					System.out.println(owned[i].getCrownName());
				} else {
					System.out.println(owned[i].getName());
				}
			}

			// Get input from the player
			System.out.print("\nInput a territory you own to see where you can expand into: ");
		
			while (true) {
				input = getStringInput();
				from = brd.getTerritory(input);

				if (from == null) {
					System.out.print("Territory does not exist: ");
				} else if (!p.isOwned(from)) {
					System.out.print("You do not own that: ");
				} else {
					break;
				}
			}

			// Determine valid expand destinations
			fromAdj = from.getConnections();
			for (int i = 0; i < fromAdj.length; i++) {
				// Check if there is a castle and no seige weapon
				if (fromAdj[i].hasCastle() && from.getUnit().getSiege() == 0) {
					fromAdj[i] = null;
				} else if (fromAdj[i].isDisputed() || p.isOwned(fromAdj[i])) {
					fromAdj[i] = null;
				}
			}

			// Display them and query the player
			System.out.println("\nDestinations: ");

			for (int i = 0; i < fromAdj.length; i++) {
				if (fromAdj[i] != null) {
					if (fromAdj[i].hasCrown()) {
						System.out.println(fromAdj[i].getCrownName());
					} else {
						System.out.println(fromAdj[i].getName());
					}
				}
			}

			System.out.print("\nChoose a destination or type 'retry' to start over: ");

			boolean val = false;
			while (!val) {
				input = getStringInput();

				// Checks for retry
				if (input.equalsIgnoreCase("retry")) {
					// Exits loop prematurely preventing isFinal from being set
					break;
				}

				to = brd.getTerritory(input);

				// Checks if the chosen territory is valid
				for (int i = 0; i < fromAdj.length; i++) {
					if (fromAdj[i] != null && fromAdj[i].equals(to)) {
						val = true;
					}
				}

				if (to == null) {
					System.out.print("Territory does not exist: ");
				} else if (!val) {
					System.out.print("Invalid: ");
				} else {
					// Runs right before exiting loop
					isFinal = true;
				}
			}
		}

		int[] temp = getUnitInput(from, to);
		clearScreen();

		// Split the unit using the input values
		Army leaving = from.getUnit().split(temp[0], temp[1], temp[2], temp[3]);
		
		// Links new split unit to player
		p.addUnit(leaving, to);

		// Move new unit
		if (to.getUnit() != null) {
			// Occupied
			to.setAttackers(leaving);
			System.out.println("Units were placed successfully, good luck in battle!");
		} else {
			// Unoccupied
			to.setUnit(leaving);
			System.out.println("Units were placed successfully, " + (to.hasCrown() ? to.getCrownName() :  to.getName()) + " is yours!");

			// Check if a crown
			if (to.hasCrown()) {
				// Give crown and money
				p.addCrowns(1);
				p.addMoney(to.getValue());

				System.out.println("Since this is a city you gain an additional crown and collect " + to.getValue() + "coins!");
			}
		}
		getConfirmation();
	}

	// Move units from a non-disputed territory into another territory you own
	// Up to two spaces away in a supply chain
	private void maneuver(Player p) {

	}

	private void spend(Player p) {

	}

	private void splitExpand(Player p) {

	}

	private void fortify(Player p) {
		
	}

	private void siegeAssault(Player p) {
		
	}

	// Clear output
	private void clearScreen() {
		for (int i = 0; i < 75; i++) {
			System.out.println();
		}
	}

	/*
		Used to get input from the user of a specific type
		Only grabs the first token of a line, ignores the rest
		low and high restrict the range of numbers, both inclusive
	*/
	private int getIntInput(int low, int high) {
		int input = 0;
		boolean valid = false;

		do {
			try {
				input = sc.nextInt();

				// In the event of bad range
				if (input < low || input > high) {
					System.out.print("Please pick a valid number: ");
				} else {
					valid = true;
				}
			} catch (InputMismatchException exp) {
				// In the event of bad type
				System.out.print("Please input a number: ");
			}
				
			// Flushes everything including '\n' from input stream
			sc.nextLine();
		} while (!valid);

		return input;
	}

	// Gets input of String type, trims leading chars
	private String getStringInput() {
		String input = sc.nextLine();

		// Skips whitespace, stops on the first non-whitespace char
		int i = 0;
		while (i < input.length() && input.charAt(i) == ' ') i++;

		// Skips non-whitespace, stops on the first whitespace
		int j = i;
		while (j < input.length() && input.charAt(j) != ' ') j++;

		// Trim input
		return input.substring(i, j);
	}

	// Pause execution
	private void getConfirmation() {
		System.out.print("Press ENTER to continue...");
		sc.nextLine();
	}

	// For debug
	public String toString() {
		String out = "";
		out += brd.toString() + "\n\n";

		for (int i = 0; i < players.length; i++) {
			out += players[i].toString() + "\n\n";
		}

		out += "\n\n";
		out += firstPlayer.getName() + "\n";

		return out;
	}

	// Gets the number of units to send from the player
	private int[] getUnitInput(Territory from, Territory to) {
		int ft, ar, cv, sg;
		Army fromUnit = from.getUnit();

		while (true) {
			clearScreen();

			// Display the army that will split
			System.out.println("\nUnits stationed in " + (from.hasCrown() ? from.getCrownName() :  from.getName()) + ": ");
			System.out.println("Footmen: " + fromUnit.getFoot() + " | Archers: " + fromUnit.getArcher() + " | Cavalry: " 
				+ fromUnit.getCavalry() + " | Siege: " + fromUnit.getSiege());
			System.out.println("\nType the number of units that will expand into " + (to.hasCrown() ? to.getCrownName() : to.getName()) + ": ");

			// Get input from player
			ft = ar = cv = sg = 0;
			if (fromUnit.getFoot() != 0) {
				System.out.print("Footmen: ");
				ft = getIntInput(0, fromUnit.getFoot());
			}
			
			if (fromUnit.getArcher() != 0) {
				System.out.print("Archers: ");
				ar = getIntInput(0, fromUnit.getArcher());
			}

			if (fromUnit.getCavalry() != 0) {
				System.out.print("Cavalry: ");
				cv = getIntInput(0, fromUnit.getCavalry());
			}

			if (fromUnit.getSiege() != 0) {
				System.out.print("Siege: ");
				sg = getIntInput(0, fromUnit.getSiege());
			}

			// Checks for errors
			int totalOut = ft + ar + cv + sg;
			int remaining = (fromUnit.getFoot() + fromUnit.getArcher() + fromUnit.getCavalry() + fromUnit.getSiege()) - totalOut;

			// No units were chosen to move
			if (totalOut == 0) {
				System.out.println("\nAt least one unit must be sent, try again.");
				getConfirmation();
				clearScreen();
				continue;
			} else {
				// Not enough units remaning
				if (from.isDisputed() && (remaining >= from.getAttackers().getTotal())) {
					System.out.println("\nRemaning units must match or exceed the number of attackers: ");
					getConfirmation();
					clearScreen();
					continue;
				} else if (remaining == 0) {
					System.out.println("\nAt least one unit must remain, try again.");
					getConfirmation();
					clearScreen();
					continue;
				}
			}

			clearScreen();

			// Checks if the player is satisfied
			System.out.println("Units staying in " + (from.hasCrown() ? from.getCrownName() :  from.getName()) + ": ");
			System.out.println("Footmen: " + (fromUnit.getFoot()-ft) + " | Archers: " + (fromUnit.getArcher()-ar) + " | Cavalry: " 
				+ (fromUnit.getCavalry()-cv) + " | Siege: " + (fromUnit.getSiege()-sg));

			System.out.println("\nUnits going to " + (to.hasCrown() ? to.getCrownName() :  to.getName()) + ": ");
			System.out.println("Footmen: " + ft + " | Archers: " + ar + " | Cavalry: " + cv + " | Siege: " + sg);

			System.out.print("\nConfirm unit placement (1 or 0): ");

			if (getIntInput(0,1) == 1) {
				break;
			}
		}

		int[] out = {ft, ar, cv, sg};
		return out;
	}
}
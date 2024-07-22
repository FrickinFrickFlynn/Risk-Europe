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
					//takeTurn(players[j]);
					System.out.println("Turn: " + (i+1) + " Player: " + (j+1));
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
	*/

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

			// Pick adjacent space
			System.out.println("\nAdjacent Territories: ");
			Territory[] adj = chosenTerritory.getConnections();
			for (int j = 0; j < adj.length; j++) {
				System.out.println(adj[j].getName());
			}

			System.out.println("\nYou must divide your starting units between your capital and one adjacent non-city space.");
			System.out.print("Please type the name of the adjacent space: ");

			choice = getStringInput();
			Territory chosenAdjacent = brd.getTerritory(choice);

			// Check for bad input, nonexistent space, unit on space, and non-adjacent space.
			while (chosenAdjacent == null || !chosenAdjacent.isAdjacent(chosenTerritory) || chosenAdjacent.getUnit() != null) {
				System.out.print("Invalid name, try again: ");
				choice = getStringInput();
				chosenAdjacent = brd.getTerritory(choice);
			}

			/* 
				Get unit distribution
				continue and break are used to control this loop
			*/
			int ft, ar, cv, sg;
			while (true) {
				clearScreen();

				// Display starting army
				System.out.println("\nStarting Units: ");
				System.out.println("Footmen: " + stArmyCnt[0] + " | Archers: " + stArmyCnt[1] + " | Cavalry: " 
					+ stArmyCnt[2] + " | Siege: " + stArmyCnt[3]);
				System.out.println("\nType the number of units to place on your capital: ");

				// Get input from player
				ft = ar = cv = sg = 0;
				if (stArmyCnt[0] != 0) {
					System.out.print("Footmen: ");
					ft = getIntInput(0, stArmyCnt[0]);
				}
				
				if (stArmyCnt[1] != 0) {
					System.out.print("Archers: ");
					ar = getIntInput(0, stArmyCnt[1]);
				}

				if (stArmyCnt[2] != 0) {
					System.out.print("Cavalry: ");
					cv = getIntInput(0, stArmyCnt[2]);
				}

				if (stArmyCnt[3] != 0) {
					System.out.print("Siege: ");
					sg = getIntInput(0, stArmyCnt[3]);
				}

				// Checks to see if no units were chosen or if they all were
				int total = ft + ar + cv + sg;
				int maxTotal = stArmyCnt[0] + stArmyCnt[1] + stArmyCnt[2] + stArmyCnt[3];

				if (total == 0) {
					System.out.println("\nAt least one unit must remain, try again.");
					getConfirmation();
					clearScreen();
					continue;
				} else if (total == maxTotal) {
					System.out.println("\nAt least one unit must go onto the other territory, try again.");
					getConfirmation();
					clearScreen();
					continue;
				}

				clearScreen();

				// Checks if the player is satisfied
				System.out.println("Units going to " + chosenTerritory.getCrownName() + ": ");
				System.out.println("Footmen: " + ft + " | Archers: " + ar + " | Cavalry: " + cv + " | Siege: " + sg);

				System.out.println("\nUnits going to " + chosenAdjacent.getName() + ": ");
				System.out.println("Footmen: " + (stArmyCnt[0]-ft) + " | Archers: " + (stArmyCnt[1]-ar) + " | Cavalry: " 
					+ (stArmyCnt[2]-cv) + " | Siege: " + (stArmyCnt[3]-sg));

				System.out.print("\nConfirm unit placement (1 or 0): ");

				if (getIntInput(0,1) == 1) {
					break;
				}
			}

			// Create capital and adjacent units
			Army capitalUnit = new Army(ft, ar, cv, sg);
			Army adjacentUnit = new Army(stArmyCnt[0]-ft, stArmyCnt[1]-ar, stArmyCnt[2]-cv, stArmyCnt[3]-sg);

			// Modify territories
			chosenTerritory.setUnit(capitalUnit);
			chosenTerritory.setCastle(true);
			chosenAdjacent.setUnit(adjacentUnit);

			// Modify player
			p.addCrowns(1);
			p.addUnit(capitalUnit, chosenTerritory);
			p.addUnit(adjacentUnit, chosenAdjacent);

			System.out.println("\nUnits were placed!");
			getConfirmation();
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
	/*private void interpretCard(int id, int action, Player p) {
		switch (id) {
			case 0:					// Null card id case
				System.out.println("Something really went wrong");
				break;
			case 1:					// Tax Spend
				(action == 1) ? tax(p) : spend(p);
				break;
			case 2:					// Tax Spend (King Me)
				(action == 1) ? tax(p) : spend(p);
				firstPlayer = p;
				break;
			case 3:					// Expand Maneuver (Fortify)
				(action == 1) ? expand(p) : maneuver(p);
				fortify(p);
				break;
			case 4:					// Expand Maneuver (Siege Assault)
				(action == 1) ? expand(p) : maneuver(p);
				siegeAssault(p);
				break;
			case 5:					// SplitExpand Maneuver
				(action == 1) ? splitExpand(p) : maneuver(p);
				break;
			default:				// Default case
				System.out.println("Invalid card id");
				break;
		}
	}*/

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

	/*
	private void tax(Player p) {
		
	}

	private void expand(Player p) {

	}

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
	*/
}
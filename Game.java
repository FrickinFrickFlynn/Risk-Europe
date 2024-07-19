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

	public Game(String[] playerNames, String boardPath, int[] cards) {
		sc = new Scanner(System.in);
		rng = new Random();
		brd = new Board(boardPath);
		cardPool = cards;
		
		// Game vars
		startAmt = 5;
		maxUnits = new int[4];
		maxUnits[0] = 25;
		maxUnits[1] = 12;
		maxUnits[2] = 12;
		maxUnits[3] = 4;

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

		// Main game loop
		while (!gameOver) {
			// Assign player order
			assignOrder();

			// Pick cards
			pickingSequence();

			// Turn1 & 2

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
		Returns the name of cards from id
	*/
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

	/*
		A brutish way to clear the output terminal
	*/
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

	/*
		Pauses execution until confirmation via enter
	*/
	private void getConfirmation() {
		System.out.print("Press ENTER to continue...");
		sc.nextLine();
	}

	/*
		To-String Method (DEBUG)
	*/
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
}
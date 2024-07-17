/*
	Game represents a game instance
	Can only play one game
	
	Follows game procedure which is sumarized below

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

	private Scanner sc;						// Scanner for quick access
	private Random rng;						// RNG for quick access

	private int startingAmount;				// Starting amount of money

	public Game(String[] playerNames, String boardPath, int[] cards) {
		sc = new Scanner(System.in);
		rng = new Random();
		brd = new Board(boardPath);
		cardPool = cards;

		startingAmount = 5;

		players = new Player[playerNames.length];
		for (int i = 0; i < playerNames.length; i++) {
			players[i] = new Player(playerNames[i], "ffa", startingAmount, 0);
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

		// Bidding sequence
		bid();

		System.out.println(firstPlayer.getName());
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
			b = getIntInput(0, startingAmount);
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
		A very basic way to clear the output terminal
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
					System.out.println("Please pick a valid number: ");
				} else {
					valid = true;
				}
			} catch (InputMismatchException exp) {
				// In the event of bad type
				System.out.println("Please input a number: ");
			}
				
			// Flushes everything including '\n' from input stream
			sc.nextLine();
		} while (!valid);

		return input;
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
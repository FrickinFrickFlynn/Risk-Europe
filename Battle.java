import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class Battle {
	//Used to "roll dice"
	private Random rand;
	private Scanner sc;

	private Territory dispTerr;
	private Army attacker;
	private Army defender;

	private boolean hasCastle;
	private boolean canCastleDef;

	//Constructor
	public Battle(Territory dispTerr) {
		this.dispTerr = dispTerr;
		attacker = dispTerr.getAtk();
		defender = dispTerr.getDef();
		hasCastle = dispTerr.hasCastle();

		rand = new Random();
		sc = new Scanner(System.in);
		canCastleDef = hasCastle;
	}

	//Returns an array of dice roll results in descending order
	private int[] rollDice(int rolls) {
		int[] results = new int[rolls];

		//Loads results array with random values from 1 to 6
		for(int i = 0; i < rolls; i++) {
			results[i] = rand.nextInt(6) + 1;
		}

		//Sort array in descending order
		for (int i = 0; i < results.length-1; i++) {
			int largest = i;

			for (int j = i+1; j < results.length; j++) {
				if (results[j] > results[largest]) {
					largest = j;
				}
			}

			// Selected largest, sort
			int temp = results[i];
			results[i] = results[largest];
			results[largest] = temp;
		}

		return results;
	}

	/*
		Performs a general attack with the given amount of dice
		Expects: attDice 1-3 and defDice 1-2
		Returns an array in the format: {att hits, def hits}
	*/
	private int[] genAttack(int[] attDice, int[] defDice) {
		//Compare the dice, defense wins ties
		int[] hits = new int[2];

		//Find bound
		int limit = (defDice.length > attDice.length) ? attDice.length : defDice.length;

		for (int i = 0; i < limit; i++) {
			if (defDice[i] >= attDice[i]) {
				//Defense wins
				hits[1]++;
			} else {
				//Offense wins
				hits[0]++;
			}
		}

		return hits;
	}

	//Returns the number of hits based on the number of rolls and the number a hit is based on
	private int countHits(int[] dice, int hitOn) {
		int hits = 0;

		for (int i = 0; i < dice.length; i++) {
			if (dice[i] >= hitOn) {
				hits++;
			}
		}

		return hits;
	}

	//Clears the terminal
	private void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
			}
		} catch (Exception e) {}
	}

	//Returns a string of dice nice
	private String displayDice(int[] dice) {
		String out = "[";

		for (int i = 0; i < dice.length; i++) {
			if (i != 0) {
				out += ", ";
			}

			out += dice[i];
		}

		return out + "]";
	}

	//Allows the defender to reroll
	private int[] castleDefense(int numOfDice) {
		System.out.print("\nThe defender can reroll, what is their choice (1or0)? ");

		// Get input from user
		do {
			try {
				int input = sc.nextInt();
				
				// Flush '\n'
				sc.nextLine();

				if (input == 0) {
					// Do nothing
					return null;
				} else if (input == 1) {
					// Continue as normal
					break;
				} else {
					// Bad number
					System.out.print("Type 1 or 0: ");
				}
			} catch (Exception e) {
				System.out.print("Type an integer: ");
			}
		} while (true);

		// Reroll and display results
		int[] reroll = rollDice(numOfDice);
		System.out.println("Def2: " + displayDice(reroll));

		// Set state
		canCastleDef = false;

		return reroll;
	}

	public Army startBattle() {
		//Holds the initial values of the armies
		int attInit = attacker.totalValue(), defInit = defender.totalValue();

		//Pad out the console
		System.out.println(dispTerr.getPrefName() + "\n" + attacker.getOwner().getName() + " vs. " + defender.getOwner().getName());
		System.out.println("\nThe battle will commence!\nPress ENTER to continue...");
		sc.nextLine();
		clearScreen();

		//Battle Loop
		int turn = 1, rank = 1;
		int attHits, defHits;

		//Battle ends once an entire army is wiped out
		while (attacker.getTotal() != 0 && defender.getTotal() != 0) {
			attHits = 0;
			defHits = 0;

			//Display turn, rank, and army standings
			System.out.println("Turn: " + turn);
			System.out.println("(A) " + attacker.getOwner().getName() + "'s Army: " + attacker);
			System.out.println("(D) " + defender.getOwner().getName() + "'s Army: " + defender + "\n");

			if (rank == 1 && (attacker.getSiege() > 0 || defender.getSiege() > 0)) {
				//Siege Attack
				System.out.println("Siege Bombardment");

				int attSiege = attacker.getSiege();
				int defSiege = defender.getSiege();

				if (attSiege != 0) {
					int[] attDice = rollDice(2 * attSiege);
					attHits = countHits(attDice, 3);

					System.out.println("\nAtt: " + displayDice(attDice));
					System.out.println("With " + attSiege + " siege unit" + (attSiege > 1 ? "s" : "") + " the attacker scored " + attHits + " hit" + (attHits > 1 ? "s" : "") + ".\n");
				}
				if (defSiege != 0) {
					int[] defDice = rollDice(2 * defSiege);

					System.out.println("\nDef: " + displayDice(defDice));
					if (canCastleDef)  {
						int[] reroll = castleDefense(defSiege);
						if (reroll != null) defDice = reroll;
					}

					defHits = countHits(defDice, 3);
					System.out.println("With " + defSiege + " siege unit" + (defSiege > 1 ? "s" : "") + " the defender scored " + defHits + " hit" + (defHits > 1 ? "s" : "") + ".");
				}

				//Cycle rank
				rank = 2;

				System.out.println("Press ENTER to continue...");
				sc.nextLine();
				clearScreen();

			} else if (rank == 2 && (attacker.getArcher() > 0 || defender.getArcher() > 0)) {
				//Archer Attack
				System.out.println("Archer Volley");

				int attArch = attacker.getArcher();
				int defArch = defender.getArcher();

				if (attArch != 0) {
					int[] attDice = rollDice(attArch);
					attHits = countHits(attDice, 5);

					System.out.println("\nAtt: " + displayDice(attDice));
					System.out.println("With " + attArch + " archer" + (attArch > 1 ? "s" : "") + " the attacker scored " + attHits + " hit" + (attHits > 1 ? "s" : "") + ".");
				}
				if (defArch != 0) {
					int[] defDice = rollDice(defArch);

					System.out.println("\nDef: " + displayDice(defDice));
					if (canCastleDef)  {
						int[] reroll = castleDefense(defArch);
						if (reroll != null) defDice = reroll;
					}

					defHits = countHits(defDice, 5);
					System.out.println("With " + defArch + " archer" + (defArch > 1 ? "s" : "") + " the defender scored " + defHits + " hit" + (defHits > 1 ? "s" : "") + ".");
				}

				//Cycle rank
				rank = 3;

				System.out.println("\nPress ENTER to continue...");
				sc.nextLine();
				clearScreen();

			} else if (rank == 3 && (attacker.getCavalry() > 0 || defender.getCavalry() > 0)) {
				//Cavalry Attack
				System.out.println("Cavalry Assault");

				int attCav = attacker.getCavalry();
				int defCav = attacker.getCavalry();

				if (attCav != 0) {
					int[] attDice = rollDice(attCav);
					attHits = countHits(attDice, 3);

					System.out.println("\nAtt: " + displayDice(attDice));
					System.out.println("With " + attCav + " cavalr" + (attCav > 1 ? "ies" : "y") + " the attacker scored " + attHits + " hit" + (attHits > 1 ? "s" : "") + ".");
				}
				if (defCav != 0) {
					int[] defDice = rollDice(defCav);
	
					System.out.println("\nDef: " + displayDice(defDice));
					if (canCastleDef)  {
						int[] reroll = castleDefense(defCav);
						if (reroll != null) defDice = reroll;
					}

					defHits = countHits(defDice, 3);
					System.out.println("With " + defCav + " cavalr" + (defCav > 1 ? "ies" : "y") + " the defender scored " + defHits + " hit" + (defHits > 1 ? "s" : "") + ".");
				}

				//Cycle rank
				rank = 4;

				System.out.print("\nPress ENTER to continue...");
				sc.nextLine();
				clearScreen();

			} else {
				//General Attack
				System.out.println("General Attack");

				int[] attDice, defDice;

				if (attacker.getTotal() < 3) {
					attDice = rollDice(attacker.getTotal());
				} else {
					attDice = rollDice(3);
				}

				if (defender.getTotal() < 2) {
					defDice = rollDice(1);
				} else {
					defDice = rollDice(2);
				}

				//Display Results
				System.out.println("Att: " + displayDice(attDice));
				System.out.println("Def: " + displayDice(defDice));
				
				if (canCastleDef)  {
					int[] reroll = castleDefense(defDice.length);
					if (reroll != null) defDice = reroll;
				}

				int[] hits = genAttack(attDice, defDice);
				attHits = hits[0];
				defHits = hits[1];

				if (attHits > 0) {
					System.out.println("\nThe attacker scored " + attHits + " hit" + (attHits > 1 ? "s" : "") + ".");
				} else {
					System.out.println("\nThe attacker scored no hits!");
				}

				if (defHits > 0) {
					System.out.println("The defender scored " + defHits + " hit" + (defHits > 1 ? "s" : "") + ".");
				} else {
					System.out.println("The defenders scored no hits!");
				}
				
				//Cycle rank
				rank = 1;
				turn++;

				//Change state
				canCastleDef = hasCastle;

				System.out.print("\nPress ENTER to continue...");
				sc.nextLine();
				clearScreen();
			}

			//Remove units based on other army's hits
			attacker.destroyUnits(defHits);
			defender.destroyUnits(attHits);
		}

		//Display Winner
		Army winner = null;

		if (attacker.getTotal() == 0 && defender.getTotal() == 0) {
			System.out.println("Both armies have defeated each other. The land is vacant!");
		} else if (defender.getTotal() == 0) {
			winner = attacker;
			System.out.println("The attacking army siezes control of " + dispTerr.getPrefName() + "!");
		} else {
			winner = defender;
			System.out.println("The defending army stood their ground!");
		}

		//Compare intial values
		System.out.println("\nInitial Values:\n  Attacking - " + attInit + "\n  Defending - " + defInit + "\n");

		//Value Lost
		int attValLost = attInit - attacker.totalValue();
		int defValLost = defInit - defender.totalValue();

		System.out.println("Unit Value Lost:\n Attacking - " + attValLost + "\n Defending - " + defValLost);
		System.out.println("\nPress ENTER to continue...");
		sc.nextLine();

		return winner;
	}
}
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class BattleCalculator {
	//Used to "roll dice"
	private static Random rand = new Random();

	//Returns an array of dice roll results in descending order
	private static int[] diceRoll(int rolls) {
		int[] results = new int[rolls];

		//Loads results array with random values from 1 to 6
		for(int i = 0; i < rolls; i++) {
			results[i] = rand.nextInt(6) + 1;
		}

		//Sort array in ascending order
		Arrays.sort(results);

		//Reverse the array
		for (int i = 0; i < results.length / 2; i++) {
			int temp = results[i];
			results[i] = results[results.length - i - 1];
			results[results.length - i - 1] = temp;
		}

		return results;
	}

	/*
		Performs a general attack with the given amount of dice
		Expects: attDice 1-3 and defDice 1-2
		Returns an array in the format: {att hits, def hits}
	*/
	private static int[] genAttack(int attDice, int defDice) {
		//Roll the dice
		int[] attRolls = diceRoll(attDice);
		int[] defRolls = diceRoll(defDice);

		//Compare the dice, defense wins ties
		int[] hits = new int[2];

		for (int i = 0; i < defDice; i++) {
			if (defRolls[i] >= attRolls[i]) {
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
	private static int countHits(int dice, int hitOn) {
		int[] rolls = diceRoll(dice);
		int hits = 0;

		for (int i = 0; i < dice; i++) {
			if (rolls[i] >= hitOn) {
				hits++;
			}
		}

		return hits;
	}

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);

		//Stores the attacking and defending armies
		Army attacker, defender;

		//Holds the initial values of the armies
		int attInit, defInit;

		//Is there a castle present?
		boolean castle = false;

		//Initialize Attacking Army
		int foot, archer, cavalry, siege;

		System.out.println("Attacking Army: ");
		System.out.print("# of Footmen: ");
		foot = sc.nextInt();

		System.out.print("# of Archers: ");
		archer = sc.nextInt();

		System.out.print("# of Cavalry: ");
		cavalry = sc.nextInt();

		System.out.print("# of Siege Weapons: ");
		siege = sc.nextInt();

		attacker = new Army(foot, archer, cavalry, siege);
		System.out.println("Attacking army set up!");
		System.out.println();

		//Initialize Defending Army
		System.out.println("Defending Army: ");
		System.out.print("# of Footmen: ");
		foot = sc.nextInt();

		System.out.print("# of Archers: ");
		archer = sc.nextInt();

		System.out.print("# of Cavalry: ");
		cavalry = sc.nextInt();

		System.out.print("# of Siege Weapons: ");
		siege = sc.nextInt();

		defender = new Army(foot, archer, cavalry, siege);
		System.out.println("Defending army setup up!");
		System.out.println();

		//Store initial army values
		attInit = attacker.totalValue();
		defInit = defender.totalValue();

		//Determine if a castle is present
		boolean inValid = false;

		System.out.print("Is there a castle present? (Y or N) ");
		do {
			String ans = sc.nextLine();

			if (ans.toLowerCase().equals("y")) {
				castle = true;
				inValid = false;
			} else if (ans.toLowerCase().equals("n")) {
				castle = false;
				inValid = false;
			} else {
				inValid = true;
			} 
		} while(inValid);

		//Pad out the console
		System.out.println("The battle will commence. Press enter to continue: ");
		sc.nextLine();
		System.out.println();

		//Battle Loop
		int turn = 1, rank = 1;
		int attHits, defHits;

		//Battle ends once an entire army is wiped out
		while (attacker.getTotal() != 0 && defender.getTotal() != 0) {
			attHits = 0;
			defHits = 0;

			//Display turn, rank, and army standings
			System.out.println("Turn: " + Integer.toString(turn) + "  Rank: " + Integer.toString(rank));
			System.out.println("Attacking Army: " + attacker);
			System.out.println("Defending Army: " + defender);
			System.out.println();

			if (rank == 1) {
				//Siege Attack
				if (attacker.getSiege() != 0) {
					attHits = countHits(2 * attacker.getSiege(), 3);
				}
				if (defender.getSiege() != 0) {
					defHits = countHits(2 * defender.getSiege(), 3);
				}

				//Increment rank
				rank++;

				//Display results
				System.out.println("With " + Integer.toString(attacker.getSiege()) + " siege unit(s) the attacker scored " + Integer.toString(attHits) + " hit(s).");
				System.out.println("With " + Integer.toString(defender.getSiege()) + " siege unit(s) the defender scored " + Integer.toString(defHits) + " hit(s).\n");

			} else if (rank == 2) {
				//Archer Attack
				if (attacker.getArcher() != 0) {
					attHits = countHits(attacker.getArcher(), 5);
				}
				if (defender.getArcher() != 0) {
					defHits = countHits(defender.getArcher(), 5);
				}

				//Increment rank
				rank++;

				//Display Results
				System.out.println("With " + Integer.toString(attacker.getArcher()) + " archer unit(s) the attacker scored " + Integer.toString(attHits) + " hit(s).");
				System.out.println("With " + Integer.toString(defender.getArcher()) + " archer unit(s) the defender scored " + Integer.toString(defHits) + " hit(s).\n");

			} else if (rank == 3) {
				//Cavalry Attack
				if (attacker.getArcher() != 0) {
					attHits = countHits(attacker.getCavalry(), 3);
				}
				if (defender.getArcher() != 0) {
					defHits = countHits(defender.getCavalry(), 3);
				}

				//Increment rank
				rank++;

				//Display Results
				System.out.println("With " + Integer.toString(attacker.getCavalry()) + " cavalry unit(s) the attacker scored " + Integer.toString(attHits) + " hit(s).");
				System.out.println("With " + Integer.toString(defender.getCavalry()) + " cavalry unit(s) the defender scored " + Integer.toString(defHits) + " hit(s).\n");

			} else {
				//General Attack
				int[] hits = genAttack(3, 2);
				attHits = hits[0];
				defHits = hits[1];

				//Display Results
				System.out.println("The attacker scored " + Integer.toString(attHits) + " hit(s).");
				System.out.println("The defender scored " + Integer.toString(defHits) + " hit(s).\n");

				//Reset rank and increment turn
				turn++;
				rank = 1;
			}

			//Wait before continuing forwards
			System.out.println("Press enter to continue to Rank " + Integer.toString(rank) + ": ");
			sc.nextLine();
			System.out.println("\n \n");

			//Remove units based on other army's hits
			attacker.destroyUnits(defHits);
			defender.destroyUnits(attHits);
		}

		//Display Winner
		String winner;

		if (attacker.getTotal() == 0) {
			winner = "defending";
		} else {
			winner = "attacking";
		}

		System.out.println("The winner is the " + winner + " army!");

		//Compare intial values
		System.out.println("Initial Values: Attacking - " + Integer.toString(attInit) + " , Defending - " + Integer.toString(defInit));
		System.out.println();

		//Value Lost
		int attValLost = attInit - attacker.totalValue();
		int defValLost = defInit - defender.totalValue();

		System.out.println("The attacking army lost a value of " + Integer.toString(attValLost) + " coins.");
		System.out.println("The defending army lost a value of " + Integer.toString(defValLost) + " coins.");
	}
}
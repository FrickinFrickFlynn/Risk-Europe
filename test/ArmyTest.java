public class ArmyTest {
	static Army testArmy;

	public static void main(String args[]) {
		//Init army with 10 foot, 2 arch, 4 cav, and 2 siege.
		testArmy = new Army(10, 2, 4, 2);

		//Print values
		printValues();

		//Remove a few units
		testArmy.destroyUnits(6);

		//Check new values
		printValues();

		//Remove more units
		testArmy.destroyUnits(10);

		//Check new values
		printValues();

		//Remove more than what is left
		testArmy.destroyUnits(4);

		//Final values
		printValues();
	}

	static void printValues() {
		System.out.println("Footsoldiers: " + Integer.toString(testArmy.getFoot()));
		System.out.println("Archers: " + Integer.toString(testArmy.getArcher()));
		System.out.println("Cavalry: " + Integer.toString(testArmy.getCavalry()));
		System.out.println("Siege Weapons: " + Integer.toString(testArmy.getSiege()));
		System.out.println("Total: " + Integer.toString(testArmy.getTotal()));
		System.out.println("Value Total: " + Integer.toString(testArmy.totalValue()));
		System.out.println();
	}
}
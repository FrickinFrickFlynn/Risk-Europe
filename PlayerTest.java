public class PlayerTest {
	public static void main(String[] args) {
		int[] testCards = {1, 2, 3, 4, 3, 6, 7, 8};

		Player p1 = new Player("Dylan", "red");
		Player p2 = new Player();

		System.out.println(p1);
		System.out.println(p2);

		p1.setMoney(14);
		p1.addMoney(6);
		p1.addMoney(-6);

		p1.giveCards(testCards);
		System.out.println(p1);

		int[] unusedCards = p1.getCards();
		for (int i = 0; i < unusedCards.length; i++) {
			System.out.print(unusedCards[i] + " ");
		}

		p1.pickCard(unusedCards[0]);
		p1.pickCard(unusedCards[4]);
		System.out.println(p1);

		unusedCards = p1.getCards();
		for (int i = 0; i < unusedCards.length; i++) {
			System.out.print(unusedCards[i] + " ");
		}
		System.out.println("\n");

		System.out.println(p1.useCard());
		System.out.println(p1);

		System.out.println(p1.useCard());
		System.out.println(p1);

		p1.pickCard(2);
		p1.pickCard(3);
		p1.useCard();
		p1.useCard();
		System.out.println(p1);

		p1.pickCard(4);
		p1.pickCard(5);
		p1.useCard();
		p1.useCard();
		System.out.println(p1);

		p1.pickCard(7);
		p1.pickCard(8);
		p1.useCard();
		System.out.println(p1);
		p1.useCard();
		System.out.println(p1);
	}
}
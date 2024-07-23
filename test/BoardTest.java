public class BoardTest {
	public static void main(String[] args) {
		// Territory t1 = new Territory("Alpha", 2, false);
		// Territory t2 = new Territory("Beta", 4, true);
		// Territory t3 = new Territory("Charlie", 3, false);
		// Territory t4 = new Territory("Delta", 1, false);
		// Territory t5 = new Territory("Echo", 5, true);

		// Territory[] terrs = {t1, t2, t3, t4, t5};
		// String[][] verts = {{"Alpha", "Delta"}, {"Delta", "Beta", "Echo"}, {"Beta", "Echo", "Charlie"}};

		Board testBoard = new Board();
		testBoard.loadFromFile("test.txt");
		System.out.println(testBoard);

		// for (int i = 0; i < terrs.length; i++) {
		// 	for (int j = 0; j < terrs.length; j++) {
		// 		if (j == i) continue;

		// 		System.out.println(terrs[i].getName() + " " + terrs[j].getName() + " " + terrs[i].isAdjacent(terrs[j]));
		// 	}
		// }


	}
}
public class GameTest {
	public static void main(String[] args) {
		String[] plist = {"Billy", "Bobby", "Johnny", "Kevin"};
		int[] cards = {1,1,2,3,3,4,5,5};

		Game theGame = new Game(plist, "risk_eu_board.txt", cards);
		theGame.start();
	}
}
/*
	Game represents a game instance
	
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
*/

public class Game {
	private Board brd;						// Stores game board
	private Player[] ply;					// Stores players
	private Player firstPlayer;				// Stores player ref who is first
	private Player winner;					// Stores the winner

	/*

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


}
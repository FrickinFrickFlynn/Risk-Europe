public class TerritoryTest {
	public static void main(String[] args) {
		Territory t1, t2;
		Army dudes = new Army(4, 3, 2, 1);

		t1 = new Territory("First", 3, false);
		t2 = new Territory();

		t2.setName("Second");
		t2.setValue(2);
		t2.setCrown(true);
		t2.setCastle(true);
		t2.addConnection(t1);
		t1.addUnit(dudes);

		System.out.println(t1.isAdjacent(t2));
		System.out.println(t2);
		System.out.println(t1);
	}
}
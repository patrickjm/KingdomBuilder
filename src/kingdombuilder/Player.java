package kingdombuilder;

import java.awt.Color;

public class Player {
	private int houses;
	private int num;
	private int cardnum;
	private Color color;
	
	public Player(int houses, int num, Color color) {
		this.houses = houses;
		this.num = num;
		this.color = color;
	}
	
	public int getHouses() { return houses; }
	public int getPlayerNum() { return num; }
	public int getCardNum() { return cardnum; }
	public Color getColor() { return color; }
	public void useHouse() { houses--; }
	
	public void setCardNum(int cardnum) { this.cardnum = cardnum; }
	
	@Override
	public String toString() { return "Player " + num; }
}

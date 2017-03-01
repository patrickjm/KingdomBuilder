package kingdombuilder;

public class Card {
	private int tile;
	private String info;
	
	public Card(int tile, String info) {
		this.tile = tile;
		this.info = info;
	}
	
	public int getTile() {
		return tile;
	}

	public String getInfo() {
		return info;
	}
}

package kingdombuilder;

public class Location extends Tile {
	private int locationType;
	
	public Location(Board board, int tile, int x, int y, int type) {
		super(board, tile, x, y);
		locationType = type;
	}
	
	public int getLocationType() { return locationType; }
	
	@Override
	public String toString() {
		return locationType + "";
	}
}

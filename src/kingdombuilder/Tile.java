package kingdombuilder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tile {
    private int tile;
    private String information;
    protected int x, y;
    protected Board board;
    
    public Tile(Board board, int tile, int x, int y) {
    	this.board = board;
        this.tile = tile;
        this.x = x;
        this.y = y;
        fetchInfo();
    }
    
    private void fetchInfo() {
        information = Assets.getTileInfo(tile) + "\n(" + x + ", " + y + ")";
    }
    
    public void update() { }
    
    public int getTile() { return tile; }
    public void setTile(int tile) { this.tile = tile; }
    public BufferedImage getImage() { return Assets.getTilemap("tiles").get(tile); }
    public BufferedImage getImageBrigher() { return Assets.getTilemap("tiles").getBrighter(tile); }
    public BufferedImage getImageDarker() { return Assets.getTilemap("tiles").getDarker(tile); }
    
    @Override
    public String toString() {
        return information;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        fetchInfo();
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public Board getBoard() { return board; }
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public Tile[] getAdjacent() {
		ArrayList<Tile> ret = new ArrayList<>();
		ret.add(getLoc(x - 1, y));
		ret.add(getLoc(x + 1, y));
		ret.add(getLoc(x, y + 1));
		ret.add(getLoc(x, y - 1));
		if(y % 2 == 0) {
			ret.add(getLoc(x - 1, y + 1));
			ret.add(getLoc(x - 1, y - 1));
		} else {
			ret.add(getLoc(x + 1, y + 1));
			ret.add(getLoc(x + 1, y - 1));
		}
		int c = 0;
		for(Tile t : ret)
			if (t != null) c++;
		Tile[] r = new Tile[c];
		c = 0;
		for(Tile t : ret)
			if (t != null)
				r[c++] = t;
		
		return r;
	}
	
	private Tile getLoc(int x, int y) {
		if (x >= 0 && y >= 0 && x < board.getWidth() && y < board.getHeight())
			return board.getTile(x, y);
		return null;
	}
	
	public boolean hasAdjacentSettlement(Player p) {
		for(Tile t : getAdjacent()) {
			if (t instanceof Settlement)
				if(((Settlement)t).getOwner() == p)
					return true;
		}
		return false;
	}
}

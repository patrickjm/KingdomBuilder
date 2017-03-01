package kingdombuilder;

public class Settlement extends Tile {
    Player owner;
    int base;
	
	public Settlement(Board board, int tile, int x, int y, Player owner, int base) {
        super(board, tile, x, y);
        this.owner = owner;
        this.base = base;
    }
    
    public Player getOwner() { return owner; }
    
    @Override
    public String toString() {
    	return "";// + owner.getPlayerNum();
    }
    
    public int getBaseTile() { return base; }
}
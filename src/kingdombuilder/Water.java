package kingdombuilder;

import java.awt.image.BufferedImage;

public class Water extends Tile {
	private int anim = 0;
	private int wait = 0;

	public Water(Board board, int tile, int x, int y) {
		super(board, tile, x, y);
	}
	
	@Override
	public BufferedImage getImage() { return Assets.getTilemap("water").get(anim); }
	
	@Override
    public BufferedImage getImageBrigher() { return Assets.getTilemap("water").getBrighter(anim); }
	
	@Override
    public BufferedImage getImageDarker() { return Assets.getTilemap("water").getDarker(anim); }
	
	@Override
	public String toString() { return "" + anim; }
	
	@Override
	public void update() {
		wait++;
		if (wait > 10) {
			wait = 0;
			anim++;
			anim %= Assets.getTilemap("water").getTileCount();
		}
	}
}

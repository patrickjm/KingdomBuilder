package kingdombuilder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.TreeMap;

public class Tilemap {
    private BufferedImage[] tiles;
    private int width, height;
    private TreeMap<Integer, int[]> origins;
    private TreeMap<Integer, BufferedImage> brighter;
    private TreeMap<Integer, BufferedImage> darker;
    
    public Tilemap(BufferedImage image, int w, int h) {
        tiles = new BufferedImage[(image.getWidth() * image.getHeight()) / (w * h)];
        width = w;
        height = h;
        origins = new TreeMap<>();
        brighter = new TreeMap<>();
        darker = new TreeMap<>();
        
        for(int i = 0; i < (image.getWidth() / w) * (image.getHeight() / h); i++)  {
            int ix = (i % (image.getWidth() / w)) * w;
            int iy = (i / (image.getHeight() / h)) * h;
            tiles[i] = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            tiles[i].getGraphics().drawImage(image, 0, 0, w, h, ix, iy, ix + w, iy + h, null);
            tiles[i].flush();
        }
    }
    
    public Tilemap(BufferedImage[] imgs) {
    	tiles = imgs;
        width = imgs[0].getWidth();
        height = imgs[0].getHeight();
        origins = new TreeMap<>();
        brighter = new TreeMap<>();
        darker = new TreeMap<>();
    }
    
    public BufferedImage get(int i) {
        return tiles[i];
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTileCount() { return tiles.length; }
    
    public void setOrigins(TreeMap<Integer, int[]> origins) { this.origins = origins; }
    public int getOriginX(int i) { return origins.get(i)[0]; }
    public int getOriginY(int i) { return origins.get(i)[1]; }
    
    public BufferedImage getBrighter(int tile) {
    	if (brighter.containsKey(tile))
    		return brighter.get(tile);
    	BufferedImage ret = new BufferedImage(tiles[tile].getWidth(), tiles[tile].getHeight(), BufferedImage.TYPE_INT_ARGB);
    	for(int i = 0; i < ret.getWidth(); i++)
    		for(int j = 0; j < ret.getHeight(); j++)
    			if (tiles[tile].getRGB(i, j) != 0)
    				ret.setRGB(i, j, new Color(tiles[tile].getRGB(i, j)).brighter().getRGB());
    	brighter.put(tile, ret);
    	return ret;
    }
    
    public BufferedImage getDarker(int tile) {
    	if (darker.containsKey(tile))
    		return darker.get(tile);
    	BufferedImage ret = new BufferedImage(tiles[tile].getWidth(), tiles[tile].getHeight(), BufferedImage.TYPE_INT_ARGB);
    	for(int i = 0; i < ret.getWidth(); i++)
    		for(int j = 0; j < ret.getHeight(); j++)
    			if (tiles[tile].getRGB(i, j) != 0)
    				ret.setRGB(i, j, new Color(tiles[tile].getRGB(i, j)).darker().darker().getRGB());
    	darker.put(tile, ret);
    	return ret;
    }
}

package kingdombuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class Board {
    private Tile[][] tiles;
    private int renderStates[][];
    private int width, height;
    private BufferedImage collisionMask;
    private static final int cMaskMargin = 50;
    private Tile selectedTile;
    private int update = -1;
    private static IBoardUpdate[] updates;
    private Game game;
    
    static {
    	updates = new IBoardUpdate[1];
    	
		updates[0] = new IBoardUpdate() {
			boolean ignoreAdjacentRule = true;
			Integer[] disallowed = new Integer[] { 5, 6, 7, 8, 10, 13 };
			Integer[] grayout = new Integer[] { 5, 6, 7, 10 };
			public int check(Player player, Board board, Tile tile, int x, int y) {
				int pCard = player.getCardNum();
				if ((!ignoreAdjacentRule && !tile.hasAdjacentSettlement(player)) || (new ArrayList<Integer>(Arrays.asList(grayout)).contains(tile.getTile()) || pCard != tile.getTile()))
					return 1 << ERenderState.DARKER.ordinal();
				else if (board.getSelectedTile() == tile)
					return 1 << ERenderState.OUTLINE_WHITESKINNY.ordinal();
				return 0;
			}
			public void update(Game game, Board board, Player player, int[][] renderStates) {
				if (board.getSelectedTile() != null && Input.mouse().getPressed(1)) {
					int x = board.getSelectedTile().getX(), y = board.getSelectedTile().getY();
					if ((renderStates[x][y] & (1 << ERenderState.DARKER.ordinal())) == 0 && !new ArrayList<Integer>(Arrays.asList(disallowed)).contains(board.getTile(x, y).getTile())) {
						board.setTile(x, y, new Settlement(board, 13, x, y, player, board.getTile(x, y).getTile()));
						board.setBoardUpdateNum(-1);
						game.useTurnHouse();
						player.useHouse();
						if (game.getTurnHouses() <= 0) {
							game.incrementPlayer();
							game.resetTurnHouses();
						}
					}
				}
			}
			public String getTooltip() {
				if (ignoreAdjacentRule)
					return "Place a town on a hex matching your card.";
				else
					return "Place a town on a hex matching your card and adjacent to an existing town.";
			}
			public void activated(Game gam, Board board) {
				ignoreAdjacentRule = true;
				for(int i = 0; i < board.getWidth(); i++) {
					for(int j = 0; j < board.getHeight(); j++) {
						if (board.getTile(i, j).getTile() == gam.getPlayer().getCardNum())
							if (board.getTile(i, j).hasAdjacentSettlement(gam.getPlayer()))
								ignoreAdjacentRule = false;
					}
				}
			}
		};
    }

    public Board(Game game, int width, int height) {
        this.width = width;
        this.height = height;
        this.game = game;
        tiles = new Tile[width][height];
        renderStates = new int[width][height];
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++)
                tiles[i][j] = null;
        
        // create the render mask
        collisionMask = new BufferedImage(getPixelWidth() + cMaskMargin * 2, getPixelHeight() + cMaskMargin * 2, BufferedImage.TYPE_INT_ARGB);
        int spacingWidth = Assets.<Integer>getSetting("tileSpacingWidth");
        int spacingHeight = Assets.<Integer>getSetting("tileSpacingHeight");
        int size = Assets.<Integer>getSetting("tileGridRender");
        int scale = Assets.<Integer>getSetting("tileGridRender") / Assets.<Integer>getSetting("tileGridSize");
        for(int j = 0; j < height; j++)
            for(int i = 0; i < width; i++) {            	
                int xo = -Assets.getTilemap("tiles").getOriginX(12) * scale + cMaskMargin;
                int yo = -Assets.getTilemap("tiles").getOriginY(12) * scale + cMaskMargin;
                drawSolidTile(collisionMask.getGraphics(), xo + i * spacingWidth + (j % 2 == 1 ? 14 : 0), yo + j * spacingHeight, size, size, 12, new Color(0, i, j).getRGB());
            }
        collisionMask.flush();
    }
    
    public void render(Game game, Graphics canvas, int x, int y) {
    	x += Assets.<Integer>getSetting("tileSpacingWidth") / 2;
    	y += Assets.<Integer>getSetting("tileSpacingHeight") / 2;
    	
    	// reset the render states & update the tiles (mostly used for animation)
    	for(int i = 0; i < width; i++)
    		for(int j = 0; j < height; j++) {
    			renderStates[i][j] = (update == -1) ? 0 : updates[update].check(game.getPlayer(), this, tiles[i][j], i, j);
    			tiles[i][j].update();
    			if (tiles[i][j].getTile() == 13)
    				renderStates[i][j] |= (1 << ERenderState.OUTLINE_COLORSKINNY.ordinal());
    		}
    	
    	
    	// get the mouse-over tile
        int mx = Input.mouse().getX(), my = Input.mouse().getY();
        if ((mx + Assets.getTilemap("tiles").getOriginX(0) * 2) >= x && (my + Assets.getTilemap("tiles").getOriginY(0) * 2) >= y && mx < x + getPixelWidth() && my < y + getPixelHeight()) {
            if (collisionMask.getRGB(mx - x + cMaskMargin, my - y + cMaskMargin) != 0) {
                Color c = new Color(collisionMask.getRGB(mx - x + cMaskMargin, my - y + cMaskMargin));
                renderStates[c.getGreen()][c.getBlue()] |= (1 << ERenderState.BRIGHTER.ordinal());
                selectedTile = tiles[c.getGreen()][c.getBlue()];
            }
        }
        
        // render the tiles
        int tileSpacingHeight = Assets.<Integer>getSetting("tileSpacingHeight");
        int tileSpacingWidth = Assets.<Integer>getSetting("tileSpacingWidth");
        int tileGridRender = Assets.<Integer>getSetting("tileGridRender");
        for(int j = 0; j < height; j++)
            for(int i = 0; i < width; i++) {
                Tile tile = getTile(i, j);
                int scale = tileGridRender / Assets.<Integer>getSetting("tileGridSize"); 
                int xo = -Assets.getTilemap("tiles").getOriginX(tile.getTile()) * scale;
                int yo = -Assets.getTilemap("tiles").getOriginY(tile.getTile()) * scale;
                
                BufferedImage render = tile.getImage();
                if ((renderStates[i][j] & (1 << ERenderState.BRIGHTER.ordinal())) != 0)
                	render = tile.getImageBrigher();
                else if ((renderStates[i][j] & (1 << ERenderState.DARKER.ordinal())) != 0)
                	render = tile.getImageDarker();
                canvas.drawImage(render, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tileGridRender, tileGridRender, null);
            }
        
        for(int i = 0; i < width; i++)
        	for(int j = 0; j < height; j++) {
        		if (renderStates[i][j] != 0) {
        			Tile tile = getTile(i, j);
        			int scale = tileGridRender / Assets.<Integer>getSetting("tileGridSize"); 
                    int xo = -Assets.getTilemap("tiles").getOriginX(tile.getTile()) * scale;
                    int yo = -Assets.getTilemap("tiles").getOriginY(tile.getTile()) * scale;
                    
                    boolean doRender = false;
                    if ((renderStates[i][j] & (1 << ERenderState.OUTLINE_GREENTHICK.ordinal())) != 0) {
                    	drawOutline(canvas, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tile.getTile(), 2, Color.green.brighter());
                    	doRender = true;
                    }
                    if ((renderStates[i][j] & (1 << ERenderState.OUTLINE_WHITESKINNY.ordinal())) != 0) {
                    	if ((renderStates[i][j] & (1 << ERenderState.OUTLINE_COLORSKINNY.ordinal())) != 0)
                    		drawOutline(canvas, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tile.getTile(), 3, Color.white);
                    	else
                    		drawOutline(canvas, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tile.getTile(), 1, Color.white);
                    	doRender = true;
                    }
                    if ((renderStates[i][j] & (1 << ERenderState.OUTLINE_COLORSKINNY.ordinal())) != 0) {
                    	doRender = true;
                    	Settlement t = (Settlement)tiles[i][j];
                    	drawOutline(canvas, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tile.getTile(), 2, t.getOwner().getColor());
                    }
                    if (doRender) {
                    	BufferedImage render = tile.getImage();
                        if ((renderStates[i][j] & (1 << ERenderState.BRIGHTER.ordinal())) != 0)
                        	render = tile.getImageBrigher();
                        else if ((renderStates[i][j] & (1 << ERenderState.DARKER.ordinal())) != 0)
                        	render = tile.getImageDarker();
                        canvas.drawImage(render, xo + x + (i * tileSpacingWidth) + (j % 2 == 1 ? tileSpacingWidth / 2 : 0), yo + y + (j * tileSpacingHeight), tileGridRender, tileGridRender, null);
                    }
        		}
        	}
        
        if(update != -1)
        	updates[update].update(game, this, game.getPlayer(), renderStates);
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getPixelWidth() { return Assets.<Integer>getSetting("tileSpacingWidth") * width; }
    public int getPixelHeight() { return Assets.<Integer>getSetting("tileSpacingHeight") * height; }
    public void setTile(int x, int y, Tile tile) { 
        tiles[x][y] = tile; 
        tile.setPosition(x, y);
    }
    
    public Tile getTile(int x, int y) { 
        return tiles[x][y];
    }
    
    public Settlement[] getTowns() {
        ArrayList<Settlement> towns = new ArrayList<>();
        for(int i = 0; i < width; i++)
            for(int j = 0; j < height; j++)
                if (tiles[i][j] instanceof Settlement)
                    towns.add((Settlement)tiles[i][j]);
        Settlement[] t = new Settlement[towns.size()];
        int c = 0;
        for(Settlement to : towns)
            t[c++] = to;
        return t;
    }
    
    public Tile getSelectedTile() {
        return selectedTile;
    }
    
    public int getBoardUpdateNum() {
    	return update;
    }
    
    public void setBoardUpdateNum(int update) {
    	this.update = update;
    	if (update >= 0 && update < updates.length)
    		updates[update].activated(game, this);
    }
    
    public IBoardUpdate getBoardUpdate() {
    	return update == -1 ? null : updates[update];
    }
    
    public Game getGame() {
    	return game;
    }
    
    private static void drawSolidTile(Graphics g, int x, int y, int sx, int sy, int tile, int color) {
        BufferedImage temp = new BufferedImage(sx, sy, BufferedImage.TYPE_INT_ARGB);
        temp.getGraphics().drawImage(Assets.getTilemap("tiles").get(tile), 0, 0, sx, sy, null);
        temp.flush();
        for(int i = 0; i < sx; i++)
            for(int j = 0; j < sy; j++)
                if (new Color(temp.getRGB(i, j), true).getAlpha() != 0)
                    temp.setRGB(i, j, color);
        g.drawImage(temp, x, y, sx, sy, null);
    }
    
    private static void drawOutline(Graphics canvas, int x, int y, int tile, int margin, Color color) {
    	int s = Assets.<Integer>getSetting("tileGridRender");
        drawSolidTile(canvas, -margin + x, -margin + y, s, s, tile, color.getRGB());
        drawSolidTile(canvas, margin + x, margin + y, s, s, tile, color.getRGB());
        drawSolidTile(canvas, margin + x, -margin + y, s, s, tile, color.getRGB());
        drawSolidTile(canvas, -margin + x, margin + y, s, s, tile, color.getRGB());
    }
}
package kingdombuilder;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Gui {
	
	public static final int BUTTON_TOWN_CREATE = 3;
	public static final int BUTTON_TURN_NEXT = 4;
	public static final int BUTTON_ARROW_LEFT = 11;
	public static final int BUTTON_ARROW_RIGHT = 12;
	static String[] invDescriptions = { "YOUR CARD" };
	static int invSelection;
	static String tooltip = "testing 1 2 3";
	
	public static void manageGui(Graphics canvas, KingdomBuilder kingdomBuilder, Game game, Board board, int x, int y) {
		Rectangle infoWindow = new Rectangle(x, y, 130, 100);
        Rectangle buttonWindow = new Rectangle((int)infoWindow.getX(), (int)(infoWindow.getY() + infoWindow.getHeight() + 30), (int)infoWindow.getWidth(), Assets.<Integer>getSetting("cardHeight") + 20);
        Rectangle chitWindow = new Rectangle((int)buttonWindow.getX(), (int)(buttonWindow.getY() + buttonWindow.getHeight() + 30), (int)buttonWindow.getWidth(), 80);
        final int tileSize = Assets.<Integer>getSetting("tileGridRender");
        
        // information window
        drawWindow(canvas, (int)infoWindow.getX(), (int)infoWindow.getY(), (int)infoWindow.getWidth(), (int)infoWindow.getHeight());
        canvas.setColor(Color.white);
        canvas.setFont(Assets.getFont("gui"));
        canvas.setColor(game.getPlayer().getColor());
        canvas.drawString("PLAYER " + (game.getPlayerNum() + 1), (int)infoWindow.getX() + 20, (int)infoWindow.getY() + 32);
        canvas.setColor(Color.WHITE);
        String info = "\nHOUSES: " + game.getPlayer().getHouses()
        		+ "\nTHIS TURN: " + game.getTurnHouses()
        		+ "\n\n" + ((board.getSelectedTile() == null) ? "" : board.getSelectedTile().toString());
        int j = 0;
        for(String str : info.split("\n")) {
            canvas.drawString(str, (int)infoWindow.getX() + 20, (int)infoWindow.getY() + 32 + (14 * j++));
        }
        
        // button window
        drawWindow(canvas, (int)buttonWindow.getX(), (int)buttonWindow.getY(), (int)buttonWindow.getWidth(), (int)buttonWindow.getHeight());
        if (drawButton(canvas, BUTTON_TOWN_CREATE, (int)buttonWindow.getX() + 20, (int)buttonWindow.getY() + 20, new Rectangle(0, 0, tileSize, tileSize)) || Input.keyboard().keyDown(KeyEvent.VK_SPACE)) {
        	board.setBoardUpdateNum(EBoardUpdate.TownSelect.ordinal());
        }
        if (drawButton(canvas, BUTTON_TURN_NEXT, (int)buttonWindow.getX() + 20, (int)buttonWindow.getY() + 55, new Rectangle(0, 0, tileSize, tileSize)) || Input.keyboard().keyDown(KeyEvent.VK_ENTER)) {
        	board.setBoardUpdateNum(-1);
        	game.incrementPlayer();
        }
        canvas.drawImage(Assets.getTilemap("cards").get(game.getPlayer().getCardNum()), (int)(buttonWindow.getX() + buttonWindow.getWidth()) - Assets.<Integer>getSetting("cardWidth"), (int)buttonWindow.getY() + 24, null); 
        
        // chit window
        int winMargin = Assets.<Integer>getSetting("windowMargin");
        int cardWidth = Assets.getTilemap("cards").get(0).getWidth(), cardHeight = Assets.getTilemap("cards").get(0).getHeight();
        drawWindow(canvas, (int)chitWindow.getX(), (int)chitWindow.getY(), (int)chitWindow.getWidth(), (int)chitWindow.getHeight());
        BufferedImage cardBI = new BufferedImage((int)chitWindow.getWidth(), (int)chitWindow.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics cardCanvas = cardBI.getGraphics();
        drawChitWindow(cardCanvas, cardBI.getWidth(), cardBI.getHeight());
        canvas.drawImage(cardBI, (int)chitWindow.getX() + winMargin, (int)chitWindow.getY() + winMargin, null);
        
//        if (drawButton(canvas, BUTTON_ARROW_LEFT, (int)cardWindow.getX() + 20, (int)cardWindow.getY() + (int)cardWindow.getHeight() - 10, new Rectangle(-5, -5, 20, 20))) {
//        	System.out.println("Left button press!");
//        }
//        if (drawButton(canvas, BUTTON_ARROW_RIGHT, (int)cardWindow.getX() + (int)cardWindow.getWidth() - 10, (int)cardWindow.getY() + (int)cardWindow.getHeight() - 10, new Rectangle(-5, -5, 20, 20))) {
//        	System.out.println("Right button press!");
//        }
        
        // draw tooltip
        setTooltip(board.getBoardUpdateNum() == -1 ? "" : board.getBoardUpdate().getTooltip());
        if (tooltip != null) {
        	canvas.setFont(Assets.getFont("gui"));
        	canvas.setColor(Color.white);
        	canvas.drawString(tooltip, 10, board.getPixelHeight() + 35);
        }
	}
	
	private static void drawChitWindow(Graphics g, int width, int height) {
		g.setFont(Assets.getFont("cards"));
        FontMetrics metric = g.getFontMetrics();
        Rectangle2D rect = metric.getStringBounds("YOUR CHITS:", g);
        g.drawString("YOUR CHITS:", (int)(width / 2 - rect.getWidth() / 2) - 4, 16);
	}
	
	private Gui() { }
	
	public static void drawWindow(Graphics canvas, int x, int y, int sx, int sy) {
		Tilemap gui = Assets.getTilemap("gui");
		int[] tiles = { 0, 1, 2, 8, 9, 10, 16, 17, 18 };
		int k = 0, scale = 32;
		final int topLeft = tiles[k++];
		final int topMiddle = tiles[k++];
		final int topRight = tiles[k++];
		final int middleLeft = tiles[k++];
		final int center = tiles[k++];
		final int middleRight = tiles[k++];
		final int bottomLeft = tiles[k++];
		final int bottomMiddle = tiles[k++];
		final int bottomRight = tiles[k++];
		
		canvas.drawImage(gui.get(topLeft), x , y, scale, scale, null); // top left
		canvas.drawImage(gui.get(topRight), x + sx, y, scale, scale, null); // top right
		canvas.drawImage(gui.get(bottomLeft), x, y + sy, scale, scale, null); // bottom left
		canvas.drawImage(gui.get(bottomRight), x + sx, y + sy, scale, scale, null); // bottom right
		// top & bottom
		canvas.drawImage(gui.get(topMiddle), x + scale , y, sx - scale, scale, null);
		canvas.drawImage(gui.get(bottomMiddle), x + scale , y + sy, sx - scale, scale, null);
		// left & right
		canvas.drawImage(gui.get(middleLeft), x , y + scale, scale, sy - scale, null);
		canvas.drawImage(gui.get(middleRight), x + sx, y + scale, scale, sy - scale, null);
		//center
		canvas.drawImage(gui.get(center), x + scale, y + scale, sx - scale, sy - scale, null);
	}
	
	public static boolean drawButton(Graphics canvas, int button, int x, int y, Rectangle rect) {
		int tileSize = Assets.<Integer>getSetting("tileGridRender");
		boolean call = false;
		if (Input.mouse().mouseIn(new Rectangle((int)rect.getX() + x, (int)rect.getY() + y, (int)rect.getWidth(), (int)rect.getHeight()))) { // is the mouse over the button?
			if (Input.mouse().getPressed(1))
				call = true;
			if (Input.mouse().getDown(1))
				canvas.drawImage(Assets.getTilemap("gui").getDarker(button), x, y, tileSize, tileSize, null);
			else
				canvas.drawImage(Assets.getTilemap("gui").getBrighter(button), x, y, tileSize, tileSize, null);
		}
		else
			canvas.drawImage(Assets.getTilemap("gui").get(button), x, y, tileSize, tileSize, null);
		
		return call;
	}
	
	public static void setTooltip(String t) { tooltip = t; }
	public static String getTooltip() { return tooltip; }
}

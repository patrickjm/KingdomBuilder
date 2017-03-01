package kingdombuilder;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

import javax.imageio.ImageIO;

public class Assets  {
    private static HashMap<String, BufferedImage> textures;
    private static HashMap<String, Tilemap> tilemaps;
    private static ArrayList<Board> boards;
    private static HashMap<Integer, String> tileInfos;
    private static HashMap<String, Object> settings;
    private static HashMap<String, Font> fonts;
    private static TreeMap<Character, Integer> defines;
    
    static {
        textures = new HashMap<>();
        tilemaps = new HashMap<>();
        tileInfos = new HashMap<>();
        settings = new HashMap<>();
        fonts = new HashMap<>();
    }
    
    public static void load() {
        try {
        	loadConfig(new Scanner(new File("config.in")));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        generateCards();
        generateWater();
    }
    
    public static BufferedImage getTexture(String key) {
        return textures.get(key);
    }
    
    public static Tilemap getTilemap(String key) {
        return tilemaps.get(key);
    }
    
    public static String getTileInfo(int tile) {
        return tileInfos.get(tile);
    }
    
    public static Board[] getBoards() {
        Board[] b = new Board[boards.size()];
        int c = 0;
        for(Board bo : boards)
            b[c++] = bo;
        return b;
    }
    
    public static Font getFont(String font) {
    	return fonts.get(font);
    }
    
    @SuppressWarnings("unchecked")
	public static <T> T getSetting(String setting) {
    	return (T)settings.get(setting);
    }
    
    public static Tile createTile(Board board, int tile, int x, int y) {
        if (tile == 5)
        	return new Water(board, tile, x, y);
        else
            return new Tile(board, tile, x, y);
    }
    
    public static Tile createTile(Board board, String tile, int x, int y) {
    	if (tile.length() > 1)
    		if (tile.startsWith("L"))
    			return new Location(board, defines.get('L'), x, y, Integer.parseInt(tile.substring(1)));
    	return createTile(board, defines.get(tile.charAt(0)), x, y);
    }
    
    private static void loadConfig(Scanner input) throws IOException {
        ArrayList<Board> maps = new ArrayList<>();
        defines = new TreeMap<>();
        
        while(input.hasNext()) {
            String com = input.nextLine();
            if (com.startsWith("#")) {
                switch(com.substring(1).split(" ")[0]) {
                	case "settings": {
                		int count = Integer.parseInt(com.split(" ")[1]);
                		for(int i = 0; i < count; i++) {
                			String[] line = input.nextLine().split(" = ");
                			if (line[1].endsWith("i"))
                				settings.put(line[0], Integer.parseInt(line[1].substring(0, line[1].length() - 1)));
                		}
                	} break;
                
                    case "origins": {
                        int count = Integer.parseInt(com.split(" ")[1]);
                        TreeMap<Integer, int[]> origins = new TreeMap<>();
                        for(int i = 0; i < count; i++) {
                            String line = input.nextLine();
                            String[] l = line.split(" - ");
                            String[] n = l[1].split(",");
                            origins.put(Integer.parseInt(l[0]), new int[] { Integer.parseInt(n[0]), Integer.parseInt(n[1]) });
                        }
                        tilemaps.get("tiles").setOrigins(origins);
                    } break;
                        
                    case "defines": {
                        int count = Integer.parseInt(com.split(" ")[1]);
                        for(int i = 0; i < count; i++) {
                            String[] line = input.nextLine().split(";")[0].split(" ");
                            defines.put(line[0].charAt(0), Integer.parseInt(line[1]));
                        }
                    } break;
                        
                    case "info": {
                        int count = Integer.parseInt(com.split(" ")[1]);
                        for(int i = 0; i < count; i++) {
                            String line = input.nextLine();
                            int tile = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                            line = line.substring(line.indexOf("\"") + 1);
                            line = line.substring(0, line.indexOf("\""));
                            line.replaceAll("\\n", "\n");
                            tileInfos.put(tile, line);
                        }
                    } break;
                    
                    case "res": {
                    	int count = Integer.parseInt(com.split(" ")[1]);
                        for(int i = 0; i < count; i++) {
                        	String[] line = input.nextLine().split(" ");
                        	if (line[0].equals("texture")) {
                        		textures.put(line[1], ImageIO.read(new File(line[2])));
                        	}
                        	else if (line[0].equals("tilemap")) {
                        		tilemaps.put(line[1], 
                        				new Tilemap(getTexture(line[2]), 
                        				line[3].equals("&") ? Assets.<Integer>getSetting("tileGridSize") : Integer.parseInt(line[3]), 
                        				line[4].equals("&") ? Assets.<Integer>getSetting("tileGridSize") : Integer.parseInt(line[4])));
                        	}
                        	else if (line[0].equals("font")) {
                        		try{
                        			fonts.put(line[1], Font.createFont(Font.TRUETYPE_FONT, new File(line[2])).deriveFont(Float.parseFloat(line[3])));
                        		} catch(FontFormatException e) {
                        			e.printStackTrace(System.err);
                        		}
                        	}
                        }
                    } break;
                        
                    case "map": {
                        Board b = new Board(null, 10, 10);
                        for(int i = 0; i < 10; i++) {
                            String[] line = input.nextLine().split(" ", 10);
                            for(int j = 0; j < 10; j++)
                                b.setTile(j, i, createTile(b, line[j], i, j));
                        }
                        maps.add(b);
                        boards = maps;
                    } break;
                }
            }
        }
    }
    
    private static void generateCards() {
    	Tilemap tiles = getTilemap("tiles");
    	int width = Assets.<Integer>getSetting("cardWidth"), height = Assets.<Integer>getSetting("cardHeight");
    	BufferedImage[] cards = new BufferedImage[tiles.getTileCount()];
    	final int tileSize = Assets.<Integer>getSetting("cardIconSize");
    	final int radius = 32;
    	for(int i = 0; i < tiles.getTileCount(); i++) {
    		cards[i] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    		Graphics g = cards[i].getGraphics();
    		// draw the card background
    		g.setColor(Color.DARK_GRAY.darker());
    		g.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);
    		g.setColor(Color.WHITE);
    		g.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
    		// draw the respective icon on the card
    		g.drawImage(tiles.get(i), (width / 2) - (tileSize / 2), (height / 3) - (tileSize / 2), (width / 2) + (tileSize / 2), (height / 3) + (tileSize / 2), 0, 0, tiles.get(i).getWidth(), tiles.get(i).getHeight(), null);
    		// draw the text at the bottom
    		g.setColor(Color.LIGHT_GRAY);
    		g.setFont(Assets.getFont("cards"));
    		String txt = "" + Assets.getTileInfo(i);
    		FontMetrics metrics = g.getFontMetrics();
    		Rectangle2D dim = metrics.getStringBounds(txt, g);
    		g.drawString(txt, (width / 2) - (int)dim.getWidth() / 2, 5 * height / 6);
    	}
    	
    	tilemaps.put("cards", new Tilemap(cards));
    }
    
    public static void generateWater() {
    	final int count = 10;
    	final int rSize = Assets.<Integer>getSetting("tileGridSize");
    	BufferedImage[] images = new BufferedImage[10];
    	for(int i = 0; i < count; i++) {
    		float alpha = (float)i / (count - 1);
    		//System.out.println(alpha);
    		images[i] = new BufferedImage(rSize, rSize, BufferedImage.TYPE_INT_ARGB);
    		images[i].getGraphics().drawImage(tilemaps.get("tiles").get(5), 0, 0, null);
    		//images[i].flush();
    		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)alpha);
    		((Graphics2D)images[i].getGraphics()).setComposite(composite);
    		((Graphics2D)images[i].getGraphics()).drawImage(tilemaps.get("tiles").get(6), 0, 0, null);
    		//images[i].flush();
    	}
    	
    	tilemaps.put("water", new Tilemap(images));
    }
}

package kingdombuilder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
    KingdomBuilder kingdomBuilder;
    Board board;
    int player = 0;
    Player[] players;
    int turnHouses;
    int turn;
    
    public Game(KingdomBuilder kb) {    	
        kingdomBuilder = kb;
        
        Board[] boards = new Board[4];
        ArrayList<Board> allBoards = new ArrayList<>(Arrays.asList(Assets.getBoards()));
        for(int i = 0; i < 4; i++)
            boards[i] = allBoards.remove((int)(Math.random() * allBoards.size()));
        
        board = new Board(this, 20, 20);
        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++)
                for(int k = 0; k < 10; k++)
                    for(int l = 0; l < 10; l++) {
                        board.setTile(i * 10 + k, j * 10 + l, boards[i * 2 + j].getTile(k, l));
                        boards[i * 2 + j].getTile(k, l).setBoard(board);
                    }
        for(int i = 0; i < 20; i++)
        	for(int j = 0; j < 20; j++)
        		board.getTile(i, j).setPosition(i, j);
        
        resetTurnHouses();
        
        Color[] colors = { Color.RED, Color.BLUE.brighter().brighter(), Color.YELLOW, Color.GREEN };
        
        int[] possibleCards = { 0, 2, 4, 9, 11 };
        players = new Player[4];
        for(int i = 0; i < 4; i++) { 
        	players[i] = new Player(20, i, colors[i]);
        	players[i].setCardNum(possibleCards[(int)(Math.random() * possibleCards.length)]);
        }
    }
    
    public void update(double dt) {
        if (Input.keyboard().keyDown(KeyEvent.VK_ESCAPE))
            System.exit(0);
    }
    
    public void render(Graphics canvas, double dt) {
    	Assets.generateWater();
    	canvas.setColor(Color.DARK_GRAY);
        canvas.fillRect(0, 0, kingdomBuilder.getWidth(), kingdomBuilder.getHeight());
        
        board.render(this, canvas, 15, 25);
        Gui.manageGui(canvas, kingdomBuilder, this, board, 620, 5);
        
//        for(int i = 0; i < 10; i++)
//        	canvas.drawImage(Assets.getTilemap("water").get(i), 10 + i * 64, 430, 64, 64,  null);
    }
    
    public int getPlayerNum() { return player; }
    public Player getPlayer() { return players[player]; }
    public Player getPlayer(int p) { return players[p]; }
    public Board getBoard() { return board; }
    public void incrementPlayer() {
    	turn++;
    	int[] possibleCards = { 0, 2, 4, 9, 11 };
    	player = (player + 1) % 4; 
    	players[player].setCardNum(possibleCards[(int)(Math.random() * possibleCards.length)]);
    }
    public int getTurn() {
		return turn;
	}

	public int getTurnHouses() { return turnHouses; }
    public void useTurnHouse() { turnHouses--; }
    public void resetTurnHouses() { turnHouses = 3; }
    
    public void victory() {
    	
    }
}

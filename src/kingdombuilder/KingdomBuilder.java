package kingdombuilder;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class KingdomBuilder extends JComponent {
    
    private JFrame window;
    private volatile boolean running;
    private double timePrevious;
    private BufferedImage canvas;
    private Game game;
    
    public KingdomBuilder(JFrame window, int width, int height) {
        this.window = window;
        
        Input.init();
        window.getContentPane().addMouseListener(Input.mouse());
        window.getContentPane().addMouseMotionListener(Input.mouse());
        window.addKeyListener(Input.keyboard());
        
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Assets.load();
        game = new Game(this);
    }
    
    public void run() {
        running = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    double fps = 15;
                    try {
                        Thread.sleep((long) ((double) 1000 / fps));
                    } catch (InterruptedException e) {
                    }
                    repaint();
                    Input.keyboard().poll();
                }
            }
        }, "gameLoop").run();
    }

    public void update(double dt) {
        Input.poll();
        game.update(dt);
        game.render(canvas.getGraphics(), dt);
    }

    @Override
    public void paint(Graphics g) {
        double dt = (double) ((System.currentTimeMillis() - timePrevious) / 1000);
        update(dt);
        timePrevious = System.currentTimeMillis();
        g.drawImage(canvas, 0, 0, null);
    }

    public boolean getRunning() {
        return running;
    }

    public void stop() {
        running = false;
    }
    
    public static void main(String[] args) {
        int width = 790, height = 435;
    	//int width = 820, height = 550;
        
        JFrame window = new JFrame("Kingdom Builder");
        window.setSize(width, height);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        KingdomBuilder g = new KingdomBuilder(window, width, height);
        g.setSize(width, height);

        window.add(g);

        window.setVisible(true);
        g.run();
    }
}

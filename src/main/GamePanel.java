package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import static main.PlayManager.staticBlocks;
import mino.Block;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1600;
    public static final int HEIGHT = 900;
    final int FPS = 30;
    Thread gameThread;
    PlayManager pm;
    public static Sound music = new Sound();
    public static Sound se = new Sound();
    public static boolean gameOver = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);

        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        pm = new PlayManager();
    }

    public void LaunchGame() {
        gameThread = new Thread(this);
        gameThread.start();

        music.play(0, true);
        music.loop();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime-lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        if (!gameOver && !KeyHandler.pausePressed) {
            pm.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        pm.draw(g2);
    }

    public void restartGame() {
        pm = new PlayManager(); // Reinitialize the PlayManager
        gameOver = false; // Reset game over state
        KeyHandler.pausePressed = false; // Ensure the game is not paused
        staticBlocks.clear(); // Clear all static blocks

        // Reset boss state
        pm.bossSpawned = false;
        pm.bossAlive = false;
        pm.boss = null;
        pm.bossSpawnTimer = 0;
        pm.bossIncoming = false;

        // Reset the character model position
        pm.cm.setXY(PlayManager.left_x + (pm.WIDTH / 2), PlayManager.bottom_y - Block.SIZE);

        // Reset the music
        music.stop();
        music.play(0, true);
        music.loop();
    }
}

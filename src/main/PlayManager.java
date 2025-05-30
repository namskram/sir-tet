package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;
import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;
import mino.entities.Boss;
import mino.entities.CharModel;

public class PlayManager {

    public final int WIDTH = 384;
    public final int HEIGHT = 768;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    public static Mino currentMino;
    public final int MINO_START_X;
    public final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks =  new ArrayList<>();

    public static int dropInterval = 30;
    public static boolean gameOver;

    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    int level = 1;
    int lines;
    int score;

    public static CharModel cm;
    public static int playerDamage = 1;

    public static Boss boss;
    public static boolean bossSpawned = false;
    public static int bossSpawnTimer = 0; // Timer to track when to spawn the boss
    public static boolean bossAlive = false;
    public boolean bossIncoming = false;
    private int bossIncomingFlashCount = 0;
    private int bossIncomingFlashTimer = 0;
    private boolean bossIncomingVisible = true;
    private int nextBossSpawnTime = 600; // Time in frames to wait before the next boss spawn
    private final Random random = new Random();
    // private boolean fadingIn;
    // private float bossMusicVolume = -80.0f;

    public PlayManager() {
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE*5;

        NEXTMINO_X = right_x + 180;
        NEXTMINO_Y = top_y + 700;

        cm = new CharModel(Color.WHITE);
        cm.setXY(left_x, bottom_y - Block.SIZE);

        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i) {
            case 0 -> mino = new Mino_L1();
            case 1 -> mino = new Mino_L2();
            case 2 -> mino = new Mino_Square();
            case 3 -> mino = new Mino_Bar();
            case 4 -> mino = new Mino_T();
            case 5 -> mino = new Mino_Z1();
            case 6 -> mino = new Mino_Z2();
        }
        return mino;
    }

    public void update() {
        if (gameOver) {
            return;
        }
        if (!bossSpawned) {
            bossAlive = true;
            bossSpawnTimer++;

            // Play the "boss incoming" sound 3 seconds before the boss spawns
            if (bossSpawnTimer == nextBossSpawnTime - 90) { // 17 seconds at 30 FPS
                GamePanel.music.pause(); // Stop any current music
                GamePanel.music.play(7, false); // Play boss music
                bossIncoming = true; // Show "BOSS INCOMING" text
                bossIncomingFlashCount = 0;
                bossIncomingFlashTimer = 0;
                bossIncomingVisible = true;
            }

            if (bossSpawnTimer >= nextBossSpawnTime) { // 20 seconds at 30 FPS
                boss = new Boss(this);
                bossSpawned = true;

                GamePanel.bossMusic.play(6, true); // Play boss music
                GamePanel.bossMusic.setVolume(-20.0f); // Set the volume for the boss music
                GamePanel.bossMusic.loop();
                bossIncoming = false; // Hide "BOSS INCOMING" text
                //fadingIn = true;
            }
        }

        if (bossIncoming) {
            bossIncomingFlashTimer++;
            if (bossIncomingFlashTimer >= 18) { // Toggle every 15 frames (~0.5s at 30 FPS)
                bossIncomingVisible = !bossIncomingVisible;
                bossIncomingFlashTimer = 0;
                if (!bossIncomingVisible) {
                    bossIncomingFlashCount++;
                }
                if (bossIncomingFlashCount >= 3) { // Flash 3 times
                    bossIncoming = false;
                    bossIncomingFlashCount = 0;
                    bossIncomingVisible = true;
                }
            }
        }
        /*

        if (fadingIn) {
            if (bossMusicVolume < 0.0f) {
                bossMusicVolume += 2.0f; 
                GamePanel.music.setVolume(bossMusicVolume);
                System.out.println("Fading in: Volume = " + bossMusicVolume);
            } else {
                fadingIn = false; 
                System.out.println("Fade-in complete: Volume = " + bossMusicVolume);
            }
        }
        */
    
        if (bossSpawned && boss != null) {
            boss.update();
        }

        if (!bossAlive) {
            bossSpawned = false; // Reset the spawn flag
            boss = null; // Remove the boss instance
            bossSpawnTimer = 0; // Reset the spawn timer
            nextBossSpawnTime = 600 + random.nextInt(301); // 600 to 900 (20s to 30s at 30 FPS)
            GamePanel.bossMusic.stop(); // Stop the boss music
            GamePanel.music.play(8, false); // Play boss defeated sound
            GamePanel.music.resume(); // Play normal music again
            GamePanel.music.loop();
        }
    
        if (currentMino.active == false) {
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);
    
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y <= MINO_START_Y + (Block.SIZE)) {
                GamePanel.gameOver = true;
                GamePanel.music.stop();
                GamePanel.music.play(2, false);
                return;
            }
    
            currentMino.deactivating = false;
    
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    
            checkDelete();
        } 
        
        else {
            currentMino.update();
            cm.update();
        }
    }

    private void checkDelete() {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x) {
                if (blockCount == 12) {

                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size()-1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;

                    if (lines % 10 == 10 && dropInterval > 1) {
                        level++;
                        if (dropInterval > 10) {
                            dropInterval -= 10;
                        }
                        else {
                            dropInterval -= 1;
                        }
                    }

                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        if (lineCount > 0) {
            GamePanel.se.play(1, false);
            int singleLineScore = 10 * level;
            score += singleLineScore * lineCount;
            playerDamage += lineCount; // Increase player damage by lines cleared

            cm.heal(lineCount); // Heal the character by lines cleared
        }
    }

    public void draw(Graphics2D g2) {
        // Draw the Tetris box boundary
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // Draw the game over line
        g2.setColor(Color.red); // Use red to make it stand out
        g2.setStroke(new BasicStroke(2f)); // Thinner line for the game over line
        g2.drawLine(left_x, top_y + (Block.SIZE*4), right_x, top_y + (Block.SIZE*4)); // Horizontal line at the top_y position

        // Draw the "NEXT" box and other UI elements
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.setColor(Color.white);
        g2.drawRect(x, y, 220, 220);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 64, y + 64);

        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y);
        y += 70;
        g2.drawString("LINES: " + lines, x, y);
        y += 70;
        g2.drawString("SCORE: " + score, x, y);

        // Draw the character model
        cm.draw(g2);

        // Draw the current and next minos
        if (currentMino != null) {
            currentMino.draw(g2);
        }
        nextMino.draw(g2);

        // Draw all static blocks
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        if (bossIncoming && bossIncomingVisible) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            int textWidth = g2.getFontMetrics().stringWidth("BOSS INCOMING");
            x = (GamePanel.WIDTH - textWidth) / 2;
            y = GamePanel.HEIGHT / 2;
            g2.drawString("BOSS INCOMING", x, y);
        }

        // Draw the boss if spawned
        if (bossSpawned && boss != null) {
            boss.draw(g2);
        }

        // Handle line clear effects
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.red);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw the game over screen
        if (GamePanel.gameOver) {
            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(50f));
            x = PlayManager.left_x + 50;
            y = PlayManager.top_y + 320;
            g2.drawString("GAME OVER", x, y);

            g2.setFont(g2.getFont().deriveFont(30f));
            g2.drawString("Press R to Restart", x + 20, y + 50);
        }

        // Draw the pause screen
        if (KeyHandler.pausePressed) {
            x = left_x + 80;
            y = top_y + 320;
            g2.setColor(Color.yellow);
            g2.setFont(g2.getFont().deriveFont(50f));
            g2.drawString("PAUSED", x, y);

            g2.setFont(g2.getFont().deriveFont(30f));
            g2.drawString("Press R to Restart", x, y + 50);
        }

        // Draw the title
        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g2.drawString("Sir Tet", x + 100, y);
    }
}

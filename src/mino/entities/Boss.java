package mino.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.PlayManager;

public class Boss {
    public int x, y;
    public int speedX = 4; // Horizontal speed
    public int speedY = 2; // Vertical speed
    private BufferedImage sprite;
    private PlayManager playManager; // Reference to the PlayManager instance

    public Boss(PlayManager playManager) {
        this.playManager = playManager; // Store the PlayManager reference

        // Initial position (centered horizontally within the Tetris box)
        x = playManager.left_x + (playManager.WIDTH / 2) - 64; // Center horizontally
        y = playManager.top_y + 32; // Spawn near the top of the Tetris box

        // Load placeholder sprite
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/dragon-right.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Move the boss left and right
        x += speedX;

        // Reverse direction if hitting the Tetris box boundaries horizontally
        if (x <= playManager.left_x) {
            x = playManager.left_x; // Prevent going out of bounds
            speedX = -speedX;
        } else if (x >= playManager.right_x - 128) { // Assuming boss sprite width is 128
            x = playManager.right_x - 128; // Prevent going out of bounds
            speedX = -speedX;
        }

        // Move the boss up and down
        y += speedY;

        // Reverse direction if hitting the Tetris box boundaries vertically
        if (y <= playManager.top_y) {
            y = playManager.top_y; // Prevent going out of bounds
            speedY = -speedY;
        } else if (y >= playManager.MINO_START_Y - 128) { // Stay above the Tetris blocks
            y = playManager.MINO_START_Y - 128; // Prevent going out of bounds
            speedY = -speedY;
        }
    }

    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, 128, 128, null); // Draw boss sprite
        } else {
            // Draw a placeholder rectangle if sprite is missing
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(x, y, 128, 128);
        }
    }
}
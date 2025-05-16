package mino.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.PlayManager;
import mino.Block;

public class Projectile {
    public int x, y;
    public int damage;
    private final int speed = Block.SIZE / 1; // Speed of the projectile
    private final int width = Block.SIZE / 4; // Width of the projectile
    private final int height = Block.SIZE; // Height of the projectile
    public boolean active = true; // Whether the projectile is still active
    private final double angle; // Angle of the projectile in degrees
    private final String source; // Source of the projectile ("player" or "boss")
    private BufferedImage image; // Image for the projectile
    private static BufferedImage playerProjectileImage;
    private static BufferedImage bossProjectileImage;

    static {
        try {
            playerProjectileImage = ImageIO.read(Projectile.class.getResourceAsStream("/arrow-diag.png"));
            bossProjectileImage = ImageIO.read(Projectile.class.getResourceAsStream("/fireball.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Projectile(int x, int y, double angle, String source, int damage) {
        this.x = x;
        this.y = y;
        this.angle = Math.toRadians(angle); // Convert angle to radians
        this.source = source; // Set the source of the projectile
        this.damage = damage;

        // Use cached images
        if (source.equals("player")) {
            image = playerProjectileImage;
        } else if (source.equals("boss")) {
            image = bossProjectileImage;
        }
    }

    public void update() {
        // Move the projectile based on its angle
        x += (int) (speed * Math.sin(angle)); // Horizontal movement
        y -= (int) (speed * Math.cos(angle)); // Vertical movement

        // Check collision with static blocks
        for (int i = 0; i < PlayManager.staticBlocks.size(); i++) {
            Block block = PlayManager.staticBlocks.get(i);
            if (x < block.x + Block.SIZE && x + width > block.x && // Horizontal overlap
                y < block.y + Block.SIZE && y + height > block.y) { // Vertical overlap
                active = false; // Deactivate the projectile
                break; // Exit the loop after collision
            }
        }

        // Check collision with falling blocks (current mino)
        for (Block block : PlayManager.currentMino.b) {
            if (x < block.x + Block.SIZE && x + width > block.x && // Horizontal overlap
                y < block.y + Block.SIZE && y + height > block.y) { // Vertical overlap
                active = false; // Deactivate the projectile
                break; // Exit the loop after collision
            }
        }

        // Check collision with the boss (only if the projectile is from the player)
        if (source.equals("player") && PlayManager.bossSpawned && PlayManager.boss != null) {
            if (x < PlayManager.boss.x + 128 && x + width > PlayManager.boss.x && // Horizontal overlap
                y < PlayManager.boss.y + 128 && y + height > PlayManager.boss.y) { // Vertical overlap
                active = false; // Deactivate the projectile
                PlayManager.boss.takeDamage(damage); // Damage the boss
            }
        }

        // Check collision with the player (only if the projectile is from the boss)
        if (source.equals("boss") && PlayManager.cm != null) {
            if (x < PlayManager.cm.b[0].x + Block.SIZE && x + width > PlayManager.cm.b[0].x && // Horizontal overlap
                y < PlayManager.cm.b[0].y + Block.SIZE && y + height > PlayManager.cm.b[0].y) { // Vertical overlap
                active = false; // Deactivate the projectile
                PlayManager.cm.takeDamage(damage); // Damage the player (implement `takeDamage` in CharModel)
            }
        }

        // Deactivate the projectile if it goes out of bounds
        if (y < 7 || y >= PlayManager.bottom_y || x < Block.SIZE * 19 || x > Block.SIZE * 31) { // Adjust bounds as needed
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        // Save the original transform
        AffineTransform originalTransform = g2.getTransform();

        // Translate to the projectile's position
        g2.translate(x + width / 2, y + height / 2);

        // Rotate based on the angle and source
        if (source.equals("player")) {
            g2.rotate(angle - Math.PI / 4);
        } 
        
        else if (source.equals("boss")) {
            g2.rotate(angle + Math.PI);
        }

        // Draw the projectile image
         g2.drawImage(image, -(width * 8) / 2, -(height * 2) / 2, width * 8, height * 2, null);

        // Restore the original transform
        g2.setTransform(originalTransform);
    }
}

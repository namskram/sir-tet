package mino.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import main.PlayManager;
import mino.Block;

public class Boss {
    public int x, y;
    public int speedX = 4; // Horizontal speed
    public int speedY = 2; // Vertical speed
    private int health;
    private int maxHealth;
    private int projectileDamage;
    private BufferedImage spriteRight;
    private BufferedImage spriteLeft;
    private BufferedImage sprite;
    private PlayManager playManager; // Reference to the PlayManager instance
    private List<Projectile> projectiles = new ArrayList<>(); // List of boss projectiles
    private int shootCooldown = 30; // Cooldown time in frames for shooting
    private int shootTimer = 0; // Timer to track frames since the last shot

    public static int bossLevel = 1;
    public static int baseHP = 10;
    public static int baseProjectileDamage = 1;

    public Boss(PlayManager playManager) {
        this.playManager = playManager; // Store the PlayManager reference

        // Initial position (centered horizontally within the Tetris box)
        x = PlayManager.left_x + (playManager.WIDTH / 2) - 64; // Center horizontally
        y = PlayManager.top_y + 32; // Spawn near the top of the Tetris box

        maxHealth = baseHP + (bossLevel - 1) * 5; // Example: +5 HP per level
        health = maxHealth; // Set initial health
        projectileDamage = baseProjectileDamage + (bossLevel - 1); // Example: +1 damage per level

        // Load placeholder sprite
        try {
            spriteRight = ImageIO.read(getClass().getResourceAsStream("/res/dragon-right.png"));
            spriteLeft = ImageIO.read(getClass().getResourceAsStream("/res/dragon-left.png"));
            sprite = spriteRight; // Default to right
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        // System.out.println("Boss takes " + damage + " damage! Boss HP: " + health);
        if (health <= 0) {
            // System.out.println("Boss defeated!");
            PlayManager.bossAlive = false; // Set boss alive status to false
            bossLevel++;
        }
    }

    public void update() {
        // Move the boss left and right
        x += speedX;

        // Reverse direction if hitting the Tetris box boundaries horizontally
        if (x <= PlayManager.left_x) {
            x = PlayManager.left_x; // Prevent going out of bounds
            speedX = -speedX;
        } else if (x >= PlayManager.right_x - 128) { // Assuming boss sprite width is 128
            x = PlayManager.right_x - 128; // Prevent going out of bounds
            speedX = -speedX;
        }

        if (speedX > 0) {
            sprite = spriteRight;
        } else if (speedX < 0) {
            sprite = spriteLeft;
        }

        // Move the boss up and down
        y += speedY;

        // Reverse direction if hitting the Tetris box boundaries vertically
        if (y <= PlayManager.top_y) {
            y = PlayManager.top_y; // Prevent going out of bounds
            speedY = -speedY;
        } else if (y >= playManager.MINO_START_Y - 128) { // Stay above the Tetris blocks
            y = playManager.MINO_START_Y - 128; // Prevent going out of bounds
            speedY = -speedY;
        }

        // Update the shoot timer
        if (shootTimer > 0) {
            shootTimer--;
        }

        // Shoot a projectile if the cooldown is over
        if (shootTimer == 0) {
            shootProjectile();
            shootTimer = shootCooldown; // Reset the cooldown timer
        }

        // Update all projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update();
            if (!projectile.active) {
                projectiles.remove(i); // Remove inactive projectiles
            }
        }
    }

    // When shooting, pass projectileDamage to the Projectile constructor
    private void shootProjectile() {
        // Get the center of the boss
        int bossCenterX = x + 64;
        int bossCenterY = y + 64;

        // Get the center of the player
        int playerCenterX = PlayManager.cm.b[0].x + Block.SIZE / 2;
        int playerCenterY = PlayManager.cm.b[0].y + Block.SIZE / 2;

        // Calculate the angle from the boss to the player
        double angleRad = Math.atan2(playerCenterY - bossCenterY, playerCenterX - bossCenterX);
        double angleDeg = Math.toDegrees(angleRad) + 90;

        // Spawn the projectile at the center of the boss
        int projectileX = bossCenterX;
        int projectileY = bossCenterY;

        projectiles.add(new Projectile(projectileX, projectileY, angleDeg, "boss", projectileDamage));
    }

    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, 128, 128, null); // Draw boss sprite
        } else {
            // Draw a placeholder rectangle if sprite is missing
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(x, y, 128, 128);
        }

        // Draw the boss's health bar
        int healthBarWidth = 128; // Full width of the health bar
        int healthBarHeight = 10; // Height of the health bar
        int currentHealthWidth = (int) ((health / (double) maxHealth) * healthBarWidth); // Scale width based on health

        // Draw the background of the health bar (gray)
        g2.setColor(java.awt.Color.GRAY);
        g2.fillRect(x, y - 15, healthBarWidth, healthBarHeight);

        // Draw the current health (green)
        g2.setColor(java.awt.Color.GREEN);
        g2.fillRect(x, y - 15, currentHealthWidth, healthBarHeight);

        // Draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw(g2);
        }
    }
}
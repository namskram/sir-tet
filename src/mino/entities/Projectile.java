package mino.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import mino.Block;

public class Projectile {
    public int x, y;
    private final int speed = Block.SIZE / 1; // Speed of the projectile
    private final int width = Block.SIZE / 4; // Width of the projectile
    private final int height = Block.SIZE; // Height of the projectile
    private final Color color = Color.YELLOW; // Color of the projectile
    public boolean active = true; // Whether the projectile is still active
    private double angle; // Angle of the projectile in degrees

    public Projectile(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = Math.toRadians(angle); // Convert angle to radians
    }

    public void update() {
        // Move the projectile based on its angle
        x += (int) (speed * Math.sin(angle)); // Horizontal movement
        y -= (int) (speed * Math.cos(angle)); // Vertical movement

        // Deactivate the projectile if it goes out of bounds
        if (y < 7 || x < Block.SIZE * 19 || x > Block.SIZE * 31) { // Adjust bounds as needed
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        // Save the original transform
        AffineTransform originalTransform = g2.getTransform();

        // Translate to the projectile's position
        g2.translate(x + width / 2, y + height / 2);

        // Rotate based on the angle
        g2.rotate(angle);

        // Draw the projectile as a rotated rectangle
        g2.setColor(color);
        g2.fillRect(-width / 2, -height / 2, width, height);

        // Restore the original transform
        g2.setTransform(originalTransform);
    }
}

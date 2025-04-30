package mino.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import mino.Block;

public class Projectile {
    public int x, y;
    private final int speed = Block.SIZE / 1; // Speed of the projectile
    private final int width = Block.SIZE / 4; // Width of the projectile
    private final int height = Block.SIZE; // Height of the projectile
    private final Color color = Color.YELLOW; // Color of the projectile
    public boolean active = true; // Whether the projectile is still active

    public Projectile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Move the projectile upward
        y -= speed;

        // Deactivate the projectile if it goes out of bounds
        if (y < 0) {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillRect(x, y, width, height);
    }
}

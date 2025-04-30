package mino.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.KeyHandler;
import main.PlayManager;
import mino.Block;
import mino.Mino;

public class CharModel {
    public Block b[] = new Block[1];
    public Block tempB[] = new Block[1];
    public int direction = 1;
    public boolean topCollision, leftCollision, rightCollision, bottomCollision;
    public BufferedImage left, right;
    public String dir;
    public int x, y;
    private final float moveSpeed = Block.SIZE/2; // 8 -> 16
    private final float jumpSpeed = Block.SIZE*2.5f; // 128 -> 64 -> 70
    private float fallSpeed = Block.SIZE/4; // 4 -> 8
    private boolean inAir = false;
    private float above;
    public List<Projectile> projectiles = new ArrayList<>(); // List of active projectiles

    public CharModel(Color c) {
        b[0] = new Block(c);
        tempB[0] = new Block(c);

        setDefaultValues();
        getCharModel();
    }

    public final void setDefaultValues() {
        x = 100;
        y = 100;
        dir = "right";
        above = 0;
    }

    public void create(Color c) {
        b[0] = new Block(c);
        tempB[0] = new Block(c);
    }

    public void setXY(int x, int y) {
        b[0].x = x;
        b[0].y = y;
    }

    public void updateXY(int direction) {

        if (!topCollision && !leftCollision && !rightCollision && !bottomCollision) {
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
        }
    }

    public final void getCharModel() {
        try {
            left = ImageIO.read(getClass().getResourceAsStream("/knight-left.png"));
            right = ImageIO.read(getClass().getResourceAsStream("/knight-right.png"));
        } 

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDirection1() {}

    public void getDirection2() {}

    public void getDirection3() {}

    public void getDirection4() {}

    public void checkMovementCollision() {
        topCollision = false;
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        checkMovingBlockCollision();
        checkStaticBlockCollision();

        for (Block b1 : b) {
            if (b1.y == PlayManager.top_y) {
                topCollision = true;
            }
        }

        for (Block b1 : b) {
            if (b1.x == PlayManager.left_x) {
                leftCollision = true;
            }
        }

        for (Block b1 : b) {
            if (b1.x + moveSpeed == PlayManager.right_x - moveSpeed) {
                rightCollision = true;
            }
        }

        for (Block b1 : b) {
            if (b1.y + fallSpeed >= PlayManager.bottom_y - Block.SIZE) {
                bottomCollision = true;
                inAir = false;
                b1.y = PlayManager.bottom_y - Block.SIZE; // Align character to the top of the floor
            }
        }

        /*
        if (!bottomCollision) {
            b[0].y += fallSpeed;
        }
        */
    }

    private void checkMovingBlockCollision() {
        Mino currentMino = PlayManager.currentMino;

        for (Block b1 : currentMino.b) {
            // Check for bottom collision
            if (b[0].y + fallSpeed >= b1.y - Block.SIZE && b[0].y < b1.y &&
                Math.abs(b1.x - b[0].x) < Block.SIZE) {
                bottomCollision = true;
                inAir = false;

                // Align the player to the top of the block
                b[0].y = b1.y - Block.SIZE;

                // Reset fallSpeed to prevent skipping
                fallSpeed = Block.SIZE / 4;

                // Skip side collision checks if a bottom collision is detected
                continue;
            }

            // Check for left collision
            if (b[0].x - moveSpeed < b1.x + Block.SIZE && b[0].x >= b1.x + Block.SIZE &&
                Math.abs(b1.y - b[0].y) < Block.SIZE) {
                leftCollision = true;
            }

            // Check for right collision
            if (b[0].x + moveSpeed + Block.SIZE > b1.x && b[0].x <= b1.x &&
                Math.abs(b1.y - b[0].y) < Block.SIZE) {
                rightCollision = true;
            }
        }
    }
    
    private void checkStaticBlockCollision() {
        for (int i = 0; i < PlayManager.staticBlocks.size(); i++) {
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            for (Block b1 : b) {
                // Check for bottom collision
                if (b1.y + fallSpeed >= targetY - Block.SIZE && b1.y < targetY &&
                    Math.abs(b1.x - targetX) < Block.SIZE) {
                    bottomCollision = true;
                    inAir = false;

                    // Align the player to the top of the block
                    b1.y = targetY - Block.SIZE;

                    // Reset fallSpeed to prevent skipping
                    fallSpeed = Block.SIZE / 4;

                    // Skip side collision checks if a bottom collision is detected
                    continue;
                }

                // Check for left collision
                if (b1.x - moveSpeed < targetX + Block.SIZE && b1.x >= targetX + Block.SIZE &&
                    Math.abs(b1.y - targetY) < Block.SIZE) {
                    leftCollision = true;
                    b1.x = targetX + Block.SIZE; // Align character to the right of the block
                }

                // Check for right collision
                if (b1.x + moveSpeed + Block.SIZE > targetX && b1.x <= targetX &&
                    Math.abs(b1.y - targetY) < Block.SIZE) {
                    rightCollision = true;
                    b1.x = targetX - Block.SIZE; // Align character to the left of the block
                }
            }
        }
    }    

    private void checkJumpCollision() {
        above = jumpSpeed; // Default jump height if no block is above
        boolean collisionDetected = false; // Track if any block is actually blocking the jump

        // Combine static and moving blocks into one list
        List<Block> allBlocks = new ArrayList<>(PlayManager.staticBlocks);
        allBlocks.addAll(Arrays.asList(PlayManager.currentMino.b));

        for (Block block : allBlocks) {
            int targetX = block.x;
            int targetY = block.y;

            // Check if the block is along the player's upward path
            if (b[0].y - jumpSpeed < targetY + Block.SIZE && b[0].y > targetY && // Block is in the upward path
                Math.abs(b[0].x - targetX) < Block.SIZE) { // Block overlaps horizontally
                int potentialAbove = b[0].y - targetY - Block.SIZE;

                // Prevent jumping if the block is directly above the player
                if (potentialAbove <= 0) {
                    above = 0; // No jump allowed
                    collisionDetected = true;
                    break; // Exit the loop since a collision is detected
                } else if (potentialAbove <= jumpSpeed) {
                    above = potentialAbove; // Adjust jump height to avoid clipping
                    collisionDetected = true;
                }
            }
        }

        // Reset `above` to default if no collisions were detected
        if (!collisionDetected) {
            above = jumpSpeed;
        }
    }
    
    public void update() {
        checkMovementCollision();

        if (!bottomCollision) {
            b[0].y += fallSpeed;
            fallSpeed *= 1.2f; // velocity
        }
        else {
            fallSpeed = Block.SIZE/4;
            //b[0].y = PlayManager.bottom_y - Block.SIZE;
        }

        // Update projectiles
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update();
            if (!projectile.active) {
                projectiles.remove(i); // Remove inactive projectiles
            }
        }

        // Shoot a projectile when the spacebar is pressed
        if (KeyHandler.spacePressed) { // Use 'R' as an example key
            shootProjectile();
            // KeyHandler.spacePressed = false; // Prevent continuous shooting
        }

        // Check if a falling block lands on the player
        Mino currentMino = PlayManager.currentMino;
        for (Block block : currentMino.b) {
            // Check if the block overlaps with the player's x position
            if (block.x < b[0].x + Block.SIZE && block.x + Block.SIZE > b[0].x &&
                block.y == b[0].y - Block.SIZE) {
                // Only trigger game over if the player is on the ground
                if (bottomCollision) {
                    GamePanel.gameOver = true;
                    GamePanel.music.stop();
                    GamePanel.music.play(2, false);
                    return;
                }
            }
        }

        if (KeyHandler.wPressed) {
            checkJumpCollision();
            if (!topCollision && !inAir) {
                b[0].y -= above;
                inAir = true;
            }
            KeyHandler.wPressed = false;
        }

        if (KeyHandler.aPressed) {
            if (!leftCollision) {
                dir = "left";
                b[0].x -= moveSpeed;
            }
        }

        if (KeyHandler.dPressed) {
            if (!rightCollision) {
                dir = "right";
                b[0].x += moveSpeed;
            }
        }
    }

    private void shootProjectile() {
        // Spawn a projectile at the player's position
        int projectileX = b[0].x + Block.SIZE / 4; // Center the projectile horizontally
        int projectileY = b[0].y - Block.SIZE / 2; // Spawn above the player
        projectiles.add(new Projectile(projectileX, projectileY));
    }

    public void draw(Graphics2D g2) {
        // Draw the character
        int margin = 16;
        BufferedImage image = null;
        switch (dir) {
            case "left" -> image = left;
            case "right" -> image = right;
        }
        g2.drawImage(image, b[0].x-margin, b[0].y-margin, 64, 64, null);

        // Draw projectiles
        for (Projectile projectile : projectiles) {
            projectile.draw(g2);
        }

    }
    
}

package mino.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;
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
    boolean topCollision, leftCollision, rightCollision, bottomCollision;
    public BufferedImage left, right;
    public String dir;
    public int x, y;

    public CharModel(Color c) {
        b[0] = new Block(c);
        tempB[0] = new Block(c);

        setDefaultValues();
        getCharModel();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        dir = "right";
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

    public void getCharModel() {
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
            if (b1.x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
        }

        for (Block b1 : b) {
            if (b1.y + Block.SIZE == PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }

    private void checkMovingBlockCollision() {
        Mino currentMino = PlayManager.currentMino;

        for (Block b1: currentMino.b) {
            if (b[0].y - Block.SIZE == b1.y && b[0].x == b1.x) {
                topCollision = true;
            }
            if (b[0].x - Block.SIZE == b1.x && b[0].y == b1.y) {
                leftCollision = true;
            }
            if (b[0].x + Block.SIZE == b1.x && b[0].y == b1.y) {
                rightCollision = true;
            }
            if (b[0].y + Block.SIZE == b1.y && b[0].x == b1.x) {
                bottomCollision = true;
            }
        }
    }

    private void checkStaticBlockCollision() {
        for (int i = 0; i < PlayManager.staticBlocks.size(); i++) {
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;
            for (Block b1 : b) {
                if (b1.y + Block.SIZE == targetY && b1.x == targetX) {
                    bottomCollision = true;
                }
            }
            for (Block b1 : b) {
                if (b1.x - Block.SIZE == targetX && b1.y == targetY) {
                    leftCollision = true;
                }
            }
            for (Block b1 : b) {
                if (b1.x + Block.SIZE == targetX && b1.y == targetY) {
                    rightCollision = true;
                }
            }
        }
    }

    public void update() {

        checkMovementCollision();

        if (KeyHandler.wPressed) {
            if (topCollision == false) {
                b[0].y -= Block.SIZE;
            }
            KeyHandler.wPressed = false;
        }

        if (KeyHandler.sPressed) {
            if (bottomCollision == false) {
                b[0].y += Block.SIZE;
            }
            KeyHandler.sPressed = false;
        }

        if (KeyHandler.aPressed) {
            if (leftCollision == false) {
                dir = "left";
                b[0].x -= Block.SIZE;
            }

            KeyHandler.aPressed = false;
        }

        if (KeyHandler.dPressed) {
            if (rightCollision == false) {
                dir = "right";
                b[0].x += Block.SIZE;
            }

            KeyHandler.dPressed = false;
        }

    }

    public void draw(Graphics2D g2) {
        //int margin = 2;
        //g2.setColor(b[0].c);
        //g2.fillRect(b[0].x+margin, b[0].y+margin, Block.SIZE-(margin*2), Block.SIZE-(margin*2));

        int margin = 15;
        BufferedImage image = null;
        switch (dir) {
            case "left" -> image = left;
            case "right" -> image = right;
        }
        g2.drawImage(image, b[0].x-margin, b[0].y-margin, 60, 60, null);

    }
    
}

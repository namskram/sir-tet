package main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {

    public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed, spacePressed;
    public static boolean wPressed, aPressed, sPressed, dPressed, qPressed, ePressed;
    public static boolean rPressed = false;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ESCAPE) {
            if (pausePressed) {
                pausePressed = false;
                GamePanel.music.resume();
                //GamePanel.music.play(0, true);
                //GamePanel.music.loop();
            } else {
                pausePressed = true;
                GamePanel.music.pause();
                //GamePanel.music.stop();
            }
        }

        if (code == KeyEvent.VK_R) {
            // Restart the game if it's over
            if (GamePanel.gameOver || pausePressed) {
                GamePanel gp = (GamePanel) e.getSource();
                gp.restartGame();
            }
        }

        if (code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_W) {
            wPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            aPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            dPressed = true;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }
        if (code == KeyEvent.VK_Q) {
            qPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        /* cant implement until frames lower
        if (code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        */

        if (code == KeyEvent.VK_A) {
            aPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            dPressed = false;
        }
        if (code == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
        if (code == KeyEvent.VK_Q) {
            qPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = false;
        }
    }

}

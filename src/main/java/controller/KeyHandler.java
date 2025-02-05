package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 'w', 'W' -> upPressed = true;
            case 'a', 'A' -> leftPressed = true;
            case 's', 'S' -> downPressed = true;
            case 'd', 'D' -> rightPressed = true;
            case KeyEvent.VK_ESCAPE -> System.exit(0);
            default -> {

            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 'w', 'W' -> upPressed = false;
            case 'a', 'A' -> leftPressed = false;
            case 's', 'S' -> downPressed = false;
            case 'd', 'D' -> rightPressed = false;
            default -> {
                // Handle other keys if necessary
            }
        }
    }
}

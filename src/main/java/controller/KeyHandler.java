package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public int hotbarSlot = -1; // -1 means no slot selected this tick

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 'w', 'W' -> upPressed = true;
            case 'a', 'A' -> leftPressed = true;
            case 's', 'S' -> downPressed = true;
            case 'd', 'D' -> rightPressed = true;
            case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                hotbarSlot = e.getKeyCode() - KeyEvent.VK_1;
            }
            case KeyEvent.VK_ESCAPE -> System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 'w', 'W' -> upPressed = false;
            case 'a', 'A' -> leftPressed = false;
            case 's', 'S' -> downPressed = false;
            case 'd', 'D' -> rightPressed = false;
        }
    }
}

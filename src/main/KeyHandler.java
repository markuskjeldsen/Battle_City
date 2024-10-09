package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();




        switch (keyEvent.getKeyCode()) {
            case 'w':
            case 'W':
                upPressed = true;
                break;
            case 'a':
            case 'A':
                leftPressed = true;
                break;
            case 's':
            case 'S':
                downPressed = true;
                break;
            case 'd':
            case 'D':
                rightPressed = true;
                break;
            default:
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {



        switch (keyEvent.getKeyCode()) {
            case 'w':
            case 'W':
                upPressed = false;
                break;
            case 'a':
            case 'A':
                leftPressed = false;
                break;
            case 's':
            case 'S':
                downPressed = false;
                break;
            case 'd':
            case 'D':
                rightPressed = false;
                break;
            default:
                break;
        }
    }
}

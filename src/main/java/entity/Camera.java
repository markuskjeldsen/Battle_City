package entity;

import controller.KeyHandler;
import controller.MouseHandler;
import tile.TileManager;
import view.GamePanel;

public class Camera {

    private final GamePanel gamePanel;
    private final TileManager tileManager;
    private final KeyHandler keyH;
    private final MouseHandler mouseH;
    public int x, y;
    public int speed;

    public Camera(GamePanel gamePanel, TileManager tileManager, KeyHandler keyH, MouseHandler mouseH) {
        this.gamePanel = gamePanel;
        this.tileManager = tileManager;
        this.keyH = keyH;
        this.mouseH = mouseH;
        // Initialize camera position and speed
        this.x = 0;
        this.y = 0;
        this.speed = 1;  // You can adjust the speed as needed
    }

    // Method to update and move the camera based on key input
    public void update() {
        if (keyH.upPressed) {
            moveCamera(0, -speed);
        }
        if (keyH.downPressed) {
            moveCamera(0, speed);
        }
        if (keyH.leftPressed) {
            moveCamera(-speed, 0);
        }
        if (keyH.rightPressed) {
            moveCamera(speed, 0);
        }
    }

    // Method to move the camera, ensuring it stays within bounds
    public void moveCamera(int deltaX, int deltaY) {
        x = Math.max(0, Math.min(tileManager.getMapDimensions().width() - gamePanel.maxScreenCol, x + deltaX));
        y = Math.max(0, Math.min(tileManager.getMapDimensions().height() - gamePanel.maxScreenRow, y + deltaY));
    }

}
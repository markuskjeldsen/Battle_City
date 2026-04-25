package view;

import controller.KeyHandler;
import tile.BlockManager;

public class Camera {
    private final GamePanel gp;
    public float x, y;
    public float zoom = 1.0f;
    private final KeyHandler keyH;

    public Camera(GamePanel gp) {
        this.gp = gp;
        this.x = 0;
        this.y = 0;
        this.keyH = gp.keyH;
    }

    public void update() {
        float speed = 4 * zoom;
        if (keyH.upPressed) y = Math.max(0, y - speed);
        if (keyH.downPressed) y = Math.min(gp.blockM.worldHeight * BlockManager.TILE_SIZE - gp.screenHeight, y + speed);
        if (keyH.leftPressed) x = Math.max(0, x - speed);
        if (keyH.rightPressed) x = Math.min(gp.blockM.worldWidth * BlockManager.TILE_SIZE - gp.screenWidth, x + speed);
    }
}

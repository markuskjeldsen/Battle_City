package entity;

import controller.MouseHandler;
import view.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;

public class Building extends Entity {
    public boolean isScheduledForDestruction;
    private final MouseHandler mouseH;
    private final GamePanel gp;
    public int x;
    public int y;


    public Building(MouseHandler mouseH, GamePanel gp, int buildingX, int buildingY) {
        this.mouseH = mouseH;
        this.gp = gp;
        this.x = buildingX;
        this.y = buildingY;
        isScheduledForDestruction = false;
        getImage();
    }

    public void getImage() {

        try {

            image = ImageIO.read(getClass().getResourceAsStream("/buildings/house1.png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        if (mouseH.right) { //destroy houses
            if (x == (mouseH.point.x / gp.tileSize ) && y == (mouseH.point.y / gp.tileSize )) {
                isScheduledForDestruction = true;
            }
        }


    }

    public void draw(Graphics2D g2, int tileSize, Camera camera) {

        int x = this.x - camera.x;
        int y = this.y - camera.y;

        g2.drawImage(image, x * tileSize, y * tileSize, tileSize, tileSize, null);

    }

}

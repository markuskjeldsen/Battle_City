package entity;

import controller.KeyHandler;
import controller.MouseHandler;
import view.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    MouseHandler mouseH;

    public Player(GamePanel gp, KeyHandler keyH, MouseHandler mouseH) {

        this.gp = gp;
        this.keyH = keyH;
        this.mouseH = mouseH;

        setDefaultValues();
        getPlayerImage();


    }

    private void setDefaultValues() {
        this.x = 100;
        this.y = 100;
        this.speed = 16;


    }

    private void getPlayerImage() {

        try {

            image = ImageIO.read(getClass().getResourceAsStream("/buildings/house1.png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void update() {
        //TODO
    }

    public void draw(Graphics2D g2) {


        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);


    }

}

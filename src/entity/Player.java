package entity;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;

public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyH;
    MouseHandler mouseH;

    public Player(GamePanel gp, KeyHandler keyH,MouseHandler mouseH) {

        this.gp = gp;
        this.keyH = keyH;
        this.mouseH = mouseH;

        setDefaultValues();
        getPlayerImage();


    }
    private void setDefaultValues(){
        x = 100;
        y = 100;
        speed = 16;


    }
    private void getPlayerImage(){

        try {

            image = ImageIO.read(getClass().getResourceAsStream("/buildings/house1.png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }
    public void update(){



        if        (keyH.upPressed){
            y -= speed;
        } else if (keyH.downPressed) {
            y += speed;
        } else if (keyH.leftPressed) {
            x -= speed;
        } else if (keyH.rightPressed) {
            x += speed;
        }

    }
    public void draw(Graphics2D g2){


        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);



    }

}

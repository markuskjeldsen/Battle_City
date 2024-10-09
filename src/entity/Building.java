package entity;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;

public class Building extends Entity{
    GamePanel gp;
    MouseHandler mouseH;
    KeyHandler keyH;
    public Point p;
    public boolean enable;


    public Building(GamePanel gp, KeyHandler keyH,MouseHandler mouseH, Point p){

        this.gp = gp;
        this.keyH = keyH;
        this.mouseH = mouseH;
        this.p = p;

        setDefaultValues(p);
        getImage();


    }
    private void setDefaultValues(Point p){

        enable = true;
        x = (p.x/gp.tileSize) *gp.tileSize;
        y = (p.y/gp.tileSize) *gp.tileSize;


    }

    public void getImage(){

        try {

            image = ImageIO.read(getClass().getResourceAsStream("/buildings/house1.png"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(){


        if(mouseH.right){ //destroy houses
            if(x == (mouseH.point.x/gp.tileSize * gp.tileSize) && y == (mouseH.point.y/gp.tileSize * gp.tileSize)){
                    enable = false;
            }
        }


    }

    public void draw(Graphics2D g2) {

        if (enable) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

    }

}

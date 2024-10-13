package tile;

import java.awt.image.BufferedImage;

public class Tile {

    public Tile(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage image;
    public boolean collision = false;



}

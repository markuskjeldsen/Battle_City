package entity;

import java.awt.image.BufferedImage;

public abstract class Entity {
    public int x, y;
    public int speed;
    public BufferedImage image;
    public int width, height;

    public boolean isCollidingWith(Entity other) {
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }
}

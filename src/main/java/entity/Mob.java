package entity;

import view.GamePanel;
import tile.BlockManager;
import tile.Block;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Mob extends Entity {
    GamePanel gp;
    public int health;
    public int maxHealth;
    public int damage;
    public int type; // 0=zombie, 1=skeleton, 2=creeper
    public boolean hostile;
    private long lastMoveTime;
    private static final int MOVE_INTERVAL = 500; // ms between moves

    public Mob(GamePanel gp, int type, int x, int y) {
        this.gp = gp;
        this.type = type;
        this.x = x;
        this.y = y;
        this.speed = type == 2 ? 2 : (type == 1 ? 1 : 2);
        this.width = 16;
        this.height = 16;
        this.lastMoveTime = System.currentTimeMillis();

        switch (type) {
            case 0: // zombie
                this.health = 20;
                this.maxHealth = 20;
                this.damage = 3;
                this.hostile = true;
                break;
            case 1: // skeleton
                this.health = 16;
                this.maxHealth = 16;
                this.damage = 4;
                this.hostile = true;
                break;
            case 2: // creeper
                this.health = 10;
                this.maxHealth = 10;
                this.damage = 10;
                this.hostile = true;
                break;
        }

        try {
            image = ImageIO.read(getClass().getResourceAsStream("/textures/mob_" + type + ".png"));
        } catch (Exception e) {
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            Color mobColor = type == 0 ? Color.GREEN : (type == 1 ? Color.GRAY : Color.CYAN);
            g.setColor(mobColor);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
        }
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastMoveTime < MOVE_INTERVAL) return;
        lastMoveTime = now;

        // find player
        Player player = gp.player;
        int dx = 0, dy = 0;

        if (player.x > x) dx = speed;
        else if (player.x < x) dx = -speed;

        if (player.y > y) dy = speed;
        else if (player.y < y) dy = -speed;

        // try horizontal movement
        if (dx != 0 && !collidesAt(x + dx, y)) {
            x += dx;
        }
        // try vertical movement
        if (dy != 0 && !collidesAt(x, y + dy)) {
            y += dy;
        }

        // attack player on contact
        if (isCollidingWith(player)) {
            player.health = Math.max(0, player.health - damage);
        }
    }

    private boolean collidesAt(int newX, int newY) {
        int left = newX / BlockManager.TILE_SIZE;
        int right = (newX + width - 1) / BlockManager.TILE_SIZE;
        int top = newY / BlockManager.TILE_SIZE;
        int bottom = (newY + height - 1) / BlockManager.TILE_SIZE;

        for (int col = left; col <= right; col++) {
            for (int row = top; row <= bottom; row++) {
                if (col < 0 || col >= gp.blockM.worldWidth || row < 0 || row >= gp.blockM.worldHeight) return true;
                if (Block.isSolid(gp.blockM.blocks[col][row])) return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, (int)(x - gp.camera.x), (int)(y - gp.camera.y), width * 2, height * 2, null);

        // health bar
        int barWidth = 24;
        int healthRatio = (int) ((float) health / maxHealth * barWidth);
        g2.setColor(Color.BLACK);
        g2.fillRect((int)(x - gp.camera.x), (int)(y - gp.camera.y) - 8, barWidth, 4);
        g2.setColor(Color.RED);
        g2.fillRect((int)(x - gp.camera.x), (int)(y - gp.camera.y) - 8, healthRatio, 4);
    }
}

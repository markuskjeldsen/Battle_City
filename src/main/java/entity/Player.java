package entity;

import controller.KeyHandler;
import controller.MouseHandler;
import tile.Block;
import tile.BlockManager;
import view.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;

public class Player extends Entity {
    GamePanel gp;
    KeyHandler keyH;
    MouseHandler mouseH;
    BlockManager bm;

    // survival stats
    public int health;
    public int maxHealth;
    public int hunger;
    public int maxHunger;
    public int selectedSlot; // 0-8 hotbar slot

    // inventory: holds block IDs
    public int[] inventory;
    public static final int INVENTORY_SIZE = 36; // 4x9
    public static final int HOTBAR_SIZE = 9;

    public Player(GamePanel gp, KeyHandler keyH, MouseHandler mouseH) {
        this.gp = gp;
        this.keyH = keyH;
        this.mouseH = mouseH;
        this.bm = gp.blockM;

        setDefaultValues();
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/textures/player.png"));
        } catch (Exception e) {
            // draw fallback rectangle
            image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
        }
    }

    private void setDefaultValues() {
        this.x = bm.worldWidth / 2 * BlockManager.TILE_SIZE - 16;
        this.y = bm.worldHeight / 2 * BlockManager.TILE_SIZE - 16;
        this.speed = 3;
        this.width = 16;
        this.height = 16;
        this.health = 20;
        this.maxHealth = 20;
        this.hunger = 20;
        this.maxHunger = 20;
        this.selectedSlot = 0;

        inventory = new int[INVENTORY_SIZE];
        // give some starting blocks
        inventory[0] = Block.BLOCK_GRASS;
        inventory[1] = Block.BLOCK_DIRT;
        inventory[2] = Block.BLOCK_STONE;
        inventory[3] = Block.BLOCK_WOOD;
        inventory[4] = Block.BLOCK_LEAVES;
        inventory[5] = Block.BLOCK_SAND;
        inventory[6] = Block.BLOCK_WATER;
        inventory[7] = Block.BLOCK_PLANKS;
        inventory[8] = Block.BLOCK_COBBLESTONE;
    }

    public void update() {
        move();
        decayHunger();
        checkBlockInteraction();
    }

    private void move() {
        int dx = 0, dy = 0;
        if (keyH.upPressed) dy = -speed;
        if (keyH.downPressed) dy = speed;
        if (keyH.leftPressed) dx = -speed;
        if (keyH.rightPressed) dx = speed;

        // normalize diagonal
        if (dx != 0 && dy != 0) {
            dx = dx * 3 / 4;
            dy = dy * 3 / 4;
        }

        // horizontal collision
        if (dx != 0) {
            int newX = x + dx;
            if (!collidesAt(newX, y)) {
                x = newX;
            }
        }

        // vertical collision
        if (dy != 0) {
            int newY = y + dy;
            if (!collidesAt(x, newY)) {
                y = newY;
            }
        }

        // world bounds
        x = Math.max(0, Math.min(x, bm.worldWidth * BlockManager.TILE_SIZE - width));
        y = Math.max(0, Math.min(y, bm.worldHeight * BlockManager.TILE_SIZE - height));
    }

    private boolean collidesAt(int newX, int newY) {
        int left = newX / BlockManager.TILE_SIZE;
        int right = (newX + width - 1) / BlockManager.TILE_SIZE;
        int top = newY / BlockManager.TILE_SIZE;
        int bottom = (newY + height - 1) / BlockManager.TILE_SIZE;

        for (int col = left; col <= right; col++) {
            for (int row = top; row <= bottom; row++) {
                if (col < 0 || col >= bm.worldWidth || row < 0 || row >= bm.worldHeight) return true;
                int blockId = bm.blocks[col][row];
                if (Block.isSolid(blockId)) return true;
            }
        }
        return false;
    }

    private void decayHunger() {
        // hunger decays every ~5 seconds (300 ticks at 60fps)
        if (gp.tick % 300 == 0) {
            hunger = Math.max(0, hunger - 1);
        }
        // starvation: lose health when hunger is 0
        if (hunger <= 0 && gp.tick % 60 == 0) {
            health = Math.max(0, health - 1);
        }
    }

    private void checkBlockInteraction() {
        if (mouseH.left) {
            // mine block at mouse position
            int tileX = mouseH.tileX;
            int tileY = mouseH.tileY;
            if (tileX >= 0 && tileX < bm.worldWidth && tileY >= 0 && tileY < bm.worldHeight) {
                int blockId = bm.blocks[tileX][tileY];
                if (blockId != Block.BLOCK_AIR) {
                    // add to inventory
                    addBlockToInventory(blockId);
                    bm.blocks[tileX][tileY] = Block.BLOCK_AIR;
                    mouseH.left = false; // one block per click
                }
            }
        }

        if (mouseH.right) {
            // place block at mouse position
            int tileX = mouseH.tileX;
            int tileY = mouseH.tileY;
            if (tileX >= 0 && tileX < bm.worldWidth && tileY >= 0 && tileY < bm.worldHeight) {
                if (bm.blocks[tileX][tileY] == Block.BLOCK_AIR) {
                    // check not placing inside player
                    int playerTileLeft = x / BlockManager.TILE_SIZE;
                    int playerTileRight = (x + width - 1) / BlockManager.TILE_SIZE;
                    int playerTileTop = y / BlockManager.TILE_SIZE;
                    int playerTileBottom = (y + height - 1) / BlockManager.TILE_SIZE;
                    boolean overlap = tileX >= playerTileLeft && tileX <= playerTileRight &&
                                      tileY >= playerTileTop && tileY <= playerTileBottom;
                    if (!overlap) {
                        int blockToPlace = inventory[selectedSlot];
                        if (blockToPlace != Block.BLOCK_AIR) {
                            bm.blocks[tileX][tileY] = blockToPlace;
                            mouseH.right = false; // one block per click
                        }
                    }
                }
            }
        }

        // number keys to select hotbar slot
        if (keyH.hotbarSlot >= 0 && keyH.hotbarSlot < HOTBAR_SIZE) {
            selectedSlot = keyH.hotbarSlot;
            keyH.hotbarSlot = -1;
        }
    }

    private void addBlockToInventory(int blockId) {
        // first try to stack into existing slots
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (inventory[i] == blockId) {
                return; // simplified: no stacking needed with our int[] approach
            }
        }
        // find empty slot
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (inventory[i] == Block.BLOCK_AIR) {
                inventory[i] = blockId;
                return;
            }
        }
    }

    public void draw(Graphics2D g2) {
        // draw player
        g2.drawImage(image, (int)(x - gp.camera.x), (int)(y - gp.camera.y), width * 2, height * 2, null);

        // draw selection highlight on block under cursor
        if (mouseH.tileX >= 0 && mouseH.tileX < bm.worldWidth &&
            mouseH.tileY >= 0 && mouseH.tileY < bm.worldHeight) {
            int screenX = mouseH.tileX * BlockManager.TILE_SIZE - (int)gp.camera.x;
            int screenY = mouseH.tileY * BlockManager.TILE_SIZE - (int)gp.camera.y;
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(screenX, screenY, BlockManager.TILE_SIZE, BlockManager.TILE_SIZE);
        }
    }

    // draw HUD on top of game
    public void drawHUD(Graphics2D g2) {
        int hudX = 10;
        int hudY = gp.screenHeight - 60;

        // health bar
        drawBar(g2, hudX, hudY, health, maxHealth, Color.RED, "❤");
        hudX += 140;

        // hunger bar
        drawBar(g2, hudX, hudY, hunger, maxHunger, new Color(200, 130, 0), "🍖");
        hudX += 140;

        // day/night indicator
        String timeStr = gp.timeOfDay < 0.5 ? "☀ Day" : "🌙 Night";
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(14f));
        g2.drawString(timeStr, hudX, hudY + 14);

        // hotbar
        drawHotbar(g2);
    }

    private void drawBar(Graphics2D g2, int x, int y, int value, int max, Color color, String icon) {
        g2.setColor(Color.BLACK);
        g2.fillRect(x, y, 120, 16);
        float ratio = (float) value / max;
        g2.setColor(color);
        g2.fillRect(x, y, (int) (120 * ratio), 16);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString(icon + " " + value + "/" + max, x + 4, y + 13);
    }

    private void drawHotbar(Graphics2D g2) {
        int hotbarY = gp.screenHeight - 36;
        int slotWidth = 36;
        int totalWidth = HOTBAR_SIZE * slotWidth;
        int hotbarX = (gp.screenWidth - totalWidth) / 2;

        // background
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(hotbarX - 4, hotbarY - 4, totalWidth + 8, 40);

        for (int i = 0; i < HOTBAR_SIZE; i++) {
            int slotX = hotbarX + i * slotWidth;
            int blockId = inventory[i];

            // slot background
            g2.setColor(i == selectedSlot ? new Color(100, 100, 100) : new Color(50, 50, 50));
            g2.fillRect(slotX, hotbarY, slotWidth, 32);

            // slot border
            g2.setColor(i == selectedSlot ? Color.YELLOW : Color.GRAY);
            g2.drawRect(slotX, hotbarY, slotWidth, 32);

            // block icon
            if (blockId != Block.BLOCK_AIR) {
                Color blockColor = Block.getBlockColor(blockId);
                g2.setColor(blockColor);
                g2.fillRect(slotX + 6, hotbarY + 6, 24, 20);
            }
        }

        // slot numbers
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(10f));
        for (int i = 0; i < HOTBAR_SIZE; i++) {
            int slotX = hotbarX + i * slotWidth;
            g2.drawString(String.valueOf(i + 1), slotX + 2, hotbarY + 12);
        }
    }
}

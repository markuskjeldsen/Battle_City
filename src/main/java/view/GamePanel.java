package view;

import controller.KeyHandler;
import controller.MouseHandler;
import entity.Mob;
import entity.Player;
import tile.Block;
import tile.BlockManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    // screen
    public final int screenWidth = 1024;
    public final int screenHeight = 700;
    final double FPS = 60;

    // world
    public BlockManager blockM;

    // entities
    KeyHandler keyH = new KeyHandler();
    MouseHandler mouseH;
    public Player player;
    List<Mob> mobs = new ArrayList<>();

    // camera
    public Camera camera;

    // game state
    Thread gameThread;
    public long tick = 0;
    public double timeOfDay = 0; // 0-1, 0=midnight, 0.5=noon

    public GamePanel() throws IOException {
        blockM = new BlockManager(this);
        mouseH = new MouseHandler(this);
        camera = new Camera(this);
        player = new Player(this, keyH, mouseH);

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
        addMouseListener(mouseH);
        addMouseWheelListener(mouseH);
    }

    public void resize(int wheelRotation) {
        camera.zoom += wheelRotation * 0.1;
        camera.zoom = Math.max(0.5f, Math.min(3.0f, camera.zoom));
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                long remaining = (long) (nextDrawTime - System.nanoTime()) / 1000000;
                if (remaining < 0) remaining = 0;
                Thread.sleep(remaining);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update() {
        tick++;

        // day/night cycle: full cycle = 60 seconds (3600 ticks)
        timeOfDay = (tick % 3600) / 3600.0;

        // update entities
        player.update();
        camera.update();

        // update mobs
        for (Mob mob : mobs) {
            mob.update();
        }

        // remove dead mobs
        mobs.removeIf(mob -> mob.health <= 0);

        // spawn mobs at night
        if (timeOfDay > 0.5 && timeOfDay < 0.95 && tick % 120 == 0) {
            spawnMob();
        }

        // despawn mobs during day
        if (timeOfDay > 0.0 && timeOfDay < 0.15) {
            if (tick % 60 == 0) {
                mobs.clear();
            }
        }

        // respawn player at center if dead and space pressed (simple respawn)
        if (player.health <= 0 && keyH.upPressed) {
            player.health = player.maxHealth;
            player.hunger = player.maxHunger;
            player.x = blockM.worldWidth / 2 * BlockManager.TILE_SIZE;
            player.y = blockM.worldHeight / 2 * BlockManager.TILE_SIZE;
        }
    }

    private void spawnMob() {
        // spawn away from player
        Random rand = new Random();
        int type = rand.nextInt(3); // 0=zombie, 1=skeleton, 2=creeper
        int angle = (int)(rand.nextDouble() * Math.PI * 2);
        int dist = 400 + rand.nextInt(200);
        int mx = (int) (player.x + Math.cos(angle) * dist);
        int my = (int) (player.y + Math.sin(angle) * dist);

        // clamp to world
        mx = Math.max(0, Math.min(mx, blockM.worldWidth * BlockManager.TILE_SIZE - 16));
        my = Math.max(0, Math.min(my, blockM.worldHeight * BlockManager.TILE_SIZE - 16));

        // find a ground tile
        int tileX = mx / BlockManager.TILE_SIZE;
        int tileY = my / BlockManager.TILE_SIZE;
        for (int y = tileY; y < blockM.worldHeight; y++) {
            if (blockM.blocks[tileX][y] != Block.BLOCK_AIR) {
                mobs.add(new Mob(this, type, tileX * BlockManager.TILE_SIZE, (y - 1) * BlockManager.TILE_SIZE));
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // sky color based on time of day
        drawSky(g2);

        // draw blocks
        blockM.draw(g2, (int) camera.x, (int) camera.y);

        // draw mobs
        for (Mob mob : mobs) {
            mob.draw(g2);
        }

        // draw player
        player.draw(g2);

        // draw HUD
        player.drawHUD(g2);

        // death screen
        if (player.health <= 0) {
            drawDeathScreen(g2);
        }

        g2.dispose();
    }

    private void drawSky(Graphics2D g2) {
        // interpolate sky color
        double dayFactor;
        if (timeOfDay < 0.25) { // dawn
            dayFactor = timeOfDay / 0.25;
        } else if (timeOfDay < 0.75) { // day
            dayFactor = 1.0;
        } else { // dusk
            dayFactor = 1.0 - (timeOfDay - 0.75) / 0.25;
        }

        Color dayColor = new Color(135, 206, 235); // sky blue
        Color nightColor = new Color(10, 10, 40);  // dark blue
        int r = (int) (nightColor.getRed() + (dayColor.getRed() - nightColor.getRed()) * dayFactor);
        int g = (int) (nightColor.getGreen() + (dayColor.getGreen() - nightColor.getGreen()) * dayFactor);
        int b = (int) (nightColor.getBlue() + (dayColor.getBlue() - nightColor.getBlue()) * dayFactor);
        g2.setColor(new Color(r, g, b));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // stars at night
        if (dayFactor < 0.5) {
            g2.setColor(Color.WHITE);
            Random starRand = new Random(42); // fixed seed for consistent stars
            for (int i = 0; i < 50; i++) {
                int sx = starRand.nextInt(screenWidth);
                int sy = starRand.nextInt(screenHeight / 2);
                int size = starRand.nextInt(2) + 1;
                g2.fillRect(sx, sy, size, size);
            }
        }
    }

    private void drawDeathScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(Color.RED);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));
        FontMetrics fm = g2.getFontMetrics();
        String deadText = "YOU DIED";
        g2.drawString(deadText, (screenWidth - fm.stringWidth(deadText)) / 2, screenHeight / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(18f));
        String pressText = "Press W to respawn";
        fm = g2.getFontMetrics();
        g2.drawString(pressText, (screenWidth - fm.stringWidth(pressText)) / 2, screenHeight / 2 + 30);
    }
}

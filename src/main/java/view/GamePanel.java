package view;

import controller.KeyHandler;
import controller.MouseHandler;
import entity.Building;
import entity.Camera;
import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    //screen settings
    final int originalTileSize = 32; //64x64 tiles
    final int scale = 2;

    public final int tileSize = originalTileSize * scale; //64x64 tile to be displayed
    public final int maxScreenCol;
    public final int maxScreenRow;
    final int screenWidth;
    final int screenHeight;
    final double FPS = 60;

    private ArrayList<Building> buildings;
    TileManager tileM;


    KeyHandler keyH = new KeyHandler();
    MouseHandler mouseH = new MouseHandler();
    Thread gameThread;
    Player player;
    Camera camera;


    public GamePanel() throws IOException {



        maxScreenCol = 16;
        maxScreenRow = 12;
        screenWidth = tileSize * maxScreenCol;
        screenHeight = tileSize * maxScreenRow;

        tileM = new TileManager(this);
        camera = new Camera(this, tileM, keyH, mouseH);
        player = new Player(this, keyH, mouseH);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(mouseH);
        this.addMouseWheelListener(mouseH);


        buildings = new ArrayList<>();


    }

    public Camera getCamera() {
        return camera;
    }

    public void resize(int zoom) {


    }

    public void constructBuilding() {

        if (mouseH.left) { //make houses
            boolean canPlace = true;

            for (Building building : buildings) {
                canPlace &= (((mouseH.point.x / tileSize * tileSize) != building.x) || (mouseH.point.y / tileSize * tileSize) != building.y);
            }

            if (canPlace) {
                Building newBuilding = new Building(this, keyH, mouseH, mouseH.point);
                buildings.add(newBuilding);

            }
        }

    }

    public void starGameThread() {
        gameThread = new Thread(this);
        gameThread.start();

    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        double remainingTime;


        while (gameThread != null) {


            // TODO update information about different things
            update();
            // TODO Draw the screen
            repaint();


            try {

                remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;


                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);


                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }


    }

    public void update() {
        camera.update();
        player.update();
        constructBuilding();

        for (Building building : buildings) {
            building.update();
        }


    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);


        for (Building building : buildings) {
            building.draw(g2);
        }


        player.draw(g2);


        g2.dispose();
    }
}

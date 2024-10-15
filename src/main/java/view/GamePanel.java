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
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements Runnable {

    //screen settings
    final float originalTileSize = 16f; //64x64 tiles
    float scale = 1.5f;

    public int tileSize = (int) (originalTileSize * scale); //64x64 tile to be displayed
    public int maxScreenCol;
    public int maxScreenRow;
    final int screenWidth;
    final int screenHeight;
    final double FPS = 60;

    private CopyOnWriteArrayList<Building> buildings;
    TileManager tileM;


    KeyHandler keyH = new KeyHandler();
    MouseHandler mouseH;
    Thread gameThread;
    Player player;
    Camera camera;


    public GamePanel() throws IOException {

        screenWidth = 1024;
        screenHeight = 700;
        maxScreenCol = screenWidth / tileSize;
        maxScreenRow = screenHeight / tileSize;

        tileM = new TileManager(this);
        mouseH = new MouseHandler(this);
        camera = new Camera(this, tileM, keyH, mouseH);
        player = new Player(this, keyH, mouseH);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(mouseH);
        this.addMouseWheelListener(mouseH);

        buildings = new CopyOnWriteArrayList<>();
    }

    public Camera getCamera() {
        return camera;
    }

    public void resize(int zoom) {
        this.scale += zoom / 10f;
        scale = Math.max(1, scale);
        scale = Math.min(5, scale);
        System.out.println(this.scale);
        tileSize = (int) (originalTileSize * scale);
        maxScreenCol = screenWidth / tileSize;
        maxScreenRow = screenHeight / tileSize;

    }

    public void constructBuilding() {

        if (mouseH.left) { //make houses
            boolean canPlace = true;

            for (Building building : buildings) {
                canPlace &= (((mouseH.point.x / tileSize * tileSize) != building.x) || (mouseH.point.y / tileSize * tileSize) != building.y);
            }

            if (canPlace) {
                int buildingX = this.camera.x + (mouseH.point.x / tileSize);
                int buildingY = this.camera.y + (mouseH.point.y / tileSize);
                buildingX = Math.max(0, buildingX);
                buildingX = Math.min(tileM.getMapDimensions().width() - 1, buildingX);
                buildingY = Math.max(0, buildingY);
                buildingY = Math.min(tileM.getMapDimensions().height() - 1, buildingY);
                System.out.println(buildingX + " " + buildingY);
                buildings.add(new Building(mouseH, this, buildingX, buildingY));
//                Building newBuilding = new Building(this, keyH, mouseH, mouseH.point)
//                buildings.add(newBuilding);

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
        buildings.removeIf(building -> building.isScheduledForDestruction);


    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);
        for (Building building : buildings) {
            building.draw(g2, tileSize, camera);
        }
        player.draw(g2);

        g2.dispose();
    }
}

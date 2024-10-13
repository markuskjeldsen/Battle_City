package tile;


import view.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManager {

    private final GamePanel gp;
    private final Tile[] tiles;
    private final int[][] mapTileNum;
    private final MapDimensions mapDimensions;

    public TileManager(GamePanel gp) throws IOException {
        this.gp = gp;
        tiles = loadTileImages();
        String filepath = "/map/map01.txt";
        mapDimensions = getMapDimensions(filepath);
        mapTileNum = loadMap(filepath);
    }

    public MapDimensions getMapDimensions() {
        return mapDimensions;
    }

    private Tile[] loadTileImages() throws IOException {
        String[] strings = new String[]{
                "/tiles/grass00.png",
                "/tiles/dirt00.png",
                "/tiles/wall00.png",
                "/tiles/water00.png"
        };
        Tile[] tiles = new Tile[strings.length];
        for (int i = 0; i < strings.length; i++) {
            tiles[i] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(strings[i]))));
        }
        return tiles;
    }

    MapDimensions getMapDimensions(String filepath) throws IOException {
        try (BufferedReader reader = getBr(filepath)) {
            String line;
            int width = 0;
            int height = 0;

            while ((line = reader.readLine()) != null) {
                if (width == 0) {
                    width = line.trim().split("\\s+").length;
                }
                height++;
            }
            return new MapDimensions(width, height);
        } catch (IOException e) {
            throw e;
        }
    }

    public record MapDimensions(int width, int height) {
    }

    public int[][] loadMap(String filepath) throws IOException {
        int[][] mapTileNum = new int[mapDimensions.width][mapDimensions.height];
        try (BufferedReader br = getBr(filepath)) {
            String line;
            int rowIndex = 0;
            while ((line = br.readLine()) != null && rowIndex < mapDimensions.height) {
                processMapLine(mapTileNum, line, rowIndex, mapDimensions.width);
                rowIndex++;
            }
        }
        return mapTileNum;
    }

    private void processMapLine(int[][] mapTileNum, String line, int rowIndex, int width) {
        String[] numbers = line.split(" ");
        for (int column = 0; column < width; column++) {
            mapTileNum[column][rowIndex] = Integer.parseInt(numbers[column]);
        }
    }

    private BufferedReader getBr(String filepath) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(filepath))));
    }

    public void draw(Graphics2D g2) {

//        g2.drawImage(tile[0].image, 0,0, gp.tileSize, gp.tileSize, null);

        int startCol = gp.getCamera().x ;
        int startRow = gp.getCamera().y ;

        int offsetX = gp.getCamera().x % gp.tileSize;
        int offsetY = gp.getCamera().y % gp.tileSize;

        int x = -offsetX;
        int y = -offsetY;

        for (int row = startRow; row < startRow + gp.maxScreenRow + 1; row++) {
            for (int col = startCol; col < startCol + gp.maxScreenCol + 1; col++) {
                if (row >= 0 && row < mapTileNum[0].length && col >= 0 && col < mapTileNum.length) {
                    int tileNum = mapTileNum[col][row];
                    g2.drawImage(tiles[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
                }
                x += gp.tileSize;
            }
            x = -offsetX;
            y += gp.tileSize;
        }


    }
}

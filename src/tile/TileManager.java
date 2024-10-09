package tile;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    GamePanel gp;
    Tile[] tile;
    int[][] mapTileNum;



    public TileManager(GamePanel gp) {

        this.gp = gp;
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];

        tile = new Tile[10];
        getTileImage();
        loadMap();

    }
    public void getTileImage(){

        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/grass00.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/dirt00.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/wall00.png"));

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/tiles/water00.png"));




        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public void loadMap() {

        try {
            InputStream is = getClass().getResourceAsStream("/map/map00.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));


            int col = 0;
            int row = 0;

            while(col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();

                while(col < gp.maxScreenCol) {

                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxScreenCol){
                    row++;
                    col = 0;
                }
            }
            br.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }





    }
    public void draw(Graphics2D g2){

//        g2.drawImage(tile[0].image, 0,0, gp.tileSize, gp.tileSize, null);


        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while ( (col < gp.maxScreenCol)  && (row < gp.maxScreenRow)  ){
            int tileNum = mapTileNum[col][row];


            g2.drawImage(tile[tileNum].image, x,y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;

            if(col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }




        }









    }
}

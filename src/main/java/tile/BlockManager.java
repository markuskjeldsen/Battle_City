package tile;

import view.GamePanel;

import java.awt.*;
import java.util.Random;

public class BlockManager {
    public static final int TILE_SIZE = 64;
    public final int worldWidth;
    public final int worldHeight;
    public int[][] blocks;
    private final GamePanel gp;
    private final Random rand = new Random();

    public BlockManager(GamePanel gp) {
        this.gp = gp;
        this.worldWidth = 200;  // 200 tiles wide
        this.worldHeight = 100; // 100 tiles tall
        this.blocks = new int[worldWidth][worldHeight];
        generateWorld();
    }

    public void generateWorld() {
        // generate terrain using simple noise-like algorithm
        for (int x = 0; x < worldWidth; x++) {
            // surface height with some variation
            double surfaceHeight = worldHeight / 3.0 +
                Math.sin(x * 0.05) * 5 +
                Math.sin(x * 0.02) * 10 +
                Math.sin(x * 0.1) * 2;
            int surfaceY = (int) surfaceHeight;

            for (int y = 0; y < worldHeight; y++) {
                if (y < surfaceY - 4) {
                    // sky
                    blocks[x][y] = Block.BLOCK_AIR;
                } else if (y == surfaceY - 4) {
                    // surface level - grass
                    blocks[x][y] = Block.BLOCK_GRASS;
                } else if (y < surfaceY) {
                    // dirt layer
                    blocks[x][y] = Block.BLOCK_DIRT;
                } else if (y < surfaceY + 10) {
                    // stone layer with some dirt
                    blocks[x][y] = rand.nextInt(5) == 0 ? Block.BLOCK_DIRT : Block.BLOCK_STONE;
                } else {
                    // deep stone with ores
                    int r = rand.nextInt(100);
                    if (r < 1) blocks[x][y] = Block.BLOCK_ORE_DIAMOND;
                    else if (r < 3) blocks[x][y] = Block.BLOCK_ORE_GOLD;
                    else if (r < 8) blocks[x][y] = Block.BLOCK_ORE_IRON;
                    else if (r < 15) blocks[x][y] = Block.BLOCK_ORE_COAL;
                    else blocks[x][y] = Block.BLOCK_STONE;
                }
            }

            // sand near water level (low areas)
            if (surfaceY > worldHeight / 2) {
                for (int y = Math.max(0, surfaceY - 4); y <= surfaceY; y++) {
                    blocks[x][y] = Block.BLOCK_SAND;
                }
            }
        }

        // generate some caves
        generateCaves();

        // generate trees
        generateTrees();

        // generate some structures (simple houses)
        generateStructures();
    }

    private void generateCaves() {
        int caveCount = worldWidth * worldHeight / 500;
        for (int i = 0; i < caveCount; i++) {
            int cx = rand.nextInt(worldWidth);
            int cy = rand.nextInt(worldHeight / 3, worldHeight * 2 / 3);
            int radius = rand.nextInt(3) + 1;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (dx * dx + dy * dy <= radius * radius) {
                        int nx = cx + dx;
                        int ny = cy + dy;
                        if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight) {
                            if (blocks[nx][ny] != Block.BLOCK_WATER) {
                                blocks[nx][ny] = Block.BLOCK_AIR;
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateTrees() {
        for (int x = 2; x < worldWidth - 2; x++) {
            if (rand.nextInt(30) == 0 && blocks[x][0] == Block.BLOCK_GRASS) {
                // find surface
                int surfaceY = 0;
                for (int y = 0; y < worldHeight; y++) {
                    if (blocks[x][y] == Block.BLOCK_GRASS) {
                        surfaceY = y - 1;
                        break;
                    }
                }

                if (surfaceY > 0) {
                    // trunk
                    int trunkHeight = rand.nextInt(3) + 3;
                    for (int ty = 0; ty < trunkHeight; ty++) {
                        if (surfaceY - ty >= 0) {
                            blocks[x][surfaceY - ty] = Block.BLOCK_WOOD;
                        }
                    }
                    // leaves
                    int topY = surfaceY - trunkHeight;
                    for (int lx = -2; lx <= 2; lx++) {
                        for (int ly = -2; ly <= 1; ly++) {
                            int nx = x + lx;
                            int ny = topY + ly;
                            if (nx >= 0 && nx < worldWidth && ny >= 0 && ny < worldHeight) {
                                if (blocks[nx][ny] == Block.BLOCK_AIR) {
                                    blocks[nx][ny] = Block.BLOCK_LEAVES;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateStructures() {
        for (int i = 0; i < 10; i++) {
            int sx = rand.nextInt(worldWidth - 10) + 5;
            // find surface
            int sy = 0;
            for (int y = 0; y < worldHeight; y++) {
                if (blocks[sx][y] == Block.BLOCK_GRASS) {
                    sy = y - 1;
                    break;
                }
            }
            if (sy > 0) {
                buildHouse(sx, sy);
            }
        }
    }

    private void buildHouse(int startX, int startY) {
        int w = 5;
        int h = 4;

        for (int x = startX - w; x <= startX + w; x++) {
            for (int y = startY - h; y <= startY; y++) {
                if (x >= 0 && x < worldWidth && y >= 0 && y < worldHeight) {
                    if (x == startX - w || x == startX + w || y == startY) {
                        blocks[x][y] = Block.BLOCK_BRICK;
                    } else if (y == startY - h) {
                        blocks[x][y] = Block.BLOCK_BRICK; // roof
                    } else if (y == startY - 1 && x == startX) {
                        blocks[x][y] = Block.BLOCK_AIR; // door
                    } else {
                        blocks[x][y] = Block.BLOCK_AIR;
                    }
                }
            }
        }

        // roof on top
        for (int x = startX - w - 1; x <= startX + w + 1; x++) {
            int y = startY - h - 1;
            if (x >= 0 && x < worldWidth && y >= 0 && y < worldHeight) {
                blocks[x][y] = Block.BLOCK_BRICK;
            }
        }
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int startCol = Math.max(0, camX / TILE_SIZE - 1);
        int endCol = Math.min(worldWidth - 1, (camX + gp.screenWidth) / TILE_SIZE + 1);
        int startRow = Math.max(0, camY / TILE_SIZE - 1);
        int endRow = Math.min(worldHeight - 1, (camY + gp.screenHeight) / TILE_SIZE + 1);

        for (int x = startCol; x <= endCol; x++) {
            for (int y = startRow; y <= endRow; y++) {
                int blockId = blocks[x][y];
                if (blockId == Block.BLOCK_AIR) continue;

                int screenX = x * TILE_SIZE - camX;
                int screenY = y * TILE_SIZE - camY;

                Color color = Block.getBlockColor(blockId);
                g2.setColor(color);
                g2.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

                // add some texture detail
                if (!Block.isTransparent(blockId)) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.drawRect(screenX, screenY, TILE_SIZE - 1, TILE_SIZE - 1);
                }

                // ore sparkle
                if (blockId >= Block.BLOCK_ORE_COAL && blockId <= Block.BLOCK_ORE_DIAMOND) {
                    g2.setColor(Color.WHITE);
                    int sparkleX = (x * 7 + y * 13) % 4;
                    int sparkleY = (x * 11 + y * 3) % 4;
                    g2.fillRect(screenX + 10 + sparkleX * 12, screenY + 10 + sparkleY * 12, 4, 4);
                }
            }
        }
    }
}

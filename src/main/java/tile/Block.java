package tile;

import java.awt.*;

public class Block {
    public static final int BLOCK_AIR = 0;
    public static final int BLOCK_GRASS = 1;
    public static final int BLOCK_DIRT = 2;
    public static final int BLOCK_STONE = 3;
    public static final int BLOCK_WOOD = 4;
    public static final int BLOCK_LEAVES = 5;
    public static final int BLOCK_SAND = 6;
    public static final int BLOCK_WATER = 7;
    public static final int BLOCK_PLANKS = 8;
    public static final int BLOCK_COBBLESTONE = 9;
    public static final int BLOCK_ORE_COAL = 10;
    public static final int BLOCK_ORE_IRON = 11;
    public static final int BLOCK_ORE_GOLD = 12;
    public static final int BLOCK_ORE_DIAMOND = 13;
    public static final int BLOCK_BRICK = 14;
    public static final int BLOCK_GLASS = 15;

    public static final int[] SOLID_BLOCKS = {
        BLOCK_DIRT, BLOCK_STONE, BLOCK_WOOD, BLOCK_LEAVES,
        BLOCK_SAND, BLOCK_PLANKS, BLOCK_COBBLESTONE,
        BLOCK_ORE_COAL, BLOCK_ORE_IRON, BLOCK_ORE_GOLD,
        BLOCK_ORE_DIAMOND, BLOCK_BRICK, BLOCK_GLASS
    };

    public static boolean isSolid(int blockId) {
        for (int solid : SOLID_BLOCKS) {
            if (blockId == solid) return true;
        }
        return false;
    }

    public static boolean isTransparent(int blockId) {
        return blockId == BLOCK_WATER || blockId == BLOCK_GLASS || blockId == BLOCK_LEAVES;
    }

    public static Color getBlockColor(int blockId) {
        return switch (blockId) {
            case BLOCK_GRASS -> new Color(34, 139, 34);
            case BLOCK_DIRT -> new Color(139, 115, 85);
            case BLOCK_STONE -> new Color(128, 128, 128);
            case BLOCK_WOOD -> new Color(139, 69, 19);
            case BLOCK_LEAVES -> new Color(34, 150, 34);
            case BLOCK_SAND -> new Color(238, 214, 175);
            case BLOCK_WATER -> new Color(30, 144, 255, 180);
            case BLOCK_PLANKS -> new Color(210, 180, 140);
            case BLOCK_COBBLESTONE -> new Color(105, 105, 105);
            case BLOCK_ORE_COAL -> new Color(50, 50, 50);
            case BLOCK_ORE_IRON -> new Color(205, 175, 160);
            case BLOCK_ORE_GOLD -> new Color(255, 215, 0);
            case BLOCK_ORE_DIAMOND -> new Color(0, 200, 200);
            case BLOCK_BRICK -> new Color(178, 34, 34);
            case BLOCK_GLASS -> new Color(200, 230, 255, 128);
            default -> Color.BLACK;
        };
    }

    public static String getBlockName(int blockId) {
        return switch (blockId) {
            case BLOCK_AIR -> "Air";
            case BLOCK_GRASS -> "Grass";
            case BLOCK_DIRT -> "Dirt";
            case BLOCK_STONE -> "Stone";
            case BLOCK_WOOD -> "Wood";
            case BLOCK_LEAVES -> "Leaves";
            case BLOCK_SAND -> "Sand";
            case BLOCK_WATER -> "Water";
            case BLOCK_PLANKS -> "Planks";
            case BLOCK_COBBLESTONE -> "Cobblestone";
            case BLOCK_ORE_COAL -> "Coal Ore";
            case BLOCK_ORE_IRON -> "Iron Ore";
            case BLOCK_ORE_GOLD -> "Gold Ore";
            case BLOCK_ORE_DIAMOND -> "Diamond Ore";
            case BLOCK_BRICK -> "Brick";
            case BLOCK_GLASS -> "Glass";
            default -> "Unknown";
        };
    }
}

package controller;

import tile.BlockManager;
import view.GamePanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseWheelListener {
    private final GamePanel gp;
    public boolean left, right;
    public Point point;
    public int tileX, tileY; // world tile coordinates

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case 1 -> left = true;
            case 2 -> {} // wheel zoom handled separately
            case 3 -> right = true;
        }
        point = e.getPoint();
        updateTileCoords();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
            case 1 -> left = false;
            case 2 -> {}
            case 3 -> right = false;
        }
        point = e.getPoint();
        updateTileCoords();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        left = false;
        right = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        gp.resize(e.getWheelRotation());
    }

    private void updateTileCoords() {
        if (point != null) {
            tileX = (point.x + (int)gp.camera.x) / BlockManager.TILE_SIZE;
            tileY = (point.y + (int)gp.camera.y) / BlockManager.TILE_SIZE;
        }
    }
}

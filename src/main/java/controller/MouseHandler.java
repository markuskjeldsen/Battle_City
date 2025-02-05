package controller;

import view.GamePanel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseWheelListener {

    private final GamePanel gp;
    public boolean left, right, middle;
    public Point point;

    public MouseHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case 1: //left mouse
                left = true;
                break;
            case 2: //mouse wheel
                middle = true;
                break;
            case 3: //right mouse
                right = true;
                break;
            default:
                System.out.print("you pressed a key that has not been mapped ");
                System.out.println(mouseEvent.getButton());

        }
        point = mouseEvent.getPoint();

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case 1: //left mouse
                left = false;
                break;
            case 2: //mouse wheel
                middle = false;
                break;
            case 3: //right mouse
                right = false;
                break;
            default:
                System.out.print("you released a key that has not been mapped ");
                System.out.println(mouseEvent.getButton());

        }
        point = mouseEvent.getPoint();

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        int wheelRotation = mouseWheelEvent.getWheelRotation();
        gp.resize(wheelRotation);
    }
}

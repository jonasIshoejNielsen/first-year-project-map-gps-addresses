package controller;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

public class MouseController extends MouseAdapter {
    private ViewController viewController;
    private Point2D lastMousePosition;

    public MouseController(ViewController viewController) {
        this.viewController = viewController;
        viewController.addMouseListeners(this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        viewController.updateCoordinate(e.getPoint());
        viewController.updateCoordinateLabel();
        viewController.updateNearestRoad();

    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        viewController.zoomCanvasView(-e.getWheelRotation(), -e.getX(), -e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D currentMousePosition = e.getPoint();
        if(SwingUtilities.isLeftMouseButton(e)){
            viewController.leftMouseDragged(currentMousePosition);
        }
    }
    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition = e.getPoint();
        if(SwingUtilities.isLeftMouseButton(e)) {
            viewController.leftMouseClick();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        viewController.setAntiAliasing(true);
        if(SwingUtilities.isLeftMouseButton(e)) {
            viewController.leftMouseReleased();
        }
    }

}

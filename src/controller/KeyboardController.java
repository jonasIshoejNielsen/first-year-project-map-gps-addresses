package controller;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardController extends KeyAdapter{
    private ViewController vc;

    public KeyboardController(JFrame frame, ViewController vc) {
        frame.addKeyListener(this);
        //vc.canvasView.addKeyListener(this);
        //vc.interactionView.addKeyListener(this);
        this.vc = vc;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'x':
                vc.toggleAntiAliasingCanvasView();
                break;
            case 'w':
                vc.panCanvasView(0, 10);
                break;
            case 'a':
                vc.panCanvasView(10, 0);
                break;
            case 's':
                vc.panCanvasView(0, -10);
                break;
            case 'd':
                vc.panCanvasView(-10, 0);
                break;
            case '+':
                vc.zoomToCenterCanvasView(1);
                break;
            case '-':
                vc.zoomToCenterCanvasView(-1);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        vc.setAntiAliasing(true);
        vc.repaintCanvas();
    }
}

package view;

import view.components.FrameLoader;

import javax.swing.*;

public class LayeredView extends JLayeredPane {
    private FrameLoader frameLoader;
    private InteractionView interactionView;
    private CanvasView canvasView;

    public LayeredView(FrameLoader frameLoader, InteractionView interactionView, CanvasView canvasView) {
        this.interactionView    = interactionView;
        this.canvasView         = canvasView;
        this.frameLoader        = frameLoader;
        add(frameLoader, new Integer(3));
        add(interactionView, new Integer(2));
        add(canvasView, new Integer(1));
    }

    public void update(int h, int w) {
        interactionView.setBounds(10, 10, w-45, h-90);
        canvasView.setBounds(0, 0, w, h);
        frameLoader.setBounds(0, 0, w, h);
    }

    public void updateFrameLoaderText(String s) {
        this.frameLoader.updateText(s);
    }

    public void hideFrameLoader() {
        this.frameLoader.hideLoader();
    }

    public void showFrameLoader() {
        this.frameLoader.showLoader();
    }
}

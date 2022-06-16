package view.components;

import controller.InteractionController;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ZoomSlider extends JSlider {
    private InteractionController interactionController;
    private Color color;

    public ZoomSlider(InteractionController interactionController) {
        this.interactionController = interactionController;
        this.color = new Color(10,10,10,150);

        setOrientation(JSlider.HORIZONTAL);
        setMajorTickSpacing(1);
        setInverted(false);
        //setPaintTicks(true);
        //setPaintLabels(true);
        setPaintTrack(true);
        setOpaque(false);
        setVisible(false);
        setFocusable(false);
        setPreferredSize(new Dimension(100,30));
        //setBorder(new LineBorder(color, 2, true));
        setSnapToTicks(true);
        setFont(new Font("Arial", Font.BOLD, 12));
        setForeground(Color.black);
        setBackground(color);

        addChangeListener(e -> zoomAction());
    }

    private void zoomAction() {
        int change = getValue() - interactionController.getCurrentZoomlevel();
        interactionController.zoom(-change);
    }

    public void update() {
        try {
            int max = interactionController.getMaxZoomlevel();
            if(max == getMaximum() || max == 0) return;
            setMaximum(max);
            updateValue();
        } catch (Exception e) {
            //Do nothing, only on unfinished loading
        } finally {
            setVisible(true);
        }
    }

    public void updateValue() {
        setValue(interactionController.getCurrentZoomlevel());
    }


}

package view.components;

import javax.swing.*;
import java.awt.*;

public class PanelGroup extends JPanel {

    public PanelGroup(Dimension dimension, Color background, boolean isOpaque, JComponent[] components) {
        if(dimension != null) setPreferredSize(dimension);
        if(background != null) setBackground(background);
        setOpaque(isOpaque);
        if(components != null) addComponents(components);
    }

    private void addComponents(JComponent[] components) {
        for(JComponent component : components) {
            add(component);
        }
    }
}

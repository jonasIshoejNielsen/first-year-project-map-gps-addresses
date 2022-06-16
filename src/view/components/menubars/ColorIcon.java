package view.components.menubars;

import javax.swing.*;
import java.awt.*;

/**
 * This class is a simple version of the ColorIcon class from:
 * http://www.java2s.com/Code/Java/2D-Graphics-GUI/ColorIcon.htm
 */
public class ColorIcon implements Icon {
    private int width;
    private int height;

    private Color color;

    public ColorIcon (Color color) {
        width = 16;
        height = 16;
        this.color = color;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(this.color);
        g.fillRect(x, y, width, height);
    }
}
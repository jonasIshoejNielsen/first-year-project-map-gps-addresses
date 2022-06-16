package view.components;

import javax.swing.*;
import java.awt.*;

public class MenuButton extends JButton {

    public MenuButton(String iconPath, int width, int height, String tooltip) {
        super();
        setIcon(new ImageIcon(getClass().getResource("/icons/" + iconPath)));
        setPreferredSize(new Dimension(width, height));
        setToolTipText("<html><body><h3>" + tooltip + "<h3>");
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusable(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}

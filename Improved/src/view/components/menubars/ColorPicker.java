package view.components.menubars;

import controller.ViewController;
import model.osm.OSMType;
import model.osm.OSMTypeParent;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.util.ArrayList;

public class ColorPicker extends JMenu {
    private JFrame frame;
    private ViewController viewController;
    private boolean showEnglishWarning = false;
    private final int MAX_MENU_SIZE = 25;

    public ColorPicker(JFrame frame, ViewController viewController) {
        super("Farvevælger");
        this.frame = frame;
        this.viewController = viewController;
        this.addMenuListener(new ColorPickerMenuListener());
        createMenu();
    }

    private void createMenu() {
        for (OSMTypeParent parent : OSMTypeParent.values()) {
            //Removal of parents with no drawn children
            if (parent == OSMTypeParent.AMENITY || parent == OSMTypeParent.CRAFT) continue;

            JMenu menu = new JMenu(parent.toString());
            add(menu);
            addChildren(parent, menu);
        }
    }

    private void addChildren(OSMTypeParent parent, JMenu menu) {
        int menuCount = 2;
        for(OSMType child : parent.getChildren()) {
            //Removal of children that are not drawn on the map
            if(!child.isLine() && !child.isArea()) continue;

            JMenuItem item = new JMenuItem(child.toString());
            item.setIcon(new ColorIcon(child.getColor()));
            menu.add(item);
            item.addActionListener( e -> updateColor(child, item));

            if(menu.getItemCount() % MAX_MENU_SIZE == 0) {
                menu = new JMenu(parent.toString() + " " + menuCount);
                add(menu);
            }
        }
    }

    private void updateColor(OSMType type, JMenuItem menuItem) {
        Color color = JColorChooser.showDialog(frame, "Farvevælger: " + type.toString(), type.getColor());
        if (color == null) return;
        type.changeColor(color);
        menuItem.setIcon(new ColorIcon(type.getColor()));
        viewController.repaintCanvas();
    }

    private class ColorPickerMenuListener implements MenuListener {
        @Override
        public void menuSelected(MenuEvent e) {
            if(showEnglishWarning) return;
            String message = "Da dette program er udviklet ved brug af Open Street Maps, så er alle komponenter " +
                    "i denne menu beskrevet på engelsk." + "\n" + "Vi undskylder ulejligheden.";
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/general/logo.png"));
            JOptionPane.showMessageDialog(frame, message, "Advarsel", JOptionPane.WARNING_MESSAGE, icon);
            showEnglishWarning = true;
        }

        @Override
        public void menuDeselected(MenuEvent e) {
            //Do nothing
        }

        @Override
        public void menuCanceled(MenuEvent e) {
            //Do nothing
        }
    }
}

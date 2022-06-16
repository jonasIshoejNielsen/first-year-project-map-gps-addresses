package view.components;

import controller.InteractionController;
import model.osm.OSMType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class FilterItem extends JComponent {
    private InteractionController interactionController;

    private boolean  isVisible;
    private ArrayList<OSMType> types;
    private JPanel   wrapper;
    private JLabel buttonText;
    private JButton  button;
    private Icon     icon;

    public FilterItem(String text, boolean isVisible, String iconPath, ArrayList<OSMType> types, InteractionController interactionController) {
        this.isVisible   = isVisible;
        this.types       = types;
        this.interactionController = interactionController;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 100));
        setBorder(new EmptyBorder(10, 15, 20, 15));

        wrapper     = new JPanel();

        icon        = new ImageIcon(getClass().getResource("/icons/filterIcons/" + iconPath));

        buttonText  = new JLabel(text, SwingConstants.CENTER);
        button      = filterItemButton(isVisible);

        button.setPreferredSize(new Dimension(48, 48));
        button.setBorderPainted(false);

        wrapper.setLayout(new BorderLayout());
        wrapper.add(button, BorderLayout.NORTH);
        wrapper.add(buttonText, BorderLayout.SOUTH);

        wrapper.setBackground(Color.red);
        wrapper.setOpaque(false);

        this.add(wrapper);

        button.addActionListener(e -> toggle());
    }

    private JButton filterItemButton(boolean isVisible){
        JButton jButton = new JButton(icon);
        jButton.setPreferredSize(new Dimension(48, 48));
        jButton.setBorderPainted(false);
        jButton.setBackground((isVisible) ? Color.green : Color.red);
        jButton.setToolTipText((isVisible) ? "<html><body>" + "<h3>Tryk for at skjule <h3>" : "<html><body>" + "<h3>Tryk for at vise<h3>");
        return jButton;
    }

    private void toggle() {
        this.isVisible = !this.isVisible;
        for(OSMType type: types) interactionController.toggleFilter(type);
        if(isVisible) {
            this.button.setBackground(Color.green);
            String html = "<html><body>" + "<h3>Tryk for at skjule <h3>";
            button.setToolTipText(html);
        }
        else {
            this.button.setBackground(Color.red);
            String html = "<html><body>" + "<h3>Tryk for at vise<h3>";
            button.setToolTipText(html);
        }
    }
}

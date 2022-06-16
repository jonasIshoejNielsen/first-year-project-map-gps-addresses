package view.components;

import model.helpers.routeGraph.MathConstants;
import model.osm.OSMAddress;
import view.InteractionView;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SelectedSearchPanel extends JComponent {
    private JLabel street;
    private JLabel cityAndZip;
    private JLabel lonLat;

    public SelectedSearchPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setPreferredSize(new Dimension(450, 70));

        JPanel rowPanel     = new JPanel(new GridLayout(2,1));
        rowPanel.setOpaque(false);
        street       = new JLabel();
        cityAndZip   = new JLabel();

        street.setFont(new Font("Arial", Font.BOLD, 18));
        cityAndZip.setFont(new Font("Arial", Font.PLAIN, 18));

        street.setForeground(Color.white);
        cityAndZip.setForeground(Color.white);

        rowPanel.add(street);
        rowPanel.add(cityAndZip);

        lonLat  = new JLabel();
        lonLat.setOpaque(false);
        lonLat.setForeground(Color.white);
        lonLat.setBackground(Color.BLACK);

        add(rowPanel, BorderLayout.CENTER);
        add(new JLabel(new ImageIcon(InteractionView.class.getResource("/icons/selectedPin.png"))), BorderLayout.WEST);
        add(lonLat, BorderLayout.SOUTH);
    }

    public void updatePanel(OSMAddress newAddress) {
        if(newAddress.getStreet() != null) street.setText(newAddress.getStreet());
        else street.setText("");
        cityAndZip.setText(newAddress.getPostCode() + ", " + newAddress.getCity());
        lonLat.setText("Longtitude: " + newAddress.getHousePlacement().getLon() / MathConstants.getLonFactor() + " Lattitude:" + newAddress.getHousePlacement().getLat()*-1);
    }
}

package view.components;

import controller.InteractionController;
import model.helpers.AddressSearch.SearchResult;
import model.helpers.routeGraph.RouteDescription;
import model.helpers.routeGraph.RouteStep;
import model.helpers.routeGraph.RouteType;
import model.osm.OSMAddress;
import view.InteractionView;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.Serializable;
import java.util.List;

public class RoutePanel extends JPanel implements Serializable {
    private static Dimension SCROLLPANE_SIZE = new Dimension(0, 300);
    private InteractionController interactionController;
    private JPanel topPanel, buttonPanel;

    private Printer printer;

    private JButton buttonToggleBike, buttonToggleWalk, buttonToggleCar;
    private JButton buttonExit, switchDirection;
    private JPanel inputPanel, inputGroup, buttonGroup;
    private JTextField firstInput, secondInput;
    private JPanel bottomPanel;
    private SearchResult from, to;
    private boolean isDefaultDirection;
    private JPanel speedPanel, descriptionPanel;
    private RouteType currentRouteType;

    public RoutePanel(SearchResult searchResult, InteractionController interactionController) {
        this.interactionController  = interactionController;
        this.from                   = searchResult;
        this.isDefaultDirection     = true;

        printer = new Printer();

        currentRouteType = RouteType.CAR;
        setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setOpaque(true);
        topPanel.setLayout(new BorderLayout());

        bottomPanel = new JPanel();
        bottomPanel.setOpaque(true);

        speedPanel          = new JPanel(new GridLayout(2,1));
        descriptionPanel    = new JPanel();

        bottomPanel.add(speedPanel);
        bottomPanel.add(descriptionPanel);

        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(350, 70));
        buttonPanel.setLayout(new BorderLayout());

        buttonGroup         = new JPanel();
        buttonToggleCar     = new MenuButton("buttons/car-selected-btn.png",50, 60,"Søg rutevejledning for biler");
        buttonToggleBike    = new MenuButton("buttons/bike-btn.png",50, 60,"Søg rutevejledning for cykler");
        buttonToggleWalk    = new MenuButton("buttons/walk-btn.png",50, 60,"Søg rutevejledning for fodgængere");

        buttonToggleCar.addActionListener(e -> selectButton(RouteType.CAR));
        buttonToggleBike.addActionListener(e -> selectButton(RouteType.BIKE));
        buttonToggleWalk.addActionListener(e -> selectButton(RouteType.WALK));

        buttonGroup.add(buttonToggleCar);
        buttonGroup.add(buttonToggleBike);
        buttonGroup.add(buttonToggleWalk);

        buttonExit = new MenuButton("buttons/close.png",50, 50,"Tilbage");
        buttonExit.addActionListener(e -> {
            interactionController.setDefaultSearch();
            interactionController.switchSearchButtonIcon(from == null && to == null);
        });

        buttonPanel.add(buttonGroup, BorderLayout.CENTER);
        buttonPanel.add(buttonExit, BorderLayout.EAST);

        //Controls the input boxes and the switch direction button
        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputGroup = new JPanel();
        inputGroup.setLayout(new GridLayout(2,1));

        if(from != null) {
            SearchResult result = from;
            firstInput = new JTextField(result.toString()); //todo make this pretty later
        }
        else firstInput = new JTextField("Fra");

        firstInput.setPreferredSize(new Dimension(350, 50));

        secondInput = new JTextField("Til");
        secondInput.setPreferredSize(new Dimension(350, 50));

        firstInput.getDocument().addDocumentListener(interactionController.inputDocumentListener(firstInput, InputFieldType.FROM));
        secondInput.getDocument().addDocumentListener(interactionController.inputDocumentListener(secondInput, InputFieldType.TO));

        firstInput.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if(firstInput.getText().equals("Fra")) firstInput.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(firstInput.getText().equals("")) firstInput.setText("Fra");
            }
        });
        secondInput.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if(secondInput.getText().equals("Til")) secondInput.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(secondInput.getText().equals("")) secondInput.setText("Til");
            }
        });


        inputGroup.add(firstInput);
        inputGroup.add(secondInput);

        switchDirection = new MenuButton("buttons/switch.png",75, 100,"Byt om på adresserne");
        switchDirection.addActionListener(e -> {
            String tmp = firstInput.getText();
            firstInput.setText(secondInput.getText());
            secondInput.setText(tmp);

            SearchResult tmpFrom    = this.from;
            this.from               = this.to;
            this.to                 = tmpFrom;

            isDefaultDirection = !isDefaultDirection;
            if(validateInput()) updateSpeedPanel();
            interactionController.clearSuggestions();
        });

        JPanel marginPanel = new JPanel();
        marginPanel.setPreferredSize(new Dimension(20, 20));
        inputPanel.add(marginPanel, BorderLayout.WEST);
        inputPanel.add(inputGroup, BorderLayout.CENTER);
        inputPanel.add(switchDirection, BorderLayout.EAST);

        topPanel.add(inputPanel, BorderLayout.SOUTH);
        topPanel.add(buttonPanel, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 100)));
        setOpaque(false);
        setBackground(Color.white);
    }

    private void selectButton(RouteType type) {
        if(currentRouteType == type) return;
        setButtonBackground(currentRouteType, false);
        setButtonBackground(type, true);
        currentRouteType = type;
        if(validateInput()) updateSpeedPanel();
        this.repaint();
    }

    private void setButtonBackground(RouteType type, boolean isDefaultBackground) {
        switch(type) {
            case CAR:
                if(!isDefaultBackground) buttonToggleCar.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/car-btn.png")));
                else buttonToggleCar.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/car-selected-btn.png")));
                break;
            case BIKE:
                if(!isDefaultBackground) buttonToggleBike.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/bike-btn.png")));
                else buttonToggleBike.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/bike-selected-btn.png")));
                break;
            case WALK:
                if(!isDefaultBackground) buttonToggleWalk.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/walk-btn.png")));
                else buttonToggleWalk.setIcon(new ImageIcon(getClass().getResource("/icons/buttons/walk-selected-btn.png")));
                break;
        }
    }

    public void setFrom(SearchResult from) {
        this.from = from;
        if(validateInput()) updateSpeedPanel();
    }

    public void setTo(SearchResult to) {
        this.to = to;
        if(validateInput()) updateSpeedPanel();
    }

    public void updateInputFields() {
        firstInput.setText(from.toString());
        secondInput.setText(to.toString());
    }

    private void clearInputs() {
        this.from = null;
        this.to = null;
        firstInput.setText("Fra");
        firstInput.setText("Til");
        interactionController.clearSuggestions();
    }

    private boolean validateInput() {
        if(this.from == this.to && (!firstInput.getText().equals("Fra") || !firstInput.getText().equals("Til")) && (!secondInput.getText().equals("Til") && !secondInput.getText().equals("Fra"))) {
            clearInputs();
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/biblethump.png"));
            String dialogText = "Du har valgt den samme adresse til og fra. Dette er ikke muligt, vælg venligst to forskellige adresser.";
            JOptionPane.showConfirmDialog(null, dialogText, "Samme adresse fejl!", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
            return false;
        }
        if(this.from != null && this.to != null) return true;
        else {
            speedPanel.removeAll();
            speedPanel.setVisible(false);
            return false;
        }
    }

    private void assertRoute(boolean useSpeed) {
        RouteDescription route;
        if(this.from != null && this.to != null) {
            route = interactionController.findRoute(from.getAddress(), to.getAddress(), currentRouteType, useSpeed, getX()+getWidth());
            assignRoute(route, useSpeed);
        }
    }

    public void assignRoute(RouteDescription route, boolean useSpeed){
        speedPanel.setVisible(false);
        speedPanel.removeAll();
        topPanel.setVisible(false);
        descriptionPanel.setVisible(true);
        descriptionPanel.removeAll();
        descriptionPanel.add(getDescriptionComponent(route, currentRouteType, useSpeed));
        bottomPanel.removeAll();
        bottomPanel.add(descriptionPanel);
        interactionController.clearSuggestions();
        interactionController.clearSelectedAddress();
    }

    private void goBackFromRoute() {
        topPanel.setVisible(true);
        updateSpeedPanel();
        descriptionPanel.setVisible(false);
        descriptionPanel.removeAll();
        interactionController.clearLocations();
    }

    private void updateSpeedPanel() {
        speedPanel.removeAll();
        speedPanel.setVisible(true);
        getSpeedChoices();
        bottomPanel.removeAll();
        bottomPanel.add(speedPanel);
        updateUI();
    }

    private void getSpeedChoices() {
        if(currentRouteType == RouteType.CAR){
            speedPanel.setLayout(new GridLayout(2,1));
            speedPanel.add(speedButton("Beregn den hurtigste rute.", true));
        }
        else speedPanel.setLayout(new GridLayout(1, 1));
        speedPanel.add(speedButton("Beregn den korteste rute.", false));
    }

    private JComponent speedButton(String text, boolean useSpeed) {
        String path     = (useSpeed) ? "speed.png" : "distance.png";
        ImageIcon icon  = new ImageIcon(getClass().getResource("/icons/" + path));
        JButton button  = new JButton(text, icon);
        button.setToolTipText("<html><body><h3>" + text + "<h3>");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> assertRoute(useSpeed));
        return button;
    }

    private JComponent getDescriptionComponent(RouteDescription route, RouteType routeType, boolean useSpeed) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(true);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(Color.red);
        wrapper.setBorder(null);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setOpaque(true);
        titlePanel.setBackground(new Color(8, 82, 78));

        JPanel labelPanel = new JPanel();
        labelPanel.setPreferredSize(new Dimension(420, 100));
        labelPanel.setLayout(new GridLayout(2,1));
        labelPanel.setOpaque(true);
        labelPanel.setBackground(new Color(8, 82, 78));
        labelPanel.setForeground(Color.white);
        labelPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelFrom    = new JLabel("<html><span style='padding: 0px 0px 10px 0px; font-size: 12px;'><span style='font-weight:bold;'>Fra: </span>" + from.toString()+"</span></html>");
        JLabel labelTo      = new JLabel("<html><span style='padding: 10px 0px 0px 0px; font-size: 12px;'><span style='font-weight:bold;'>Til: </span>" + to.toString()+"</span></html>");

        labelFrom.setForeground(Color.white);
        labelTo.setForeground(Color.white);

        labelPanel.add(labelFrom);
        labelPanel.add(labelTo);

        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout(new BorderLayout());
        overviewPanel.setPreferredSize(new Dimension(350, 70));
        overviewPanel.setOpaque(true);
        overviewPanel.setBackground(new Color(14, 120, 114));

        JLabel overviewLabel = new JLabel(route.speedToString()+" ("+ route.distanceToString() + ")", SwingConstants.CENTER);
        overviewLabel.setForeground(Color.white);
        overviewLabel.setIcon(getRouteIcon(routeType));
        overviewLabel.setPreferredSize(new Dimension(350, 40));

        String speedText  = (useSpeed) ? "Viser den hurtigste route." : "Viser den korteste route.";
        JLabel speedLabel = new JLabel(speedText, SwingConstants.CENTER);
        speedLabel.setForeground(Color.white);
        speedLabel.setPreferredSize(new Dimension(350, 30));

        overviewPanel.add(speedLabel, BorderLayout.SOUTH);
        overviewPanel.add(overviewLabel, BorderLayout.CENTER);

        JButton goBack = new MenuButton("goBack.png", 75, 50, "Gå tilbage");
        goBack.addActionListener(e -> goBackFromRoute());

        titlePanel.add(labelPanel, BorderLayout.CENTER);
        titlePanel.add(goBack, BorderLayout.WEST);

        headerPanel.add(overviewPanel, BorderLayout.NORTH);
        headerPanel.add(getRouteScrollPane(route, routeType), BorderLayout.CENTER);

        JButton printButton = new JButton(new ImageIcon(getClass().getResource("/icons/general/print.png")));
        printButton.addActionListener(e -> printer.print(wrapper, from.toString(), to.toString()));
        headerPanel.add(printButton, BorderLayout.SOUTH);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(headerPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private ImageIcon getRouteIcon(RouteType routeType) {
        String path = "";
        switch (routeType) {
            case CAR:
                path = "/icons/routeIcons/car-white.png";
                break;

            case BIKE:
                path = "/icons/routeIcons/bike-white.png";
                break;

            case WALK:
                path = "/icons/routeIcons/walk-white.png";
                break;
        }
        return new ImageIcon(getClass().getResource(path));
    }

    private JScrollPane getRouteScrollPane(RouteDescription routeDescription, RouteType routeType) {
        JScrollPane scrollPane = new JScrollPane(getRouteLabelPanel(routeDescription, routeType));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(Color.pink);
        scrollPane.setPreferredSize(SCROLLPANE_SIZE);
        return scrollPane;
    }

    private JPanel getRouteLabelPanel(RouteDescription routeDescription, RouteType routeType) {
        List<RouteStep> route   = routeDescription.getSteps();
        JPanel panel            = new JPanel();
        panel.setLayout(new GridLayout(route.size(),1));
        boolean isFirst = true;
        for(RouteStep s: route) {
            panel.add(routeLabel(s, isFirst, routeType));
            isFirst = false;
        }
        return panel;
    }

    private JComponent routeLabel(RouteStep step, boolean isFirst, RouteType routeType) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.setBackground(Color.white);

        String path = "";
        if(step.getDirection() == RouteDirection.FORWARD) path  = "forward";
        if(step.getDirection() == RouteDirection.LEFT) path     = "left";
        if(step.getDirection() == RouteDirection.RIGHT) path    = "right";
        if(step.getDirection() == RouteDirection.NONE) {
            if(routeType == RouteType.CAR)  path = "car-black";
            if(routeType == RouteType.WALK) path = "walk-black";
            if(routeType == RouteType.BIKE) path = "bike-black";
        }

        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/routeIcons/" + path + ".png"));

        JLabel img = new JLabel();
        img.setIcon(icon);
        img.setOpaque(true);
        img.setBackground(Color.white);
        img.setPreferredSize(new Dimension(45, 35));
        img.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());

        JTextArea  textArea  = new JTextArea();
        textArea.setPreferredSize(new Dimension(250, 50));
        textArea.setText(step.getDescription());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(true);
        if(!isFirst) textArea.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(20, 20, 20, 100)));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setCaretPosition(0);

        JLabel distanceText = new JLabel(step.getDistance(), SwingConstants.RIGHT);
        distanceText.setPreferredSize(new Dimension(55, 50));
        distanceText.setOpaque(true);
        distanceText.setBackground(Color.white);
        if(!isFirst) distanceText.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(20, 20, 20, 100)));

        textPanel.add(textArea, BorderLayout.CENTER);
        textPanel.add(distanceText, BorderLayout.EAST);

        wrapper.add(textPanel, BorderLayout.CENTER);
        wrapper.add(img, BorderLayout.WEST);
        return wrapper;
    }
}



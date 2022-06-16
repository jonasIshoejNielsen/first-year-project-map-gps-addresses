package view.components;

import controller.InteractionController;
import model.helpers.AddressSearch.SearchID;
import model.helpers.AddressSearch.SearchResult;
import model.osm.OSMAddress;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuggestionsPanel extends JComponent implements Serializable {
    private InteractionController interactionController;

    private JTextField inputField;
    private InputFieldType type;
    private String inputText;

    public SuggestionsPanel(List<SearchResult> results, String inputText, InteractionController interactionController, JTextField inputField, InputFieldType type) {
        this.interactionController  = interactionController;
        this.inputField             = inputField;
        this.inputText              = inputText;
        this.type                   = type;

        setLayout(new GridLayout(results.size() + 1,1));
        setOpaque(false);
        setBackground(Color.red);

        boolean isFirst = true;
        for(SearchResult result : results) {
            add(suggestionButton(result, isFirst));
            isFirst = false;
        }
        add(cancelButton());
    }

    private JComponent suggestionButton(SearchResult result, boolean isFirst) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBackground(Color.red);

        wrapper.add(getSearchIcon(), BorderLayout.WEST);
        wrapper.add(getSearchButton(result, isFirst), BorderLayout.CENTER);


        return wrapper;
    }

    private JButton getSearchButton(SearchResult result, boolean isFirst) {
        JButton button = new JButton("<html>" + getHighlightedResult(result) + "</html>");

        button.setPreferredSize(new Dimension(350,30));
        button.setToolTipText("<html><body><h3>Vælg " + result.toString() + "<h3>");
        button.setOpaque(false);
        button.setBackground(Color.red);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if(isFirst) button.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(20, 20, 20, 100)));
        else button.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(20, 20, 20, 100)));

        button.addActionListener(e -> {
            interactionController.setSelectedAddress(result, inputField, type);
            if(result.getId() == SearchID.ADDRESS) {
                this.updateButtons(result.getAddress());
            }
        });

        return button;
    }

    private JLabel getSearchIcon() {
        JLabel icon = new JLabel();
        icon.setPreferredSize(new Dimension(30, 30));
        icon.setIcon(new ImageIcon(getClass().getResource("/icons/searchPin.png")));
        return icon;
    }

    private JComponent cancelButton() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBackground(Color.red);

        JLabel icon = new JLabel();
        icon.setPreferredSize(new Dimension(30, 30));
        icon.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/searchClose.png")));

        JButton button = new JButton("<html>Fjern forslag..</html>");

        button.setPreferredSize(new Dimension(350,30));
        button.setToolTipText("<html><body><h3>Tryk her for at fjerne forslag.<h3>");
        button.setOpaque(false);
        button.setBackground(Color.red);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(20, 20, 20, 100)));

        button.addActionListener(e -> interactionController.clearSuggestions());

        wrapper.add(icon, BorderLayout.WEST);
        wrapper.add(button, BorderLayout.CENTER);

        return wrapper;
    }

    private void updateButtons(OSMAddress address) {
        List<SearchResult> results = new ArrayList<>();

        address.sortHousePlacements();
        int max = (address.getHousePlacements().size() > 8) ? 8 : address.getHousePlacements().size();

        for(int i = 0; i < max; i++) {
            OSMAddress temp = new OSMAddress(address, address.getHousePlacement(i));
            SearchResult result = new SearchResult(temp, SearchID.HOUSENUMBER);
            results.add(result);
        }
        this.inputText = "";
        interactionController.appendSuggestions(results, inputText, inputField, type);
    }

    private String getHighlightedResult(SearchResult result) {
        String address = result.toString().toLowerCase();

        if(address.contains(inputText.toLowerCase())) {
            int index = address.indexOf(inputText.toLowerCase());
            address = result.toString();

            String before = address.substring(0, index);
            String highlight = address.substring(index, index + inputText.length());
            String after = address.substring(index + inputText.length(), address.length());

            highlight = "<span style='font-weight:bold;'>" + highlight + "</span>";
            return before + highlight + after;
        }

        return result.toString();
    }
}
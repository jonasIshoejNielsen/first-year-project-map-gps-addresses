package view;

import controller.InteractionController;
import model.helpers.AddressSearch.SearchRegexs;
import model.helpers.AddressSearch.SearchResult;
import model.helpers.routeGraph.RouteDescription;
import model.osm.OSMAddress;
import view.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractionView extends JComponent{
    private InteractionController interactionController;
    private JLabel devToolsLabel;
    private JPanel westContainer;
    private JPanel suggestionPanel;
    private JScrollPane filterModulesScrollPane;
    private boolean showFilters, useRealLifeCoordinates = true, showFPSCounter = false, showZoomLevel = false, useDefaultCursor = true;

    private SearchResult selectedAddress;
    private List<SearchResult> resultList;
    private JTextField searchInput;
    private JPanel selectedSearchWrapper;
    private SelectedSearchPanel selectedSearchPanel;
    private JButton searchButton;
    private RoutePanel routePanel;
    private ZoomSlider zoomSlider;
    private ButtonType selectedButton;
    private JButton pointOfInterest;
    private JButton newNode;
    private JButton clickToAddRoute;
    private JButton toggleFilters;

    public InteractionView(InteractionController interactionController) {
        this.interactionController = interactionController;

        setPreferredSize(new Dimension(200, 500));
        setLayout(new BorderLayout());
        showFilters     = false;
        resultList      = new ArrayList<>();
        searchInput     = new JTextField("Søg.."); //primary search input

        suggestionPanel = new JPanel();
        suggestionPanel.setOpaque(true);
        suggestionPanel.setBackground(Color.white);
        suggestionPanel.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 100)));
        suggestionPanel.setVisible(false);

        devToolsLabel           = new DevToolsLabel();
        JButton zoomIn          = new MenuButton("zoomIn.png",50, 50,"Zoom ind");
        JButton zoomOut         = new MenuButton("zoomOut.png", 50, 50,"Zoom ud");
        JButton resetPosition   = new MenuButton("location.png",50, 50,"Centrér kortet");
        pointOfInterest         = new MenuButton("questionCursor.png",50, 50,"Vis nærmeste vej");
        newNode                 = new MenuButton("new.png",50, 50,"Tilføj en kommentar på kortet");
        clickToAddRoute         = new MenuButton("routeMap.png",50, 50,"Tilføj en rute med musen");
        toggleFilters           = new MenuButton("filter.png",50, 50,"Tryk her for at vise filtre");

        zoomSlider              = new ZoomSlider(interactionController);

        JPanel devToolsPanel        = new PanelGroup(null, new Color(0,0,0,180), true, new JComponent[]{devToolsLabel, zoomSlider});
        JPanel buttonGroupNorth     = new PanelGroup(new Dimension(55,170), null, false, new JComponent[]{zoomIn, zoomOut, resetPosition});
        JPanel buttonGroupSouth     = new PanelGroup(new Dimension(55,220), null, false, new JComponent[]{pointOfInterest, newNode, clickToAddRoute, toggleFilters});
        JPanel filterModules        = new PanelGroup(null, Color.white, true,null);
        filterModules.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        for(FilterType t : FilterType.values()) {
            filterModules.add(new FilterItem(t.getText(), t.isVisible(), t.getIconPath(), t.getChildren(), interactionController));
        }

        filterModulesScrollPane = new JScrollPane(filterModules);
        filterModulesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        filterModulesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        filterModulesScrollPane.setVisible(showFilters);
        filterModulesScrollPane.setOpaque(false);
        filterModulesScrollPane.setBorder(new EmptyBorder(0, 100, 0, 100));

        JPanel eastContainer = new PanelGroup(new Dimension(80,200), Color.white, false, new JComponent[]{buttonGroupNorth, buttonGroupSouth});

        JPanel southContainer = new JPanel();
        southContainer.setLayout(new BorderLayout());
        southContainer.setOpaque(false);
        southContainer.add(filterModulesScrollPane, BorderLayout.NORTH);
        southContainer.add(devToolsPanel, BorderLayout.EAST);

        westContainer = new JPanel();
        westContainer.setOpaque(false);

        add(eastContainer, BorderLayout.EAST);
        add(southContainer, BorderLayout.SOUTH);
        add(westContainer, BorderLayout.WEST);

        setDefaultSearch();

        toggleFilters.addActionListener(e -> {
            selectButton(ButtonType.TOGGLE_FILTERS);
            update();
        });

        zoomIn.addActionListener(e -> interactionController.zoom(1));
        zoomOut.addActionListener(e -> interactionController.zoom(-1));
        resetPosition.addActionListener(e -> interactionController.resetPosition());
        pointOfInterest.addActionListener(e -> {
            selectButton(ButtonType.POINT_OF_INTEREST);
        });
        newNode.addActionListener(e -> {
            selectButton(ButtonType.NEW_NODE);
        });
        clickToAddRoute.addActionListener(e -> {
            selectButton(ButtonType.ADD_ROUTE);
        });
    }

    public void switchSearchButtonIcon(boolean setDefaultIcon) {
        if(setDefaultIcon) searchButton.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/search.png")));
        else searchButton.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/clearSearch.png")));
    }


    private void selectButton(ButtonType type) {
        if(selectedButton != null) setButtonBackground(selectedButton, false);
        if(selectedButton != type) {
            setButtonBackground(type, true);
            selectedButton = type;
        }
        else selectedButton = null;
        this.repaint();
    }

    public void setButtonBackground(ButtonType type, boolean shouldBecomeActive) {
        String path;
        switch(type) {
            case NEW_NODE:
                interactionController.setAddNode(shouldBecomeActive);
                path = (!shouldBecomeActive) ? "new.png" : "new-selected.png";
                newNode.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/"+path)));
                if(shouldBecomeActive) this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                else this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
            case POINT_OF_INTEREST:
                interactionController.toggleShowNearestRoad();
                path = (!shouldBecomeActive) ? "questionCursor.png" : "questionCursor-selected.png";
                pointOfInterest.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/"+path)));
                if(shouldBecomeActive) this.setCursor(new Cursor(Cursor.HAND_CURSOR));
                else this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
            case ADD_ROUTE:
                interactionController.setClickToFindRoute(shouldBecomeActive);
                path = (!shouldBecomeActive) ? "routeMap.png" : "routeMap-selected.png";
                clickToAddRoute.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/"+path)));
                if(shouldBecomeActive) this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                else this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
            case TOGGLE_FILTERS:
                path = (!shouldBecomeActive) ? "filter.png" : "filter-selected.png";
                toggleFilters.setIcon(new ImageIcon(RoutePanel.class.getResource("/icons/"+path)));
                showFilters = !showFilters;
                filterModulesScrollPane.setVisible(showFilters);
                break;
        }
    }

    public void setDefaultSearch() {
        searchInput.setPreferredSize(new Dimension(250, 70));
        searchInput.getDocument().addDocumentListener(inputDocumentListener(searchInput, InputFieldType.DEFAULT));

        searchInput.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e){
                if(searchInput.getText().equals("Søg..")) searchInput.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(searchInput.getText().equals("")) searchInput.setText("Søg..");
            }
        });

        searchButton = new MenuButton("search.png",100, 75,"Søg");
        searchButton.addActionListener(e -> {
            if(resultList != null && resultList.size() > 0) {
                switchSearchButtonIcon(false);
                OSMAddress address = resultList.get(0).getAddress();
                setSelectedAddress(resultList.get(0), searchInput, InputFieldType.DEFAULT);
                interactionController.goTo(address);
            } else {
                switchSearchButtonIcon(true);
                searchInput.setText("Søg..");
                selectedAddress = null;
                selectedSearchWrapper.setVisible(false);
                interactionController.clearSelectedAddress();
                //alertNoResults();
            }

        });
        searchButton.setOpaque(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);

        JButton routeButton = new MenuButton("route.png",70, 70,"Rutevejledning");
        routeButton.addActionListener(e -> setRouteSearch());
        routeButton.setOpaque(false);
        routeButton.setContentAreaFilled(false);
        routeButton.setBorderPainted(false);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel searchGroup = new JPanel();
        searchGroup.setMaximumSize(new Dimension(350, 70));
        searchGroup.setLayout(new BorderLayout());
        searchGroup.setOpaque(true);
        searchGroup.setBackground(Color.white);
        searchGroup.setBorder(BorderFactory.createLineBorder(new Color(20, 20, 20, 100)));

        JPanel searchInputGroup = new JPanel();
        searchInputGroup.setOpaque(false);
        searchInput.setOpaque(false);
        searchInput.setBorder(BorderFactory.createEmptyBorder());
        searchInputGroup.add(searchInput);

        JPanel searchButtonGroup  = new JPanel();
        searchButtonGroup.setLayout(new BorderLayout());
        searchButtonGroup.setOpaque(false);

        searchButtonGroup.add(searchButton, BorderLayout.WEST);
        searchButtonGroup.add(routeButton, BorderLayout.EAST);

        selectedSearchWrapper = new JPanel();
        selectedSearchWrapper.setVisible(false);
        selectedSearchWrapper.setOpaque(true);
        selectedSearchWrapper.setBackground(new Color(30, 66, 142));

        selectedSearchPanel = new SelectedSearchPanel();
        selectedSearchWrapper.add(selectedSearchPanel);

        searchGroup.add(searchInputGroup, BorderLayout.WEST);
        searchGroup.add(searchButtonGroup, BorderLayout.EAST);
        searchGroup.add(selectedSearchWrapper, BorderLayout.SOUTH);

        resultList.clear();
        suggestionPanel.removeAll();

        wrapper.add(searchGroup, BorderLayout.NORTH);
        wrapper.add(suggestionPanel, BorderLayout.CENTER);

        setWestContainer(wrapper);
    }

    private void setRouteSearch() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false);

        resultList.clear();
        suggestionPanel.removeAll();
        selectedSearchWrapper.setVisible(false);

        routePanel = new RoutePanel(selectedAddress ,interactionController);

        wrapper.add(routePanel, BorderLayout.NORTH);
        wrapper.add(suggestionPanel, BorderLayout.CENTER);

        setWestContainer(wrapper);
        interactionController.clearSelectedAddress();
    }

    private void setWestContainer(JComponent component) {
        westContainer.removeAll();
        westContainer.add(component);
        update();
    }

    /*/////////////////////
    SEARCHING
    //////////////////////*/
    public DocumentListener inputDocumentListener(JTextField inputField, InputFieldType type) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            private void change() {
                String input = inputField.getText().trim();
                if(input.equals("Fra") || input.equals("Til") || input.equals("Søg..")) return;
                if(input.length() < 3) {
                    clearSuggestions();
                    return;
                }
                if(input.matches(SearchRegexs.getFullAddressRegex())) {
                    appendSuggestions(houseSearch(input, SearchRegexs.getFullAddressRegex()), input, inputField, type);

                } else if (input.matches(SearchRegexs.getAddressRegex())) {
                    Matcher matcher = Pattern.compile(SearchRegexs.getAddressRegex()).matcher(input);
                    if(matcher.matches()) {
                        input = matcher.group("street").trim();
                        appendSuggestions(interactionController.addressSearch(input), input, inputField, type);
                    }

                } else if (input.matches(SearchRegexs.getHouseRegex())) {
                   appendSuggestions(houseSearch(input, SearchRegexs.getHouseRegex()), input, inputField, type);

                } else {
                    appendSuggestions(interactionController.addressSearch(input), input, inputField, type);
                }
            }
            private List<SearchResult> houseSearch(String input, String regex) {
                Matcher matcher = Pattern.compile(regex).matcher(input);

                if(matcher.matches()) {
                    return interactionController.houseSearch(matcher.group("street"), matcher.group("number"));
                }

                return null;
            }
        };
    }



    public void appendSuggestions(List<SearchResult> results, String inputText, JTextField inputField, InputFieldType type) {
        suggestionPanel.removeAll();
        if(results != null && results.size() > 0) {
            resultList = results;
            suggestionPanel.add(new SuggestionsPanel(results, inputText, interactionController, inputField, type));
            suggestionPanel.setVisible(true);
        } else {
            clearSuggestions();
            alertNoResults();
        }
        update();
    }

    private JPanel searchResult() {
        JPanel searchResult = new JPanel();
        searchResult.setLayout(new BoxLayout(searchResult, BoxLayout.Y_AXIS));
        searchResult.setVisible(false);
        searchResult.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
        return searchResult;
    }

    /*searching end*/

    public void toggleCoordinateType() {
        useRealLifeCoordinates = !useRealLifeCoordinates;
    }

    public boolean isUseRealLifeCoordinatesOn() {
        return useRealLifeCoordinates;
    }

    public void toggleFPSCounter() {
        showFPSCounter = !showFPSCounter;
    }

    public boolean isFPSCounterOn() {
        return showFPSCounter;
    }

    public void toggleZoomLevels() {
        showZoomLevel = !showZoomLevel;
    }

    public void setSelectedAddress(SearchResult result, JTextField inputField, InputFieldType type) {
        if(type == InputFieldType.DEFAULT) {
            this.selectedAddress = result;
            interactionController.goTo(selectedAddress.getAddress());
            selectedSearchPanel.updatePanel(selectedAddress.getAddress());
            selectedSearchWrapper.setVisible(true);
            switchSearchButtonIcon(false);
        }
        if(type == InputFieldType.FROM) routePanel.setFrom(result);
        else if(type == InputFieldType.TO) routePanel.setTo(result);

        inputField.setText(result.toString());
        clearSuggestions();
    }

    private void alertNoResults() {
        suggestionPanel.setVisible(true);
        suggestionPanel.removeAll();
        suggestionPanel.add(new JLabel("Ingen resultater.."));
    }

    public void clearSuggestions() {
        resultList.clear();
        suggestionPanel.removeAll();
        suggestionPanel.setVisible(false);
    }

    public void toggleMouseDraggedCursor(boolean isDragging) {
        if(isDragging) {
            if(useDefaultCursor) {
                useDefaultCursor = false;
                this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        }
        else {
            useDefaultCursor = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void updateCoordinateLabel(Point2D modelCoords, float lonFactor, float fps, int zoomLevel) {
        int DEFAULT_LON_FACTOR = -999;
        if(lonFactor == DEFAULT_LON_FACTOR) return; //lonFactor has not been initialized
        String labelText;
        if(useRealLifeCoordinates){
            DecimalFormat df = new DecimalFormat(".0000");
            labelText = "Lon: " + df.format(modelCoords.getX()/lonFactor) + " Lat: " + df.format(modelCoords.getY()*-1);
        } else {
            DecimalFormat df = new DecimalFormat(".0000000000");
            labelText = "X: " + df.format(modelCoords.getX()) + " Y: " + df.format(modelCoords.getY());
        }
        if(showFPSCounter){
            DecimalFormat df = new DecimalFormat(".0");
            labelText += " / FPS: " + df.format(fps);
        }
        if(showZoomLevel){
            labelText += " / zoomLevel: " + zoomLevel;
        }
        devToolsLabel.setText(labelText);
        update();
    }

    public void update() {
        zoomSlider.update();
        this.revalidate();
    }

    public void updateZoomSliderValue() {
        zoomSlider.updateValue();
    }


    public boolean isZoomLevelOn() {
        return showZoomLevel;
    }

    public void setRouteDescription(RouteDescription routeDescription, boolean isUsingSpeedCalculation) {
        if(routeDescription == null) return;
        List<SearchResult> listFrom = interactionController.addressSearch(routeDescription.getRoadFrom());
        List<SearchResult> listTo   = interactionController.addressSearch(routeDescription.getRoadTo());
        if(listFrom.size() == 0 || listTo.size() == 0) return;
        setRouteSearch();
        routePanel.setFrom(listFrom.get(0));
        routePanel.setTo(listTo.get(0));
        routePanel.updateInputFields();
        routePanel.assignRoute(routeDescription, isUsingSpeedCalculation);
    }
}

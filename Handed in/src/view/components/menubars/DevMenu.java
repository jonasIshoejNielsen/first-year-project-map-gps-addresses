package view.components.menubars;

import controller.ModelController;
import controller.ViewController;

import javax.swing.*;

public class DevMenu extends JMenu {
    private ViewController viewController;
    private ModelController modelController;
    private ImageIcon checkmark;

    private boolean coordinates;
    private boolean fps;
    private boolean zoomLevels;
    private boolean kdTree;
    private boolean housenumbers;
    private boolean nearestFigur;
    private boolean testInfo;
    private boolean drawVisited;
    private boolean nearestRoad;

    public DevMenu(ViewController viewController, ModelController modelController) {
        super("Værktøjer");
        this.viewController = viewController;
        this.modelController  = modelController;
        this.checkmark = new ImageIcon(getClass().getResource("/icons/checkmark.png"));

        updateBooleans();
        createMenu();
    }

    private void updateBooleans() {
        coordinates  = !viewController.isUseRealLifeCoordinatesOn();
        fps          = viewController.isFPSCounterOn();
        zoomLevels   = viewController.isZoomLevelOn();
        kdTree       = viewController.isKdTreeOn();
        housenumbers = viewController.isHouseNumbersOn();
        nearestFigur = viewController.isNearestFigurOn();
        testInfo     = modelController.isTestInfoOn();
        drawVisited  = modelController.isDrawVisitedOn();
        nearestRoad  = viewController.isNearestRoadOn();
    }

    private void createMenu() {
        createToggleCoordinatesItem();
        createToggleFPSItem();
        createToggleZoomlevelItem();
        createToggleKDTreeViewItem();
        createToggleAddressesItem();
        createToggleDrawNearestItem();
        createLoadWithTestInfoItem();
        createToggleDrawVisitedItem();
        createToggleShowNearestRoad();
    }

    private void createToggleCoordinatesItem() {
        JMenuItem toggleCoordinates = new JMenuItem("Vis Koordinater");
        if(coordinates) toggleCoordinates.setIcon(checkmark);
        toggleCoordinates.addActionListener(e -> {
            viewController.toggleCoordinateType();
            if(!coordinates) toggleCoordinates.setIcon(checkmark);
            else             toggleCoordinates.setIcon(null);
            coordinates = !coordinates;
        });
        add(toggleCoordinates);
    }

    private void createToggleFPSItem() {
        JMenuItem toggleFPS = new JMenuItem("Vis FPS");
        if(fps) toggleFPS.setIcon(checkmark);
        toggleFPS.addActionListener(e -> {
            viewController.toggleFPS();
            if(!fps) toggleFPS.setIcon(checkmark);
            else     toggleFPS.setIcon(null);
            fps = !fps;
        });
        add(toggleFPS);
    }

    private void createToggleZoomlevelItem() {
        JMenuItem toggleZoomLevels = new JMenuItem("Vis ZoomLevels");
        if(zoomLevels) toggleZoomLevels.setIcon(checkmark);
        toggleZoomLevels.addActionListener(e -> {
            viewController.toggleZoomLevels();
            if(!zoomLevels) toggleZoomLevels.setIcon(checkmark);
            else            toggleZoomLevels.setIcon(null);
            zoomLevels = !zoomLevels;
        });
        add(toggleZoomLevels);
    }

    private void createToggleKDTreeViewItem() {
        JMenuItem toggleKdTreeView = new JMenuItem("Vis KD-Træ");
        if(kdTree)  toggleKdTreeView.setIcon(checkmark);
        toggleKdTreeView.addActionListener(e -> {
            viewController.toggleKdTreeView();
            if(!kdTree) toggleKdTreeView.setIcon(checkmark);
            else        toggleKdTreeView.setIcon(null);
            kdTree = !kdTree;
        });
        add(toggleKdTreeView);
    }

    private void createToggleAddressesItem() {
        JMenuItem toggleAdressesView = new JMenuItem("Vis Husnumre");
        if(housenumbers) toggleAdressesView.setIcon(checkmark);
        toggleAdressesView.addActionListener(e -> {
            viewController.toggleAdressView();
            if(!housenumbers) toggleAdressesView.setIcon(checkmark);
            else              toggleAdressesView.setIcon(null);
            housenumbers = !housenumbers;
        });
        add(toggleAdressesView);
    }

    private void createToggleDrawNearestItem() {
        JMenuItem toggleDrawNearest = new JMenuItem("Tegn Nærmeste Figur");
        if(nearestFigur) toggleDrawNearest.setIcon(checkmark);
        toggleDrawNearest.addActionListener(e -> {
            viewController.toggleDrawNearest();
            if(!nearestFigur) toggleDrawNearest.setIcon(checkmark);
            else              toggleDrawNearest.setIcon(null);
            nearestFigur = !nearestFigur;
        });
        add(toggleDrawNearest);
    }

    private void createLoadWithTestInfoItem() {
        JMenuItem toggleLoadWithTestInfo = new JMenuItem("Indlæs med test info");
        if(testInfo) toggleLoadWithTestInfo.setIcon(checkmark);
        toggleLoadWithTestInfo.addActionListener(e -> {
            modelController.toggleLoadWithTestInfo();
            if(!testInfo) toggleLoadWithTestInfo.setIcon(checkmark);
            else          toggleLoadWithTestInfo.setIcon(null);
            testInfo = !testInfo;
        });
        add(toggleLoadWithTestInfo);
    }

    private void createToggleDrawVisitedItem() {
        JMenuItem toggleDrawVisited = new JMenuItem("Tegn alle nodes besøgt");
        if(drawVisited) toggleDrawVisited.setIcon(checkmark);
        toggleDrawVisited.addActionListener(e -> {
            modelController.toggleDrawVisited();
            if(!drawVisited) toggleDrawVisited.setIcon(checkmark);
            else             toggleDrawVisited.setIcon(null);
            drawVisited = !drawVisited;
        });
        add(toggleDrawVisited);
    }

    private void createToggleShowNearestRoad() {
        JMenuItem toggleShowNearestRoad = new JMenuItem("Vis nærmeste vej");
        if(nearestRoad) toggleShowNearestRoad.setIcon(checkmark);
        toggleShowNearestRoad.addActionListener(e -> {
            viewController.toggleShowNearestRoad();
            if(!nearestRoad) toggleShowNearestRoad.setIcon(checkmark);
            else             toggleShowNearestRoad.setIcon(null);
            nearestRoad = !nearestRoad;
        });
        add(toggleShowNearestRoad);
    }
}

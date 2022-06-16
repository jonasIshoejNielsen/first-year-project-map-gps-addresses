package view.components.menubars;

import controller.ModelController;
import model.helpers.parsers.OSMParser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class LoadMap extends JMenuItem {
    private view.Frame frame;
    private ModelController modelController;

    private boolean mapLoaded = false;
    private static final int MAP_PROGRESS_STAGES = 5;

    LoadMap(view.Frame frame, ModelController modelController) {
        super("Indlæs kort");
        this.frame = frame;

        this.modelController = modelController;
        setIcon(new ImageIcon(getClass().getResource("/icons/loadGray.png")));

        addActionListener(e -> action());
    }

    private void action() {
        JFileChooser fileChooser = new FileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(".osm .tgm .zip", "osm", "tgm", "zip"));
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) startLoading(fileChooser);
        mapLoaded = false;
    }

    private void startLoading(JFileChooser fileChooser) {
        String fileName = fileChooser.getSelectedFile().getAbsolutePath();
        Thread loadFileThread = new Thread(() -> {
            if(!modelController.load(fileName)){
                String errorMessage = modelController.getLoadErrorMessage();
                JOptionPane.showMessageDialog(null, errorMessage, "Fejl", JOptionPane.ERROR_MESSAGE);
                mapLoaded = true;
            }
            mapLoaded = true;
        });

        Thread loadPopupThread = new Thread(() -> loadPopupThread());
        frame.showFrameLoader();
        loadFileThread.start();
        loadPopupThread.start();
    }

    private void loadPopupThread() {
        boolean keepRunning = true;
        OSMParser osmParser = null;
        frame.updateFrameLoaderText("Vent venligst, ToggleMaps indlæser dit kort.");
        while(keepRunning){
            if(modelController.getOSMParser() != null){
                osmParser = modelController.getOSMParser();
                keepRunning = false;
            }
            if(mapLoaded) keepRunning = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        keepRunning = !mapLoaded;
        while(keepRunning){
            int[] counters = osmParser.getCountersCurrent();
            int[] countersExpected = osmParser.getCountersExpected();
            boolean isBuildingMap = osmParser.getIsBuildingMap();
            int mapProgress = osmParser.getMapProgress();
            updateFeedbackText(counters, countersExpected, isBuildingMap, mapProgress);
            if(mapLoaded) keepRunning = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        System.gc();
        frame.hideFrameLoader();
    }

    private void updateFeedbackText(int[] counters, int[] countersExpected, boolean isBuildingMap, int mapProgress) {
        int nodeCount               = counters[0];
        int wayCount                = counters[1];
        int relationCount           = counters[2];
        int nodeCountExpected       = countersExpected[0];
        int wayCountExpected        = countersExpected[1];
        int relationCountExpected   = countersExpected[2];
        if(isBuildingMap) {
            if(mapProgress == MAP_PROGRESS_STAGES)
                frame.updateFrameLoaderText("Vent venligst, ToggleMaps har næsten indlæst dit kort.");
            else {
                StringBuilder stringBuilder = new StringBuilder().append("Kort bygges: ").append(mapProgress).append(" / ")
                                                                    .append(MAP_PROGRESS_STAGES).append(" kort dele bygget.");
                frame.updateFrameLoaderText(stringBuilder.toString());
            }
        } else {
            if (relationCount > 0)
                updateFrame(relationCount, relationCountExpected, "relations");
            else if (wayCount > 0)
                updateFrame(wayCount, wayCountExpected, "ways");
            else
                updateFrame(nodeCount, nodeCountExpected, "nodes");
        }
    }
    private void updateFrame(int currentCount, int expectedMaxCount, String whatIsLoaded ){
        StringBuilder stringBuilderMessage = new StringBuilder();
        if (currentCount > expectedMaxCount)
            stringBuilderMessage.append("Læser OSM fil: ").append(whatIsLoaded).append(" indlæst.");
        else
            stringBuilderMessage.append("Læser OSM fil: ").append(currentCount).append(" af ").append(expectedMaxCount).append(" ").append(whatIsLoaded).append(" indlæst.");
        frame.updateFrameLoaderText(stringBuilderMessage.toString());
    }
}

package view.components.menubars;

import controller.ModelController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Files extends JMenu {
    private view.Frame frame;
    private ModelController modelController;

    public Files( view.Frame frame, ModelController modelController) {
        super("Filer");
        this.frame = frame;
        this.modelController = modelController;
        createMenu();
    }

    private void createMenu() {
        add(new LoadMap(frame, modelController));
        createSaveMap();
        addSeparator();
        createLoadSmallOSM();
        addSeparator();

        JMenuItem exit = new JMenuItem("Luk Toggle Maps");
        exit.addActionListener(e -> System.exit(0));
        add(exit);
    }

    private void createSaveMap() {
        JMenuItem saveXML = new JMenuItem("Gem kort");
        saveXML.setIcon(new ImageIcon(getClass().getResource("/icons/saveGray.png")));
        add(saveXML);

        saveXML.addActionListener(e -> {
            JFileChooser fileChooser = new FileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(".tgm", "tgm"));
            int returnValue = fileChooser.showSaveDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if(!modelController.saveMap(fileName)){
                    String errorMessage = modelController.getSaveErrorMessage();
                    JOptionPane.showMessageDialog(null, errorMessage, "Fejl", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Kort gemt", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void createLoadSmallOSM() {
        JMenuItem loadSmallOSM = new JMenuItem("Indlæs start kort");
        add(loadSmallOSM);

        loadSmallOSM.addActionListener(e -> modelController.loadMapFromResource());
    }
}

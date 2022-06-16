package view.components.menubars;

import controller.ModelController;
import controller.ViewController;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ColorThemes extends JMenu {
    private ViewController viewController;
    private ImageIcon checkmark;

    private boolean grayscaleOn;
    private ModelController modelController;
    private JFrame frame;

    public ColorThemes(ViewController viewController, ModelController modelController, JFrame frame) {
        super("Temaer");
        this.viewController = viewController;
        this.modelController = modelController;
        this.frame = frame;
        this.checkmark = new ImageIcon(getClass().getResource("/icons/checkmark.png"));
        createMenu();
    }

    private void createMenu() {
        createLoadTMC();
        createSaveTMC();
        addSeparator();
        createGrayscaleItem();
        createLoadStartTCM();
    }

    private void createGrayscaleItem() {
        JMenuItem grayscale = new JMenuItem("Gråskalering");
        grayscaleOn = viewController.isGrayscaleOn();
        if(grayscaleOn) grayscale.setIcon(checkmark);

        grayscale.addActionListener(e -> {
            viewController.toggleGrayscale();
            if(!this.grayscaleOn) grayscale.setIcon(checkmark);
            else                  grayscale.setIcon(null);
            this.grayscaleOn = !this.grayscaleOn;
        });
        add(grayscale);
    }

    private void createLoadTMC() {
        JMenuItem loadTMC = new JMenuItem("Indlæs farve tema");
        loadTMC.setIcon(new ImageIcon(getClass().getResource("/icons/loadBlue.png")));
        add(loadTMC);

        loadTMC.addActionListener(e -> {
            JFileChooser fileChooser = new FileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(".tmc", "tmc"));
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                modelController.load(fileName);
            }
        });
    }

    private void createSaveTMC() {
        JMenuItem saveXML = new JMenuItem("Gem farve tema");
        saveXML.setIcon(new ImageIcon(getClass().getResource("/icons/saveBlue.png")));
        add(saveXML);

        saveXML.addActionListener(e -> {
            JFileChooser fileChooser = new FileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter(".tmc", "tmc"));
            int returnValue = fileChooser.showSaveDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                modelController.saveColorTheme(fileName);
            }
        });
    }

    private void createLoadStartTCM() {
        JMenuItem loadStartTGM = new JMenuItem("Indlæs standard tema");
        add(loadStartTGM);

        loadStartTGM.addActionListener(e -> modelController.loadThemeFromResource());

    }
}

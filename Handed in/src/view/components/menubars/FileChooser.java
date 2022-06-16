package view.components.menubars;

import javax.swing.*;
import java.io.File;

class FileChooser extends JFileChooser {

    FileChooser() {
        File workingDirectory = new File(System.getProperty("user.dir"));
        setCurrentDirectory(workingDirectory);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
    }
}

package view.components;

import controller.ModelController;
import controller.ViewController;
import view.components.menubars.*;

import javax.swing.*;

public class MenuBar extends JMenuBar {
    public MenuBar(view.Frame frame, ViewController viewController, ModelController modelController) {
        add(new Files(frame, modelController));
        add(new DevMenu(viewController, modelController));
        add(new ColorThemes(viewController, modelController, frame));
        add(new ColorPicker(frame, viewController));
    }
}

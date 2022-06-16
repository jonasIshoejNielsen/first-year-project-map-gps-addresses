import javax.swing.*;

import controller.*;
import model.Model;
import view.*;
import view.components.FrameLoader;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        Model model = new Model();
        FrameLoader frameLoader = new FrameLoader();
        model.loadThemeResource();

        SwingUtilities.invokeLater(() -> {
            ModelController modelController             = new ModelController(model);
            InteractionController interactionController = new InteractionController();
            InteractionView interactionView             = new InteractionView(interactionController);
            CanvasView canvasView                       = new CanvasView(model);
            LayeredView layeredView                     = new LayeredView(frameLoader, interactionView, canvasView);
            ViewController viewController               = new ViewController(interactionView, canvasView, model);
            Frame frame                                 = new Frame(layeredView, modelController, viewController);
            interactionController.setViewController(viewController, modelController);
            new MouseController(viewController);
            new KeyboardController(frame, viewController);
        });
        try {
            Thread.sleep(2000); //Higher value needed for slower computers. 2000 worked on all test computers.
        }
        catch (Exception ignored) {}
        model.loadMapResource();
        frameLoader.hideLoader();
    }
}

package view;

import controller.ModelController;
import controller.ViewController;
import view.components.FrameLoader;
import view.components.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Frame extends JFrame {
    private LayeredView layeredView;
    public Frame(LayeredView layeredView, ModelController modelController, ViewController viewController) {
        super("Toggle Maps");
        this.layeredView = layeredView;
        add(layeredView);
        
        setIconImage(new ImageIcon(Frame.class.getResource("/icons/general/logo.png")).getImage());
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 800));
        setVisible(true);
        setJMenuBar(new MenuBar(this, viewController, modelController));
        setLocationRelativeTo(null);
        pack();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layeredView.update(getHeight(), getWidth());
            }
        });

        FrameLoader.changeFont(this, "Roboto-Regular.ttf", 16f);
    }

    public void updateFrameLoaderText(String s) {
        this.layeredView.updateFrameLoaderText(s);
    }

    public void hideFrameLoader() {
        this.layeredView.hideFrameLoader();
    }

    public void showFrameLoader() {
        this.layeredView.showFrameLoader();
    }
}


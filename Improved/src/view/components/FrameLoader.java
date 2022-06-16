package view.components;

import model.helpers.Load;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FrameLoader extends JPanel {
    private JLabel text;

    public FrameLoader() {
        setOpaque(true);
        setBackground(Color.white);

        JPanel wrapper = new JPanel();
        wrapper.setMaximumSize(new Dimension(250, 400));
        wrapper.setLayout(new GridLayout(2,1));
        wrapper.setOpaque(false);

        text = new JLabel("<html>ToggleMaps indlæses, vent venligst..</html>", SwingConstants.CENTER);

        JLabel logo = new JLabel(new ImageIcon(FrameLoader.class.getResource("/icons/general/toggleMapsLogo.png")));
        logo.setPreferredSize(new Dimension(350, 150));

        JLabel loadingGif = new JLabel(new ImageIcon(FrameLoader.class.getResource("/icons/general/loading.gif")));
        loadingGif.setPreferredSize(new Dimension(350, 156));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(350, 200));

        infoPanel.add(loadingGif);
        infoPanel.add(text);

        wrapper.add(logo);
        wrapper.add(infoPanel);

        add(wrapper);
    }

    public void hideLoader() {
        this.setVisible(false);
    }

    public void showLoader() {
        this.setVisible(true);
    }

    public void updateText(String s)  {
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        this.text.setText(s);
    }

    public static void changeFont (Component component, String path, float fontSize) {
        Font font = null;
        InputStream fontStream = Load.class.getResourceAsStream("/fonts/" + path);
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        /*try {
            Path p = Files.createTempFile("resource-font-", ".ext");
            Files.copy(fontStream, p, StandardCopyOption.REPLACE_EXISTING);
            FileInputStream input = new FileInputStream(p.toFile());
            font = Font.createFont(Font.TRUETYPE_FONT, input);
            p.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }*/

        assert font != null;
        font = font.deriveFont(fontSize);
        component.setFont(font);
        if (component instanceof Container) for (Component child :((Container) component).getComponents ()) changeFont (child, font);
    }
    private static void changeFont (Component component, Font font) {
        component.setFont(font);
        if (component instanceof Container) for (Component child :((Container) component).getComponents ()) changeFont (child, font);
    }

}

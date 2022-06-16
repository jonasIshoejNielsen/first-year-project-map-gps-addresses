package view;

import model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class CanvasViewTest {

    @Test
    @DisplayName("Test that the grayscale calculator works as expected")
    void testGrayscaleCalulator() {
        CanvasView canvasView = new CanvasView(new Model());
        Color color = new Color(200,150,100); //Close colors
        Color colorTest = canvasView.calculateGrayscale(color);
        assertEquals(colorTest, new Color(150,150,150));

        color = new Color(255, 255, 255); //Highest point
        colorTest = canvasView.calculateGrayscale(color);
        assertEquals(colorTest, new Color(255, 255, 255));

        color = new Color(0, 0, 0); //Lower point
        colorTest = canvasView.calculateGrayscale(color);
        assertEquals(colorTest, new Color(0, 0, 0));

        color = new Color(40, 160, 232); //Random color
        colorTest = canvasView.calculateGrayscale(color);
        assertEquals(colorTest, new Color(144, 144, 144));

        color = new Color(233, 155, 87); //Giving a calculation that is decimal.
        colorTest = canvasView.calculateGrayscale(color);
        assertEquals(colorTest, new Color(158, 158, 158));
    }

    @Test
    @DisplayName("Testing that the change of AA is done right")
    void testAA() {
        CanvasView canvasView = new CanvasView(new Model());
        canvasView.setAntiAliasing(true);
        canvasView.setAntiAliasing(false);

        canvasView.toggleAntiAliasing();
    }

    @Test
    @DisplayName("Test of the starting zoomlevel is correct")
    void testGetZoomlevel() {
        CanvasView canvasView = new CanvasView(new Model());
        assertEquals(0, canvasView.getCurrentZoomlevel());
    }
}
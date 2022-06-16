package model.helpers;

import java.awt.geom.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import model.helpers.drawing.EnhancedShape;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Scanner;

class KDTreeTest {

    @Test
    @Disabled
    @DisplayName("Given expected ways, correct ways can be pulled from wayMap")
    void testKD()
    {
        File file = new File("testResources/smallTest.txt");
        try {
            Scanner s = new Scanner(file);

            long time = System.nanoTime();

            ArrayList<EnhancedShape> arraylist = new ArrayList<>();
            while(s.hasNext()) {
                String[] line=s.nextLine().split(" ");
                Point2D point1 =new Point2D.Double(Double.parseDouble(line[0]), Double.parseDouble(line[1]));
                Point2D point2 =new Point2D.Double(Double.parseDouble(line[2]), Double.parseDouble(line[3]));
                Path2D path=new Path2D.Double();
                path.moveTo(point1.getX(), point1.getY());
                path.lineTo(point2.getX(), point2.getY());
                EnhancedShape enhancedShape =new EnhancedShape(path);
                arraylist.add(enhancedShape);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



}

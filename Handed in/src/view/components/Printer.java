package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

class Printer {

    void print(JComponent inputComponent, String from, String to) {
        JComponent printComponent = getPrintComponent(inputComponent);
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("ToggleMaps Rutevejledning fra " + from + " til " + to);

        printerJob.setPrintable (new Printable() {
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) pg;
                g2.translate(pf.getImageableX(), pf.getImageableY());
                printComponent.paint(g2);
                return Printable.PAGE_EXISTS;
            }
        });

        if (!printerJob.printDialog()) return;
        try {
            printerJob.print();
        } catch (PrinterException ex) {
            ImageIcon icon = new ImageIcon(getClass().getResource("icons/general/logo"));
            String message = "Der er opstået en fejl under udprintning \n Prøv igen senere.";
            JOptionPane.showMessageDialog(null, message, "Print fejl", JOptionPane.WARNING_MESSAGE, icon);
        }
    }

    private JComponent getPrintComponent(JComponent inputComponent) {
        JComponent com  = (JComponent) inputComponent.getComponents()[1];
        JComponent com2 = (JComponent) com.getComponents()[1];
        JComponent com3 = (JComponent) com2.getComponents()[0];
        return (JComponent) com3.getComponents()[0];
    }
}

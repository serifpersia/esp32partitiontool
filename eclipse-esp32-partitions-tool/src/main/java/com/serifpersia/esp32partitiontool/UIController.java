package com.serifpersia.esp32partitiontool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.FileWriter;

import java.io.IOException;

public class UIController implements ActionListener {
    private UI ui;

    public UIController(UI ui) {
        this.ui = ui;
        attachListeners();
    }

    private void attachListeners() {
        // Attach listener to the "Generate CSV" button
        ui.getGenCSVButton().addActionListener(this);
        
        // Attach listeners to each checkbox
        for (int i = 0; i < ui.getNumCheckboxes(); i++) {
            ui.getCheckBox(i).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Check if the event source is the "Generate CSV" button
        if (e.getSource() == ui.getGenCSVButton()) {
            System.out.println("Generate CSV Button Clicked!");
            // Add your logic here
            exportToCSV();
        }
        
        
        // Check if the event source is a checkbox
        else if (e.getSource() instanceof JCheckBox) {
            JCheckBox checkbox = (JCheckBox) e.getSource();
            int toggleID = getToggleID(checkbox);
            String state = checkbox.isSelected() ? "Enabled" : "Disabled";
            System.out.println("Toggle " + toggleID + ": State " + state);
        }
    }

    // Method to get the ID of the checkbox
    private int getToggleID(JCheckBox checkbox) {
        // Iterate through the array of checkboxes to find the index of the given checkbox
        for (int i = 0; i < ui.getNumCheckboxes(); i++) {
            if (checkbox == ui.getCheckBox(i)) {
                // Index starts from 1
                return i + 1;
            }
        }
        return -1; // Return -1 if the checkbox is not found
    }
    private void exportToCSV() {
        FileDialog dialog = new FileDialog(new Frame(), "Export Partitions CSV", FileDialog.SAVE);
        
        // Set default filename to partitions.csv
        dialog.setFile("partitions.csv");

        dialog.setVisible(true);

        String fileName = dialog.getFile();
        if (fileName != null) {
            String filePath = dialog.getDirectory() + fileName;

            try (FileWriter writer = new FileWriter(filePath)) {
                // Write CSV header
                writer.write("Toggle ID,State\n");

                // Write checkbox states
                for (int i = 0; i < ui.getNumCheckboxes(); i++) {
                    JCheckBox checkbox = ui.getCheckBox(i);
                    int toggleID = getToggleID(checkbox);
                    String state = checkbox.isSelected() ? "Enabled" : "Disabled";
                    writer.write(toggleID + "," + state + "\n");
                }

                System.out.println("CSV exported successfully to: " + filePath);
            } catch (IOException ex) {
                System.err.println("Error exporting CSV: " + ex.getMessage());
            }
        }
    }

}

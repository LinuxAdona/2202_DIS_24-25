package obj;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class AvailabilityCellRenderer extends DefaultTableCellRenderer {

    // Define the color for unavailable
    private static final Color UNAVAILABLE_COLOR = new Color(0xff4545); // Red

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Get the availability status from the third column
        String availability = (String) table.getValueAt(row, 2); // Get availability from the third column
        if (availability != null && availability.equals("Unavailable")) {
            setBackground(UNAVAILABLE_COLOR);
            setForeground(Color.WHITE); // Change text color for better contrast
        } else {
            setBackground(table.getBackground()); // Use the default background color
            setForeground(Color.BLACK); // Default text color
        }

        // Handle selection
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } 
        
        return this;
    }
}

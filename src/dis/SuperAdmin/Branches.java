/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dis.SuperAdmin;

import Database.DBConnection;
import dis.Admin.Accounts;
import dis.Admin.Report;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import obj.AvailabilityCellRenderer;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Branches extends javax.swing.JFrame {

    private JComboBox<String> cityComboBox;
    private JComboBox<String> municipalityComboBox;
    private JComboBox<String> barangayComboBox;
    
    /**
     * Creates new form Branches
     */
    public Branches() {
        initComponents();
        loadBranches();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getLoggedInUserID() {
        return Login.loggedInUserID;
    }
    
    private void loadBranches() {
        DefaultTableModel model = (DefaultTableModel) tbBranches.getModel();
        model.setRowCount(0);
        String userID = getLoggedInUserID();

        for (int i = 0; i < tbBranches.getColumnModel().getColumnCount(); i++) {
            tbBranches.getColumnModel().getColumn(i).setResizable(false);
        }

        double totalElectricUsage = 0;
        double totalWaterUsage = 0;
        double totalElectricBill = 0;
        double totalWaterBill = 0;

        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT b.branch_id, a.city, a.municipality, a.barangay, p.first_name, p.last_name FROM branches b " 
                    + "LEFT JOIN address a ON b.address_id = a.address_id "
                    + "LEFT JOIN profiles p ON p.user_id = b.user_id";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int branchId = rs.getInt("branch_id");
                    String municipality = rs.getString("municipality");
                    String first_name = rs.getString("first_name");
                    String last_name = rs.getString("last_name");
                    String fullName = (first_name != null && last_name != null) ? first_name + " " + last_name : "N/A";
                    model.addRow(new Object[]{branchId, municipality, fullName});
                    
                    String usageSql = "SELECT meter_usage, meter_type "
                            + "FROM meters "
                            + "GROUP BY meter_type";
                    try (PreparedStatement psUsage = conn.prepareStatement(usageSql)) {
                        try (ResultSet rsUsage = psUsage.executeQuery()) {
                            while (rsUsage.next()) {
                                String meterType = rsUsage.getString("meter_type");
                                double totalUsage = rsUsage.getDouble("meter_usage");
                                if ("electric".equals(meterType)) {
                                    totalElectricUsage = totalUsage;
                                } else if ("water".equals(meterType)) {
                                    totalWaterUsage = totalUsage;
                                }
                            }
                        }
                    }
                }
            }
            
            String billElectricSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalElectricityBill "
                    + "FROM billings WHERE meter_type = 'electric'";
            String billWaterSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalWaterBill "
                    + "FROM billings WHERE meter_type = 'water'";

            // Calculate total electric bill
            try (PreparedStatement psEBill = conn.prepareStatement(billElectricSql)) {
                try (ResultSet rsEBill = psEBill.executeQuery()) {
                    if (rsEBill.next()) {
                        totalElectricBill = rsEBill.getDouble("totalElectricityBill");
                    }
                }
            }

            // Calculate total water bill
            try (PreparedStatement psWBill = conn.prepareStatement(billWaterSql)) {
                try (ResultSet rsWBill = psWBill.executeQuery()) {
                    if (rsWBill.next()) {
                        totalWaterBill = rsWBill.getDouble("totalWaterBill");
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }

        lblEUsage.setText(totalElectricUsage + " kwh");
        lblWusage.setText(totalWaterUsage + " m³");
        lblEBill.setText("PHP " + totalElectricBill);
        lblWBill.setText("PHP " + totalWaterBill);
    }
    
    private void loadDoors(int branchId) {
        DefaultTableModel model = (DefaultTableModel) tbDoors.getModel();
        model.setRowCount(0); // Clear existing data

        // Disable resizing of columns
        for (int i = 0; i < tbDoors.getColumnModel().getColumnCount(); i++) {
            tbDoors.getColumnModel().getColumn(i).setResizable(false);
        }

        tbDoors.setDefaultRenderer(Object.class, new AvailabilityCellRenderer());

        double totalElectricUsage = 0;
        double totalWaterUsage = 0;
        double totalElectricBill = 0;
        double totalWaterBill = 0;

        try (Connection conn = DBConnection.Connect()) {
            // Load doors for the selected branch
            String sql = "SELECT d.door_id, d.door_number, d.available "
                    + "FROM doors d "
                    + "WHERE d.branch_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, branchId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int doorId = rs.getInt("door_id");
                        int doorNumber = rs.getInt("door_number");
                        int available = rs.getInt("available");
                        String availability = (available == 0 || available < 0) ? "Unavailable" : "Available";
                        model.addRow(new Object[]{doorId, doorNumber, availability});

                        // Fetch usage for each door
                        String usageSql = "SELECT meter_usage, meter_type "
                                + "FROM meters "
                                + "WHERE door_id = ? "
                                + "ORDER BY meter_type";
                        try (PreparedStatement psUsage = conn.prepareStatement(usageSql)) {
                            psUsage.setInt(1, doorId);
                            try (ResultSet rsUsage = psUsage.executeQuery()) {
                                while (rsUsage.next()) {
                                    String meterType = rsUsage.getString("meter_type");
                                    double totalUsage = rsUsage.getDouble("meter_usage");
                                    if ("electric".equals(meterType)) {
                                        totalElectricUsage += totalUsage; // Accumulate electric usage
                                    } else if ("water".equals(meterType)) {
                                        totalWaterUsage += totalUsage; // Accumulate water usage
                                    }
                                }
                            }
                        }

                        // Fetch billing for each door
                        String billingElectricitySql = "SELECT IFNULL(SUM(b.meter_bill), 0) AS totalElectricityBill "
                                + "FROM billings b INNER JOIN doors d ON b.door_id = d.door_id WHERE b.meter_type = 'electric' AND d.branch_id = ?";
                        String billingWaterSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalWaterBill "
                                + "FROM billings b INNER JOIN doors d ON b.door_id = d.door_id WHERE b.meter_type = 'water' AND d.branch_id = ?";
                        
                        try (PreparedStatement psEBill = conn.prepareStatement(billingElectricitySql)) {
                            psEBill.setInt(1, branchId);
                            try (ResultSet rsEBill = psEBill.executeQuery()) {
                                if (rsEBill.next()) {
                                    totalElectricBill = rsEBill.getDouble("totalElectricityBill");
                                }
                            }
                        }

                        // Calculate total water bill
                        try (PreparedStatement psWBill = conn.prepareStatement(billingWaterSql)) {
                            psWBill.setInt(1, branchId);
                            try (ResultSet rsWBill = psWBill.executeQuery()) {
                                if (rsWBill.next()) {
                                    totalWaterBill = rsWBill.getDouble("totalWaterBill");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }

        // Update labels with total usage and bills
        lblEUsage.setText(totalElectricUsage + " kwh");
        lblWusage.setText(totalWaterUsage + " m³");
        lblEBill.setText("PHP " + totalElectricBill);
        lblWBill.setText("PHP " + totalWaterBill);
    }
    
    private void loadUsage(int doorId) {
        double totalElectricUsage = 0;
        double totalWaterUsage = 0;
        double totalElectricBill = 0;
        double totalWaterBill = 0;

        try (Connection conn = DBConnection.Connect()) {
            // Retrieve meter usage for the door
            String usageSql = "SELECT meter_usage, meter_type "
                    + "FROM meters "
                    + "WHERE door_id = ? "
                    + "ORDER BY meter_type";
            try (PreparedStatement psUsage = conn.prepareStatement(usageSql)) {
                psUsage.setInt(1, doorId);
                try (ResultSet rsUsage = psUsage.executeQuery()) {
                    while (rsUsage.next()) {
                        String meterType = rsUsage.getString("meter_type");
                        double totalUsage = rsUsage.getDouble("meter_usage");
                        if ("electric".equals(meterType)) {
                            totalElectricUsage += totalUsage; // Accumulate electric usage
                        } else if ("water".equals(meterType)) {
                            totalWaterUsage += totalUsage; // Accumulate water usage
                        }
                    }
                }
            }

            // Retrieve billing information for the door
            String billingElectricitySql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalElectricityBill "
                    + "FROM billings WHERE meter_type = 'electric' AND door_id = ?";
            String billingWaterSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalWaterBill "
                    + "FROM billings WHERE meter_type = 'water' AND door_id = ?";

            try (PreparedStatement psEBill = conn.prepareStatement(billingElectricitySql); PreparedStatement psWBill = conn.prepareStatement(billingWaterSql)) {
                psEBill.setInt(1, doorId);
                psWBill.setInt(1, doorId);

                try (ResultSet rsEBill = psEBill.executeQuery(); ResultSet rsWBill = psWBill.executeQuery()) {
                    if (rsEBill.next()) {
                        totalElectricBill = rsEBill.getDouble("totalElectricityBill");
                    }
                    if (rsWBill.next()) {
                        totalWaterBill = rsWBill.getDouble("totalWaterBill");
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }

        // Update labels with total usage and bills
        lblEUsage.setText(totalElectricUsage + " kwh");
        lblWusage.setText(totalWaterUsage + " m³");
        lblEBill.setText("PHP " + totalElectricBill);
        lblWBill.setText("PHP " + totalWaterBill);
    }
    
    private void loadAddressData() {
        loadCities(); // Load cities when the form is initialized
    }

    private void loadCities() {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT city FROM address";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cityComboBox.addItem(rs.getString("city"));
                }
            }

            // Add action listener to cityComboBox
            cityComboBox.addActionListener(e -> loadMunicipalities((String) cityComboBox.getSelectedItem()));
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void loadMunicipalities(String city) {
        municipalityComboBox.removeAllItems(); // Clear previous items
        barangayComboBox.removeAllItems(); // Clear barangays when city changes

        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT municipality FROM address WHERE city = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, city);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        municipalityComboBox.addItem(rs.getString("municipality"));
                    }
                }
            }

            // Add action listener to municipalityComboBox
            municipalityComboBox.addActionListener(e -> loadBarangays((String) municipalityComboBox.getSelectedItem()));
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void loadBarangays(String municipality) {
        barangayComboBox.removeAllItems(); // Clear previous items

        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT barangay FROM address WHERE municipality = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, municipality);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        barangayComboBox.addItem(rs.getString("barangay"));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void addRoom(int branchId) {
        try {
            // Prompt the user for the door number and availability
            String doorNumberStr = JOptionPane.showInputDialog(this, "Enter the door number:");
            int doorNumber;
            try {
                doorNumber = Integer.parseInt(doorNumberStr);
            } catch (NumberFormatException e) {
                showErrorMessage("Invalid door number. Please enter a valid integer.");
                return; // Exit the method if the input is invalid
            }

            // Insert the new room into the doors table
            try (Connection conn = DBConnection.Connect()) {
                String insertRoomSql = "INSERT INTO doors (branch_id, door_number, available) VALUES (?, ?, 4)";
                try (PreparedStatement ps = conn.prepareStatement(insertRoomSql)) {
                    ps.setInt(1, branchId);
                    ps.setInt(2, doorNumber);
                    ps.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Room added successfully.");
            loadDoors(branchId); // Refresh the doors table for the selected branch
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void addBranch(String city, String municipality, String barangay, int numberOfRooms) {
        try (Connection conn = DBConnection.Connect()) {
            // Get the address ID based on the selected city, municipality, and barangay
            int addressId = getAddressId(city, municipality, barangay);

            // Insert the branch
            String insertBranchSql = "INSERT INTO branches (address_id, user_id, created_at) VALUES (?, ?, NOW())";
            try (PreparedStatement ps = conn.prepareStatement(insertBranchSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, addressId);
                ps.setString(2, getLoggedInUserID()); // Assuming the user ID is obtained from the logged-in user
            ps.executeUpdate();

                // Get the generated branch ID
                int branchId = -1;
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        branchId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Inserting branch failed, no ID obtained.");
                    }
                }

                // Add the specified number of rooms
                for (int i = 0; i < numberOfRooms; i++) {
                    String insertRoomSql = "INSERT INTO doors (branch_id, door_number, available) VALUES (?, ?, ?)";
                    try (PreparedStatement psRoom = conn.prepareStatement(insertRoomSql)) {
                        psRoom.setInt(1, branchId);
                        psRoom.setInt(2, (i + 100) + 1); // Door number
                        psRoom.setInt(3, 4);
                        psRoom.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(this, "Branch and rooms added successfully.");
                loadBranches(); // Refresh the branches table
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private int getAddressId(String city, String municipality, String barangay) {
        int addressId = -1;
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT address_id FROM address WHERE city = ? AND municipality = ? AND barangay = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, city);
                ps.setString(2, municipality);
                ps.setString(3, barangay);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        addressId = rs.getInt("address_id");
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
        return addressId;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        navPanel = new javax.swing.JPanel();
        lblHome = new javax.swing.JLabel();
        lblLogout = new javax.swing.JLabel();
        lblAccounts = new javax.swing.JLabel();
        lblHome2 = new javax.swing.JLabel();
        lblReport = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        heroPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblBG = new javax.swing.JLabel();
        deetPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbBranches = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbDoors = new javax.swing.JTable();
        cardPanel9 = new javax.swing.JPanel();
        lblEBill = new javax.swing.JLabel();
        lblEUsage = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cardPanel3 = new javax.swing.JPanel();
        lblWBill = new javax.swing.JLabel();
        lblWusage = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(132, 176, 255));

        navPanel.setBackground(new java.awt.Color(249, 249, 249));

        lblHome.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/home-solid-24-black.png"))); // NOI18N
        lblHome.setText(" Home");
        lblHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblHomeMouseClicked(evt);
            }
        });

        lblLogout.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-out-regular-24.png"))); // NOI18N
        lblLogout.setText("Log out ");
        lblLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLogout.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLogoutMouseClicked(evt);
            }
        });

        lblAccounts.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblAccounts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-account-solid-24.png"))); // NOI18N
        lblAccounts.setText(" Accounts");
        lblAccounts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAccounts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAccountsMouseClicked(evt);
            }
        });

        lblHome2.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHome2.setForeground(new java.awt.Color(0, 128, 241));
        lblHome2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/business-solid-24 (1).png"))); // NOI18N
        lblHome2.setText(" Branches");
        lblHome2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblReport.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/report-solid-24.png"))); // NOI18N
        lblReport.setText(" Report");
        lblReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblReport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblReportMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout navPanelLayout = new javax.swing.GroupLayout(navPanel);
        navPanel.setLayout(navPanelLayout);
        navPanelLayout.setHorizontalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navPanelLayout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogout)
                    .addGroup(navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblAccounts, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addComponent(lblHome, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblHome2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        navPanelLayout.setVerticalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblHome)
                .addGap(91, 91, 91)
                .addComponent(lblAccounts)
                .addGap(18, 18, 18)
                .addComponent(lblHome2)
                .addGap(18, 18, 18)
                .addComponent(lblReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblLogout)
                .addGap(30, 30, 30))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        heroPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblTitle.setText("Branches");

        lblBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Background-Admin.png"))); // NOI18N

        javax.swing.GroupLayout heroPanelLayout = new javax.swing.GroupLayout(heroPanel);
        heroPanel.setLayout(heroPanelLayout);
        heroPanelLayout.setHorizontalGroup(
            heroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heroPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblBG, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        heroPanelLayout.setVerticalGroup(
            heroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heroPanelLayout.createSequentialGroup()
                .addComponent(lblBG, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, heroPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTitle)
                .addGap(11, 11, 11))
        );

        deetPanel.setBackground(new java.awt.Color(247, 247, 247));

        tbBranches.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbBranches.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Branch", "Admin"
            }
        ));
        tbBranches.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbBranches.setRowHeight(30);
        tbBranches.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBranchesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbBranches);

        tbDoors.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbDoors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Door Number", "Available"
            }
        ));
        tbDoors.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbDoors.setRowHeight(30);
        tbDoors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDoorsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbDoors);

        cardPanel9.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel9.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblEBill.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblEBill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEBill.setText("Electric Usage");

        lblEUsage.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblEUsage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEUsage.setText("0 kwh");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bolt-solid-resident.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel9Layout = new javax.swing.GroupLayout(cardPanel9);
        cardPanel9.setLayout(cardPanel9Layout);
        cardPanel9Layout.setHorizontalGroup(
            cardPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEUsage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        cardPanel9Layout.setVerticalGroup(
            cardPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(cardPanel9Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(lblEUsage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEBill, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        cardPanel3.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblWBill.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblWBill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWBill.setText("Water Usage");

        lblWusage.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblWusage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWusage.setText("0 m³");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/water-droplet.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel3Layout = new javax.swing.GroupLayout(cardPanel3);
        cardPanel3.setLayout(cardPanel3Layout);
        cardPanel3Layout.setHorizontalGroup(
            cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblWusage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblWBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        cardPanel3Layout.setVerticalGroup(
            cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel3Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(lblWusage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblWBill, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 16, Short.MAX_VALUE)))
                .addContainerGap())
        );

        btnDelete.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/trash-regular-24.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
        });
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/plus-regular-24.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddMouseClicked(evt);
            }
        });
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout deetPanelLayout = new javax.swing.GroupLayout(deetPanel);
        deetPanel.setLayout(deetPanelLayout);
        deetPanelLayout.setHorizontalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cardPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deetPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete))
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        deetPanelLayout.setVerticalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(cardPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(heroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deetPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addComponent(heroPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(navPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(navPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHomeMouseClicked
        SuperAdmin_DB adminDB = new SuperAdmin_DB();
        adminDB.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblHomeMouseClicked

    private void lblLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLogoutMouseClicked
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_lblLogoutMouseClicked

    private void lblAccountsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAccountsMouseClicked
        Super_Accounts accounts = new Super_Accounts();
        accounts.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblAccountsMouseClicked

    private void lblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReportMouseClicked
        Report report = new Report();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblReportMouseClicked

    private void tbBranchesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbBranchesMouseClicked
        int selectedRow = tbBranches.getSelectedRow();
        if (selectedRow != -1) {
            // Retrieve the branch ID from the first column (index 0)
            int branchId = (int) tbBranches.getValueAt(selectedRow, 0);
            loadDoors(branchId); // Load doors for the selected branch
        }
    }//GEN-LAST:event_tbBranchesMouseClicked

    private void tbDoorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDoorsMouseClicked
        int selectedRow = tbDoors.getSelectedRow();
        if (selectedRow != -1) {
            int doorId = (int) tbDoors.getValueAt(selectedRow, 0);
            loadUsage(doorId);
        }
    }//GEN-LAST:event_tbDoorsMouseClicked

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked

    }//GEN-LAST:event_btnDeleteMouseClicked

    private boolean checkIfBranchHasTenants(int branchId) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT COUNT(*) FROM profiles WHERE door_id IN (SELECT door_id FROM doors WHERE branch_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, branchId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if there are tenants
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
        return false;
    }

    private void deleteBranch(int branchId) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "DELETE FROM branches WHERE branch_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, branchId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Branch deleted successfully.");
                loadBranches(); // Refresh the branches table
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private boolean checkIfRoomHasTenants(int doorId) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT COUNT(*) FROM profiles WHERE door_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doorId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if there are tenants
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
        return false;
    }

    private void deleteRoom(int doorId) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "DELETE FROM doors WHERE door_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doorId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Room deleted successfully.");
                loadDoors(doorId); // Refresh the doors table
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String[] options = {"Delete Branch", "Delete Room"};
        int choice = JOptionPane.showOptionDialog(this, "What would you like to delete?", "Delete Option",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Delete Branch
            int selectedRow = tbBranches.getSelectedRow();
            if (selectedRow != -1) {
                int branchId = (int) tbBranches.getValueAt(selectedRow, 0);
                if (checkIfBranchHasTenants(branchId)) {
                    showErrorMessage("Cannot delete branch with existing tenants.");
                } else {
                    deleteBranch(branchId);
                }
            } else {
                showErrorMessage("Please select a branch to delete.");
            }
        } else if (choice == 1) { // Delete Room
            int selectedRow = tbDoors.getSelectedRow();
            if (selectedRow != -1) {
                int doorId = (int) tbDoors.getValueAt(selectedRow, 0);
                if (checkIfRoomHasTenants(doorId)) {
                    showErrorMessage("Cannot delete room with existing tenants.");
                } else {
                    deleteRoom(doorId);
                }
            } else {
                showErrorMessage("Please select a room to delete.");
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddMouseClicked
    
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String[] options = {"Add Branch", "Add Room"};
        int choice = JOptionPane.showOptionDialog(this, "What would you like to add?", "Add Option",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // Add Branch
            // Initialize combo boxes
            cityComboBox = new JComboBox<>();
            municipalityComboBox = new JComboBox<>();
            barangayComboBox = new JComboBox<>();

            // Load address data into combo boxes
            loadAddressData();

            // Ask for the number of rooms
            String numberOfRoomsStr = JOptionPane.showInputDialog(this, "Enter the number of rooms to add:");
            int numberOfRooms;
            try {
                numberOfRooms = Integer.parseInt(numberOfRoomsStr);
            } catch (NumberFormatException e) {
                showErrorMessage("Invalid number of rooms. Please enter a valid integer.");
                return; // Exit the method if the input is invalid
            }

            Object[] message = {
                "Select City:", cityComboBox,
                "Select Municipality:", municipalityComboBox,
                "Select Barangay:", barangayComboBox
            };
            int option = JOptionPane.showConfirmDialog(this, message, "Add Branch", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String city = (String) cityComboBox.getSelectedItem();
                String municipality = (String) municipalityComboBox.getSelectedItem();
                String barangay = (String) barangayComboBox.getSelectedItem();

                // Add the branch to the database
                addBranch(city, municipality, barangay, numberOfRooms);
            }
        } else if (choice == 1) { // Add Room
            int selectedRow = tbBranches.getSelectedRow();
            if (selectedRow != -1) {
                int branchId = (int) tbBranches.getValueAt(selectedRow, 0);
                addRoom(branchId);
            } else {
                showErrorMessage("Please select a branch to add a room.");
            }
        }
    }//GEN-LAST:event_btnAddActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Branches.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Branches.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Branches.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Branches.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Branches().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JPanel cardPanel3;
    private javax.swing.JPanel cardPanel9;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel deetPanel;
    private javax.swing.JPanel heroPanel;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAccounts;
    private javax.swing.JLabel lblBG;
    private javax.swing.JLabel lblEBill;
    private javax.swing.JLabel lblEUsage;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblHome2;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWBill;
    private javax.swing.JLabel lblWusage;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTable tbBranches;
    private javax.swing.JTable tbDoors;
    // End of variables declaration//GEN-END:variables
}

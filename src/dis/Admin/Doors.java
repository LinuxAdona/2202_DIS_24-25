/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dis.Admin;

import obj.AvailabilityCellRenderer;
import javax.swing.JOptionPane;
import strt.Login;
import Database.DBConnection;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ADMIN
 */
public class Doors extends javax.swing.JFrame {

    /**
     * Creates new form Doors
     */
    public Doors() {
        initComponents();
        loadDoors();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getLoggedInUserID() {
        return Login.loggedInUserID;
    }
    
    private void loadDoors() {
        DefaultTableModel model = (DefaultTableModel) tbDoors.getModel();
        model.setRowCount(0);
        String userID = getLoggedInUserID();

        for (int i = 0; i < tbDoors.getColumnModel().getColumnCount(); i++) {
            tbDoors.getColumnModel().getColumn(i).setResizable(false);
        }
        
        tbDoors.setDefaultRenderer(Object.class, new AvailabilityCellRenderer());
        
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT d.door_id, d.door_number, d.available "
                    + "FROM doors d "
                    + "WHERE branch_id = (SELECT branch_id FROM branches WHERE user_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int doorID = rs.getInt("door_id");
                    int doorNumber = rs.getInt("door_number");
                    int available = rs.getInt("available");
                    String availability = (available == 0 || available < 0) ? "Unavailable" : "Available";
                    model.addRow(new Object[]{doorID, doorNumber, availability});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void loadResidents(int doorId) {
        DefaultTableModel model = (DefaultTableModel) tbResidents.getModel();
        model.setRowCount(0); // Clear existing data

        for (int i = 0; i < tbResidents.getColumnModel().getColumnCount(); i++) {
            tbResidents.getColumnModel().getColumn(i).setResizable(false);
        }

        double totalElectricUsage = 0;
        double totalWaterUsage = 0;
        double totalElectricBill = 0;
        double totalWaterBill = 0;

        try (Connection conn = DBConnection.Connect()) {
            // Load residents
            String sql = "SELECT u.user_id, p.first_name, p.last_name, p.contact_number "
                    + "FROM profiles p "
                    + "JOIN users u ON p.user_id = u.user_id "
                    + "WHERE p.door_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, doorId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String name = rs.getString("first_name") + " " + rs.getString("last_name");
                        String contact = rs.getString("contact_number");
                        model.addRow(new Object[]{userId, name, contact});

                        // Calculate usage for each resident
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
                                        totalElectricUsage = totalUsage;
                                    } else if ("water".equals(meterType)) {
                                        totalWaterUsage = totalUsage;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Calculate total bills for the door
            String billElectricSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalElectricityBill "
                    + "FROM billings WHERE meter_type = 'electric' AND door_id = ?";
            String billWaterSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalWaterBill "
                    + "FROM billings WHERE meter_type = 'water' AND door_id = ?";

            // Calculate total electric bill
            try (PreparedStatement psEBill = conn.prepareStatement(billElectricSql)) {
                psEBill.setInt(1, doorId);
                try (ResultSet rsEBill = psEBill.executeQuery()) {
                    if (rsEBill.next()) {
                        totalElectricBill = rsEBill.getDouble("totalElectricityBill");
                    }
                }
            }

            // Calculate total water bill
            try (PreparedStatement psWBill = conn.prepareStatement(billWaterSql)) {
                psWBill.setInt(1, doorId);
                try (ResultSet rsWBill = psWBill.executeQuery()) {
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

    private void loadUsage(int userID) {
        int residentCount = 0;
        try (Connection conn = DBConnection.Connect()) {
            
            String doorIdSql = "SELECT door_id FROM profiles WHERE user_id = ?";
            int doorID = -1;
            try (PreparedStatement psDoor = conn.prepareStatement(doorIdSql)) {
                psDoor.setInt(1, userID);
                ResultSet rsDoor = psDoor.executeQuery();
                if (rsDoor.next()) {
                    doorID = rsDoor.getInt("door_id");
                }
            }

            if (doorID == -1) {
                showErrorMessage("No door found for this user.");
                return;
            }
            
            String residentSql = "SELECT COUNT(*) AS resident_count FROM profiles WHERE door_id = ?";
            try (PreparedStatement psResident = conn.prepareStatement(residentSql)) {
                psResident.setInt(1, doorID);
                ResultSet rsResident = psResident.executeQuery();
                if (rsResident.next()) {
                    residentCount = rsResident.getInt("resident_count");
                }
            }

            if (residentCount == 0) {
                showErrorMessage("No residents found for this door. Cannot add readings.");
                return;
            }

            // Retrieve meter usage for the door
            String usageSql = "SELECT meter_usage, meter_type "
                    + "FROM meters "
                    + "WHERE door_id = ? "
                    + "ORDER BY meter_type";
            try (PreparedStatement psUsage = conn.prepareStatement(usageSql)) {
                psUsage.setInt(1, doorID);
                try (ResultSet rsUsage = psUsage.executeQuery()) {
                    while (rsUsage.next()) {
                        String meterType = rsUsage.getString("meter_type");
                        double totalUsage = rsUsage.getDouble("meter_usage") / residentCount;
                        if ("electric".equals(meterType)) {
                            lblEUsage.setText(totalUsage + " kwh");
                        } else if ("water".equals(meterType)) {
                            lblWusage.setText(totalUsage + " m³");
                        }
                    }
                }
            }

            // Retrieve billing information for the user
            String billingElectricitySql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalElectricityBill "
                    + "FROM billings WHERE meter_type = 'electric' AND user_id = ?";
            String billingWaterSql = "SELECT IFNULL(SUM(meter_bill), 0) AS totalWaterBill "
                    + "FROM billings WHERE meter_type = 'water' AND user_id = ?";

            try (PreparedStatement psEBill = conn.prepareStatement(billingElectricitySql); PreparedStatement psWBill = conn.prepareStatement(billingWaterSql)) {
                psEBill.setInt(1, userID);
                psWBill.setInt(1, userID);

                try (ResultSet rsEBill = psEBill.executeQuery(); ResultSet rsWBill = psWBill.executeQuery()) {
                    if (rsEBill.next()) {
                        lblEBill.setText("PHP " + rsEBill.getString("totalElectricityBill"));
                    }
                    if (rsWBill.next()) {
                        lblWBill.setText("PHP " + rsWBill.getString("totalWaterBill"));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
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
        tbDoors = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbResidents = new javax.swing.JTable();
        cardPanel9 = new javax.swing.JPanel();
        lblEBill = new javax.swing.JLabel();
        lblEUsage = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cardPanel3 = new javax.swing.JPanel();
        lblWBill = new javax.swing.JLabel();
        lblWusage = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnReading = new javax.swing.JButton();
        lbl = new javax.swing.JLabel();
        txtEusage = new javax.swing.JTextField();
        lblw = new javax.swing.JLabel();
        txtWusage = new javax.swing.JTextField();

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
        lblHome2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/door-open-solid-24-blue.png"))); // NOI18N
        lblHome2.setText(" Doors");
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
        lblTitle.setText("Doors");

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

        tbDoors.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbDoors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Door No.", "Available"
            }
        ));
        tbDoors.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbDoors.setRowHeight(30);
        tbDoors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDoorsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbDoors);

        tbResidents.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbResidents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Name", "Contact"
            }
        ));
        tbResidents.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbResidents.setRowHeight(30);
        tbResidents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbResidentsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbResidents);

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
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
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

        btnReading.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnReading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/plus-circle-regular-24.png"))); // NOI18N
        btnReading.setText(" Add Reading");
        btnReading.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnReadingMouseClicked(evt);
            }
        });
        btnReading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReadingActionPerformed(evt);
            }
        });

        lbl.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lbl.setText("Electricity:");

        txtEusage.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtEusage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEusageActionPerformed(evt);
            }
        });

        lblw.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblw.setText("Water:");

        txtWusage.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

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
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(lbl)
                        .addGap(6, 6, 6)
                        .addComponent(txtEusage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(lblw)
                        .addGap(6, 6, 6)
                        .addComponent(txtWusage, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnReading, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                        .addComponent(cardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(deetPanelLayout.createSequentialGroup()
                                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(deetPanelLayout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(lbl, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(deetPanelLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(txtEusage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(deetPanelLayout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(lblw, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(deetPanelLayout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(txtWusage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(2, 2, 2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deetPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())))))
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
        Admin_DB adminDB = new Admin_DB();
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
        Accounts accounts = new Accounts();
        accounts.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblAccountsMouseClicked

    private void btnReadingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReadingActionPerformed
        int selectedDoorRow = tbDoors.getSelectedRow();
        int userID = Integer.parseInt(getLoggedInUserID());
        if (selectedDoorRow != -1) {
            int doorID = (int) tbDoors.getValueAt(selectedDoorRow, 0);

            // Get the current values from the text fields
            double electricUsage = Double.parseDouble(txtEusage.getText());
            double waterUsage = Double.parseDouble(txtWusage.getText());

            // Retrieve residents for the selected door
            int residentCount = 0;
            double totalElectricCost = 0;
            double totalWaterCost = 0;

            try (Connection conn = DBConnection.Connect()) {
                String residentSql = "SELECT COUNT(*) AS resident_count FROM profiles WHERE door_id = ?";
                try (PreparedStatement psResident = conn.prepareStatement(residentSql)) {
                    psResident.setInt(1, doorID);
                    ResultSet rsResident = psResident.executeQuery();
                    if (rsResident.next()) {
                        residentCount = rsResident.getInt("resident_count");
                    }
                }

                if (residentCount == 0) {
                    showErrorMessage("No residents found for this door. Cannot add readings.");
                    return;
                }

                // Calculate costs
                double electricRate = 11.50; // PHP per kWh
                double waterRate = 2.00; // PHP per cubic meter
                totalElectricCost = (electricUsage * electricRate) / residentCount;
                totalWaterCost = (waterUsage * waterRate) / residentCount;

                // Add or update the new readings in the meters table
                String insertSql = "INSERT INTO meters (door_id, meter_type, meter_usage, reading_date) VALUES (?, ?, ?, CURDATE())";
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setInt(1, doorID);
                    psInsert.setString(2, "electric");
                    psInsert.setDouble(3, electricUsage);
                    psInsert.executeUpdate();

                    psInsert.setInt(1, doorID);
                    psInsert.setString(2, "water");
                    psInsert.setDouble(3, waterUsage);
                    psInsert.executeUpdate();
                }

                // Distribute billing costs among residents
                String billingSql = "INSERT INTO billings (door_id, user_id, rent, meter_type, meter_bill, due_date, status) "
                        + "SELECT ?, user_id, ?, ?, ?, CURDATE() + INTERVAL 30 DAY, 'unpaid' "
                        + "FROM profiles WHERE door_id = ?";
                try (PreparedStatement psBilling = conn.prepareStatement(billingSql)) {
                    psBilling.setInt(1, doorID);
                    psBilling.setDouble(2, (2000 / residentCount) / 2);
                    psBilling.setString(3, "electric");
                    psBilling.setDouble(4, totalElectricCost);
                    psBilling.setInt(5, doorID);
                    psBilling.executeUpdate();

                    psBilling.setInt(1, doorID);
                    psBilling.setDouble(2, (2000 / residentCount) / 2);
                    psBilling.setString(3, "water");
                    psBilling.setDouble(4, totalWaterCost);
                    psBilling.setInt(5, doorID);
                    psBilling.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Readings and billing added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsage(userID); // Optionally load usage for the door
            } catch (SQLException e) {
                showErrorMessage("Database error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a door to add readings.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnReadingActionPerformed

    private void tbDoorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDoorsMouseClicked
        int selectedRow = tbDoors.getSelectedRow();
        if (selectedRow != -1) {
            int doorId = (int) tbDoors.getValueAt(selectedRow, 0);
            loadResidents(doorId);
        }
    }//GEN-LAST:event_tbDoorsMouseClicked

    private void tbResidentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbResidentsMouseClicked
        int selectedRow = tbResidents.getSelectedRow();
        if (selectedRow != -1) {
            int userID = (int) tbResidents.getValueAt(selectedRow, 0);
            loadUsage(userID);
        }
    }//GEN-LAST:event_tbResidentsMouseClicked

    private void btnReadingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReadingMouseClicked
        
    }//GEN-LAST:event_btnReadingMouseClicked

    private void txtEusageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEusageActionPerformed
        
    }//GEN-LAST:event_txtEusageActionPerformed

    private void lblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReportMouseClicked
        Report report = new Report();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblReportMouseClicked

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
            java.util.logging.Logger.getLogger(Doors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Doors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Doors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Doors.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Doors().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReading;
    private javax.swing.JPanel cardPanel3;
    private javax.swing.JPanel cardPanel9;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel deetPanel;
    private javax.swing.JPanel heroPanel;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl;
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
    private javax.swing.JLabel lblw;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTable tbDoors;
    private javax.swing.JTable tbResidents;
    private javax.swing.JTextField txtEusage;
    private javax.swing.JTextField txtWusage;
    // End of variables declaration//GEN-END:variables
}

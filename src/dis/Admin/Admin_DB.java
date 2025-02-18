/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dis.Admin;

import Database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Admin_DB extends javax.swing.JFrame {

    /**
     * Creates new form Resident_DB
     */
    public Admin_DB() {
        initComponents();
        welcomeMsg();
        loadCards();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private String getLoggedInUserID() {
        return Login.loggedInUserID;
    }
    
    private void welcomeMsg() {
        String userID = getLoggedInUserID();
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT p.first_name, p.last_name "
                    + "FROM profiles p "
                    + "LEFT JOIN users u ON u.user_id = p.user_id "
                    + "WHERE p.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String fName = rs.getString("first_name");
                    String lName = rs.getString("last_name");

                    lblName.setText(fName + " " + lName + "!");
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void loadCards() {
        try (Connection conn = DBConnection.Connect()) {
            String rSql = "SELECT get_occupied_units_by_branch((SELECT branch_id FROM branches WHERE user_id = ?)) AS totalOccupiedUnits";
            String dSql = "SELECT get_available_rooms_by_branch((SELECT branch_id FROM branches WHERE user_id = ?)) AS totalAvailableRooms";
            String sqlEu = "SELECT IFNULL(SUM(m.meter_usage), 0) AS totalElectricityUsage FROM meters m "
                    + "INNER JOIN doors d ON d.door_id = m.door_id "
                    + "WHERE m.meter_type = 'electric' AND d.branch_id = (SELECT branch_id FROM branches WHERE user_id = ?)";
            String sqlEb = "SELECT IFNULL(SUM(b.meter_bill), 0) AS totalElectricityBill FROM billings b "
                    + "INNER JOIN doors d ON b.door_id = d.door_id "
                    + "WHERE b.meter_type = 'electric' AND d.branch_id = (SELECT branch_id FROM branches WHERE user_id = ?)";
            String sqlWb = "SELECT IFNULL(SUM(b.meter_bill), 0) AS totalWaterBill FROM billings b "
                    + "INNER JOIN doors d ON b.door_id = d.door_id "
                    + "WHERE b.meter_type = 'water' AND d.branch_id = (SELECT branch_id FROM branches WHERE user_id = ?)";
            String sqlWu = "SELECT IFNULL(SUM(m.meter_usage), 0) AS totalWaterUsage FROM meters m "
                    + "INNER JOIN doors d ON d.door_id = m.door_id "
                    + "WHERE m.meter_type = 'water' AND d.branch_id = (SELECT branch_id FROM branches WHERE user_id = ?)";
            try (PreparedStatement psR = conn.prepareStatement(rSql); 
                    PreparedStatement psD = conn.prepareStatement(dSql); 
                    PreparedStatement psE = conn.prepareStatement(sqlEu); 
                    PreparedStatement psEb = conn.prepareStatement(sqlEb); 
                    PreparedStatement psWb = conn.prepareStatement(sqlWb); 
                    PreparedStatement psW = conn.prepareStatement(sqlWu)) {
                String userID = getLoggedInUserID();
                psR.setString(1, userID);
                psD.setString(1, userID);
                psW.setString(1, userID);
                psE.setString(1, userID);
                psEb.setString(1, userID);
                psWb.setString(1, userID);
                ResultSet rsD = psD.executeQuery(); 
                ResultSet rsR = psR.executeQuery();
                ResultSet rsE = psE.executeQuery();
                ResultSet rsW = psW.executeQuery();
                ResultSet rsEb = psEb.executeQuery();
                ResultSet rsWb = psWb.executeQuery();

                if (rsR.next()) {
                    lblResidents.setText(rsR.getString("totalOccupiedUnits"));
                }

                if (rsD.next()) {
                    lblDoors.setText(rsD.getString("totalAvailableRooms"));
                }

                if (rsE.next()) {
                    lblElectricUsage.setText(rsE.getString("totalElectricityUsage") + " kwh");
                }

                if (rsW.next()) {
                    lblWaterUsage.setText(rsW.getString("totalWaterUsage") + " m³");
                }

                if (rsWb.next()) {
                    lblWaterBill.setText("PHP " + rsWb.getString("totalWaterBill"));
                }

                if (rsEb.next()) {
                    lblElecBill.setText("PHP " + rsEb.getString("totalElectricityBill"));
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
        lblDoorPage = new javax.swing.JLabel();
        lblReport = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        heroPanel = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblBG = new javax.swing.JLabel();
        deetPanel = new javax.swing.JPanel();
        cardPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblResidents = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cardPanel1 = new javax.swing.JPanel();
        lblElecBill = new javax.swing.JLabel();
        lblElectricUsage = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cardPanel3 = new javax.swing.JPanel();
        lblWaterBill = new javax.swing.JLabel();
        lblWaterUsage = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cardPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblDoors = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(132, 176, 255));

        navPanel.setBackground(new java.awt.Color(249, 249, 249));

        lblHome.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHome.setForeground(new java.awt.Color(0, 128, 241));
        lblHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/home-solid-24.png"))); // NOI18N
        lblHome.setText(" Home");
        lblHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

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

        lblDoorPage.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblDoorPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/door-open-solid-24.png"))); // NOI18N
        lblDoorPage.setText(" Doors");
        lblDoorPage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDoorPage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDoorPageMouseClicked(evt);
            }
        });

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
                        .addComponent(lblDoorPage, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(lblDoorPage)
                .addGap(18, 18, 18)
                .addComponent(lblReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblLogout)
                .addGap(30, 30, 30))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        heroPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblWelcome.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        lblWelcome.setText("Welcome,");

        lblName.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblName.setText("Name!");

        lblBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/Background-Admin.png"))); // NOI18N

        javax.swing.GroupLayout heroPanelLayout = new javax.swing.GroupLayout(heroPanel);
        heroPanel.setLayout(heroPanelLayout);
        heroPanelLayout.setHorizontalGroup(
            heroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heroPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(heroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName)
                    .addComponent(lblWelcome))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblBG, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        heroPanelLayout.setVerticalGroup(
            heroPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(heroPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblWelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblBG, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        deetPanel.setBackground(new java.awt.Color(247, 247, 247));

        cardPanel.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Residents");

        lblResidents.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblResidents.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResidents.setText("0");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/group-solid-168.png"))); // NOI18N

        javax.swing.GroupLayout cardPanelLayout = new javax.swing.GroupLayout(cardPanel);
        cardPanel.setLayout(cardPanelLayout);
        cardPanelLayout.setHorizontalGroup(
            cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblResidents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        cardPanelLayout.setVerticalGroup(
            cardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(cardPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(lblResidents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cardPanel1.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblElecBill.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblElecBill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElecBill.setText("Electric Bill");

        lblElectricUsage.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        lblElectricUsage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblElectricUsage.setText("0 kwh");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bolt-solid-180.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel1Layout = new javax.swing.GroupLayout(cardPanel1);
        cardPanel1.setLayout(cardPanel1Layout);
        cardPanel1Layout.setHorizontalGroup(
            cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblElectricUsage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblElecBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        cardPanel1Layout.setVerticalGroup(
            cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblElectricUsage)
                .addGap(18, 18, 18)
                .addComponent(lblElecBill)
                .addGap(49, 49, 49))
        );

        cardPanel3.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblWaterBill.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblWaterBill.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWaterBill.setText("Water Bill");

        lblWaterUsage.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        lblWaterUsage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWaterUsage.setText("0 m³");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/droplet-solid-180.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel3Layout = new javax.swing.GroupLayout(cardPanel3);
        cardPanel3.setLayout(cardPanel3Layout);
        cardPanel3Layout.setHorizontalGroup(
            cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel3Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblWaterUsage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblWaterBill, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        cardPanel3Layout.setVerticalGroup(
            cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblWaterUsage)
                .addGap(18, 18, 18)
                .addComponent(lblWaterBill)
                .addGap(56, 56, 56))
        );

        cardPanel4.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Available");

        lblDoors.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblDoors.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDoors.setText("0");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/room-available.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel4Layout = new javax.swing.GroupLayout(cardPanel4);
        cardPanel4.setLayout(cardPanel4Layout);
        cardPanel4Layout.setHorizontalGroup(
            cardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDoors, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );
        cardPanel4Layout.setVerticalGroup(
            cardPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDoors)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(43, 43, 43))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout deetPanelLayout = new javax.swing.GroupLayout(deetPanel);
        deetPanel.setLayout(deetPanelLayout);
        deetPanelLayout.setHorizontalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        deetPanelLayout.setVerticalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
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
                .addComponent(deetPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void lblDoorPageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDoorPageMouseClicked
        Doors doors = new Doors();
        doors.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblDoorPageMouseClicked

    private void lblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReportMouseClicked
        Report report = new Report();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblReportMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardPanel;
    private javax.swing.JPanel cardPanel1;
    private javax.swing.JPanel cardPanel3;
    private javax.swing.JPanel cardPanel4;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel deetPanel;
    private javax.swing.JPanel heroPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblAccounts;
    private javax.swing.JLabel lblBG;
    private javax.swing.JLabel lblDoorPage;
    private javax.swing.JLabel lblDoors;
    private javax.swing.JLabel lblElecBill;
    private javax.swing.JLabel lblElectricUsage;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblResidents;
    private javax.swing.JLabel lblWaterBill;
    private javax.swing.JLabel lblWaterUsage;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    // End of variables declaration//GEN-END:variables
}

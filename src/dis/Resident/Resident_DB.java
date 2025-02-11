/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dis.Resident;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import Database.DBConnection;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Resident_DB extends javax.swing.JFrame {

    /**
     * Creates new form Resident_DB
     */
    public Resident_DB() {
        initComponents();
        welcomeMsg();
        displayPendingPaymentsAndDormitoryDetails();
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
            String sql = "SELECT p.first_name, d.door_number, p.profile_picture "
                    + "FROM profiles p "
                    + "LEFT JOIN users u ON u.user_id = p.user_id "
                    + "LEFT JOIN doors d ON d.door_id = p.door_id "
                    + "WHERE p.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String fName = rs.getString("first_name");
                    String doorNo = rs.getString("door_number");
                    
                    lblName.setText(fName + "!");
                    if (doorNo == null) {
                        lblDoor.setText("Door N/A");
                    } else {
                        lblDoor.setText("Door " + doorNo);
                    }
                    byte[] imgBytes = rs.getBytes("profile_picture");
                    if (imgBytes != null) {
                        ImageIcon profilePicture = new ImageIcon(imgBytes);
                        lblPfp.setIcon(profilePicture);
                    } else {
                        lblPfp.setIcon(new ImageIcon(getClass().getResource("/assets/default-profile.png")));
                    }
                }
            }
            String unreadNotificationsSql = "SELECT COUNT(*) AS unread_count FROM notifications "
                    + "WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?) AND status = 'unread'";
            try (PreparedStatement unreadPs = conn.prepareStatement(unreadNotificationsSql)) {
                unreadPs.setString(1, userID);
                ResultSet unreadRs = unreadPs.executeQuery();
                if (unreadRs.next() && unreadRs.getInt("unread_count") > 0) {
                    lblNotif.setIcon(new ImageIcon(getClass().getResource("/assets/bell-ring-solid-24.png"))); // Change to unread icon
                } else {
                    lblNotif.setIcon(new ImageIcon(getClass().getResource("/assets/bell-solid-24.png"))); // Default icon
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void loadCards() {
        String userID = getLoggedInUserID();
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            // Get the resident_id for the logged-in user
            String residentIdSql = "SELECT resident_id FROM residents WHERE user_id = ?";
            int residentId = -1; // Default value if not found
            try (PreparedStatement residentPs = conn.prepareStatement(residentIdSql)) {
                residentPs.setString(1, userID);
                ResultSet residentRs = residentPs.executeQuery();
                if (residentRs.next()) {
                    residentId = residentRs.getInt("resident_id");
                } else {
                    showErrorMessage("Resident ID not found for the logged-in user.");
                    return;
                }
            }

            // Now get the electric and water usage for the specific resident
            String eSql = "SELECT IFNULL(SUM(meter_usage), 0) AS electric_usage FROM meters WHERE meter_type = 'electric' AND resident_id = ?";
            String wSql = "SELECT IFNULL(SUM(meter_usage), 0) AS water_usage FROM meters WHERE meter_type = 'water' AND resident_id = ?";

            try (PreparedStatement psE = conn.prepareStatement(eSql); PreparedStatement psW = conn.prepareStatement(wSql)) {

                psE.setInt(1, residentId);
                psW.setInt(1, residentId);

                try (ResultSet rsE = psE.executeQuery(); ResultSet rsW = psW.executeQuery()) {

                    if (rsE.next()) {
                        lblEusage.setText(rsE.getString("electric_usage") + " kwh");
                    }

                    if (rsW.next()) {
                        lblWusage.setText(rsW.getString("water_usage") + " m³");
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void displayPendingPaymentsAndDormitoryDetails() {
        String userID = getLoggedInUserID();
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

            // Fetch pending payments
            String billingSql = "SELECT rent, water_usage, electric_usage, total_due, due_date, status "
                    + "FROM billings "
                    + "WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?) "
                    + "AND status = 'unpaid'";
            try (PreparedStatement billingPs = conn.prepareStatement(billingSql)) {
                billingPs.setString(1, userID);
                ResultSet billingRs = billingPs.executeQuery();

                // Check if there are pending payments
                if (billingRs.next()) {
                    // If there are pending payments, retrieve the values
                    double rent = billingRs.getDouble("rent");
                    double waterUsage = billingRs.getDouble("water_usage");
                    double electricUsage = billingRs.getDouble("electric_usage");
                    double total_due = billingRs.getDouble("total_due");
                    java.sql.Date dueDateSql = billingRs.getDate("due_date");
                    String formattedDueDate = dateFormat.format(dueDateSql);

                    // Set the text fields with the retrieved values
                    txtRent.setText("PHP " + rent);
                    txtWater.setText("PHP " + waterUsage);
                    txtElectric.setText("PHP " + electricUsage);
                    txtTotal.setText("PHP " + total_due);
                    txtDueDate.setText(formattedDueDate);

                    // Check if the due date has passed
                    if (dueDateSql.before(new java.sql.Date(System.currentTimeMillis()))) {
                        // Create a notification
                        String notificationMessage = "Your payment is overdue! Total due: PHP " + total_due;
                        createNotification(conn, notificationMessage);
                    }
                } else {
                    // If no pending payments, set fields to N/A
                    txtRent.setText("N/A");
                    txtWater.setText("N/A");
                    txtElectric.setText("N/A");
                    txtTotal.setText("N/A");
                    txtDueDate.setText("N/A");
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void createNotification(Connection conn, String message) throws SQLException {
        String insertNotificationSql = "INSERT INTO notifications (resident_id, message, status) VALUES "
                + "((SELECT resident_id FROM residents WHERE user_id = ?), ?, 'unread')";
        try (PreparedStatement insertNotificationPs = conn.prepareStatement(insertNotificationSql)) {
            insertNotificationPs.setString(1, getLoggedInUserID()
            );
        insertNotificationPs.setString(2, message);
            insertNotificationPs.executeUpdate();
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
        lblHistory = new javax.swing.JLabel();
        lblNotif = new javax.swing.JLabel();
        lblLogout = new javax.swing.JLabel();
        lblProfile = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDoor = new javax.swing.JLabel();
        btmPanel = new javax.swing.JPanel();
        lblElectric = new javax.swing.JLabel();
        txtElectric = new javax.swing.JTextField();
        lblTotal1 = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JTextField();
        btnPayment = new javax.swing.JButton();
        lblTotal2 = new javax.swing.JLabel();
        txtRent = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        lblTotal4 = new javax.swing.JLabel();
        txtWater = new javax.swing.JTextField();
        cardPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblEusage = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cardPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblWusage = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblPfp = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(249, 249, 249));

        mainPanel.setBackground(new java.awt.Color(132, 176, 255));

        navPanel.setBackground(new java.awt.Color(247, 247, 247));

        lblHome.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHome.setForeground(new java.awt.Color(0, 128, 241));
        lblHome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/home-solid-24.png"))); // NOI18N
        lblHome.setText(" Home");
        lblHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblHistory.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHistory.setText(" History");
        lblHistory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblHistoryMouseClicked(evt);
            }
        });

        lblNotif.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        lblNotif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bell-solid-24.png"))); // NOI18N
        lblNotif.setToolTipText("Notifications");
        lblNotif.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblNotif.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNotifMouseClicked(evt);
            }
        });

        lblLogout.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        lblLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/door-open-solid-24.png"))); // NOI18N
        lblLogout.setToolTipText("Log Out");
        lblLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLogout.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLogoutMouseClicked(evt);
            }
        });

        lblProfile.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-circle-solid-24.png"))); // NOI18N
        lblProfile.setText(" Profile");
        lblProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblProfile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblProfileMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout navPanelLayout = new javax.swing.GroupLayout(navPanel);
        navPanel.setLayout(navPanelLayout);
        navPanelLayout.setHorizontalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblHome)
                .addGap(18, 18, 18)
                .addComponent(lblHistory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblProfile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNotif)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLogout)
                .addGap(22, 22, 22))
        );
        navPanelLayout.setVerticalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNotif, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProfile, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(lblHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblWelcome.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        lblWelcome.setText("Welcome to Solid-State Dorm,");

        lblName.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblName.setText("Name!");

        lblDoor.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblDoor.setText("Door Number");
        lblDoor.setToolTipText("Room Number");

        btmPanel.setBackground(new java.awt.Color(247, 247, 247));
        btmPanel.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        lblElectric.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblElectric.setText("Electric Bill:");

        txtElectric.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtElectric.setFocusable(false);

        lblTotal1.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblTotal1.setText("Due Date:");

        txtDueDate.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtDueDate.setFocusable(false);

        btnPayment.setBackground(new java.awt.Color(154, 164, 255));
        btnPayment.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        btnPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/credit-card-regular-24.png"))); // NOI18N
        btnPayment.setText(" Pay Bills");
        btnPayment.setToolTipText("Request Maintenance");
        btnPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPaymentActionPerformed(evt);
            }
        });

        lblTotal2.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblTotal2.setText("Rent:");

        txtRent.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtRent.setFocusable(false);

        txtTotal.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtTotal.setFocusable(false);

        lblTotal.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblTotal.setText("Total Due:");

        lblTotal4.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblTotal4.setText("Water Bill:");

        txtWater.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtWater.setFocusable(false);
        txtWater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtWaterActionPerformed(evt);
            }
        });

        cardPanel1.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Electric Usage");

        lblEusage.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblEusage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEusage.setText("0 kwh");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bolt-solid-resident.png"))); // NOI18N

        javax.swing.GroupLayout cardPanel1Layout = new javax.swing.GroupLayout(cardPanel1);
        cardPanel1.setLayout(cardPanel1Layout);
        cardPanel1Layout.setHorizontalGroup(
            cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEusage, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        cardPanel1Layout.setVerticalGroup(
            cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(cardPanel1Layout.createSequentialGroup()
                        .addComponent(lblEusage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );

        cardPanel3.setBackground(new java.awt.Color(255, 255, 255));
        cardPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Water Usage");

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
                .addGap(10, 10, 10)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblWusage, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        cardPanel3Layout.setVerticalGroup(
            cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(cardPanel3Layout.createSequentialGroup()
                        .addComponent(lblWusage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addContainerGap())
        );

        javax.swing.GroupLayout btmPanelLayout = new javax.swing.GroupLayout(btmPanel);
        btmPanel.setLayout(btmPanelLayout);
        btmPanelLayout.setHorizontalGroup(
            btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btmPanelLayout.createSequentialGroup()
                .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(btmPanelLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(cardPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cardPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(btmPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(btmPanelLayout.createSequentialGroup()
                                .addComponent(lblElectric)
                                .addGap(0, 84, Short.MAX_VALUE))
                            .addComponent(txtElectric))
                        .addGap(18, 18, 18)
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotal4)
                            .addComponent(txtWater, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotal2)
                            .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(37, 37, 37)
                .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTotal1)
                    .addComponent(lblTotal)
                    .addComponent(txtDueDate)
                    .addComponent(txtTotal)
                    .addComponent(btnPayment, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btmPanelLayout.setVerticalGroup(
            btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btmPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(btmPanelLayout.createSequentialGroup()
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cardPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cardPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(btmPanelLayout.createSequentialGroup()
                                .addComponent(lblElectric)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtElectric, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(btmPanelLayout.createSequentialGroup()
                                    .addComponent(lblTotal4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtWater, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(btmPanelLayout.createSequentialGroup()
                                    .addComponent(lblTotal2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(btnPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(btmPanelLayout.createSequentialGroup()
                        .addComponent(lblTotal1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblPfp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/default-profile.png"))); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/background-resident.png"))); // NOI18N

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btmPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblPfp, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblName)
                    .addComponent(lblWelcome)
                    .addComponent(lblDoor))
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPfp, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(contentPanelLayout.createSequentialGroup()
                                .addComponent(lblWelcome)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblName)
                                .addGap(23, 23, 23)
                                .addComponent(lblDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(btmPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(navPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(navPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void txtWaterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtWaterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtWaterActionPerformed

    private void lblProfileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblProfileMouseClicked
        Profile profile = new Profile();
        profile.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblProfileMouseClicked

    private void btnPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaymentActionPerformed
        String userID = getLoggedInUserID();
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        // Get the total due amount from the text field
        String totalDueText = txtTotal.getText().replace("PHP ", "").trim();
        double totalDue = 0;
        try {
            totalDue = Double.parseDouble(totalDueText);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid total due amount.");
            return;
        }

        // Prompt for payment amount
        String paymentInput = JOptionPane.showInputDialog(this, "Enter the amount to pay:", "Payment", JOptionPane.QUESTION_MESSAGE);
        if (paymentInput == null) {
            return; // User canceled the input
        }

        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(paymentInput);
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid payment amount.");
            return;
        }

        if (paymentAmount <= 0) {
            showErrorMessage("Payment amount must be greater than zero.");
            return;
        }

        // Check payment conditions
        try (Connection conn = DBConnection.Connect()) {
            // Fetch current billing details for the logged-in user
            String billingSql = "SELECT rent, water_usage, electric_usage, due_date FROM billings WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            double rent = 0, waterUsage = 0, electricUsage = 0;
            java.sql.Date dueDate = null;

            try (PreparedStatement billingPs = conn.prepareStatement(billingSql)) {
                billingPs.setString(1, userID);
                ResultSet billingRs = billingPs.executeQuery();
                if (billingRs.next()) {
                    rent = billingRs.getDouble("rent");
                    waterUsage = billingRs.getDouble("water_usage");
                    electricUsage = billingRs.getDouble("electric_usage");
                    dueDate = billingRs.getDate("due_date");
                }
            }

            // Determine which options to show based on the payment amount
            List<String> optionsList = new ArrayList<>();
            if (paymentAmount <= rent) {
                optionsList.add("Rent");
            }
            if (paymentAmount <= waterUsage) {
                optionsList.add("Water Usage");
            }
            if (paymentAmount <= electricUsage) {
                optionsList.add("Electric Usage");
            }

            // If no options are available, inform the user
            if (optionsList.isEmpty() && paymentAmount != totalDue) {
                JOptionPane.showMessageDialog(this, "Payment amount exceeds all billing options. Please pay the total due amount.");
                return;
            }

            // Handle payment scenarios
            if (paymentAmount < totalDue) {
                // Ask user where to apply the payment
                String selectedOption = (String) JOptionPane.showInputDialog(this, "Where would you like to apply the payment?", "Payment Allocation", JOptionPane.QUESTION_MESSAGE, null, optionsList.toArray(), optionsList.get(0));

                if (selectedOption == null) {
                    return; // User canceled the selection
                }

                // Update the selected usage
                String updateUsageSql = "UPDATE billings SET ";
                if (selectedOption.equals("Rent")) {
                    updateUsageSql += "rent = rent - ? ";
                } else if (selectedOption.equals("Water Usage")) {
                    updateUsageSql += "water_usage = water_usage - ? ";
                } else if (selectedOption.equals("Electric Usage")) {
                    updateUsageSql += "electric_usage = electric_usage - ? ";
                }
                updateUsageSql += "WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";

                try (PreparedStatement updateUsagePs = conn.prepareStatement(updateUsageSql)) {
                    updateUsagePs.setDouble(1, paymentAmount);
                    updateUsagePs.setString(2, userID);
                    updateUsagePs.executeUpdate();
                }

                // Insert payment record
                String paymentSql = "INSERT INTO payments (resident_id, amount_paid, date_paid) VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), ?, CURDATE())";
                try (PreparedStatement paymentPs = conn.prepareStatement(paymentSql)) {
                    paymentPs.setString(1, userID);
                    paymentPs.setDouble(2, paymentAmount);
                    paymentPs.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Payment of PHP " + paymentAmount + " applied to " + selectedOption + ".");
            } else if (paymentAmount == totalDue) {
                // Update the existing billing record to set it as 'paid'
                String billingUpdateSql = "UPDATE billings SET status = 'paid' WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
                try (PreparedStatement billingPs = conn.prepareStatement(billingUpdateSql)) {
                    billingPs.setString(1, userID);
                    billingPs.executeUpdate();
                }

                // Add one month to the existing due date
                java.sql.Date newDueDate = new java.sql.Date(dueDate.getTime() + (30L * 24 * 60 * 60 * 1000)); // Adding 30 days

                // Insert a new billing record with 0 usages and the new due date
                String newBillingSql = "INSERT INTO billings (resident_id, rent, water_usage, electric_usage, due_date, status) "
                        + "VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), ?, 0, 0, ?, 'unpaid')";
                try (PreparedStatement newBillingPs = conn.prepareStatement(newBillingSql)) {
                    newBillingPs.setString(1, userID);
                    newBillingPs.setDouble(2, rent); // Keep the original rent
                    newBillingPs.setDate(3, newDueDate); // Use the new due date
                    newBillingPs.executeUpdate();
                }

                // Insert payment record
                String paymentSql = "INSERT INTO payments (resident_id, amount_paid, date_paid) VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), ?, CURDATE())";
                try (PreparedStatement paymentPs = conn.prepareStatement(paymentSql)) {
                    paymentPs.setString(1, userID);
                    paymentPs.setDouble(2, paymentAmount);
                    paymentPs.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Payment of PHP " + paymentAmount + " accepted. The previous billing record has been marked as 'paid', and a new record has been created with 0 usages.");
            } else if (paymentAmount > totalDue) {
                // Similar changes for this scenario as well
                // Update the existing billing record to set it as 'paid'
                String billingUpdateSql = "UPDATE billings SET status = 'paid' WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
                try (PreparedStatement billingPs = conn.prepareStatement(billingUpdateSql)) {
                    billingPs.setString(1, userID);
                    billingPs.executeUpdate();
                }

                // Add one month to the existing due date
                java.sql.Date newDueDate = new java.sql.Date(dueDate.getTime() + (30L * 24 * 60 * 60 * 1000)); // Adding 30 days

                // Insert a new billing record with 0 usages and the new due date
                String newBillingSql = "INSERT INTO billings (resident_id, rent, water_usage, electric_usage, due_date, status) "
                        + "VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), 2000, 0, 0, ?, 'unpaid')";
                try (PreparedStatement newBillingPs = conn.prepareStatement(newBillingSql)) {
                    newBillingPs.setString(1, userID);
                    newBillingPs.setDate(2, newDueDate); // Use the new due date
                    newBillingPs.executeUpdate();
                }

                // Insert payment record for the total due amount
                String paymentSql = "INSERT INTO payments (resident_id, amount_paid, date_paid) VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), ?, CURDATE())";
                try (PreparedStatement paymentPs = conn.prepareStatement(paymentSql)) {
                    paymentPs.setString(1, userID);
                    paymentPs.setDouble(2, totalDue); // Only accept the amount equal to the total due
                    paymentPs.executeUpdate();
                }

                // Calculate change
                double change = paymentAmount - totalDue;
                JOptionPane.showMessageDialog(this, "Payment of PHP " + totalDue + " accepted. Change: PHP " + change + ". All usages have been set to 0, and rent is now PHP 2000 with a new due date in 1 month.");
            }
            displayPendingPaymentsAndDormitoryDetails(); // Refresh the displayed information
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnPaymentActionPerformed

    private void lblHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHistoryMouseClicked
        History history = new History();
        history.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblHistoryMouseClicked

    private void showNotifications() {
        String userID = getLoggedInUserID();
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            String notificationsSql = "SELECT notif_id, message, notif_date, status FROM notifications "
                    + "WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            try (PreparedStatement notificationsPs = conn.prepareStatement(notificationsSql)) {
                notificationsPs.setString(1, userID);
                ResultSet notificationsRs = notificationsPs.executeQuery();

                // Create a table to display notifications
                String[] columnNames = {"Notification ID", "Message", "Date", "Status"};
                ArrayList<Object[]> data = new ArrayList<>();
                while (notificationsRs.next()) {
                    int notifId = notificationsRs.getInt("notif_id"); // Get notif_id as an integer
                    String message = notificationsRs.getString("message");
                    java.sql.Timestamp notifDate = notificationsRs.getTimestamp("notif_date");
                    String status = notificationsRs.getString("status");
                    data.add(new Object[]{notifId, message, notifDate, status}); // Store in the correct order
                }

                // Show the notifications in a dialog
                JTable notificationsTable = new JTable(data.toArray(new Object[0][]), columnNames);
                notificationsTable.setFillsViewportHeight(true);
                JScrollPane scrollPane = new JScrollPane(notificationsTable);

                // Add mouse listener to handle row clicks
                notificationsTable.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        int row = notificationsTable.getSelectedRow();
                        if (row != -1) {
                            // Get the notification ID from the data
                            int notifId = (int) data.get(row)[0]; // Assuming the first column is notif_id
                            markNotificationAsRead(conn, notifId);
                            
                            showNotifications(); // Refresh the notifications after marking as read
                        }
                    }
                });

                JOptionPane.showMessageDialog(this, scrollPane, "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }

    private void markNotificationAsRead(Connection conn, int notifId) {
        String updateNotificationSql = "UPDATE notifications SET status = 'read' WHERE notif_id = ?";
        try (PreparedStatement updateNotificationPs = conn.prepareStatement(updateNotificationSql)) {
            updateNotificationPs.setInt(1, notifId);
            updateNotificationPs.executeUpdate();
        } catch (SQLException e) {
            showErrorMessage("Failed to mark notification as read: " + e.getMessage());
        }
    }
    
    private void lblNotifMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNotifMouseClicked
        showNotifications();
    }//GEN-LAST:event_lblNotifMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btmPanel;
    private javax.swing.JButton btnPayment;
    private javax.swing.JPanel cardPanel1;
    private javax.swing.JPanel cardPanel3;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel lblDoor;
    private javax.swing.JLabel lblElectric;
    private javax.swing.JLabel lblEusage;
    private javax.swing.JLabel lblHistory;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNotif;
    private javax.swing.JLabel lblPfp;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JLabel lblTotal4;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JLabel lblWusage;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JTextField txtElectric;
    private javax.swing.JTextField txtRent;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtWater;
    // End of variables declaration//GEN-END:variables
}

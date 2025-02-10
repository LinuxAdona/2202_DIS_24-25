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
import obj.UserSession;
import Database.DBConnection;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
        System.out.println("User ID: " + userID);
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT p.first_name, p.last_name, d.door_number, p.profile_picture "
                    + "FROM profiles p "
                    + "LEFT JOIN users u ON u.user_id = p.user_id "
                    + "LEFT JOIN doors d ON d.door_id = p.door_id "
                    + "WHERE p.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String fName = rs.getString("first_name");
                    String lName = rs.getString("last_name");
                    String doorNo = rs.getString("door_number");
                    
                    lblName.setText(fName + "!");
                    if (doorNo == null) {
                        lblDoor.setText("Door #N/A");
                    } else {
                        lblDoor.setText("Door #" + doorNo);
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
            String billingSql = "SELECT rent, water_usage, electric_usage, total_due, due_date FROM billings WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?) AND status = 'unpaid'";
            try (PreparedStatement billingPs = conn.prepareStatement(billingSql)) {
                billingPs.setString(1, userID);
                ResultSet billingRs = billingPs.executeQuery();

                boolean hasPendingPayments = false;
                while (billingRs.next()) {
                    hasPendingPayments = true;
                    double rent = billingRs.getDouble("rent");
                    double waterUsage = billingRs.getDouble("water_usage");
                    double electricUsage = billingRs.getDouble("electric_usage");
                    double total_due = billingRs.getDouble("total_due");
                    java.sql.Date dueDateSql = billingRs.getDate("due_date");
                    String formattedDueDate = dateFormat.format(dueDateSql);

                    txtElectric.setEditable(false);
                    txtElectric.setBackground(Color.WHITE);
                    txtElectric.setDisabledTextColor(Color.BLACK);
                    
                    txtDueDate.setEditable(false);
                    txtDueDate.setBackground(Color.WHITE);
                    txtDueDate.setDisabledTextColor(Color.BLACK);
                    txtElectric.setDisabledTextColor(Color.BLACK);
                    
                    txtRent.setEditable(false);
                    txtRent.setBackground(Color.WHITE);
                    txtRent.setDisabledTextColor(Color.BLACK);
                    
                    txtWater.setEditable(false);
                    txtWater.setBackground(Color.WHITE);
                    txtWater.setDisabledTextColor(Color.BLACK);
                    
                    txtTotal.setEditable(false);
                    txtTotal.setBackground(Color.WHITE);
                    txtTotal.setDisabledTextColor(Color.BLACK);
                    
                    txtRent.setText("PHP " + rent);
                    txtWater.setText("PHP " + waterUsage);
                    txtTotal.setText("PHP " + total_due);
                    txtElectric.setText("PHP " + electricUsage);
                    txtDueDate.setText(formattedDueDate);
                }

                if (!hasPendingPayments) {
                    txtElectric.setText("N/A");
                    txtWater.setText("N/A");
                    txtRent.setText("N/A");
                    txtTotal.setText("No Pending Payment.");
                    txtDueDate.setText("N/A");
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
        lblHistory = new javax.swing.JLabel();
        lblNotif = new javax.swing.JLabel();
        lblLogout = new javax.swing.JLabel();
        lblSummary = new javax.swing.JLabel();
        lblProfile = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDoor = new javax.swing.JLabel();
        btmPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
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
        lblPfp = new javax.swing.JLabel();

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
        lblHistory.setText(" Payments");
        lblHistory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblNotif.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        lblNotif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/bell-solid-24.png"))); // NOI18N
        lblNotif.setToolTipText("Notifications");
        lblNotif.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

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

        lblSummary.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblSummary.setText(" Summary");
        lblSummary.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblProfile.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-circle-solid-24.png"))); // NOI18N
        lblProfile.setText(" Profile");
        lblProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout navPanelLayout = new javax.swing.GroupLayout(navPanel);
        navPanel.setLayout(navPanelLayout);
        navPanelLayout.setHorizontalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblHome)
                .addGap(18, 18, 18)
                .addComponent(lblHistory)
                .addGap(18, 18, 18)
                .addComponent(lblSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
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
                    .addComponent(lblSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel1.setText("Pending Payments:");

        lblElectric.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblElectric.setText("Electric Bill:");

        txtElectric.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        txtElectric.setFocusable(false);

        lblTotal1.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblTotal1.setText("Due Date:");

        txtDueDate.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        txtDueDate.setFocusable(false);

        btnPayment.setBackground(new java.awt.Color(154, 164, 255));
        btnPayment.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        btnPayment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/credit-card-regular-24.png"))); // NOI18N
        btnPayment.setText(" Pay Bills");
        btnPayment.setToolTipText("Request Maintenance");
        btnPayment.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblTotal2.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblTotal2.setText("Rent:");

        txtRent.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtRent.setFocusable(false);

        txtTotal.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtTotal.setFocusable(false);

        lblTotal.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
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

        javax.swing.GroupLayout btmPanelLayout = new javax.swing.GroupLayout(btmPanel);
        btmPanel.setLayout(btmPanelLayout);
        btmPanelLayout.setHorizontalGroup(
            btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btmPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(btmPanelLayout.createSequentialGroup()
                            .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtDueDate)
                                .addGroup(btmPanelLayout.createSequentialGroup()
                                    .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblElectric)
                                        .addComponent(lblTotal1))
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(txtElectric))
                            .addGap(18, 18, 18)
                            .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTotal)
                                .addGroup(btmPanelLayout.createSequentialGroup()
                                    .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                        .addComponent(lblTotal4, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtWater, javax.swing.GroupLayout.Alignment.LEADING))
                                    .addGap(18, 18, 18)
                                    .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTotal2)
                                        .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btmPanelLayout.setVerticalGroup(
            btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btmPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(btmPanelLayout.createSequentialGroup()
                        .addComponent(lblElectric)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtElectric, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTotal1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, btmPanelLayout.createSequentialGroup()
                        .addGroup(btmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(btmPanelLayout.createSequentialGroup()
                                .addComponent(lblTotal4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtWater, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(btmPanelLayout.createSequentialGroup()
                                .addComponent(lblTotal2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(btnPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        lblPfp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/default-profile.png"))); // NOI18N

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
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblWelcome))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(lblDoor)
                        .addGap(16, 278, Short.MAX_VALUE))))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(lblWelcome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPfp, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(btmPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btmPanel;
    private javax.swing.JButton btnPayment;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblDoor;
    private javax.swing.JLabel lblElectric;
    private javax.swing.JLabel lblHistory;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNotif;
    private javax.swing.JLabel lblPfp;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JLabel lblTotal4;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JTextField txtElectric;
    private javax.swing.JTextField txtRent;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtWater;
    // End of variables declaration//GEN-END:variables
}

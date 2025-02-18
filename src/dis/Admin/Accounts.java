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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Accounts extends javax.swing.JFrame {

    /**
     * Creates new form Accounts
     */
    public Accounts() {
        initComponents();
        loadAccounts();
        loadDoors();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getLoggedInUserID() {
        return Login.loggedInUserID;
    }

    private void loadAccounts() {
        DefaultTableModel model = (DefaultTableModel) tbAccounts.getModel();
        model.setRowCount(0);

        tbAccounts.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbAccounts.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbAccounts.getColumnModel().getColumnCount(); i++) {
            tbAccounts.getColumnModel().getColumn(i).setResizable(false);
        }

        try (Connection conn = DBConnection.Connect()) {
            String userID = getLoggedInUserID();

            // Get the branch ID for the logged-in user
            String branchIdSql = "SELECT branch_id FROM branches WHERE user_id = ?";
            String branchId = null;
            try (PreparedStatement psBranch = conn.prepareStatement(branchIdSql)) {
                psBranch.setString(1, userID);
                ResultSet rsBranch = psBranch.executeQuery();
                if (rsBranch.next()) {
                    branchId = rsBranch.getString("branch_id");
                }
            }

            if (branchId == null) {
                showErrorMessage("No branch found for the logged-in user.");
                return;
            }

            // Load accounts for the specific branch or accounts without a door assigned
            String userSql = "SELECT u.user_id, p.first_name, p.last_name, p.contact_number, p.sex, d.door_number "
                    + "FROM users u "
                    + "INNER JOIN profiles p ON u.user_id = p.user_id "
                    + "LEFT JOIN doors d ON p.door_id = d.door_id "
                    + "WHERE (u.role = 'residents' AND (d.door_id IS NULL OR d.branch_id = ?)) "
                    + "ORDER BY u.user_id ASC";

            try (PreparedStatement psUser = conn.prepareStatement(userSql)) {
                psUser.setString(1, branchId);
                ResultSet rsUser = psUser.executeQuery();

                while (rsUser.next()) {
                    String userId = rsUser.getString("user_id");
                    String name = rsUser.getString("first_name") + " " + rsUser.getString("last_name");
                    String contact = rsUser.getString("contact_number");
                    String sex = rsUser.getString("sex");
                    String doorNumber = rsUser.getString("door_number") != null ? rsUser.getString("door_number") : "N/A";

                    model.addRow(new Object[]{userId, name, contact, sex, doorNumber});
                }
            }

            if (model.getRowCount() < 6) {
                tbAccounts.getColumnModel().getColumn(0).setPreferredWidth(40);
                tbAccounts.getColumnModel().getColumn(1).setPreferredWidth(359);
                tbAccounts.getColumnModel().getColumn(2).setPreferredWidth(120);
                tbAccounts.getColumnModel().getColumn(3).setPreferredWidth(80);
                tbAccounts.getColumnModel().getColumn(4).setPreferredWidth(80);
            } else {
                tbAccounts.getColumnModel().getColumn(0).setPreferredWidth(40);
                tbAccounts.getColumnModel().getColumn(1).setPreferredWidth(343);
                tbAccounts.getColumnModel().getColumn(2).setPreferredWidth(120);
                tbAccounts.getColumnModel().getColumn(3).setPreferredWidth(80);
                tbAccounts.getColumnModel().getColumn(4).setPreferredWidth(80);
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void updateFields(String userId) {
        DefaultTableModel model = (DefaultTableModel) tbPayments.getModel();
        model.setRowCount(0);

        tbPayments.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbPayments.getColumnModel().getColumnCount(); i++) {
            tbPayments.getColumnModel().getColumn(i).setResizable(false);
        }
        
        try (Connection conn = DBConnection.Connect()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
            
            String profilesSql = "SELECT * FROM profiles WHERE user_id = ?";
            String doorSql = "SELECT * FROM doors WHERE door_id = (SELECT door_id FROM profiles WHERE user_id = ?)";
            String addressSql = "SELECT * FROM address WHERE address_id = (SELECT address_id FROM profiles WHERE user_id = ?)";
            String paymentSql = "SELECT * FROM payments WHERE user_id = ?   ";

            try (PreparedStatement psProfile = conn.prepareStatement(profilesSql)) {
                psProfile.setString(1, userId);
                ResultSet rsProfile = psProfile.executeQuery();

                if (rsProfile.next()) {
                    String name = rsProfile.getString("first_name") + " " + rsProfile.getString("last_name");
                    String contact = rsProfile.getString("contact_number");
                    String sex = rsProfile.getString("sex");
                    java.sql.Date dueDateSql = rsProfile.getDate("date_of_birth");
                    String formattedDueDate = dateFormat.format(dueDateSql);


                    byte[] imgBytes = rsProfile.getBytes("profile_picture");
                    if (imgBytes != null) {
                        ImageIcon profilePicture = new ImageIcon(imgBytes);
                        lblPfp.setIcon(profilePicture);
                    } else {
                        lblPfp.setIcon(new ImageIcon(getClass().getResource("/assets/default-profile.png")));
                    }
                    lblName.setText(name);
                    lblContact.setText(contact);
                    if ("Male".equals(sex)) {
                        lblSex.setIcon(new ImageIcon(getClass().getResource("/assets/male-regular-24.png")));   
                    } else {
                        lblSex.setIcon(new ImageIcon(getClass().getResource("/assets/female-regular-24.png")));
                    }
                    lblSex.setText(" " + sex);
                    lblDob.setText(formattedDueDate);

                    String doorNumber = "N/A";
                    try (PreparedStatement psDoor = conn.prepareStatement(doorSql)) {
                        psDoor.setString(1, userId);
                        ResultSet rsDoor = psDoor.executeQuery();

                        if (rsDoor.next()) {
                            doorNumber = rsDoor.getString("door_number");
                        }
                    }
                    lblDoor.setText("Door " + doorNumber);

                    try (PreparedStatement psAddress = conn.prepareStatement(addressSql)) {
                        psAddress.setString(1, userId);
                        ResultSet rsAddress = psAddress.executeQuery();

                        if (rsAddress.next()) {
                            String city = rsAddress.getString("city");
                            String municipality = rsAddress.getString("municipality");
                            String barangay = rsAddress.getString("barangay");

                            String fullAddress = barangay + ", " + municipality + ", " + city;
                            lblAddress.setText(fullAddress);
                        }
                    }
                }
            }
            
            try (PreparedStatement psP = conn.prepareStatement(paymentSql)) {
                psP.setString(1, userId);
                ResultSet rsP = psP.executeQuery();
                
                while (rsP.next()) {
                    String amount = "PHP " + rsP.getString("amount_paid");
                    Date dobDate = rsP.getDate("date_paid");
                    SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String dob = newFormat.format(dobDate);
                    
                    model.addRow(new Object[]{amount, dob});
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void deleteAccount(String userId) {
        try (Connection conn = DBConnection.Connect()) {
            String userSql = "DELETE FROM users WHERE user_id = ?";
            String meterSql = "DELETE FROM meters WHERE user_id = ?";
            String notifSql = "DELETE FROM notifications WHERE user_id = ?";
            String paySql = "DELETE FROM payments WHERE user_id = ?";
            String profileSql = "DELETE FROM profiles WHERE user_id = ?";

            try (PreparedStatement psUser = conn.prepareStatement(userSql); 
                    PreparedStatement psMeter = conn.prepareStatement(meterSql); 
                    PreparedStatement psNotif = conn.prepareStatement(notifSql); 
                    PreparedStatement psPay = conn.prepareStatement(paySql); 
                    PreparedStatement psProfile = conn.prepareStatement(profileSql)) {

                psMeter.setString(1, userId);
                psMeter.executeUpdate();

                psNotif.setString(1, userId);
                psNotif.executeUpdate();

                psPay.setString(1, userId);
                psPay.executeUpdate();

                psProfile.setString(1, userId);
                psProfile.executeUpdate();

                psUser.setString(1, userId);
                psUser.executeUpdate();

                JOptionPane.showMessageDialog(this, "Account deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void loadDoors() {
        try (Connection conn = DBConnection.Connect()) {
            String doorSql = "SELECT * FROM doors";
            try (PreparedStatement psDoor = conn.prepareStatement(doorSql); ResultSet rsDoor = psDoor.executeQuery()) {
                cbDoor.removeAllItems();
                while (rsDoor.next()) {
                    String doorNumber = rsDoor.getString("door_number");
                    cbDoor.addItem(doorNumber);
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
        lblHome1 = new javax.swing.JLabel();
        lblDoors = new javax.swing.JLabel();
        lblReport = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();
        heroPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblBG = new javax.swing.JLabel();
        deetPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbAccounts = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblPfp = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDoor = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbPayments = new javax.swing.JTable();
        lblDoor2 = new javax.swing.JLabel();
        lblContact = new javax.swing.JLabel();
        lblSex = new javax.swing.JLabel();
        lblDob = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        lblDoor1 = new javax.swing.JLabel();
        cbDoor = new javax.swing.JComboBox<>();
        btnAssign = new javax.swing.JButton();
        btnMessage = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnSearch1 = new javax.swing.JButton();

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

        lblHome1.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblHome1.setForeground(new java.awt.Color(0, 128, 241));
        lblHome1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-account-solid-24 (1).png"))); // NOI18N
        lblHome1.setText(" Accounts");
        lblHome1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblDoors.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblDoors.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/door-open-solid-24.png"))); // NOI18N
        lblDoors.setText(" Doors");
        lblDoors.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDoors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDoorsMouseClicked(evt);
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
                        .addComponent(lblHome1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addComponent(lblHome, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblDoors, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblReport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(22, 22, 22))
        );
        navPanelLayout.setVerticalGroup(
            navPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblHome)
                .addGap(91, 91, 91)
                .addComponent(lblHome1)
                .addGap(18, 18, 18)
                .addComponent(lblDoors)
                .addGap(18, 18, 18)
                .addComponent(lblReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblLogout)
                .addGap(30, 30, 30))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));

        heroPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 48)); // NOI18N
        lblTitle.setText("Accounts");

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

        tbAccounts.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbAccounts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Contact", "Sex", "Door"
            }
        ));
        tbAccounts.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbAccounts.setRowHeight(30);
        tbAccounts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAccountsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbAccounts);
        if (tbAccounts.getColumnModel().getColumnCount() > 0) {
            tbAccounts.getColumnModel().getColumn(1).setHeaderValue("Name");
            tbAccounts.getColumnModel().getColumn(2).setHeaderValue("Contact");
            tbAccounts.getColumnModel().getColumn(3).setHeaderValue("Sex");
        }

        btnDelete.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/trash-regular-24.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblPfp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/default-profile.png"))); // NOI18N

        lblName.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N

        lblDoor.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblDoor.setText("Door Number");
        lblDoor.setToolTipText("Room Number");

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        tbPayments.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        tbPayments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Amount", "Date"
            }
        ));
        tbPayments.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbPayments.setRowHeight(30);
        tbPayments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPaymentsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tbPayments);

        lblDoor2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        lblDoor2.setText("Payments");
        lblDoor2.setToolTipText("Room Number");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(106, Short.MAX_VALUE)
                .addComponent(lblDoor2)
                .addGap(99, 99, 99))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDoor2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblContact.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblContact.setText("Contact No.");
        lblContact.setToolTipText("Room Number");

        lblSex.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblSex.setText("Sex");
        lblSex.setToolTipText("Room Number");

        lblDob.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblDob.setText("Date Of Birth");
        lblDob.setToolTipText("Room Number");

        lblAddress.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblAddress.setText("Address");
        lblAddress.setToolTipText("Room Number");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblPfp)
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDoor)
                            .addComponent(lblContact)
                            .addComponent(lblDob)
                            .addComponent(lblAddress)
                            .addComponent(lblSex)))
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblPfp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblContact, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDob, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSex, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(74, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblDoor1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblDoor1.setText("Door No:");
        lblDoor1.setToolTipText("Room Number");

        cbDoor.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        btnAssign.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/edit-regular-24.png"))); // NOI18N
        btnAssign.setText("Assign");
        btnAssign.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });

        btnMessage.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/mail-send-regular-24.png"))); // NOI18N
        btnMessage.setText("Message");
        btnMessage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMessageActionPerformed(evt);
            }
        });

        txtSearch.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/search-regular-24.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearch1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnSearch1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/refresh-regular-24.png"))); // NOI18N
        btnSearch1.setText("Refresh");
        btnSearch1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearch1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout deetPanelLayout = new javax.swing.GroupLayout(deetPanel);
        deetPanel.setLayout(deetPanelLayout);
        deetPanelLayout.setHorizontalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(deetPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 685, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6))
                            .addGroup(deetPanelLayout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch1)
                                .addGap(4, 4, 4)))
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMessage)
                            .addComponent(lblDoor1)
                            .addComponent(cbDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8))))
        );
        deetPanelLayout.setVerticalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(btnMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(lblDoor1)
                        .addGap(6, 6, 6)
                        .addComponent(cbDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(heroPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deetPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void lblLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLogoutMouseClicked
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Log Out?", "Log Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Login login = Login.getInstance();
            login.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_lblLogoutMouseClicked

    private void lblHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHomeMouseClicked
        Admin_DB adminDB = new Admin_DB();
        adminDB.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblHomeMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = tbAccounts.getSelectedRow();
        if (selectedRow != -1) {
            String userId = tbAccounts.getValueAt(selectedRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteAccount(userId);
                loadAccounts();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
        int selectedRow = tbAccounts.getSelectedRow();
        String userID = getLoggedInUserID();
        if (selectedRow != -1) {
            String userId = tbAccounts.getValueAt(selectedRow, 0).toString();
            String selectedDoor = (String) cbDoor.getSelectedItem();

            try (Connection conn = DBConnection.Connect()) {
                conn.setAutoCommit(false); // Start transaction

                // Get the current door_id of the user (if any)
                String currentDoorSql = "SELECT door_id FROM profiles WHERE user_id = ?";
                String currentDoorId = null;
                try (PreparedStatement psCurrentDoor = conn.prepareStatement(currentDoorSql)) {
                    psCurrentDoor.setString(1, userId);
                    ResultSet rsCurrentDoor = psCurrentDoor.executeQuery();
                    if (rsCurrentDoor.next()) {
                        currentDoorId = rsCurrentDoor.getString("door_id");
                    }
                }

                // Check availability of the selected door
                String availabilitySql = "SELECT available FROM doors WHERE door_id = ("
                        + "SELECT d.door_id FROM doors d "
                        + "INNER JOIN branches b ON d.branch_id = b.branch_id "
                        + "WHERE d.door_id = (SELECT DISTINCT p.door_id FROM profiles p "
                        + "INNER JOIN doors d ON p.door_id = d.door_id "
                        + "INNER JOIN branches b ON b.branch_id = d.branch_id "
                        + "WHERE d.door_number = ? AND d.branch_id IN (SELECT branch_id FROM branches WHERE user_id = ?)) "
                        + "AND b.branch_id IN (SELECT branch_id FROM branches WHERE user_id = ?) "
                        + "LIMIT 1) ";
                int availableCount = 0;
                try (PreparedStatement psAvailability = conn.prepareStatement(availabilitySql)) {
                    psAvailability.setString(1, selectedDoor);
                    psAvailability.setString(2, userID);
                    psAvailability.setString(3, userID);
                    ResultSet rsAvailability = psAvailability.executeQuery();
                    if (rsAvailability.next()) {
                        availableCount = rsAvailability.getInt("available");
                    }
                }

                // Check if the selected door is available
                if (availableCount <= 0) {
                    showErrorMessage("Cannot assign door. The door is already at full capacity (0 available).");
                    return;
                }

                // Update the profiles table to assign the new door
                String updateSql = "UPDATE profiles SET door_id = ("
                        + "SELECT d.door_id FROM doors d "
                        + "INNER JOIN branches b ON d.branch_id = b.branch_id "
                        + "WHERE d.door_id = (SELECT DISTINCT p.door_id FROM profiles p INNER JOIN doors d ON p.door_id = d.door_id WHERE d.door_number = ?) "
                        + "AND b.branch_id IN (SELECT branch_id FROM branches WHERE user_id = ?) "
                        + "LIMIT 1) "
                        + "WHERE user_id = ?;";
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setString(1, selectedDoor);
                    psUpdate.setString(2, userID);
                    psUpdate.setString(3, userId);
                    int rowsAffected = psUpdate.executeUpdate();

                    if (rowsAffected > 0) {
                        // If the user had an old door, increment its available count
                        if (currentDoorId != null) {
                            String incrementSql = "UPDATE doors SET available = available + 1 WHERE door_id = (SELECT door_id FROM doors WHERE door_id = ?)";
                            try (PreparedStatement psIncrement = conn.prepareStatement(incrementSql)) {
                                psIncrement.setString(1, currentDoorId);
                                psIncrement.executeUpdate();
                            }
                        }

                        // Decrement the available count for the new door
                        String decrementSql = "UPDATE doors SET available = available - 1 WHERE door_number = ?";
                        try (PreparedStatement psDecrement = conn.prepareStatement(decrementSql)) {
                            psDecrement.setString(1, selectedDoor);
                            psDecrement.executeUpdate();
                        }

                        conn.commit(); // Commit transaction
                        JOptionPane.showMessageDialog(this, "Door assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAccounts();
                    } else {
                        showErrorMessage("Failed to assign door. Please try again.");
                    }
                }
            } catch (SQLException e) {
                showErrorMessage("Database error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to assign a door.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnAssignActionPerformed

    private void tbAccountsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAccountsMouseClicked
        int selectedRow = tbAccounts.getSelectedRow();
        if (selectedRow != -1) {
            String userId = tbAccounts.getValueAt(selectedRow, 0).toString();
            updateFields(userId);
        }
    }//GEN-LAST:event_tbAccountsMouseClicked

    private void btnMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMessageActionPerformed
        int selectedRow = tbAccounts.getSelectedRow();
        if (selectedRow != -1) {
            String userId = tbAccounts.getValueAt(selectedRow, 0).toString();

            String billSql = "SELECT * FROM billings WHERE user_id = ?";
            StringBuilder billingInfo = new StringBuilder();
            boolean hasUnpaidBill = false;

            try (Connection conn = DBConnection.Connect(); PreparedStatement psBill = conn.prepareStatement(billSql)) {
                psBill.setString(1, userId);
                ResultSet rsBill = psBill.executeQuery();

                if (rsBill.next()) {
                    String rent = rsBill.getString("rent");
                    String waterUsage = rsBill.getString("water_usage");
                    String electricUsage = rsBill.getString("electric_usage");
                    String totalDue = rsBill.getString("total_due");
                    String status = rsBill.getString("status");

                    billingInfo.append("Rent: ").append(rent).append("\n")
                            .append("Water Usage: ").append(waterUsage).append("\n")
                            .append("Electric Usage: ").append(electricUsage).append("\n")
                            .append("Total Due: ").append(totalDue).append("\n")
                            .append("Status: ").append(status).append("\n");

                    if (status.equals("unpaid")) {
                        hasUnpaidBill = true;
                    }
                }
            } catch (SQLException e) {
                showErrorMessage("Database error: " + e.getMessage());
                return;
            }

            String message = JOptionPane.showInputDialog(this,
                    "Enter your message to the user:\n\n" + (hasUnpaidBill ? billingInfo.toString() : "No unpaid bills."),
                    "Send Message", JOptionPane.PLAIN_MESSAGE);

            if (message != null && !message.trim().isEmpty()) {
                String insertSql = "INSERT INTO notifications (resident_id, message, status) VALUES (?, ?, 'unread')";
                try (Connection conn = DBConnection.Connect(); PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    psInsert.setString(1, userId);
                    psInsert.setString(2, message);
                    psInsert.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Message sent successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    showErrorMessage("Database error: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Message cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to send a message.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnMessageActionPerformed

    private void lblDoorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDoorsMouseClicked
        Doors doors = new Doors();
        doors.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblDoorsMouseClicked

    private void lblReportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblReportMouseClicked
        Report report = new Report();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lblReportMouseClicked

    private void tbPaymentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPaymentsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPaymentsMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String searchTerm = txtSearch.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tbAccounts.getModel();
        model.setRowCount(0); // Clear existing rows

        try (Connection conn = DBConnection.Connect()) {
            // Modify the SQL query to search for the term in relevant fields
            String userSql = "SELECT u.user_id "
                    + "FROM users u "
                    + "INNER JOIN profiles p ON u.user_id = p.user_id "
                    + "INNER JOIN doors d ON p.door_id = d.door_id "
                    + "WHERE (u.role = 'residents') "
                    + "AND (p.first_name LIKE ? OR p.last_name LIKE ? OR p.contact_number LIKE ?) "
                    + "ORDER BY u.user_id ASC";

            try (PreparedStatement psUser = conn.prepareStatement(userSql)) {
                // Use wildcards for searching
                String searchPattern = "%" + searchTerm + "%";
                psUser.setString(1, searchPattern);
                psUser.setString(2, searchPattern);
                psUser.setString(3, searchPattern);
                ResultSet rsUser = psUser.executeQuery();

                if (!rsUser.isBeforeFirst()) { // Check if the ResultSet is empty
                    System.out.println("No accounts found for the search term: " + searchTerm);
                }

                while (rsUser.next()) {
                    String userId = rsUser.getString("user_id");

                    // Fetch profile details
                    String profileSql = "SELECT * FROM profiles WHERE user_id = ?";
                    try (PreparedStatement psProfile = conn.prepareStatement(profileSql)) {
                        psProfile.setString(1, userId);
                        ResultSet rsProfile = psProfile.executeQuery();

                        if (rsProfile.next()) {
                            String name = rsProfile.getString("first_name") + " " + rsProfile.getString("last_name");
                            String contact = rsProfile.getString("contact_number");
                            String sex = rsProfile.getString("sex");

                            // Fetch door details
                            String doorSql = "SELECT d.door_number FROM doors d "
                                    + "INNER JOIN profiles p ON d.door_id = p.door_id "
                                    + "WHERE p.user_id = ?";
                            String doorNumber = "N/A";
                            try (PreparedStatement psDoor = conn.prepareStatement(doorSql)) {
                                psDoor.setString(1, userId);
                                ResultSet rsDoor = psDoor.executeQuery();

                                if (rsDoor.next()) {
                                    doorNumber = rsDoor.getString("door_number");
                                }
                            }

                            model.addRow(new Object[]{userId, name, contact, sex, doorNumber});
                        }
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearch1ActionPerformed
        loadAccounts();
    }//GEN-LAST:event_btnSearch1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssign;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnMessage;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearch1;
    private javax.swing.JComboBox<String> cbDoor;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel deetPanel;
    private javax.swing.JPanel heroPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblBG;
    private javax.swing.JLabel lblContact;
    private javax.swing.JLabel lblDob;
    private javax.swing.JLabel lblDoor;
    private javax.swing.JLabel lblDoor1;
    private javax.swing.JLabel lblDoor2;
    private javax.swing.JLabel lblDoors;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblHome1;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPfp;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblSex;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTable tbAccounts;
    private javax.swing.JTable tbPayments;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}

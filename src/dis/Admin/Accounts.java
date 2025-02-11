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

    private void loadAccounts() {
        DefaultTableModel model = (DefaultTableModel) tbAccounts.getModel();
        model.setRowCount(0);

        tbAccounts.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbAccounts.getTableHeader().setReorderingAllowed(false);

        for (int i = 0; i < tbAccounts.getColumnModel().getColumnCount(); i++) {
            tbAccounts.getColumnModel().getColumn(i).setResizable(false);
        }

        try (Connection conn = DBConnection.Connect()) {
            String userSql = "SELECT u.user_id "
                           + "FROM users u "
                           + "LEFT JOIN admins a ON u.user_id = a.user_id "
                           + "WHERE a.user_id IS NULL "
                           + "ORDER BY u.user_id ASC";
            String profilesSql = "SELECT * FROM profiles";
            String doorSql = "SELECT * FROM doors";

            try (PreparedStatement psUser = conn.prepareStatement(userSql); ResultSet rsUser = psUser.executeQuery()) {

                while (rsUser.next()) {
                    String userId = rsUser.getString("user_id");

                    String profileSql = profilesSql + " WHERE user_id = ?";
                    try (PreparedStatement psProfile = conn.prepareStatement(profileSql)) {
                        psProfile.setString(1, userId);
                        ResultSet rsProfile = psProfile.executeQuery();

                        if (rsProfile.next()) {
                            String name = rsProfile.getString("first_name") + " " + rsProfile.getString("last_name");
                            String contact = rsProfile.getString("contact_number");
                            String sex = rsProfile.getString("sex");
                            Date dobDate = rsProfile.getDate("date_of_birth");
                            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
                            String dob = newFormat.format(dobDate);

                            String doorSqlQuery = doorSql + " WHERE door_id = ?";
                            try (PreparedStatement psDoor = conn.prepareStatement(doorSqlQuery)) {
                                psDoor.setString(1, rsProfile.getString("door_id"));
                                ResultSet rsDoor = psDoor.executeQuery();

                                String doorNumber = "";
                                if (rsDoor.next()) {
                                    doorNumber = rsDoor.getString("door_number");
                                }

                                model.addRow(new Object[]{userId, name, contact, sex, doorNumber});
                            }
                        }
                    }
                }
            }

            if (model.getRowCount() < 6) {
                tbAccounts.getColumnModel().getColumn(0).setPreferredWidth(40);
                tbAccounts.getColumnModel().getColumn(1).setPreferredWidth(397);
                tbAccounts.getColumnModel().getColumn(2).setPreferredWidth(120);
                tbAccounts.getColumnModel().getColumn(3).setPreferredWidth(80);
                tbAccounts.getColumnModel().getColumn(4).setPreferredWidth(42);
            } else {
                tbAccounts.getColumnModel().getColumn(0).setPreferredWidth(40);
                tbAccounts.getColumnModel().getColumn(1).setPreferredWidth(380);
                tbAccounts.getColumnModel().getColumn(2).setPreferredWidth(120);
                tbAccounts.getColumnModel().getColumn(3).setPreferredWidth(80);
                tbAccounts.getColumnModel().getColumn(4).setPreferredWidth(43);
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void updateFields(String userId) {
        try (Connection conn = DBConnection.Connect()) {
            String profilesSql = "SELECT * FROM profiles WHERE user_id = ?";
            String doorSql = "SELECT * FROM doors WHERE door_id = (SELECT door_id FROM profiles WHERE user_id = ?)";
            String addressSql = "SELECT * FROM address WHERE address_id = (SELECT address_id FROM profiles WHERE user_id = ?)";
            String billSql = "SELECT * FROM billings WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            String meterSql = "SELECT * FROM meters WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";

            try (PreparedStatement psProfile = conn.prepareStatement(profilesSql)) {
                psProfile.setString(1, userId);
                ResultSet rsProfile = psProfile.executeQuery();

                if (rsProfile.next()) {
                    String name = rsProfile.getString("first_name") + " " + rsProfile.getString("last_name");
                    String contact = rsProfile.getString("contact_number");
                    String sex = rsProfile.getString("sex");
                    Date dobDate = rsProfile.getDate("date_of_birth");
                    SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String dob = newFormat.format(dobDate);

                    byte[] imgBytes = rsProfile.getBytes("profile_picture");
                    if (imgBytes != null) {
                        ImageIcon profilePicture = new ImageIcon(imgBytes);
                        lblPfp.setIcon(profilePicture);
                    } else {
                        lblPfp.setIcon(new ImageIcon(getClass().getResource("/assets/default-profile.png")));
                    }
                    lblName.setText(name);
                    lblContact.setText(contact);
                    txtSex.setText(sex);
                    txtDob.setText(dob);

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

                    try (PreparedStatement psBill = conn.prepareStatement(billSql)) {
                        psBill.setString(1, userId);
                        ResultSet rsBill = psBill.executeQuery();

                        if (rsBill.next()) {
                            String rent = rsBill.getString("rent");
                            String waterUsage = rsBill.getString("water_usage");
                            String electricUsage = rsBill.getString("electric_usage");
                            String totalDue = rsBill.getString("total_due");
                            java.sql.Date dueDateSql = rsBill.getDate("due_date"); // Retrieve as java.sql.Date
                            String formattedDueDate = (dueDateSql != null) ? newFormat.format(dueDateSql) : "N/A"; // Format only if not null
                            String status = rsBill.getString("status");

                            txtRent.setText(rent);
                            txtWbill.setText("PHP " + waterUsage);
                            txtEbill.setText("PHP " + electricUsage);
                            txtTotal.setText("PHP " + totalDue);
                            txtDueDate.setText(formattedDueDate);

                            cbStatus.removeAllItems();
                            cbStatus.addItem("Paid");
                            cbStatus.addItem("Unpaid");
                            cbStatus.setSelectedItem(status.equals("paid") ? "Paid" : "Unpaid");
                        }
                    }

                    try (PreparedStatement psMeter = conn.prepareStatement(meterSql)) {
                        psMeter.setString(1, userId);
                        ResultSet rsMeter = psMeter.executeQuery();

                        while (rsMeter.next()) {
                            String meterType = rsMeter.getString("meter_type");
                            double totalUsage = rsMeter.getDouble("meter_usage");
                            if ("electric".equals(meterType)) {
                                txtEusage.setText(totalUsage + " kwh");
                            } else if ("water".equals(meterType)) {
                                txtWusage.setText(totalUsage + " m³");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }
    
    private void deleteAccount(String userId) {
        try (Connection conn = DBConnection.Connect()) {
            String userSql = "DELETE FROM users WHERE user_id = ?";
            String meterSql = "DELETE FROM meters WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            String notifSql = "DELETE FROM notifications WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            String paySql = "DELETE FROM payments WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
            String resSql = "DELETE FROM residents WHERE user_id = ?";
            String profileSql = "DELETE FROM profiles WHERE user_id = ?";

            try (PreparedStatement psUser = conn.prepareStatement(userSql); PreparedStatement psMeter = conn.prepareStatement(meterSql); PreparedStatement psNotif = conn.prepareStatement(notifSql); PreparedStatement psPay = conn.prepareStatement(paySql); PreparedStatement psRes = conn.prepareStatement(resSql); PreparedStatement psProfile = conn.prepareStatement(profileSql)) {

                psMeter.setString(1, userId);
                psMeter.executeUpdate();

                psNotif.setString(1, userId);
                psNotif.executeUpdate();

                psPay.setString(1, userId);
                psPay.executeUpdate();

                psRes.setString(1, userId);
                psRes.executeUpdate();

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
        lblElectricUsage = new javax.swing.JLabel();
        lblElectricUsage1 = new javax.swing.JLabel();
        lblElectricUsage2 = new javax.swing.JLabel();
        lblElectricUsage3 = new javax.swing.JLabel();
        lblElectricUsage4 = new javax.swing.JLabel();
        lblElectricUsage5 = new javax.swing.JLabel();
        lblElectricUsage6 = new javax.swing.JLabel();
        txtEusage = new javax.swing.JTextField();
        txtEbill = new javax.swing.JTextField();
        txtWusage = new javax.swing.JTextField();
        txtWbill = new javax.swing.JTextField();
        txtRent = new javax.swing.JTextField();
        txtDueDate = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        cbStatus = new javax.swing.JComboBox<>();
        lblContact = new javax.swing.JLabel();
        lblContact1 = new javax.swing.JLabel();
        txtSex = new javax.swing.JTextField();
        txtDob = new javax.swing.JTextField();
        lblContact2 = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        lblDoor1 = new javax.swing.JLabel();
        cbDoor = new javax.swing.JComboBox<>();
        btnAssign = new javax.swing.JButton();
        btnMessage = new javax.swing.JButton();

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

        lblDoor.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblDoor.setText("Door Number");
        lblDoor.setToolTipText("Room Number");

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        lblElectricUsage.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage.setText("Electric Usage:");

        lblElectricUsage1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage1.setText("Electric Bill:");

        lblElectricUsage2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage2.setText("Water Usage:");

        lblElectricUsage3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage3.setText("Water Bill:");

        lblElectricUsage4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage4.setText("Rent:");

        lblElectricUsage5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage5.setText("Due Date:");

        lblElectricUsage6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        lblElectricUsage6.setText("Total Due:");

        txtEusage.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtEusage.setFocusable(false);

        txtEbill.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtEbill.setFocusable(false);

        txtWusage.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtWusage.setFocusable(false);

        txtWbill.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtWbill.setFocusable(false);

        txtRent.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtRent.setFocusable(false);

        txtDueDate.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtDueDate.setFocusable(false);

        txtTotal.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtTotal.setFocusable(false);

        cbStatus.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEusage))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEbill))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWusage))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtWbill))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRent))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblElectricUsage5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDueDate)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage)
                    .addComponent(txtEusage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage1)
                    .addComponent(txtEbill, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage2)
                    .addComponent(txtWusage, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage3)
                    .addComponent(txtWbill, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage4)
                    .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage5)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblElectricUsage6)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        lblContact.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblContact.setText("Contact No.");
        lblContact.setToolTipText("Room Number");

        lblContact1.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblContact1.setText("Sex:");
        lblContact1.setToolTipText("Room Number");

        txtSex.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtSex.setFocusable(false);

        txtDob.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        txtDob.setFocusable(false);

        lblContact2.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        lblContact2.setText("Date Of Birth:");
        lblContact2.setToolTipText("Room Number");

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
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblContact1)
                                    .addComponent(txtSex, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblContact2)
                                    .addComponent(txtDob, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblAddress)))
                    .addComponent(lblName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPfp)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblContact, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblContact1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblContact2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSex, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDob, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        javax.swing.GroupLayout deetPanelLayout = new javax.swing.GroupLayout(deetPanel);
        deetPanel.setLayout(deetPanelLayout);
        deetPanelLayout.setHorizontalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 685, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbDoor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(deetPanelLayout.createSequentialGroup()
                                .addComponent(lblDoor1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(btnAssign, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        deetPanelLayout.setVerticalGroup(
            deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(deetPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE))
                    .addGroup(deetPanelLayout.createSequentialGroup()
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(deetPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deetPanelLayout.createSequentialGroup()
                                .addComponent(lblDoor1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDoor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, deetPanelLayout.createSequentialGroup()
                                .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
        if (selectedRow != -1) {
            String userId = tbAccounts.getValueAt(selectedRow, 0).toString();
            String selectedDoor = (String) cbDoor.getSelectedItem();

            try (Connection conn = DBConnection.Connect()) {
                String countSql = "SELECT COUNT(*) AS resident_count FROM profiles WHERE door_id = (SELECT door_id FROM doors WHERE door_number = ?)";
                try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                    psCount.setString(1, selectedDoor);
                    ResultSet rsCount = psCount.executeQuery();

                    int currentCount = 0;
                    if (rsCount.next()) {
                        currentCount = rsCount.getInt("resident_count");
                    }

                    if (currentCount >= 4) {
                        showErrorMessage("Cannot assign door. The door is already at full capacity (4 residents).");
                        return;
                    }

                    String updateSql = "UPDATE profiles SET door_id = (SELECT door_id FROM doors WHERE door_number = ?) WHERE user_id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, selectedDoor);
                        psUpdate.setString(2, userId);
                        int rowsAffected = psUpdate.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Door assigned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadAccounts();
                        } else {
                            showErrorMessage("Failed to assign door. Please try again.");
                        }
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

            String billSql = "SELECT * FROM billings WHERE resident_id = (SELECT resident_id FROM residents WHERE user_id = ?)";
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
                String insertSql = "INSERT INTO notifications (resident_id, message, status) VALUES ((SELECT resident_id FROM residents WHERE user_id = ?), ?, 'unread')";
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssign;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnMessage;
    private javax.swing.JComboBox<String> cbDoor;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel deetPanel;
    private javax.swing.JPanel heroPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblBG;
    private javax.swing.JLabel lblContact;
    private javax.swing.JLabel lblContact1;
    private javax.swing.JLabel lblContact2;
    private javax.swing.JLabel lblDoor;
    private javax.swing.JLabel lblDoor1;
    private javax.swing.JLabel lblDoors;
    private javax.swing.JLabel lblElectricUsage;
    private javax.swing.JLabel lblElectricUsage1;
    private javax.swing.JLabel lblElectricUsage2;
    private javax.swing.JLabel lblElectricUsage3;
    private javax.swing.JLabel lblElectricUsage4;
    private javax.swing.JLabel lblElectricUsage5;
    private javax.swing.JLabel lblElectricUsage6;
    private javax.swing.JLabel lblHome;
    private javax.swing.JLabel lblHome1;
    private javax.swing.JLabel lblLogout;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPfp;
    private javax.swing.JLabel lblReport;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel navPanel;
    private javax.swing.JTable tbAccounts;
    private javax.swing.JTextField txtDob;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JTextField txtEbill;
    private javax.swing.JTextField txtEusage;
    private javax.swing.JTextField txtRent;
    private javax.swing.JTextField txtSex;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtWbill;
    private javax.swing.JTextField txtWusage;
    // End of variables declaration//GEN-END:variables
}

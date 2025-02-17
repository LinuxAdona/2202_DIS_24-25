/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dis.Resident;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DBConnection;
import dis.Admin.Doors;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import strt.Login;

/**
 *
 * @author ADMIN
 */
public class Profile extends javax.swing.JFrame {

    private byte[] profilePictureBytes;
    /**
     * Creates new form Profile
     */
    public Profile() {
        initComponents();
        configurations();
        loadUserDetails();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    private void configurations() {
        spDob.setModel(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spDob, "MM/dd/yyyy");
        spDob.setEditor(dateEditor);
        loadCities();
    }

    private void loadCities() {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT city FROM address";
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
                cbCity.removeAllItems();
                while (rs.next()) {
                    cbCity.addItem(rs.getString("city"));
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Error loading cities: " + e.getMessage());
        }
    }

    private void loadMunicipalities(String city) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT municipality FROM address WHERE city = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, city);
                try (ResultSet rs = stmt.executeQuery()) {
                    cbMunicipality.removeAllItems();
                    while (rs.next()) {
                        cbMunicipality.addItem(rs.getString("municipality"));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Error loading municipalities: " + e.getMessage());
        }
    }

    private void loadBarangays(String municipality) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT DISTINCT barangay FROM address WHERE municipality = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, municipality);
                try (ResultSet rs = stmt.executeQuery()) {
                    cbBarangay.removeAllItems();
                    while (rs.next()) {
                        cbBarangay.addItem(rs.getString("barangay"));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Error loading barangays: " + e.getMessage());
        }
    }

    private String getAddressId(String city, String municipality, String barangay) {
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT address_id FROM address WHERE city = ? AND municipality = ? AND barangay = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, city);
                stmt.setString(2, municipality);
                stmt.setString(3, barangay);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("address_id");
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
        return null;
    }

    private void loadUserDetails() {
        String userID = Login.loggedInUserID; // Get the logged-in user ID
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT u.username, u.email, u.password, p.first_name, p.last_name, p.contact_number, p.sex, p.date_of_birth, "
                    + "p.profile_picture, a.city, a.municipality, a.barangay "
                    + "FROM users u "
                    + "JOIN profiles p ON u.user_id = p.user_id "
                    + "JOIN address a ON p.address_id = a.address_id "
                    + "WHERE u.user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    txtUser.setText(rs.getString("username"));
                    txtEmail.setText(rs.getString("email"));
                    txtPass.setText(rs.getString("password"));
                    txtFname.setText(rs.getString("first_name"));
                    txtLname.setText(rs.getString("last_name"));
                    txtCno.setText(rs.getString("contact_number"));
                    cbSex.setSelectedItem(rs.getString("sex"));
                    spDob.setValue(rs.getDate("date_of_birth")); // Assuming date_of_birth is of type Date
                    byte[] imgBytes = rs.getBytes("profile_picture");
                    if (imgBytes != null) {
                        profilePictureBytes = imgBytes;
                    }
                    cbCity.setSelectedItem(rs.getString("city"));
                    loadMunicipalities(rs.getString("city")); // Load municipalities based on city
                    cbMunicipality.setSelectedItem(rs.getString("municipality"));
                    loadBarangays(rs.getString("municipality")); // Load barangays based on municipality
                    cbBarangay.setSelectedItem(rs.getString("barangay"));
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
        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtReEnter = new javax.swing.JPasswordField();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        txtPass = new javax.swing.JPasswordField();
        lblPass1 = new javax.swing.JLabel();
        lblUser1 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblUser2 = new javax.swing.JLabel();
        txtFname = new javax.swing.JTextField();
        lblUser3 = new javax.swing.JLabel();
        txtLname = new javax.swing.JTextField();
        txtCno = new javax.swing.JTextField();
        lblUser4 = new javax.swing.JLabel();
        cbSex = new javax.swing.JComboBox<>();
        lblUser5 = new javax.swing.JLabel();
        spDob = new javax.swing.JSpinner();
        lblUser6 = new javax.swing.JLabel();
        lblUser7 = new javax.swing.JLabel();
        btnChoose = new javax.swing.JButton();
        lblUser8 = new javax.swing.JLabel();
        lblUser9 = new javax.swing.JLabel();
        lblUser10 = new javax.swing.JLabel();
        cbCity = new javax.swing.JComboBox<>();
        cbMunicipality = new javax.swing.JComboBox<>();
        cbBarangay = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(249, 249, 249));

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Update Profile");

        lblUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser.setText("Username:");

        lblPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblPass.setText("Re-enter Password:");

        txtUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        txtReEnter.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtReEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReEnterActionPerformed(evt);
            }
        });

        btnBack.setBackground(new java.awt.Color(154, 164, 255));
        btnBack.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/arrow-back-regular-24.png"))); // NOI18N
        btnBack.setText("Go back");
        btnBack.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(154, 164, 255));
        btnUpdate.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/edit-alt-regular-24.png"))); // NOI18N
        btnUpdate.setText(" Update");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        txtPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPassActionPerformed(evt);
            }
        });

        lblPass1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblPass1.setText("Password:");

        lblUser1.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser1.setText("E-mail:");

        txtEmail.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        lblUser2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser2.setText("First Name:");

        txtFname.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtFname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFnameActionPerformed(evt);
            }
        });

        lblUser3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser3.setText("Last Name:");

        txtLname.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtLname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLnameActionPerformed(evt);
            }
        });

        txtCno.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtCno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCnoActionPerformed(evt);
            }
        });

        lblUser4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser4.setText("Contact No.:");

        cbSex.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        cbSex.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        lblUser5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser5.setText("Sex:");

        spDob.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        lblUser6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser6.setText("Date of Birth:");

        lblUser7.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser7.setText("Profile Picture:");

        btnChoose.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnChoose.setText("Choose");
        btnChoose.setToolTipText("Choose a 170x170 image for your profile.");
        btnChoose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseActionPerformed(evt);
            }
        });

        lblUser8.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser8.setText("City:");

        lblUser9.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser9.setText("Municipality:");

        lblUser10.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser10.setText("Barangay:");

        cbCity.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        cbCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCityActionPerformed(evt);
            }
        });

        cbMunicipality.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        cbMunicipality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMunicipalityActionPerformed(evt);
            }
        });

        cbBarangay.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblUser1)
                                    .addComponent(lblPass1)
                                    .addComponent(lblUser)
                                    .addComponent(txtPass)
                                    .addComponent(txtEmail)
                                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblUser2)
                                    .addComponent(txtFname, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                    .addComponent(lblUser3)
                                    .addComponent(txtLname)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbCity, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblUser8))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblUser9)
                                            .addComponent(cbMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                .addComponent(btnBack)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                            .addComponent(lblUser4)
                                            .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(txtCno))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblUser5))
                                    .addGap(18, 18, 18)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblUser6)
                                        .addComponent(spDob, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                    .addGap(258, 258, 258)
                                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                            .addGap(130, 130, 130)
                                            .addComponent(lblUser7))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                            .addGap(0, 0, Short.MAX_VALUE)
                                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblUser10)
                                                .addComponent(cbBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE))))
                                .addComponent(lblPass)
                                .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addComponent(txtReEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnChoose))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblUser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUser1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(lblUser2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUser3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPass1)
                            .addComponent(lblUser9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbMunicipality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblUser8)
                        .addGap(31, 31, 31)))
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPass)
                    .addComponent(lblUser10)
                    .addComponent(lblUser7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnChoose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtReEnter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbBarangay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblUser4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblUser5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(lblUser6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
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

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtReEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReEnterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtReEnterActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        Resident_DB residentDB = new Resident_DB();
        residentDB.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        String userID = Login.loggedInUserID; // Get the logged-in user ID
        if (userID == null) {
            showErrorMessage("User  ID is null. Please log in again.");
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            // Update user details
            String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, txtUser.getText());
                stmt.setString(2, txtEmail.getText());
                stmt.setString(3, new String(txtPass.getPassword()));
                stmt.setString(4, userID);
                stmt.executeUpdate();
            }

            // Update profile details
            String profileSql = "UPDATE profiles SET first_name = ?, last_name = ?, contact_number = ?, sex = ?, date_of_birth = ?, address_id = ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(profileSql)) {
                stmt.setString(1, txtFname.getText());
                stmt.setString(2, txtLname.getText());
                stmt.setString(3, txtCno.getText());
                stmt.setString(4, (String) cbSex.getSelectedItem());
                stmt.setDate(5, new java.sql.Date(((java.util.Date) spDob.getValue()).getTime()));
                String addressId = getAddressId((String) cbCity.getSelectedItem(), (String) cbMunicipality.getSelectedItem(), (String) cbBarangay.getSelectedItem());
                stmt.setString(6, addressId);
                stmt.setString(7, userID);
                stmt.executeUpdate();
            }

            // Update profile picture if a new one is chosen
            if (profilePictureBytes != null) {
                String imgSql = "UPDATE profiles SET profile_picture = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(imgSql)) {
                    stmt.setBytes(1, profilePictureBytes);
                    stmt.setString(2, userID);
                    stmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException e) {
            showErrorMessage("Database error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPassActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtFnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFnameActionPerformed

    private void txtLnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLnameActionPerformed

    private void txtCnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCnoActionPerformed

    private void btnChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseActionPerformed
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Choose a Profile Picture");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();

            try {
                BufferedImage img = ImageIO.read(selectedFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                profilePictureBytes = baos.toByteArray();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the image file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnChooseActionPerformed

    private void cbCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCityActionPerformed
        loadMunicipalities((String) cbCity.getSelectedItem());
    }//GEN-LAST:event_cbCityActionPerformed

    private void cbMunicipalityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMunicipalityActionPerformed
        loadBarangays((String) cbMunicipality.getSelectedItem());
    }//GEN-LAST:event_cbMunicipalityActionPerformed

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
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Profile.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Profile().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnChoose;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cbBarangay;
    private javax.swing.JComboBox<String> cbCity;
    private javax.swing.JComboBox<String> cbMunicipality;
    private javax.swing.JComboBox<String> cbSex;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblPass1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUser1;
    private javax.swing.JLabel lblUser10;
    private javax.swing.JLabel lblUser2;
    private javax.swing.JLabel lblUser3;
    private javax.swing.JLabel lblUser4;
    private javax.swing.JLabel lblUser5;
    private javax.swing.JLabel lblUser6;
    private javax.swing.JLabel lblUser7;
    private javax.swing.JLabel lblUser8;
    private javax.swing.JLabel lblUser9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JSpinner spDob;
    private javax.swing.JTextField txtCno;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFname;
    private javax.swing.JTextField txtLname;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JPasswordField txtReEnter;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}

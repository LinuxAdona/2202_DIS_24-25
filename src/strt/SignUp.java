package strt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Database.DBConnection;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUp extends javax.swing.JFrame {

    public SignUp() {
        initComponents();
        configurations();
    }
    
    private void configurations() {
        spDob.setModel(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spDob, "MM/dd/yyyy");
        spDob.setEditor(dateEditor);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtReEnter = new javax.swing.JPasswordField();
        btnBack = new javax.swing.JButton();
        btnSignup = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Sign Up");

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

        btnSignup.setBackground(new java.awt.Color(154, 164, 255));
        btnSignup.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnSignup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-plus-regular-24.png"))); // NOI18N
        btnSignup.setText("Sign up");
        btnSignup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSignup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSignupActionPerformed(evt);
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
        lblUser4.setText("Contact Number:");

        cbSex.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        cbSex.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        lblUser5.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser5.setText("Sex:");

        spDob.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        lblUser6.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser6.setText("Date of Birth:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUser1)
                    .addComponent(lblPass1)
                    .addComponent(lblUser)
                    .addComponent(lblPass)
                    .addComponent(btnBack)
                    .addComponent(txtReEnter, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addComponent(txtPass)
                    .addComponent(txtEmail)
                    .addComponent(txtUser))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUser2)
                    .addComponent(txtFname)
                    .addComponent(lblUser3)
                    .addComponent(txtLname)
                    .addComponent(lblUser4)
                    .addComponent(txtCno)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblUser5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUser6)
                            .addComponent(spDob, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                    .addComponent(btnSignup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblUser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUser1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblUser2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUser3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPass1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUser4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPass)
                    .addComponent(lblUser5)
                    .addComponent(lblUser6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtReEnter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSignup)
                    .addComponent(btnBack))
                .addGap(28, 28, 28))
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

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPassActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        Login loginForm = Login.getInstance();
        loginForm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSignupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignupActionPerformed
        String username = txtUser.getText();
        String email = txtEmail.getText();
        String password = new String(txtPass.getPassword());
        String reEnteredPassword = new String(txtReEnter.getPassword());
        String fName = txtFname.getText();
        String lName = txtLname.getText();
        String cNo = txtCno.getText();
        String sex = (String) cbSex.getSelectedItem();
        
        Date dobDate = (Date) spDob.getValue();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dob = dateFormat.format(dobDate);

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()
                || reEnteredPassword.isEmpty() || fName.isEmpty() || lName.isEmpty()
                || cNo.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(reEnteredPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            String checkSql = "SELECT * FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username or email already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String insertSql = "INSERT INTO users (username, email, password, first_name, last_name, contact_number, sex, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String residentSql = "INSERT INTO residents(user_id) VALUES (?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                    PreparedStatement residentStmt = conn.prepareStatement(residentSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.setString(4, fName);
                insertStmt.setString(5, lName);
                insertStmt.setString(6, cNo);
                insertStmt.setString(7, sex);
                insertStmt.setString(8, dob);
                insertStmt.executeUpdate();
                
                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    String user_id = keys.getString(1);
                    
                    residentStmt.setString(1, user_id);
                    residentStmt.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            btnBackActionPerformed(evt);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSignupActionPerformed

    private void txtFnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFnameActionPerformed

    private void txtLnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLnameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLnameActionPerformed

    private void txtCnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCnoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnSignup;
    private javax.swing.JComboBox<String> cbSex;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblPass1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUser1;
    private javax.swing.JLabel lblUser2;
    private javax.swing.JLabel lblUser3;
    private javax.swing.JLabel lblUser4;
    private javax.swing.JLabel lblUser5;
    private javax.swing.JLabel lblUser6;
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

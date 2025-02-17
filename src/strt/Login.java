package strt;

import Database.DBConnection;
import obj.User;
import obj.UserSession;
import dis.Admin.Admin_DB;
import dis.Resident.Resident_DB;
import dis.SuperAdmin.SuperAdmin_DB;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class Login extends javax.swing.JFrame {

    private static Login main = null;
    public static String loggedInUserID;

    public Login() {
        initComponents();
    }

    public static Login getInstance() {
        if (main == null) {
            main = new Login();
        }
        return main;
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public void resetFields() {
        txtUser.setText("");
        txtPass.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnRegister = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(249, 249, 249));

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 0, 0));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Log in");

        lblUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser.setForeground(new java.awt.Color(0, 0, 0));
        lblUser.setText("Username/E-mail:");

        lblPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblPass.setForeground(new java.awt.Color(0, 0, 0));
        lblPass.setText("Password:");

        txtUser.setBackground(new java.awt.Color(255, 255, 255));
        txtUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtUser.setForeground(new java.awt.Color(0, 0, 0));
        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        txtPass.setBackground(new java.awt.Color(255, 255, 255));
        txtPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtPass.setForeground(new java.awt.Color(0, 0, 0));
        txtPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPassActionPerformed(evt);
            }
        });
        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPassKeyPressed(evt);
            }
        });

        btnLogin.setBackground(new java.awt.Color(154, 164, 255));
        btnLogin.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(0, 0, 0));
        btnLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-in-regular-24.png"))); // NOI18N
        btnLogin.setText(" Log in");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnRegister.setBackground(new java.awt.Color(154, 164, 255));
        btnRegister.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnRegister.setForeground(new java.awt.Color(0, 0, 0));
        btnRegister.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/user-plus-regular-24.png"))); // NOI18N
        btnRegister.setText(" Register");
        btnRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUser)
                    .addComponent(lblPass)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addComponent(btnLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(18, 18, 18)
                            .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtUser, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtPass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnLoginActionPerformed
        String user = txtUser.getText();
        String password = new String(txtPass.getPassword());

        if (user.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both username and password.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT u.user_id, p.first_name, p.last_name, u.password, u.role "
                    + "FROM profiles p "
                    + "JOIN users u ON p.user_id = u.user_id "
                    + "WHERE u.username = ? OR u.email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user);
                pstmt.setString(2, user);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String userId = rs.getString("user_id");
                    loggedInUserID = userId;
                    String fName = rs.getString("first_name");
                    String lName = rs.getString("last_name");
                    String fullName = fName + " " + lName;
                    String storedPassword = rs.getString("password");

                    User loggedInUser = new User(userId, user, storedPassword);
                    UserSession.setCurrentUser(loggedInUser);
                    if (storedPassword.equals(password)) {
                        sql = "SELECT * FROM users WHERE user_id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, userId);
                            ResultSet rsR = ps.executeQuery();

                            if (rsR.next()) {
                                String role = rsR.getString("role");
                                
                                if ("admins".equals(role)) {
                                    String sqlB = "SELECT u.user_id, b.branch_id "
                                            + "FROM users u "
                                            + "LEFT JOIN branches b ON b.user_id = u.user_id "
                                            + "WHERE u.user_id = ?";
                                    try (PreparedStatement psB = conn.prepareStatement(sqlB)) {
                                        psB.setString(1, userId);
                                        ResultSet rsB = psB.executeQuery();

                                        if (rsB.next()) {
                                            String branch = rsB.getString("branch_id");
                                            if (branch == null) {
                                                JOptionPane.showMessageDialog(null, "This user does not have a branch assigned yet.", "Error", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                Admin_DB adminDB = new Admin_DB();
                                                adminDB.setVisible(true);
                                                resetFields();
                                                this.dispose();
                                                JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!", "Login Successful",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                            }
                                        }
                                    }
                                } else if ("residents".equals(role)) {
                                    sql = "SELECT d.door_number "
                                            + "FROM profiles p "
                                            + "LEFT JOIN users u ON u.user_id = p.user_id "
                                            + "LEFT JOIN doors d ON d.door_id = p.door_id "
                                            + "WHERE p.user_id = ?";
                                    try (PreparedStatement rStmt = conn.prepareStatement(sql)) {
                                        rStmt.setString(1, userId);
                                        ResultSet rsRes = rStmt.executeQuery();

                                        if (rsRes.next()) {
                                            String doorNo = rsRes.getString("door_number");
                                            if (doorNo == null) {
                                                JOptionPane.showMessageDialog(null, "This user does not have a door assigned yet.", "Error", JOptionPane.ERROR_MESSAGE);
                                            } else {
                                                Resident_DB residentDB = new Resident_DB();
                                                residentDB.setVisible(true);
                                                resetFields();
                                                this.dispose();
                                                JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!",
                                                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                                            }
                                        }
                                    }
                                } else if ("super".equals(role)) {
                                    SuperAdmin_DB superAdminDB = new SuperAdmin_DB();
                                    superAdminDB.setVisible(true);
                                    resetFields();
                                    this.dispose();
                                    JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!", "Login Successful",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    showErrorMessage("User not recognized.");
                                }
                            }
                        }
                    } else {
                        showErrorMessage("Invalid password.");
                    }
                } else {
                    showErrorMessage("User  not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Database error: " + e.getMessage());
        }
    }// GEN-LAST:event_btnLoginActionPerformed

    private void txtPassKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtPassKeyPressed

    }// GEN-LAST:event_txtPassKeyPressed

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtPassActionPerformed

    }// GEN-LAST:event_txtPassActionPerformed

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtUserActionPerformed

    }// GEN-LAST:event_txtUserActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRegisterActionPerformed
        Registration signUp = new Registration();
        signUp.setVisible(true);
        this.setVisible(false);
    }// GEN-LAST:event_btnRegisterActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}

package strt;

import Database.DBConnection;
import obj.User;
import obj.UserSession;
import dis.Admin_DB;
import dis.Resident_DB;
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnSignup = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitle.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("Log in");

        lblUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblUser.setText("Username/E-mail:");

        lblPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        lblPass.setText("Password:");

        txtUser.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        txtPass.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
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
        btnLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/log-in-regular-24.png"))); // NOI18N
        btnLogin.setText("Log in");
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUser)
                    .addComponent(lblPass)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnLogin)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                            .addComponent(btnSignup))
                        .addComponent(txtUser, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtPass, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addComponent(lblUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSignup)
                    .addComponent(btnLogin))
                .addGap(28, 28, 28))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
        
    }//GEN-LAST:event_txtPassActionPerformed

    private void btnSignupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSignupActionPerformed
        SignUp signUp = new SignUp();
        signUp.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnSignupActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        String user = txtUser.getText();
        String password = new String(txtPass.getPassword());
        String userSql = "";

        if (user.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both username and password.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.Connect()) {
            String sql = "SELECT u.user_id, p.first_name, p.last_name, u.password FROM profiles p JOIN users u ON p.user_id = u.user_id "
                    + "WHERE u.username = ? OR email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user);
                pstmt.setString(2, user);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String fName = rs.getString("first_name");
                    String lName = rs.getString("last_name");
                    String fullName = fName + " " + lName;
                    String storedPassword = rs.getString("password");
                    
                    User loggedInUser = new User(loggedInUserID, user, storedPassword);
                    UserSession.setCurrentUser(loggedInUser);
                    String userID = UserSession.getCurrentUserID();
                    if (storedPassword.equals(password)) {
                        sql = "SELECT * FROM admins WHERE user_id = ?";
                        try (PreparedStatement adminStmt = conn.prepareStatement(sql)) {
                            adminStmt.setInt(1, userId);
                            ResultSet adminRs = adminStmt.executeQuery();

                            if (adminRs.next()) {
                                Admin_DB adminDB = new Admin_DB();
                                adminDB.setVisible(true);
                                resetFields();
                                this.dispose();
                                JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);                               
                            } else {
                                sql = "SELECT * FROM residents WHERE user_id = ?";
                                try (PreparedStatement residentStmt = conn.prepareStatement(sql)) {
                                    residentStmt.setInt(1, userId);
                                    ResultSet residentRs = residentStmt.executeQuery();

                                    if (residentRs.next()) {
                                        Resident_DB residentDB = new Resident_DB();
                                        residentDB.setVisible(true);
                                        resetFields();
                                        this.dispose();
                                        JOptionPane.showMessageDialog(this, "Welcome, " + fullName + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        showErrorMessage("User not recognized.");
                                    }
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
    }//GEN-LAST:event_btnLoginActionPerformed

    private void txtPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyPressed
        
    }//GEN-LAST:event_txtPassKeyPressed

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnSignup;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables
}

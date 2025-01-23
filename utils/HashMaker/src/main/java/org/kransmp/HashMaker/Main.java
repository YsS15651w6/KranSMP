package org.kransmp.HashMaker;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
@SuppressWarnings("unused")     
public class Main {
    private JButton createButton, copyButton;
    private JTextArea logTextArea;
    private JCheckBox darkModeCheckBox;
    private JPanel buttonPanel, panel;
    private String result = "";
    private Color defButton;
    
    public Main() {
        JFrame frame = new JFrame("HashMaker v1.1 stable (build 37)");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        createButton = new JButton("Create hash");
        copyButton = new JButton("Copy latest hash");
        logTextArea = new JTextArea();
        darkModeCheckBox = new JCheckBox("Dark mode");
        darkModeCheckBox.addItemListener(e -> toggleDarkMode(frame));
        
        createButton.setPreferredSize(new Dimension(200, 50));
        copyButton.setPreferredSize(new Dimension(200, 50));
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logTextArea.setEditable(false);    
        logTextArea.setLineWrap(true);     
        logTextArea.setWrapStyleWord(true);
        defButton = createButton.getBackground();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        buttonPanel = new JPanel(); // Initialize buttonPanel here
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(copyButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);
        panel.add(darkModeCheckBox, BorderLayout.SOUTH);
        
        createButton.addActionListener(e -> hash());
        copyButton.addActionListener(e -> copyHash());
        
        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
    
    private void hash() {
        JPasswordField passwordField = new JPasswordField();                                      
        int option = JOptionPane.showConfirmDialog(null, passwordField,                           
                "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(password.getBytes()); 
                StringBuilder hexString = new StringBuilder();         
                for (byte b : hashBytes) {                             
                    hexString.append(String.format("%02x", b));        
                }
                result = hexString.toString();
                logTextArea.append("SHA-256 hash: " + result + ".\n");
                logTextArea.append("You can now copy your hash using the copy button and send it to a server administrator.\n");
            } catch (NoSuchAlgorithmException e) {
                logTextArea.append("Error hashing password: " + e.getMessage() + "\n");  
            }
        }
    }
    
    private void copyHash() {
        if (!result.isEmpty()) {
            copyToClipboard(result);
            logTextArea.append("Copied password hash to system clipboard.\n");
        } else {
            logTextArea.append("Nothing to copy to system clipboard.\n");
        }
    }
    
    private void toggleDarkMode(JFrame frame) {
        boolean isDarkMode = darkModeCheckBox.isSelected();                 
        Color backgroundColor = isDarkMode ? Color.DARK_GRAY : Color.WHITE; 
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;           
        Color panelColor = isDarkMode ? Color.GRAY : Color.WHITE;           
        frame.getContentPane().setBackground(backgroundColor);
        createButton.setBackground(isDarkMode ? Color.DARK_GRAY : defButton);
        createButton.setForeground(textColor);                                      
        copyButton.setBackground(isDarkMode ? Color.DARK_GRAY : defButton);
        copyButton.setForeground(textColor);                                      
        logTextArea.setBackground(isDarkMode ? Color.DARK_GRAY : Color.WHITE);      
        logTextArea.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);          
        buttonPanel.setBackground(panelColor);                                      
        darkModeCheckBox.setForeground(textColor);                                  
        darkModeCheckBox.setBackground(backgroundColor);                            
        panel.setBackground(backgroundColor);                                       
        panel.setForeground(backgroundColor);                                       
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
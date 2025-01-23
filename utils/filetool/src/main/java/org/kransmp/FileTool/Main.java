package org.kransmp.FileTool;                                                                                                                      
                                                                                                                                                   
import org.json.JSONArray;                                                                                                                         
import org.json.JSONObject;                                                                                                                        
import javax.swing.*;                                                                                                                              
import java.awt.*;                                                                                                                                 
import java.awt.event.*;                                                                                                                           
import java.io.*;                                                                                                                                  
import java.security.MessageDigest;                                                                                                                
import java.security.NoSuchAlgorithmException;                                                                                                     
import javax.swing.filechooser.FileNameExtensionFilter;                                                                                            
import com.exaroton.api.ExarotonClient;                                                                                                            
import com.exaroton.api.server.Server;                                                                                                             
import com.exaroton.api.server.ServerFile;                                                                                                         
@SuppressWarnings("unused")                                                                                                                        
public class Main {                                                                                                                                
    private static final String API_TOKEN = "--REMOVED--";
    private static final String SERVER_ID = "--REMOVED--";                                                                                    
    private static final String[] ALLOWED_PASSWORD_HASHES = {                                                                                      
            "4dfd9ae5247e9f8f8338a8416f7161e870260d873cc13317dbff713ec0d10e5c",                                                                    
            "e9ec438626c512bac14bf89609e704dabc683110c8801a684a70da3184d20470",                                                                    
            "fad803b204984fd088af01e6380bbff8594a661e016a480972dadb4944dbad69",
            "4c4e6389e976b082a86dbec5fc1078d63e511a620aff4097355c324c7b6ae41f"                                                                     
    };                                                                                                                                             
                                                                                                                                                   
    private JButton uploadButton, deleteButton;                                                                                                    
    private JTextArea logTextArea;                                                                                                                 
    private JFileChooser fileChooser;                                                                                                              
    private JCheckBox darkModeCheckBox;                                                                                                            
    private JPanel buttonPanel, panel;                                                                                                             
    private Color defButton;                                                                                                                       
    boolean ongoing = false;                                                                                                                       
                                                                                                                                                   
    public Main() {                                                                                                                                
        // Frame Setup                                                                                                                             
        JFrame frame = new JFrame("FileTool v5.1.2 stable (build 389.3 rev 4)");                                                                   
        frame.setSize(700, 600);                                                                                                                   
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                                                                                      
        frame.setLocationRelativeTo(null);                                                                                                         
                                                                                                                                                   
        // Create components                                                                                                                       
        uploadButton = new JButton("Upload File");                                                                                                 
        deleteButton = new JButton("Delete File");                                                                                                 
        logTextArea = new JTextArea();                                                                                                             
        fileChooser = new JFileChooser();                                                                                                          
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image and Video Files", "jpg", "jpeg", "png", "mp4", "webm"));                      
                                                                                                                                                   
        // Dark Mode Checkbox                                                                                                                      
        darkModeCheckBox = new JCheckBox("Dark Mode");                                                                                             
        darkModeCheckBox.addItemListener(e -> toggleDarkMode(frame));                                                                              
                                                                                                                                                   
        // Set the button size and center them                                                                                                     
        uploadButton.setPreferredSize(new Dimension(200, 50));                                                                                     
        deleteButton.setPreferredSize(new Dimension(200, 50));                                                                                     
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);                                                                                    
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);                                                                                    
        defButton = uploadButton.getBackground();                                                                                                  
        // Set up the text area (read-only)                                                                                                        
        logTextArea.setEditable(false);                                                                                                            
        logTextArea.setLineWrap(true);                                                                                                             
        logTextArea.setWrapStyleWord(true);                                                                                                        
                                                                                                                                                   
        // Layout                                                                                                                                  
        panel = new JPanel();                                                                                                                      
        panel.setLayout(new BorderLayout());                                                                                                       
        buttonPanel = new JPanel();                                                                                                                
        buttonPanel.setLayout(new FlowLayout());                                                                                                   
        buttonPanel.add(uploadButton);                                                                                                             
        buttonPanel.add(deleteButton);                                                                                                             
        panel.add(buttonPanel, BorderLayout.NORTH);                                                                                                
        panel.add(new JScrollPane(logTextArea), BorderLayout.CENTER);                                                                              
        panel.add(darkModeCheckBox, BorderLayout.SOUTH);                                                                                           
                                                                                                                                                   
        // Button Action Listeners                                                                                                                 
        uploadButton.addActionListener(e -> authenticateAndUpload());                                                                              
        deleteButton.addActionListener(e -> authenticateAndDelete());                                                                              
                                                                                                                                                   
        // Add panel to frame                                                                                                                      
        frame.add(panel);                                                                                                                          
        frame.setVisible(true);                                                                                                                    
    }                                                                                                                                              
                                                                                                                                                   
    private void authenticateAndUpload() {                                                                                                         
        if (!ongoing) {                                                                                                                            
        	ongoing = true;                                                                                                                        
        	JPasswordField passwordField = new JPasswordField();                                                                                   
            int option = JOptionPane.showConfirmDialog(null, passwordField,                                                                        
                    "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);                                              
                                                                                                                                                   
            if (option == JOptionPane.OK_OPTION) {                                                                                                 
                String password = new String(passwordField.getPassword());                                                                         
                if (checkHash(password)) {                                                                                                         
                    logTextArea.append("Password accepted. Proceeding with file upload...\n");                                                     
                    uploadFile(password);                                                                                                          
                } else {                                                                                                                           
                    logTextArea.append("Incorrect password! Please try again.\n");                                                                 
                    ongoing = false;                                                                                                               
                }                                                                                                                                  
            } else {                                                                                                                               
            	logTextArea.append("Operation cancelled.\n");                                                                                      
            	ongoing = false;                                                                                                                   
            }                                                                                                                                      
        } else {                                                                                                                                   
        	logTextArea.append("Please wait for ongoing operation to finish!\n");                                                                  
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
    private void authenticateAndDelete() {                                                                                                         
        if (!ongoing) {                                                                                                                            
        	ongoing = true;                                                                                                                        
        	JPasswordField passwordField = new JPasswordField();                                                                                   
            int option = JOptionPane.showConfirmDialog(null, passwordField,                                                                        
                    "Enter your password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);                                              
                                                                                                                                                   
            if (option == JOptionPane.OK_OPTION) {                                                                                                 
                String password = new String(passwordField.getPassword());                                                                         
                if (checkHash(password)) {                                                                                                         
                    logTextArea.append("Password accepted. Proceeding with file deletion...\n");                                                   
                    deleteFile(password);                                                                                                          
                } else {                                                                                                                           
                    logTextArea.append("Incorrect password! Please try again.\n");                                                                 
                    ongoing = false;                                                                                                               
                }                                                                                                                                  
            } else {                                                                                                                               
            	logTextArea.append("Operation cancelled.\n");                                                                                      
            	ongoing = false;                                                                                                                   
            }                                                                                                                                      
        } else {                                                                                                                                   
        	logTextArea.append("Please wait for ongoing operation to finish!\n");                                                                  
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
                                                                                                                                                   
    private boolean checkHash(String password) {                                                                                                   
        try {                                                                                                                                      
            MessageDigest digest = MessageDigest.getInstance("SHA-256");                                                                           
            byte[] hashBytes = digest.digest(password.getBytes());                                                                                 
            StringBuilder hexString = new StringBuilder();                                                                                         
            for (byte b : hashBytes) {                                                                                                             
                hexString.append(String.format("%02x", b));                                                                                        
            }                                                                                                                                      
            String hashedPassword = hexString.toString();                                                                                          
            for (String allowedHash : ALLOWED_PASSWORD_HASHES) {                                                                                   
                if (hashedPassword.equals(allowedHash)) {                                                                                          
                    return true;                                                                                                                   
                }                                                                                                                                  
            }                                                                                                                                      
        } catch (NoSuchAlgorithmException e) {                                                                                                     
            logTextArea.append("Error generating password hash.\n");                                                                               
        }                                                                                                                                          
        return false;                                                                                                                              
    }                                                                                                                                              
                                                                                                                                                   
    private void uploadFile(String password) {                                                                                                     
        int returnValue = fileChooser.showOpenDialog(null);                                                                                        
        if (returnValue == JFileChooser.APPROVE_OPTION) {                                                                                          
            File file = fileChooser.getSelectedFile();                                                                                             
            logTextArea.append("Selected file: " + file.getAbsolutePath() + "\n");                                                                 
            uploadToServer(file);                                                                                                                  
            logTextArea.append("File uploaded successfully!\n");                                                                                   
            ongoing = false;                                                                                                                       
            logUploadData(password, file.getName());                                                                                               
        } else {                                                                                                                                   
        	logTextArea.append("Operation cancelled.\n");                                                                                          
        	ongoing = false;                                                                                                                       
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
    private void uploadToServer(File file) {                                                                                                       
        logTextArea.append("Uploading file...\n");                                                                                                 
        ExarotonClient client = new ExarotonClient(API_TOKEN);                                                                                     
        Server server = client.getServer(SERVER_ID);                                                                                               
        ServerFile serverFile = server.getFile("/media/" + file.getName());                                                                        
        try {                                                                                                                                      
            serverFile.upload(file.toPath());                                                                                                      
        } catch (Exception e) {                                                                                                                    
            logTextArea.append("Error: " + e.getMessage() + "\n");                                                                                 
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
    private void logUploadData(String password, String fileName) {                                                                                 
        try {                                                                                                                                      
            ExarotonClient client = new ExarotonClient(API_TOKEN);                                                                                 
            Server server = client.getServer(SERVER_ID);                                                                                           
            ServerFile file = server.getFile("/media/upload_log.json");                                                                            
                                                                                                                                                   
            String content = file.getContent();                                                                                                    
            JSONObject logData;                                                                                                                    
            if (content.isEmpty()) {                                                                                                               
                logData = new JSONObject();                                                                                                        
                logData.put("uploads", new JSONArray());                                                                                           
            } else {                                                                                                                               
                logData = new JSONObject(content);                                                                                                 
            }                                                                                                                                      
                                                                                                                                                   
            String passwordHash = hashPassword(password);                                                                                          
                                                                                                                                                   
            JSONArray uploads = logData.getJSONArray("uploads");                                                                                   
            JSONObject uploadInfo = new JSONObject();                                                                                              
            uploadInfo.put("password_hash", passwordHash);                                                                                         
            uploadInfo.put("file_name", fileName);                                                                                                 
            uploads.put(uploadInfo);                                                                                                               
                                                                                                                                                   
            file.putContent(logData.toString(4));                                                                                                  
            logTextArea.append("Upload data written to log file successfully.\n");                                                                 
                                                                                                                                                   
        } catch (Exception e) {                                                                                                                    
            logTextArea.append("Error writing to log file: " + e.getMessage() + "\n");                                                             
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
    private void deleteFile(String password) {                                                                                                     
    try {                                                                                                                                          
        String passwordHash = hashPassword(password); // Get hashed password                                                                       
                                                                                                                                                   
        // Access the "upload_log.json" file from the server's media folder                                                                        
        ExarotonClient client = new ExarotonClient(API_TOKEN);                                                                                     
        Server server = client.getServer(SERVER_ID);                                                                                               
        ServerFile file = server.getFile("/media/upload_log.json");                                                                                
                                                                                                                                                   
        // Get the current content of the log file                                                                                                 
        String content = file.getContent();                                                                                                        
                                                                                                                                                   
        // If the content is empty or the "uploads" array is empty, inform the user and stop                                                       
        if (content.isEmpty() || content.equals("{\"uploads\": []}")) {                                                                            
            logTextArea.append("No uploaded files found in the log.\n");                                                                           
            return;  // Exit the method if the log is empty or contains no uploads                                                                 
        }                                                                                                                                          
                                                                                                                                                   
        // Parse existing content as JSON                                                                                                          
        JSONObject logData = new JSONObject(content);                                                                                              
        JSONArray uploads = logData.getJSONArray("uploads");                                                                                       
                                                                                                                                                   
        // Collect matching file names                                                                                                             
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();                                                                         
        for (int i = 0; i < uploads.length(); i++) {                                                                                               
            JSONObject uploadInfo = uploads.getJSONObject(i);                                                                                      
            String logPasswordHash = uploadInfo.getString("password_hash");                                                                        
                                                                                                                                                   
            // If the password hash matches, add the file to the combo box model                                                                   
            if (logPasswordHash.equals(passwordHash)) {                                                                                            
                String fileName = uploadInfo.getString("file_name");                                                                               
                model.addElement(fileName);                                                                                                        
            }                                                                                                                                      
        }                                                                                                                                          
                                                                                                                                                   
        if (model.getSize() == 0) {                                                                                                                
            logTextArea.append("No files found uploaded by this password.\n");                                                                     
            return;  // Exit if no matching files are found                                                                                        
        }                                                                                                                                          
                                                                                                                                                   
        // Display a dropdown for file selection                                                                                                   
        JComboBox<String> fileDropdown = new JComboBox<>(model);                                                                                   
        int result = JOptionPane.showOptionDialog(null,                                                                                            
                fileDropdown,                                                                                                                      
                "Select file to delete",                                                                                                           
                JOptionPane.OK_CANCEL_OPTION,                                                                                                      
                JOptionPane.QUESTION_MESSAGE,                                                                                                      
                null,                                                                                                                              
                null,                                                                                                                              
                null);                                                                                                                             
                                                                                                                                                   
        if (result == JOptionPane.OK_OPTION) {                                                                                                     
            String selectedFile = (String) fileDropdown.getSelectedItem();                                                                         
            int choice = JOptionPane.showConfirmDialog(null,                                                                                       
                    "Are you sure you want to delete the file: " + selectedFile + "?",                                                             
                    "Delete File", JOptionPane.YES_NO_OPTION);                                                                                     
                                                                                                                                                   
            if (choice == JOptionPane.YES_OPTION) {                                                                                                
                // Find and delete the selected file                                                                                               
                for (int i = 0; i < uploads.length(); i++) {                                                                                       
                    JSONObject uploadInfo = uploads.getJSONObject(i);                                                                              
                    String fileName = uploadInfo.getString("file_name");                                                                           
                    if (fileName.equals(selectedFile)) {                                                                                           
                        deleteFileFromServer(selectedFile, uploads, i, file);                                                                      
                        logTextArea.append("File deleted successfully: " + selectedFile + "\n");                                                   
                        ongoing = false;                                                                                                           
                        break;                                                                                                                     
                    }                                                                                                                              
                }                                                                                                                                  
                                                                                                                                                   
                // Remove the selected file from the upload log after deletion                                                                     
                JSONArray updatedUploads = new JSONArray();                                                                                        
                for (int i = 0; i < uploads.length(); i++) {                                                                                       
                    JSONObject uploadInfo = uploads.getJSONObject(i);                                                                              
                    if (!uploadInfo.getString("file_name").equals(selectedFile)) {                                                                 
                        updatedUploads.put(uploadInfo);                                                                                            
                    }                                                                                                                              
                }                                                                                                                                  
                logData.put("uploads", updatedUploads);                                                                                            
                                                                                                                                                   
                // Update the upload log with the new list after deletion                                                                          
                file.putContent(logData.toString(4));  // Pretty print with indentation                                                            
                logTextArea.append("Log file updated successfully.\n");                                                                            
            } else {                                                                                                                               
                logTextArea.append("File deletion cancelled.\n");                                                                                  
                ongoing = false;                                                                                                                   
            }                                                                                                                                      
        } else {                                                                                                                                   
        	logTextArea.append("Operation cancelled.\n");                                                                                          
        	ongoing = false;                                                                                                                       
        }                                                                                                                                          
                                                                                                                                                   
    } catch (Exception e) {                                                                                                                        
        logTextArea.append("Error deleting file: " + e.getMessage() + "\n");                                                                       
    }                                                                                                                                              
}                                                                                                                                                  
    private void deleteFileFromServer(String fileName, JSONArray uploads, int index, ServerFile logFile) {                                         
        try {                                                                                                                                      
            // Delete file from server                                                                                                             
            ExarotonClient client = new ExarotonClient(API_TOKEN);                                                                                 
            Server server = client.getServer(SERVER_ID);                                                                                           
            ServerFile serverFile = server.getFile("/media/" + fileName);                                                                          
            serverFile.delete();                                                                                                                   
                                                                                                                                                   
            // Remove the file from the log                                                                                                        
            uploads.remove(index);                                                                                                                 
                                                                                                                                                   
            // Update the log file by writing back the modified log data                                                                           
            JSONObject updatedLogData = new JSONObject();                                                                                          
            updatedLogData.put("uploads", uploads);                                                                                                
            logFile.putContent(updatedLogData.toString(4));  // Pretty print with indentation                                                      
                                                                                                                                                   
        } catch (Exception e) {                                                                                                                    
            logTextArea.append("Error deleting file from server: " + e.getMessage() + "\n");                                                       
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
                                                                                                                                                   
    private String hashPassword(String password) {                                                                                                 
        try {                                                                                                                                      
            MessageDigest digest = MessageDigest.getInstance("SHA-256");                                                                           
            byte[] hashBytes = digest.digest(password.getBytes());                                                                                 
            StringBuilder hexString = new StringBuilder();                                                                                         
            for (byte b : hashBytes) {                                                                                                             
                hexString.append(String.format("%02x", b));                                                                                        
            }                                                                                                                                      
            return hexString.toString();                                                                                                           
        } catch (NoSuchAlgorithmException e) {                                                                                                     
            logTextArea.append("Error hashing password: " + e.getMessage() + "\n");                                                                
            return null;                                                                                                                           
        }                                                                                                                                          
    }                                                                                                                                              
                                                                                                                                                   
    // Toggle between Dark Mode and Light Mode                                                                                                     
    private void toggleDarkMode(JFrame frame) {                                                                                                    
        boolean isDarkMode = darkModeCheckBox.isSelected();                                                                                        
        Color backgroundColor = isDarkMode ? Color.DARK_GRAY : Color.WHITE;                                                                        
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;                                                                                  
        Color panelColor = isDarkMode ? Color.GRAY : Color.WHITE;                                                                                  
        frame.getContentPane().setBackground(backgroundColor);                                                                                     
        uploadButton.setBackground(isDarkMode ? Color.DARK_GRAY : defButton);                                                                      
        uploadButton.setForeground(textColor);                                                                                                     
        deleteButton.setBackground(isDarkMode ? Color.DARK_GRAY : defButton);                                                                      
        deleteButton.setForeground(textColor);                                                                                                     
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

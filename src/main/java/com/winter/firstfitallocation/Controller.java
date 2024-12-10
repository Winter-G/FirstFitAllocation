package com.winter.firstfitallocation;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.*;

public class Controller {

    @FXML
    private TextArea outputArea;

    @FXML
    private TextField partitionSizeField;

    @FXML
    private TextField jobSizeField;

    // Run allocation on button click
    @FXML
    protected void onRunAllocation() {
        try {
            FirstFitAllocation allocation = new FirstFitAllocation();
            String result = allocation.runFirstFitAlgorithm(); // Run the algorithm
            outputArea.setText(result); // Show the result in TextArea
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    // Load data from database on button click
    @FXML
    protected void onLoadData() {
        try {
            // Establish database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/memory_db", "root", "gnwg2001");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs");

            // Build string from result set
            StringBuilder data = new StringBuilder("Jobs Data:\n");
            while (rs.next()) {
                data.append("Job ID: ").append(rs.getInt("job_id")).append(", Job Size: ").append(rs.getInt("job_size")).append("\n");
            }

            // Close resources
            rs.close();
            stmt.close();
            conn.close();

            // Display data in the TextArea
            outputArea.setText(data.toString());
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    // Add new partition to the database
    @FXML
    protected void onAddPartition() {
        try {
            int partitionSize = Integer.parseInt(partitionSizeField.getText()); // Get partition size from input

            // Establish database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/memory_db", "root", "gnwg2001");
            String insertPartitionQuery = "INSERT INTO partitions (partition_size, allocated) VALUES (?, FALSE)";

            try (PreparedStatement stmt = conn.prepareStatement(insertPartitionQuery)) {
                stmt.setInt(1, partitionSize); // Set the partition size
                stmt.executeUpdate(); // Execute the insert query
                outputArea.setText("New partition added with size: " + partitionSize);
            }

            // Close resources
            conn.close();
        } catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid partition size.");
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    // Add new job to the database
    @FXML
    protected void onAddJob() {
        try {
            int jobSize = Integer.parseInt(jobSizeField.getText()); // Get job size from input

            // Establish database connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3308/memory_db", "root", "gnwg2001");
            String insertJobQuery = "INSERT INTO jobs (job_size, allocated_partition) VALUES (?, NULL)";

            try (PreparedStatement stmt = conn.prepareStatement(insertJobQuery)) {
                stmt.setInt(1, jobSize); // Set the job size
                stmt.executeUpdate(); // Execute the insert query
                outputArea.setText("New job added with size: " + jobSize);
            }

            // Close resources
            conn.close();
        } catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid job size.");
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }
}



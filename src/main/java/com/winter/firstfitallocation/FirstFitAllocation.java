package com.winter.firstfitallocation;

import java.sql.*;

public class FirstFitAllocation {
    private static final String DB_URL = "jdbc:mysql://localhost:3308/memory_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "gnwg2001";

    public String runFirstFitAlgorithm() {
        StringBuilder result = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Start transaction
            connection.setAutoCommit(false);

            // Get jobs and partitions
            ResultSet jobs = connection.createStatement().executeQuery("SELECT * FROM jobs");
            ResultSet partitions = connection.createStatement().executeQuery("SELECT * FROM partitions WHERE allocated = FALSE");

            // Prepare SQL statements
            String allocateJobQuery = "UPDATE partitions SET partition_size = ?, allocated = TRUE WHERE partition_id = ?";
            String updateJobQuery = "UPDATE jobs SET allocated_partition = ? WHERE job_id = ?";

            try (PreparedStatement allocatePartition = connection.prepareStatement(allocateJobQuery);
                 PreparedStatement updateJob = connection.prepareStatement(updateJobQuery)) {

                // Allocate jobs to partitions
                while (jobs.next()) {
                    int jobId = jobs.getInt("job_id");
                    int jobSize = jobs.getInt("job_size");

                    // Try to allocate job to available partitions
                    boolean allocated = false;
                    while (partitions.next()) {
                        int partitionId = partitions.getInt("partition_id");
                        int partitionSize = partitions.getInt("partition_size");

                        // If partition is large enough and not allocated, allocate the job
                        if (partitionSize >= jobSize) {
                            // Update partition and job records
                            allocatePartition.setInt(1, partitionSize - jobSize); // Update partition size
                            allocatePartition.setInt(2, partitionId); // Set partition ID
                            int rowsUpdated = allocatePartition.executeUpdate();
                            if (rowsUpdated > 0) {
                                updateJob.setInt(1, partitionId); // Set partition for job
                                updateJob.setInt(2, jobId); // Set job ID
                                updateJob.executeUpdate();
                                result.append("Job ").append(jobId).append(" allocated to Partition ").append(partitionId).append("\n");
                                allocated = true;
                                break;
                            }
                        }
                    }
                    if (!allocated) {
                        result.append("Job ").append(jobId).append(" could not be allocated.\n");
                    }
                }

                // Commit transaction
                connection.commit();
                result.append("Memory Allocation Complete.");
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                result.append("Error: ").append(e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.append("Error: ").append(e.getMessage());
        }
        return result.toString();
    }
}

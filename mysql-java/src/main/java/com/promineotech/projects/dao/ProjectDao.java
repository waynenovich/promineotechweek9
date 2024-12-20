package com.promineotech.projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.promineotech.projects.entity.Project;
import com.promineotech.projects.exception.DbException;
import com.promineotech.provided.util.DaoBase;

public class ProjectDao extends DaoBase {

    // Constants for table names
    @SuppressWarnings("unused")
    private static final String CATEGORY_TABLE = "category";
    @SuppressWarnings("unused")
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    @SuppressWarnings("unused")
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    @SuppressWarnings("unused")
    private static final String STEP_TABLE = "step";

    /**
     * Inserts a new project into the database.
     * 
     * @param project The project object containing the details to insert.
     * @return The same project object with the generated project ID set.
     */
    public Project insertProject(Project project) {
        String sql = "INSERT INTO " + PROJECT_TABLE +
                " (project_name, estimated_hours, actual_hours, difficulty, notes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection()) {
            // Start transaction
            startTransaction(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Set parameters using DaoBase's utility method
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                // Execute the statement
                stmt.executeUpdate();

                // Get the ID of the newly inserted project
                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);

                // Commit the transaction
                commitTransaction(conn);

                // Set the project ID in the project object and return it
                project.setProjectId(projectId);
                return project;
            } catch (Exception e) {
                // Roll back the transaction in case of an exception
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }
}

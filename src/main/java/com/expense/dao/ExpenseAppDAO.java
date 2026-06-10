package com.expense.dao;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.expense.model.Expense;
import com.expense.model.Category;
import com.expense.util.DatabaseConnection;

public class ExpenseAppDAO {
    private static final String ADD_CATEGORY = "INSERT INTO categories(name) VALUES (?)";
    private static final String DELETE_CATEGORY = "DELETE FROM categories WHERE id = ?";
    private static final String GET_ALL_CATEGORIES = "SELECT * FROM categories";
    private static final String ADD_EXPENSE = "INSERT INTO expense(category_id, description, amount, created_at) VALUES (?, ?, ?, ?)";
    private static final String DELETE_EXPENSE = "DELETE FROM expense WHERE id = ?";
    private static final String UPDATE_EXPENSE = "UPDATE expense SET category_id = ?, description = ?, amount = ? WHERE id = ?";
    private static final String GET_ALL_EXPENSES = "SELECT e.*, c.name as category_name FROM expense e LEFT JOIN categories c ON e.category_id = c.id ORDER BY e.created_at DESC";
    
    public int addCategory(Category category) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(ADD_CATEGORY, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, category.getName());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }
    public boolean removeCategory(int categoryId) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY);
        ) {
            stmt.setInt(1, categoryId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_ALL_CATEGORIES);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        }
        return categories;
    }
    public int addExpense(Expense expense) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(ADD_EXPENSE, Statement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, expense.getCategory_id());
            stmt.setString(2, expense.getDescription());
            stmt.setInt(3, expense.getAmount());
            stmt.setTimestamp(4, Timestamp.valueOf(expense.getCreated_at()));
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }
    public boolean deleteExpense(int expenseId) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(DELETE_EXPENSE);
        ) {
            stmt.setInt(1, expenseId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public boolean updateExpense(Expense expense) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_EXPENSE);
        ) {
            stmt.setInt(1, expense.getCategory_id());
            stmt.setString(2, expense.getDescription());
            stmt.setInt(3, expense.getAmount());
            stmt.setInt(4, expense.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    public List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(GET_ALL_EXPENSES);
            ResultSet rs = stmt.executeQuery();
        ) {
            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setCategory_id(rs.getInt("category_id"));
                expense.setDescription(rs.getString("description"));
                expense.setAmount(rs.getInt("amount"));
                expense.setCreated_at(rs.getTimestamp("created_at").toLocalDateTime());
                expenses.add(expense);
            }
        }
        return expenses;
    }
    public String getCategoryNameById(int categoryId) throws SQLException {
        try (
            Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT name FROM categories WHERE id = ?");
        ) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "Unknown";
    }
}


package com.expense.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import com.expense.dao.ExpenseAppDAO;
import com.expense.model.Category;
import com.expense.model.Expense;

public class ExpenseAppGui extends JFrame {
    private ExpenseAppDAO expenseAppDAO;
    private JButton categoryButton, expenseButton;
    private JTextField nameField;
    public ExpenseAppGui() {
        expenseAppDAO = new ExpenseAppDAO();
        initializeComponents();
        setupComponents();
        setupEventListeners();
    }
    private void initializeComponents() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);

        nameField = new JTextField(20);
        categoryButton = new JButton("Manage Categories");
        expenseButton = new JButton("Manage Expenses");
        
        categoryButton.setPreferredSize(new Dimension(200, 60));
        expenseButton.setPreferredSize(new Dimension(200, 60));
    }
    private void setupComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        add(categoryButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(expenseButton, gbc);
    }
    private void setupEventListeners() {
        categoryButton.addActionListener(e -> onCategoryButton());
        expenseButton.addActionListener(e -> onExpenseButton());
    }
    private void onCategoryButton() {
        JFrame categoryFrame = new JFrame("Category Manager");
        categoryFrame.setSize(600, 400);
        categoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        categoryFrame.setLocationRelativeTo(this);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Category Name:");
        JTextField categoryNameField = new JTextField(20);
        JButton addButton = new JButton("Add Category");
        JButton removeButton = new JButton("Remove Category");
        JButton refreshButton = new JButton("Refresh");
        inputPanel.add(nameLabel);
        inputPanel.add(categoryNameField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(refreshButton);
        String[] columnNames = {"ID", "Name"};
        DefaultTableModel categoryTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable categoryTable = new JTable(categoryTableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addCategoryAction(categoryFrame, categoryNameField, categoryTable, categoryTableModel));
        removeButton.addActionListener(e -> removeCategoryAction(categoryFrame, categoryTable, categoryTableModel));
        refreshButton.addActionListener(e -> loadCategories(categoryTable, categoryTableModel));
        categoryFrame.add(panel);
        categoryFrame.setVisible(true);
        loadCategories(categoryTable, categoryTableModel);
    }
    private void onExpenseButton() {
        JFrame expenseFrame = new JFrame("Expense Manager");
        expenseFrame.setSize(1080, 720);
        expenseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        expenseFrame.setLocationRelativeTo(this);
        expenseFrame.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        JTextField expenseNameField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JComboBox<String> categoryCombo = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(expenseNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryCombo, gbc);

        JButton addExpenseButton = new JButton("Add Expense");
        JButton deleteExpenseButton = new JButton("Delete Expense");
        JButton updateExpenseButton = new JButton("Update Expense");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addExpenseButton);
        buttonPanel.add(updateExpenseButton);
        buttonPanel.add(deleteExpenseButton);
        buttonPanel.add(refreshButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        String[] columnNames = {"ID", "Name", "Amount", "Category", "Date"};
        DefaultTableModel expenseTableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable expenseTable = new JTable(expenseTableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        expenseFrame.add(northPanel, BorderLayout.NORTH);
        expenseFrame.add(new JScrollPane(expenseTable), BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select an expense to edit or delete:"));
        expenseFrame.add(statusPanel, BorderLayout.SOUTH);

        loadCategoriesToCombo(categoryCombo);

        addExpenseButton.addActionListener(e -> addExpenseAction(expenseFrame, expenseNameField, amountField, categoryCombo, expenseTable, expenseTableModel));
        deleteExpenseButton.addActionListener(e -> deleteExpenseAction(expenseFrame, expenseTable, expenseTableModel));
        updateExpenseButton.addActionListener(e -> updateExpenseAction(expenseFrame, expenseNameField, amountField, categoryCombo, expenseTable, expenseTableModel));
        refreshButton.addActionListener(e -> refreshExpenseAction(categoryCombo, expenseTable, expenseTableModel));

        expenseFrame.setVisible(true);
        
        loadExpenses(expenseTable, expenseTableModel);
    }
    private void loadCategories(JTable table, DefaultTableModel model) {
        try {
            List<Category> categories = expenseAppDAO.getAllCategories();
            model.setRowCount(0);
            for (Category c : categories) {
                model.addRow(new Object[]{c.getId(), c.getName()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoriesToCombo(JComboBox<String> combo) {
        try {
            List<Category> categories = expenseAppDAO.getAllCategories();
            combo.removeAllItems();
            for (Category c : categories) {
                combo.addItem(c.getName());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCategoryAction(JFrame frame, JTextField nameField, JTable table, DefaultTableModel model) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Category name is required!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Category category = new Category(name);
            int categoryId = expenseAppDAO.addCategory(category);
            if (categoryId != -1) {
                JOptionPane.showMessageDialog(frame, "Category added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                loadCategories(table, model);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeCategoryAction(JFrame frame, JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Please select a category to remove!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try {
            if (expenseAppDAO.removeCategory(id)) {
                JOptionPane.showMessageDialog(frame, "Category removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCategories(table, model);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to remove category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error removing category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExpenseAction(JFrame frame, JTextField nameField, JTextField amountField, JComboBox<String> categoryCombo, JTable table, DefaultTableModel model) {
        String description = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        
        if (description.isEmpty() || amountText.isEmpty() || selectedCategory == null) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int amount = Integer.parseInt(amountText);
            int categoryId = getCategoryIdByName(selectedCategory);
            if (categoryId == -1) {
                JOptionPane.showMessageDialog(frame, "Invalid category selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Expense expense = new Expense(categoryId, description, amount, java.time.LocalDateTime.now());
            int expenseId = expenseAppDAO.addExpense(expense);
            if (expenseId != -1) {
                JOptionPane.showMessageDialog(frame, "Expense added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                amountField.setText("");
                loadExpenses(table, model);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding expense: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteExpenseAction(JFrame frame, JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Please select an expense to delete!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int expenseId = (int) model.getValueAt(row, 0);
        try {
            if (expenseAppDAO.deleteExpense(expenseId)) {
                JOptionPane.showMessageDialog(frame, "Expense deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExpenses(table, model);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error deleting expense: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpenseAction(JFrame frame, JTextField nameField, JTextField amountField, JComboBox<String> categoryCombo, JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(frame, "Please select an expense to update!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String description = nameField.getText().trim();
        String amountText = amountField.getText().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        
        if (description.isEmpty() || amountText.isEmpty() || selectedCategory == null) {
            JOptionPane.showMessageDialog(frame, "All fields are required!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int amount = Integer.parseInt(amountText);
            int categoryId = getCategoryIdByName(selectedCategory);
            if (categoryId == -1) {
                JOptionPane.showMessageDialog(frame, "Invalid category selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int expenseId = (int) model.getValueAt(row, 0);
            Expense expense = new Expense(categoryId, description, amount, java.time.LocalDateTime.now());
            expense.setId(expenseId);
            
            if (expenseAppDAO.updateExpense(expense)) {
                JOptionPane.showMessageDialog(frame, "Expense updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                nameField.setText("");
                amountField.setText("");
                loadExpenses(table, model);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update expense.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating expense: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshExpenseAction(JComboBox<String> combo, JTable table, DefaultTableModel model) {
        loadCategoriesToCombo(combo);
        
        loadExpenses(table, model);
        
        JOptionPane.showMessageDialog(null, "Data refreshed successfully!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadExpenses(JTable table, DefaultTableModel model) {
        try {
            List<Expense> expenses = expenseAppDAO.getAllExpenses();
            model.setRowCount(0);
            for (Expense expense : expenses) {
                String categoryName = expenseAppDAO.getCategoryNameById(expense.getCategory_id());
                model.addRow(new Object[]{
                    expense.getId(), 
                    expense.getDescription(), 
                    expense.getAmount(), 
                    categoryName, 
                    expense.getCreated_at().toString()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCategoryIdByName(String categoryName) {
        try {
            List<Category> categories = expenseAppDAO.getAllCategories();
            for (Category c : categories) {
                if (c.getName().equals(categoryName)) {
                    return c.getId();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting category ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }
}

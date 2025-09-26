package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.model.Expense;
import com.todo.dao.ExpenseTrackerDAO;
import com.todo.util.DatabaseConnection;

import java.util.Date;

public class ExpenseTrackerGUI extends JFrame {
    private ExpenseTrackerDAO ExpenseTrackerDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completedCheckBox;
    private JButton addButton, deleteButton, editButton, refreshButton;
    private JComboBox<String> categoryComboBox;

    public ExpenseTrackerGUI() {
        ExpenseTrackerDAO = new ExpenseTrackerDAO();
        todoTable = new JTable();
        tableModel = new DefaultTableModel();
        initializeComponents();
        setupComponents();
        setupEventListeners();
        filterExpense();
    }
    private void filterExpense(){
        if(categoryComboBox.getSelectedItem().equals("All")){
            loadExpenses();
        }
        else if(categoryComboBox.getSelectedItem().equals("Pending")){
            loadPendingExpenses();
        }
        else{
            loadAmount SpentExpenses();
        }
    } 
       private void initializeComponents() {
        setCategory("Expense App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Category", "Description", "Amount Spent", "Created At", "Updated At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inputs
        titleField = new JTextField(25);

        descriptionArea = new JTextArea(4, 25);
        descriptionArea.setEditable(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completedCheckBox = new JCheckBox("Amount Spent");

        // Buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Update");
        refreshButton = new JButton("Refresh");

        // Filter dropdown
        String[] categoryOptions = {"All", "Amount Spent", "Pending"};
        categoryComboBox = new JComboBox<>(categoryOptions);
    }
    private void setupComponents() {
        setLayout(new BorderLayout());

        // Input panel for title, description, completed checkbox
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(completedCheckBox, gbc);

        // Button panel for Add, Update, Delete, Refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // Filter panel for filter label and combo box
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(categoryComboBox);

        // North panel to combine filter, input, and button panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(inputPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(todoTable), BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a todo to edit or delete:"));
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> updateExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        refreshButton.addActionListener(e -> refreshExpense());
        categoryComboBox.addActionListener(e -> filterExpense());

        // ✅ Add this listener
        todoTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedExpense();
            }
        });
    }


    private void addExpense(){
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();
        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Expense todo = new Expense(title,description);
            todo.setAmount Spent(completed);
            ExpenseTrackerDAO.createtodo(todo);

            JOptionPane.showMessageDialog(this,"Expense added succesfully","Success",JOptionPane.INFORMATION_MESSAGE);
            loadExpenses();
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error adding todo","Failure",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateExpense() {
        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to update", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completedCheckBox.isSelected();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category or Description is empty!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Expense todo = ExpenseTrackerDAO.getExpenseById(id);
            if (todo != null) {
                todo.setCategory(title);
                todo.setDescription(description);
                todo.setAmount Spent(completed);
                todo.setUpdated_at(java.time.LocalDateTime.now());

                if (ExpenseTrackerDAO.updateExpense(todo)) {
                    JOptionPane.showMessageDialog(this, "Expense updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadExpenses();
                } else {
                    JOptionPane.showMessageDialog(this, "Update failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteExpense(){
        int row = todoTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try{
            if(ExpenseTrackerDAO.deleteExpense(id)==true){
            JOptionPane.showMessageDialog(this,"Expense deleted successfully","Success",JOptionPane.INFORMATION_MESSAGE);}
            else{
                JOptionPane.showMessageDialog(this,"Failed to delete the row","Failed",JOptionPane.WARNING_MESSAGE);
            }

        }
        catch (SQLException e){
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        loadExpenses();
    }
    private void refreshExpense(){
        loadExpenses();
    }
    private void loadPendingExpenses(){
        try{
            List<Expense> todos = ExpenseTrackerDAO.getPendingExpenses();
            updateTable(todos);
        }catch (SQLException e){
            JOptionPane.showMessageDialog(this,"Amount Spent Expenses Loaded"+e.getMessage(),"Loaded",JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
    }
    private void loadAmount SpentExpenses(){
        try{
            List<Expense> todos = ExpenseTrackerDAO.getAmount SpentExpenses();
            updateTable(todos);
        }catch (SQLException e){
            JOptionPane.showMessageDialog(this,"Amount Spent Expenses Loaded"+e.getMessage(),"Loaded",JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();
        }
    }
    private void loadExpenses(){
        try {
            List<Expense> todos = ExpenseTrackerDAO.getAllExpenses();
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void updateTable(List<Expense> todos){
        tableModel.setRowCount(0);
        for(Expense todo : todos){
            Object[] row = {todo.getId(), todo.getCategory(), todo.getDescription(), todo.isAmount Spent(), todo.getCreated_at(), todo.getUpdated_at()};
            tableModel.addRow(row);
        }
    }
    private void loadSelectedExpense(){
        int row  = todoTable.getSelectedRow();
        if(row >= 0){
            String title = tableModel.getValueAt(row, 1).toString();
            String description = tableModel.getValueAt(row, 2).toString();
            boolean completed = Boolean.parseBoolean(tableModel.getValueAt(row, 3).toString());
            titleField.setText(title);
            descriptionArea.setText(description);
            completedCheckBox.setSelected(completed);
        }
    }
}

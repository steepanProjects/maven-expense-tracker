package com.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Expense {
    private int id;
    private int category_id;
    private String description;
    private BigDecimal amt_spent;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
    // For display purposes - not stored in database
    private String category_name;

    public Expense() {}

    public Expense(int category_id, String description, BigDecimal amt_spent) {
        this.category_id = category_id;
        this.description = description;
        this.amt_spent = amt_spent;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public Expense(int id, int category_id, String description, BigDecimal amt_spent, 
                   LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.category_id = category_id;
        this.description = description;
        this.amt_spent = amt_spent;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmt_spent() {
        return amt_spent;
    }

    public void setAmt_spent(BigDecimal amt_spent) {
        this.amt_spent = amt_spent;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", category_id=" + category_id +
                ", description='" + description + '\'' +
                ", amt_spent=" + amt_spent +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}

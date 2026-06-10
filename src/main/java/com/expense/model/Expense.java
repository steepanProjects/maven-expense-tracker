
package com.expense.model;
import java.time.LocalDateTime;
public class Expense{
    private int id;
    private String description; 
    private int category_id;
    private int amount;
    private LocalDateTime created_at;
    public int getId(){
        return id;
    }
    public Expense(){
        this.created_at = LocalDateTime.now();
    }
    public Expense(int category_id , String description, int amount, LocalDateTime created_at){
        this();
        this.category_id = category_id;
        this.description = description;
        this.amount = amount;
        this.created_at = created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}

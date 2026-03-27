package com.acxiom.librarymgmt.models;

import java.util.Date;

public class Book {
    private String serialNo;
    private String name;
    private String authorName;
    private String category;
    private String type; // book/movie
    private String status; // available/issued/damaged/lost
    private double cost;
    private Date procurementDate;
    private int quantity;

    public Book() {
        // Required for Firestore
    }

    public Book(String serialNo, String name, String authorName, String category, String type, String status, double cost, Date procurementDate, int quantity) {
        this.serialNo = serialNo;
        this.name = name;
        this.authorName = authorName;
        this.category = category;
        this.type = type;
        this.status = status;
        this.cost = cost;
        this.procurementDate = procurementDate;
        this.quantity = quantity;
    }

    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public Date getProcurementDate() { return procurementDate; }
    public void setProcurementDate(Date procurementDate) { this.procurementDate = procurementDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

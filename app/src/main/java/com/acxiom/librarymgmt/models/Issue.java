package com.acxiom.librarymgmt.models;

import java.util.Date;

public class Issue {
    private String issueId;
    private String serialNo;
    private String bookName;
    private String authorName;
    private String membershipId;
    private Date issueDate;
    private Date returnDate;
    private Date actualReturnDate;
    private String status; // active/returned/overdue
    private double fineCalculated;
    private boolean finePaid;
    private String remarks;

    public Issue() {
        // Required for Firestore
    }

    public Issue(String issueId, String serialNo, String bookName, String authorName, String membershipId, Date issueDate, Date returnDate, Date actualReturnDate, String status, double fineCalculated, boolean finePaid, String remarks) {
        this.issueId = issueId;
        this.serialNo = serialNo;
        this.bookName = bookName;
        this.authorName = authorName;
        this.membershipId = membershipId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.fineCalculated = fineCalculated;
        this.finePaid = finePaid;
        this.remarks = remarks;
    }

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }

    public String getSerialNo() { return serialNo; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public Date getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(Date actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineCalculated() { return fineCalculated; }
    public void setFineCalculated(double fineCalculated) { this.fineCalculated = fineCalculated; }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

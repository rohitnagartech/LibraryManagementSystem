package com.acxiom.librarymgmt.models;

import java.util.Date;

public class Membership {
    private String membershipId;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String contactAddress;
    private String aadharCardNo;
    private String membershipType; // 6months/1year/2years
    private String status; // active/inactive
    private Date startDate;
    private Date endDate;
    private double amountPendingFine;

    public Membership() {
        // Required for Firestore
    }

    public Membership(String membershipId, String firstName, String lastName, String contactNumber, String contactAddress, String aadharCardNo, String membershipType, String status, Date startDate, Date endDate, double amountPendingFine) {
        this.membershipId = membershipId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.contactAddress = contactAddress;
        this.aadharCardNo = aadharCardNo;
        this.membershipType = membershipType;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amountPendingFine = amountPendingFine;
    }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }

    public String getAadharCardNo() { return aadharCardNo; }
    public void setAadharCardNo(String aadharCardNo) { this.aadharCardNo = aadharCardNo; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getAmountPendingFine() { return amountPendingFine; }
    public void setAmountPendingFine(double amountPendingFine) { this.amountPendingFine = amountPendingFine; }
}

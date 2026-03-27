package com.acxiom.librarymgmt.models;

public class IssueRequest {
    private String requestId;
    private String membershipId;
    private String bookName;
    private String requestedDate;
    private String fulfilledDate;
    private String status; // pending/fulfilled

    public IssueRequest() {
        // Required for Firestore
    }

    public IssueRequest(String requestId, String membershipId, String bookName, String requestedDate, String fulfilledDate, String status) {
        this.requestId = requestId;
        this.membershipId = membershipId;
        this.bookName = bookName;
        this.requestedDate = requestedDate;
        this.fulfilledDate = fulfilledDate;
        this.status = status;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getRequestedDate() { return requestedDate; }
    public void setRequestedDate(String requestedDate) { this.requestedDate = requestedDate; }

    public String getFulfilledDate() { return fulfilledDate; }
    public void setFulfilledDate(String fulfilledDate) { this.fulfilledDate = fulfilledDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

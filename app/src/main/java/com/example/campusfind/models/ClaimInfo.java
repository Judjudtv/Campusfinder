package com.example.campusfind.models;

// Claim + pangalan ng item at user (galing sa JOIN ng 3 tables),
// para makita sa listahan ang pangalan at hindi lang ID number
public class ClaimInfo {
    private int claimId;
    private int itemId;
    private int userId;
    private String itemName;
    private String userName;
    private String claimStatus; // "Pending", "Approved", "Rejected"

    public ClaimInfo(int claimId, int itemId, int userId,
                     String itemName, String userName, String claimStatus) {
        this.claimId = claimId;
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.userName = userName;
        this.claimStatus = claimStatus;
    }

    public int getClaimId() { return claimId; }
    public int getItemId() { return itemId; }
    public int getUserId() { return userId; }
    public String getItemName() { return itemName; }
    public String getUserName() { return userName; }
    public String getClaimStatus() { return claimStatus; }
}
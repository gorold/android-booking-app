package com.gerald.java1dapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingsModel {
    private String booker;
    private Date startTime;
    private Date endTime;
    private String roomId;
    private String roomName;
    private String bookerName;
    private HashMap<String, Boolean> bookees;
    private List<String> allUsers;
    private boolean approved;
    private boolean rejected;

    public BookingsModel() {}

    public BookingsModel(String thisUser, String userOne, String userTwo, String bookerName, Date startTime, Date endTime, String roomId, String roomName) {
        this.approved = false;
        this.rejected = false;
        this.booker = thisUser;
        this.allUsers = new ArrayList<>();
        allUsers.add(thisUser);
        allUsers.add(userOne);
        allUsers.add(userTwo);
        this.bookees = new HashMap<>();
        bookees.put(userOne, false);
        bookees.put(userTwo, false);
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
        this.roomName = roomName;
        this.bookerName = bookerName;
    }

    public List<String> getAllUsers() {
        return allUsers;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setAllUsers(List<String> allUsers) {
        this.allUsers = allUsers;
    }

    public boolean getApproved() {return approved;}

    public boolean getRejected() {return rejected;}

    public Date getStartTime() {return startTime;}

    public Date getEndTime() {return endTime;}

    public String getRoomId() {return roomId;}

    public String getBooker() {
        return booker;
    }

    public void setStartTime(Date startTime) {this.startTime = startTime;}

    public void setEndTime(Date endTime) {this.endTime = endTime;}

    public void setRoomId(String roomId) {this.roomId = roomId;}

    public void setBooker(String booker) {
        this.booker = booker;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBookerName() {
        return bookerName;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public HashMap<String, Boolean> getBookees() {
        return bookees;
    }

    public void setBookees(HashMap<String, Boolean> bookees) {
        this.bookees = bookees;
    }

    public boolean isAvailable(Date bookingStartTime, Date bookingEndTime) {
        if ((bookingStartTime.after(this.endTime) && bookingEndTime.after(this.endTime)) || (bookingEndTime.before(this.startTime) && bookingStartTime.before(this.startTime)))
            return true;
        else
            return false;
    }
}
